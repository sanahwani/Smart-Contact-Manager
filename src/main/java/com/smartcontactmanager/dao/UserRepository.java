package com.smartcontactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.entities.User;

public interface UserRepository extends JpaRepository< User,Integer> {
	
	//crerate a method whch user is retrnng using ur email i,e whenever u call user by email , u hve to pass it
	//whch will come in query =:email  andn user will appear fr that email
	@Query("select u from User u where u.email = :email") //dynamic email 
	public User getUserByUserName(@Param("email") String email);

}
