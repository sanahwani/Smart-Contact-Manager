package com.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Sign-up - Smart Contact Manager");
		model.addAttribute("user", new User()); //sendng user data tht wl come in form from here to from back 
		return "signup";
	}
	
	//handler for rregstrng user
	
	/**all the fields mtchng with our form and this user will come in user var except for checkbox bcz 
	 * thts not in our User class. so creating anther Var OF requestparam to accept checkbox andmkng it by default false
	 *  Model for sending  data frm here
	 *  Httpsession to send msg
	 *  @Valid for hb validator, mst be used bfre @Model attrbte, stores result in obj BindingResult. */
	
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1 ,
			@RequestParam(value="agreement",defaultValue="false") boolean agreement,
			Model model, HttpSession session) {
		
	try {
		System.out.println(user);
		if(!agreement) {
			System.out.println("you have not agreeed to terms and conditions");
			throw new Exception("you have not agreed to terms and conditions");
		}		
		
		if(result1.hasErrors()) {
			System.out.println("error"+result1.toString());
			model.addAttribute("user", user);//data will cme back in form
			return "signup";
		}
		
		//if agreed to terms and condtns
		//setting other fields
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));//on gtng pass frm user , passwordencoder wl encode the passwrd and set in user psswrd
 
    	User result=this.userRepository.save(user);
		
		model.addAttribute("user", new User()); //settting new user. fields wl gt blanked
		session.setAttribute("message", new Message("Successfully registered...","alert-success"));		
		return "signup";
		
		
		
		}catch(Exception e) {
			
		e.printStackTrace();
		model.addAttribute("user", user);
		session.setAttribute("message", new Message("Something went wrong ..."+e.getMessage(),"alert-danger"));
		return "signup";
	}
		
	}

	//handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login Page");
		return "login";
	}

}
