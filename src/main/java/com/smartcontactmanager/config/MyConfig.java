package com.smartcontactmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;


@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder(BCryptVersion.$2Y);
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return daoAuthenticationProvider;
	}

	//configure methods, to authenticate
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//we have to tell it type of authntctn we r using like in memory or database authtnctn
		auth.authenticationProvider(authenticationProvider()); //passing abve obj fr db
	}

	

	//for route i.e whch route wl b accessed to whm
	@Override
	public void configure(HttpSecurity http) throws Exception {
		//all urls strtng with admin cn be accsed by smneone who as role as admin only and
		//same fe users and permittng left urls for all. and allwng form based login
		
	http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
	.antMatchers("/user/**").hasRole("USER")
	.antMatchers("/**").permitAll().and().formLogin()
	.loginPage("/signin")
	.loginProcessingUrl("/dologin")
	.defaultSuccessUrl("/user/index")
	.failureUrl("/login-fail")
	.and().csrf().disable();
	} //telling login page is dfrnt nt instead of by default loginpage of spring and
	//loginprocessingurl- to whcvh url u wnt to submt usernme and passwrd. defaultSuccessfulUrl-to whch page to land aftr succfl login
	
	
	
}


