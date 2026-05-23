package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

    private final UserDetailsService getUserDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    ForgotController(UserDetailsService getUserDetailsService) {
        this.getUserDetailsService = getUserDetailsService;
    }

    @RequestMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    @PostMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session, org.springframework.ui.Model model) {

        User user = this.userRepository.getUserByUserName(email);

        if (user == null) {
            model.addAttribute("message", "User does not exist with this email !!");
            return "forgot_email_form";
        }

        int otp = 100000 + new Random().nextInt(900000);

        String subject = "OTP From SCM";
        String message =
                "<div style='max-width:500px;margin:auto;padding:20px;"
                + "border:1px solid #e2e2e2;border-radius:10px;"
                + "font-family:Arial,sans-serif;background-color:#f9f9f9;text-align:center;'>"
                + "<h2 style='color:#4A00E0;margin-bottom:10px;'>Smart Contact Manager</h2>"
                + "<h1 style='color:#ff5722;letter-spacing:3px;'>" + otp + "</h1>"
                + "<p style='font-size:16px;color:#555;'>Your OTP for password reset</p>"
                + "<p style='color:#888;'>OTP is valid for 5 minutes</p>"
                + "</div>";

        boolean flag = this.emailService.sendEmail(subject, message, email);

        if (flag) {
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);
            return "verify_otp";
        } else {
            model.addAttribute("message", "OTP not sent !!");
            return "forgot_email_form";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp") int otp, HttpSession session, org.springframework.ui.Model model) {

        Integer myOtp = (Integer) session.getAttribute("myotp");

        if (myOtp == null) {
            model.addAttribute("message", "Session expired! Please try again.");
            return "forgot_email_form";
        }

        if (myOtp == otp) {
            return "normal/password_change_form";
        } else {
            model.addAttribute("message", "You have entered wrong OTP !!");
            return "verify_otp";
        }
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpSession session,Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Password and Confirm Password do not match!");
            return "normal/password_change_form";
        }

        String email = (String) session.getAttribute("email");
        User user = this.userRepository.getUserByUserName(email);

        if (user == null) {
            model.addAttribute("message", "User not found !!");
            return "forgot_email_form";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);

        model.addAttribute("message", "Password changed successfully!");
        return "login";
    }
}