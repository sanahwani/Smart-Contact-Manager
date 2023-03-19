package com.smartcontactmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//here we hve to bring data from db  and do authentictn and retrn user details
		
		//fetchng user from db, we need UserRepositry fr this, so using its obj
		User user=userRepository.getUserByUserName(username); //pssng username from abve, it wl retrn user data so strng it in var
		
		if(user==null) {
			throw new UsernameNotFoundException("could not found user...");
		}
		
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		return customUserDetails;
	}

}
