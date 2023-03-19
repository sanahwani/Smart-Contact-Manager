package com.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
//import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontactmanager.dao.ContactRepository;
import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;
import com.smartcontactmanager.helper.Message;
import com.razorpay.*;

/*when spring secuirty is applied in springboot, it protects all urls and itself places login logiut system and genrtes
password too itself on console, but here we dont want it to do so by itself, we want it to protect certain urls only say urls starting from 
user eg locolhost:8080/user/** and  we dnt want it to autogenrte passwrd fr checkin but to take saved username and [passs from db.
so we have to confgure it 
for this we need to providde implemntatn of UserDetails and UserDetailsService in CustomUserDetails and UserDetailServiceImpl
and then write secuirty configuration class with all configrtn . a simple class whch extends WebSecuirtyConfiguresAdapter
*/

@Controller

// urls only user can access
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private  ContactRepository  contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//methd fr adding common data ton response so it cn be used in any methd
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName=principal.getName();
		System.out.println("Username"+ userName);
		//now gwt data from db using this username
		
		User user=userRepository.getUserByUserName(userName);
		System.out.println("user" +user);
		
		model.addAttribute("user", user);
	}
	
	//dashboard handler
	@RequestMapping("index")
		public String dashboard(Model model, Principal principal) { //primcpal is part of java securty , it rtrns unique identifier of your entity i.e username here
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
		}
	
	//open add form controller
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	

//processsing add contact form 
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,
			HttpSession session) {
		//all fields frm add_contact_form will be mapped to fields of contact obj of Contact class
		//in requestparam file frm profileImage wl b saved in MultipartFile obj file. 
		//adding contact now
		//getting user first who is logged in and then adding contact in list and updting it	
		////httpSession to send success/error msg. in sessioin var we stre temprly smnthng fr sme interval of time and can then remve it aftr shwng
		
		try {
		String name=principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		contact.setUser(user);
		
		user.getContacts().add(contact); 
		this.userRepository.save(user);
		
		//processung and uploading file(img) frst
		if(file.isEmpty()) {
			
			contact.setImage("contact.png"); //setting default img)
			
			System.out.println("file is empty");
		}else{
			//upload file to folder and then update name in contact . so instead of image in db we wl b having link of image in db (saves strge)
		contact.setImage(file.getOriginalFilename());
			
			//to upload we frst need path wehere we want to upload
		File saveFile=	new ClassPathResource("static/img").getFile();
		
		Path path=	Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		
		//pass inputStream, target(where to upload) and options (if file alrdy prsnt, whethr to replce it or nt) in parameter
		Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
		System.out.println("image uploaded");
		
		}
		
		user.getContacts().add(contact); //adding contact to contact list of user
	
		this.userRepository.save(user);//updating user i.e saving in db
		
		
		
		System.out.println("contact "+ contact);
		System.out.println("added to db");
		
		//message success
		//message obj has 2 parameters content and type
		session.setAttribute("message", new Message ("Your contact is added ! Add more. " ,"success"));
		
		
		
		
		}catch(Exception e) {
		System.out.println("Error"+ e.getMessage());
		e.printStackTrace();
		
		//error msg
		session.setAttribute("message", new Message ("Something went wrong.. Try Again ! !" ,"danger"));
		
	}
		
		return "normal/add_contact_form";
	}
		


