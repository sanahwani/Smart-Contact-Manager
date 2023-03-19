package com.smartcontactmanager.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smartcontactmanager.entities.User;

public class CustomUserDetails implements UserDetails{
	
	private User user;
	//geetting user data frm this user
	
	
 //crerating cnstrctr for user var
	public CustomUserDetails(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//retrnng authrties of user, so using this class
		 SimpleGrantedAuthority  simpleGrantedAuthority =new SimpleGrantedAuthority(user.getRole());
		return List.of(simpleGrantedAuthority); //it wl rtrn its rolr be it normal or admin
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return true; //mkng it retrn true
	}

	@Override
	public boolean isAccountNonLocked() {
		
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

}
