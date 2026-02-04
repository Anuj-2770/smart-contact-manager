package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
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
	        Principal principal , Model model) {
		try {
	        // get logged-in user
	        String username = principal.getName();
	        User user = userRepository.getUserByUserName(username);

	        // file handling
	        if (!file.isEmpty()) {
	            contact.setImage(file.getOriginalFilename());
	            File file1 = new ClassPathResource("static/image").getFile();
	            Path path =  Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
	            Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
	        } else {
	            contact.setImage("contact.png");
	        }
	        // message block pop 
	  model.addAttribute("message", new Message("Your contact is successfully added !! Add More","success"));
	        
	        // set user
	        contact.setUser(user);
	        user.getContacts().add(contact);
	        userRepository.save(user);
	        System.out.println("Contact saved successfully");

	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("message", new Message(e.getMessage(),"success"));
	    }

		return "normal/add-contact-form";
	}
	
	// show contact handler
	//per page contact show = 5[n]
	// current page 0[page]
	@GetMapping("/view_contact/{page}")
	public String viewContact(@PathVariable("page") Integer page ,Model m , Principal principal) {
		m.addAttribute("title" , "view contact page");
		
		//contact ki list bejana hai
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		//Current page - page
		//current per contact - 5
		org.springframework.data.domain.Pageable pageable = PageRequest.of(page, 5);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/view_contact";
	}
	
	// showing particular contact details 
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId , Model model, Principal principal) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
	    Contact contact =  contactOptional.get();
	    // for the security reson i checked the userid == contact get user id
	    String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId()) {
	    model.addAttribute("contact", contact);
	    model.addAttribute("title", contact.getName());
		}
		return "normal/contact_detail";
	}
	
	// delete the contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId ,Model model ,HttpSession session ,Principal principal) {
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
	    Contact contact =  contactOptional.get();
	    contact.setUser(null);
	    
	   // String userName = principal.getName();
		//User user = this.userRepository.getUserByUserName(userName);
	   // if(user.getId()==contact.getUser().getId())
	    this.contactRepository.delete(contact);
	    session.setAttribute("Message", new Message("Contact Delete successfully!!" , "success"));
		return "redirect:/user/view_contact/0";
	}
	
	// update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid , Model m) {
		m.addAttribute("title", "update form");
		Contact contact = this.contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);
		return "normal/update-form";
	}
	
	// contact process update handler
	@RequestMapping(value="/process-update" ,method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact ,@RequestParam("profileImage") MultipartFile file
			,Model m ,HttpSession session , Principal principal) {
		//old contact detail
		Contact oldcontactdetail =  this.contactRepository.findById(contact.getcId()).get();
		try {
			if(!file.isEmpty()) {

			    // delete old photo ONLY if exists
			    if(oldcontactdetail.getImage() != null) {
			        File deleteFile = new ClassPathResource("static/image").getFile();
			        File file2 = new File(deleteFile, oldcontactdetail.getImage());
			        file2.delete();
			    }

			    // save new photo
			    File file1 = new ClassPathResource("static/image").getFile();
			    Path path = Paths.get(
			        file1.getAbsolutePath() + File.separator + file.getOriginalFilename()
			    );
			    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			    contact.setImage(file.getOriginalFilename());
			}
			else {
			    // image change nahi ki
			    contact.setImage(oldcontactdetail.getImage());
			}

			
			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("Message", new Message("Your contact updated success" , "success"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getcId()+"/contact";
	}

	// profile handler
	@GetMapping("/profile")
	public String profilePage(Model model) {
	 
		model.addAttribute("title", "profile page");
		return "normal/profile";
	}
}
