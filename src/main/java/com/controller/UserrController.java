package com.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dao.ContactRepository;
import com.dao.UserrRepository;
import com.helper.Message;
import com.model.Contact;
import com.model.Userr;

@Controller
@RequestMapping("/user")
public class UserrController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserrController.class);
	
	@Autowired
	private UserrRepository userRepo;
	
	@Autowired
	private ContactRepository contRepo;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
		
	// method for adding common data to response
	@ModelAttribute
	public void commondata(Model m,Principal p)
	{
		String userName = p.getName(); // it will give the username(email) of person who is login				
		Userr userr =	userRepo.getUserrByUserrName(userName);		
		m.addAttribute("userr",userr);
	}
	
	// dashboard
	@RequestMapping("/")
	public String dashboard(Model m,Principal p)
	{		
		m.addAttribute("title","Dashboard");		
		return "user/dashboard";
	}
	
	//open add form handler
	@GetMapping("/addcontact")
	public String addcontact(Model m,Principal p)
	{
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact",new Contact());		
		return "user/addcontact";
	}
	
	// processing addcontact
	@PostMapping("/processcontact")
	public String processcontact(@ModelAttribute("contact") Contact contact,
			BindingResult br,Principal p,
			HttpSession session,
			@RequestParam("cimage") MultipartFile file) // after adding BindingResult we will get the String from cimage field
	{                                                   // always put BindingResult next to @ModelAttribute        
		try {
		
			String userName = p.getName();		
			//processing and uploading file
			
			if(file.isEmpty())
			{
				//if the file is empty then try our message
				logger.debug("File is empty");
				contact.setCimage("contact.png");
			
			}
			else {
				// upload the file to folder and save the name to contact table
				contact.setCimage(file.getOriginalFilename());				
				File saveFile = new ClassPathResource("static/image").getFile();	
				Path path = Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());		 
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				 
				logger.debug("Image is uploaded successfully");
			}
			
			Userr userr = userRepo.getUserrByUserrName(userName);			
			contact.setUserr(userr);			
			userr.getUcontacts().add(contact);			
			userRepo.save(userr);			
			logger.debug("Contact Added Successfully...");			
			session.setAttribute("message", new Message("Your contact is added successfully !!","success"));
			
			}catch(Exception e)
			{
				logger.error(e.getLocalizedMessage());
				session.setAttribute("message",new Message("Something went wrong !!","danger"));
			}		
		return "user/addcontact";
	}
	
	// show contacts handler
	// per page = 3[n]
	// current page = 0[page]
	@GetMapping("/viewcontacts/{page}")
	public String viewcontacts(@PathVariable("page") Integer page,Model m,Principal p,HttpSession session)
	{// here we have use page variable for pagination
		
		String userName = p.getName();		
		Userr userr = userRepo.getUserrByUserrName(userName);		
		Pageable pageable = PageRequest.of(page, 5); // current page and contacts per page-5		
		Page<Contact> contacts = contRepo.getContactsByUid(userr.getUid(),pageable);		
		m.addAttribute("contacts",contacts);		
		m.addAttribute("currentPage",page);		
		m.addAttribute("totalPages",contacts.getTotalPages());		
		m.addAttribute("title","View User Contacts");
		return "user/viewcontacts";
	}
	
	
	
	// showing particular contact details
	@RequestMapping("/{cid}/contactdetails")
	public String contact(@PathVariable("cid") Integer cid,Model m,Principal p)
	{
		logger.debug("cid: " + cid);		
		Optional<Contact> contOpt =contRepo.findById(cid);
		Contact contact = contOpt.get();
		//
		String userName = p.getName();
		Userr userr = userRepo.getUserrByUserrName(userName);
		
		if(userr.getUid()== contact.getUserr().getUid())
		{
			m.addAttribute("title",contact.getCname());
			m.addAttribute("contact",contact);
		}		
		return "user/contactdetails";
	}
	
	
	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deletecontact(@PathVariable("cid") Integer cid, Model m,Principal p,HttpSession session) throws IOException
	{
	 Optional<Contact> contOpt	= contRepo.findById(cid);
	Contact contact = contOpt.get();
	
	String userName = p.getName();
	Userr userr = userRepo.getUserrByUserrName(userName);
		
	if(userr.getUid()== contact.getUserr().getUid())
	{
		// before deleting contact, delete photo of contact
		//contact.getImage
		if(!contact.getCimage().equals("contact.png"))
		{
			File imagefile = new ClassPathResource("static/image").getFile();
			Path path = Paths.get(imagefile.getAbsolutePath() + File.separator+contact.getCimage());
			
			Files.delete(path);
		}
		
		//contact.setUserr(null);   //--|   // due to cascade all we are unable to delete thats why we have to null that column before deleting. and after then only it will be able to delete.
		//contRepo.delete(contact); //--|-- use this code when using without orphanRemoval
		
		userr.getUcontacts().remove(contact);   //--|
		userRepo.save(userr);					//--|-- use this code when using orphanRemoval
		session.setAttribute("message", new Message("Contact deleted successfully...","success"));
	}

		return "redirect:/user/viewcontacts/0";
	}
	
	//open update form handler
	@PostMapping("/update/{cid}")
	public String updatecontact(@PathVariable("cid") Integer cid, Model m)
	{
		Contact contact = contRepo.findById(cid).get();		
		m.addAttribute("contact",contact);		
		m.addAttribute("title","Update Contact");
		return "user/updatecontact";
	}
	
	// processupdate handler
	@RequestMapping(value="/processupdate",method=RequestMethod.POST)
	public String processupdate(@ModelAttribute Contact contact,
			BindingResult br,@RequestParam("cimage") MultipartFile file,
			Model m,Principal p,HttpSession session)
	{
		try {
			//check image			
			String userName = p.getName();			
			//old contact details
			Optional<Contact> oldcontOpt =  contRepo.findById(contact.getCid());
			Contact oldcont = oldcontOpt.get();					
 			
			if(!file.isEmpty())
			{
				// delete old photo and add new photo
				if(oldcont.getCimage() != null)
				{	
					try {
						File imagefile = new ClassPathResource("static/image").getFile();
						Path path = Paths.get(imagefile.getAbsolutePath() + File.separator+oldcont.getCimage());				
						Files.delete(path);					
						logger.debug("old image delete successfully...");				
					}catch (Exception e) {
						logger.error(e.getLocalizedMessage());
						session.setAttribute("message",new Message("Profile pic is already deleted !","danger"));
					}
				}
				//======= add new photo
				contact.setCimage(file.getOriginalFilename());				
				File saveFile = new ClassPathResource("static/image").getFile();					
				Path path2 = Paths.get(saveFile.getAbsolutePath()+ File.separator+file.getOriginalFilename());					 
				Files.copy(file.getInputStream(), path2, StandardCopyOption.REPLACE_EXISTING);					 
				logger.debug("new image uploaded successfully...");				
			}
			else {
				contact.setCimage(oldcont.getCimage());				
			}			
						
			Userr userr = userRepo.getUserrByUserrName(userName);		 	
			contact.setUserr(userr);		
			userr.getUcontacts().add(contact);   //---|
												 //   |---you can also use this instead of this two lines :  contRepo.save(contact);
			userRepo.save(userr);			     //---|			
			logger.debug("Contact Updated Successfully...");			
			session.setAttribute("message",new Message("Your contact is updated successfully !!","success"));			
		}catch(Exception e) {
			logger.error(e.getLocalizedMessage());			
			session.setAttribute("message",new Message("Something went wrong !!","danger"));
		}
		
		
		logger.debug(contact.getCname());
		logger.debug("CID: "+contact.getCid());		
		return "redirect:/user/"+contact.getCid() +"/contactdetails";
	}
	
	
	// your profile handler
	@GetMapping("/profile")
	public String profile(Model m)
	{
		m.addAttribute("title","Profile Page");		
		return "user/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String settings(){		
		return "user/settings";
	}
	
	
	
	// change password handler
	@PostMapping("/changepassword")
	public String changepassword(Principal p, @RequestParam("oldpassword") String oldpassword, @RequestParam("newpassword") String newpassword, HttpSession session )
	{		
		String userName = p.getName();		
		Userr logedinuserr = userRepo.getUserrByUserrName(userName);
		
		if(bCryptPasswordEncoder.matches(oldpassword, logedinuserr.getUpassword()))
		{
			// change the password
			logedinuserr.setUpassword(bCryptPasswordEncoder.encode(newpassword));
			userRepo.save(logedinuserr);
			session.setAttribute("message",new Message("Your password is changed successfully...","success"));			
		}else{
			session.setAttribute("message",new Message("Please Enter correct old password!","danger"));
			return "redirect:/user/settings";
		}		
		return "redirect:/user/";
	}
	
}
