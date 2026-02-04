package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		//System.out.println("USERNAME"+userName);
		//get the user using usenName(email)
		User user = userRepository.getUserByUserName(userName);
		//System.out.println("User"+user);
		model.addAttribute("user", user);
	}
	
	// for the index page...
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		return "normal/user-dashboard";
	}
	
	// open add Contact form handler
	@GetMapping("/add-contact-form")
	public String openAddContact(Model model) {
		model.addAttribute("title" ,"ADD CONTACT" );
		model.addAttribute("contact", new Contact());
		return "normal/add-contact-form";
	}
	
	//processing the add contact form
	@PostMapping("/process-contact")
	public String  processContact(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
	        Principal principal) {
		try {
	        // get logged-in user
	        String username = principal.getName();
	        User user = userRepository.getUserByUserName(username);

	        // file handling
	        if (!file.isEmpty()) {
	            contact.setImage(file.getOriginalFilename());
	        } else {
	            contact.setImage("default.png");
	        }

	        // set user
	        contact.setUser(user);
	        user.getContacts().add(contact);

	        userRepository.save(user);

	        System.out.println("Contact saved successfully");

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

		return "normal/add-contact-form";
	}

}
