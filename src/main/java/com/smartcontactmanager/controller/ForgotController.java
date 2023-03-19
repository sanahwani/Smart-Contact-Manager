package com.smartcontactmanager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	
	//genrtng  random otp
	Random random=new Random(1000);
	
	//email id form open controller	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
		
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
		System.out.println(email);
		
		//genrtng  random otp
		
		int otp=random.nextInt(999999);
		
		System.out.println(otp);
		
		//writing code for send otp to email
		String subject="OTP FRROM SCM";
		String message=""
				+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
				+ "<h1>"
				+ "OTP IS "
				+ "<b>" +otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		String to= email;
		
		boolean flag=this.emailService.sendEmail(subject, message, to);
		
		if(flag) {
			session.setAttribute("myotp",otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {
			session.setAttribute("message", "Check your mail id");
			return "forgot_email_form";
		}
		
		
	}

//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam ("otp") int otp, HttpSession session) {
		
		
		int myOtp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		
		//checking if otp sent my server is same as entered by user
		if(myOtp==otp) {
			
			//password change form
			
			//gettng user by email
			User user=this.userRepository.getUserByUserName(email);
			
				if(user==null) {
					//send msg
					session.setAttribute("message", "User does not exist with this email id");
					return "forgot_email_form";
				}else {
				return "password_change_form";
				}
		}else {
			session.setAttribute("message", "you have entered wrong otp");
		return "verify_otp";
	}
	}
	
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		String email=(String)session.getAttribute("email");
		User  user=this.userRepository.getUserByUserName(email);
		user.setPassword(this.bcrypt.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/signin?change=password changed successfully ! ";
	}

}

