package com.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.entities.*;

//thhese all r intrfces, we dnt gve its body, its implmntn is gvn by spring data jpa
public interface ContactRepository extends JpaRepository <Contact, Integer>{

	
	//pagination
	//fetching all cntcts frm contactrepostry
		
	//we give it user id and it will mke query for user
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pePageable);//when u wl pass user id here, it wl go to abve in where statmnt

//using interface page, page is a sublist of the list of  objects, it allws to gain informtn abt the data at the certain position
//passing user id and pageble to it. pageable is a abstract interface for pageable information i.e it stores info of pageable
	// it wl have 2 things-> contacts per page and current page and it will accordingly fetch data frm cntct repo and stres in Page obj abve

	
	//for search fn
	public List<Contact> findByNameContainingAndUser(String name, User user); //search user and then in it search gven name in paramttr
}
