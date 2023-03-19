 package com.smartcontactmanager.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="USER")
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	//hb validations
	@NotBlank(message="Name Field is required") 
	@Size(min=3,max=20, message="Name must be between 3 and 20 letters")
	private String name;
	
	@Column(unique=true)
	@NotBlank(message="Email Field is required")
	private String email;
	
	private String password;
	private String role;
	private boolean enabled;
	private String imageUrl;
	
	@Column(length=600)
	private String about;

	//user is parent entity and cntct is child entty, so orpnremvel =true means whn child entty is unlinked frm parent entity i.e whn we delete a cntct, child wl b rmvd frm db as well
	//since 1 user can have mny contacts. so to store cntcts.. (mappedBy =user means no tble of f.k will b crtd here. only user column wl mnge foreign key tble mngemmnt
	@OneToMany(cascade=CascadeType.ALL,mappedBy="user",orphanRemoval=true) //cascade means when user wil b sved or dltd, all cntcts rltd to this user will b saved or deltd. fetch lazy mwans contact will cme only when called
	private List<Contact> contacts=new ArrayList<>(); //whemevr obj of user wil b created, blank arrylist wl gt stred here and then set later
	
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
				+ "]";
	}
	
	
	

}
