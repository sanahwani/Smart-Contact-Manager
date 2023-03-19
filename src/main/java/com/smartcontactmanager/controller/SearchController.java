package com.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;

//making it rest, so nw it desnt rtrn view but body as it is
@RestController
public class SearchController {
	
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private UserRepository userRepository;
	
	//search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		User user=this.userRepository.getUserByUserName(principal.getName());//prncpl wl retrn u.name of user logged in and getrepo wl retrn user of that username
		List<Contact> contacts=	this.contactRepository.findByNameContainingAndUser(query, user);
	
		
		return ResponseEntity.ok(contacts);
		
		
	}

}
