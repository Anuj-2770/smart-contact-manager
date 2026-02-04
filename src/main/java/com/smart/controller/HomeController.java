package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
import com.smart.dao.UserRepository;
//import com.smart.entities.Contact;
//import com.smart.entities.User;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String test() {
//		User user = new User();
//		user.setName("Anuj Yadav");
//		user.setEmail("anuj@gmail.com");
//		Contact contact = new Contact();
//		user.getContacts().add(contact);
//		userRepository.save(user);
//		return "working";
//	}
	
// home handler
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	//about handler
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	//signup handler
	@RequestMapping("/signup")
	public String signUp(Model model) {
		model.addAttribute("title", "Ragister - Smart Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	//login handler
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
	
	//handler for regidtering user
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
	                           BindingResult result1,
	                           Model model,
	                           RedirectAttributes redirectAttributes) {

	    if (result1.hasErrors()) {
	        model.addAttribute("user", user);
	        return "signup";
	    }

	    // Duplicate email check
	    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
	        redirectAttributes.addFlashAttribute(
	            "message",
	            new Message("Email already registered!", "alert-danger")
	        );
	        return "redirect:/signup";
	    }

	    try {
	        user.setRole("ROLE_USER");
	        user.setEnabled(true);
	        user.setImageUrl("default.png");
	        user.setPassword(passwordEncoder.encode(user.getPassword()));

	        userRepository.save(user);

	        // Use flash attribute instead of session
	        redirectAttributes.addFlashAttribute(
	            "message",
	            new Message("Successfully registered!!", "alert-success")
	        );

	        return "redirect:/signup";

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute(
	            "message",
	            new Message("Something went wrong! " + e.getMessage(), "alert-danger")
	        );
	        return "redirect:/signup";
	    }
}
}