//show contacts hadler
	
	//show contacts per page= 5[n]
	//current page = 0 -> [page] variable
	
	@GetMapping("/show-contacts/{page}") //page is a path var tht we take frm user-> whch pge he wants to visit
	public String showContacts(@PathVariable("page") Integer page,Model m,Principal principal) {
		m.addAttribute("title", "show user contacts");
		
		//have to send contact list frm db
		
		//using contactrepository
		//gettng cntct list of only tht user who is logged in
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		//since Pageable is parent class of PageRequest, so strng child in parent interface
		//Pageable has 2 things-> show contacts per page= 5[n] and current page = 0 -> [page]
		Pageable pageable=	PageRequest.of(page, 3); //using of fn of PageRequest class, it takes 2 parameters-> page and size
		
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
		//it will retrn page of contacts
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}

	
	//showing particular contact details
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		
		System.out.println( "cid" + cId);
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);	
		Contact contact=contactOptional.get();
		
		
		//check if the same person logged in is accessing the contact in his contacts or outsde of his cntcts

		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			
		model.addAttribute("title",contact.getName());
			model.addAttribute("contact",contact);
		}
	
		
		
		return "normal/contact_detail";
	}
	
	
	//delete contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId,Model model, HttpSession session, Principal principal) {
		
		Contact contact=this.contactRepository.findById(cId).get();	
		
		System.out.println("contact" + contact.getcId());
		
		//unlinking from contact
		   		//gettng user
		User user=this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact); //rmvng cntct
		
		//now to rmve cntct frm whole list, we need object matching. we override equals methd in Contact class in whch ech cntct wl b ematched with this partcle cntct tht we wnt to deleet
		//fr this mtchng , equals methd wl b clld, and when the id mtches with any id in list, it wl rmve tht frm list

		this.userRepository.save(user);
		
		session.setAttribute("message", new Message("Contact deleted successfully...", "success"));
		
		return "redirect:/user/show-contacts/0";
	}

	//open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid,Model m) {
		
		m.addAttribute("title","update contact");
		
		Contact contact=this.contactRepository.findById(cid).get();
		m.addAttribute("contact",contact);
		return "normal/update_form";
	}
	
	//update form handler
	@RequestMapping(value="/process-update", method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file, 
			Model m ,HttpSession session,Principal principal){//this contact var wl hve data cmng from Contact class, also mke sprtr var fr image
	
		
		//old contact details
		Contact oldcontactDetail=this.contactRepository.findById(contact.getcId()).get();
		
		try {
			//image
			if(!file.isEmpty()){
				
				//delete old photo
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile, oldcontactDetail.getImage()); //passinh parent and child in paramtr
				file1.delete();
				
				//update new photo
				File saveFile=	new ClassPathResource("static/img").getFile();
				
				Path path=	Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				//pass inputStream, target(where to upload) and options (if file alrdy prsnt, whethr to replce it or nt) in parameter
				Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
				
			}else {
				//if file is nt empty, replce old cntct umage into new
				contact.setImage(oldcontactDetail.getImage());
			}
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated", "success"));
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(contact.getcId() + contact.getName());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}

	
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile() {
	
		
		return "normal/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSettings() {
		
		return "normal/settings";
	}

	//change password
	@PostMapping("/change-Password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		
		//getting actual old password of user from db
		String userName=principal.getName();
		User currentUser=	this.userRepository.getUserByUserName(userName);
		System.out.println(currentUser);
		
		//checking if passwrd in db matches with entered pass then save the newpassword in placeof old one
		//using BCryptPasswordEncoder for this
		
		if(this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword())) {
			//change the pass
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword)); //settng pass in encoded form
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your Password is successfully changed", "alert-success"));
			
			
			
		}else {
			//error
			session.setAttribute("message", new Message("Please enter correct Old Password", "danger"));
			
			return "redirect:/user/settings"; 
			
		}
		return "redirect:/user/index"; 
	}
	
	//creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws Exception  {
		
		
		System.out.println(data);
		int amt=Integer.parseInt(data.get("amount").toString());
		
		var client=new RazorpayClient("rzp_test_XrL132tX1s33Hg","7EPcySCuNheLr4ZodqFTo3cg"); //get this from razorpay
	
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100); //cnvrtng rupeess to paisa
		ob.put("currency", "INR");
		ob.put("receipt", "txn_235425");
		
		//creating new order
		Order order=client.orders.create(ob);
		System.out.println(order);
		
		return order.toString();
	}
	
	
	
}

	
