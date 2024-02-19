package com.smartcontact.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontact.smartcontactmanager.dao.ContactRepository;
import com.smartcontact.smartcontactmanager.dao.UserRepository;
import com.smartcontact.smartcontactmanager.entities.Contact;
import com.smartcontact.smartcontactmanager.entities.User;
import com.smartcontact.smartcontactmanager.helper.FileUploader;
import com.smartcontact.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository repo;
    @Autowired
    private ContactRepository contactRepo;
    @Autowired 
    private FileUploader fileUploader;
 
    @ModelAttribute
    void addCommonData(Model m,Principal p){
        String username =p.getName();
        User user=repo.findByName(username);
        System.out.println(user);
        m.addAttribute("user", user);
    }

    @GetMapping("/dashboard") 
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String user_dashboard(Model m,Principal p){
        return "user/user_dashboard";
    }


    @GetMapping("/setting")
    public String setting() {
        return "user/user_setting";
    }


    @GetMapping("/addContact")
    public String addContact(@ModelAttribute("contact") Contact contact,Model m){
        return "user/add_contact";
    }

    @PostMapping("/addContact")
    public String postMethodName(@ModelAttribute("contact") Contact contact,
    Principal p,@RequestParam("profileImage") MultipartFile file,HttpSession session) {
     try{
        String name=p.getName();

        User user=this.repo.findByName(name);

        contact.setUser(user);

        if(file.isEmpty()){
            contact.setImage("contact.png");

        }
        else{
            System.out.println(file.getOriginalFilename());
            contact.setImage(file.getOriginalFilename());
            // Save file location
           
            boolean f=this.fileUploader.uploadFile(file);
            if(f){
                System.out.println("Image Saved");
            }
            else{
                session.setAttribute("message", new Message("Something Went wrong ","alert-danger"));

            }
;        }

        user.getContacts().add(contact);
        
        this.repo.save(user);
          
        session.setAttribute("message", new Message("Contact Added ","alert-success"));
     }
     catch(Exception e){
        e.printStackTrace();
        System.out.println(e.getMessage());
        session.setAttribute("message", new Message("Something Went wrong ","alert-danger"));

     }

        return "user/add_contact";
    }

    @GetMapping("/showContacts/{page}")
    public String showContacts(@PathVariable("page") Integer page,Model m,Principal p) {
        String username=p.getName();
        User user=this.repo.findByName(username);
        Pageable pageable=PageRequest.of(page,1);
        Page<Contact> contacts=this.contactRepo.findContactsByUserId(user.getId(),pageable);
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "user/show_contacts";
    }

    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId")Integer cId,Model m,Principal p) {
        Optional<Contact> contact=this.contactRepo.findById(cId);
        Contact c=contact.get();
        String username=p.getName();
        User user =this.repo.findByName(username);
        if(user.getId()==c.getUser().getId())
                m.addAttribute("c", c);
        return "user/contact_detail";
    }
    
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,Model m,HttpSession s,Principal p){
            
            System.out.println("Inide delte");
        this.contactRepo.deleteById(cId);

        
            s.setAttribute("message", new Message("Contact Deleted Successfully...","alert-success"));
        
        return "redirect:/user/showContacts/0";

    }   

    @PostMapping("/updateContact{cId}")
    public String postMethodName(@PathVariable("cId")Integer cId, Model m) {
        //TODO: process POST request
        Contact contact=this.contactRepo.findById(cId).get();
        m.addAttribute("contact", contact);
        return "user/update_contact";
    }
    @PostMapping("/update-Contact")
    public String updateHandler(@ModelAttribute("contact") Contact contact,
    Principal p,@RequestParam("profileImage") MultipartFile file,HttpSession session ){
            try{
                Contact oldContact =this.contactRepo.findById(contact.getcId()).get();
                if(!file.isEmpty()){

                    File deleteFile=new ClassPathResource("static/image").getFile();
                    File file1=new File(deleteFile,oldContact.getImage());
                    file1.delete();

                   boolean  f=this.fileUploader.uploadFile(file);
                   if(f){
                        System.out.println("Saved");
                   }
                   else{
                    System.out.println("Not Saved");
                   }
                   contact.setImage(file.getOriginalFilename());
                }
                else{
                    contact.setImage(oldContact.getImage());
                }
                String username=p.getName();
                User user=this.repo.findByName(username);
                contact.setUser(user);
                this.contactRepo.save(contact);
                session.setAttribute("message", new Message("Your contact is Updated", "alert-success"));
            }catch(Exception e){}



        return "redirect:/user/"+contact.getcId()+"/contact";
    }
    



    @GetMapping("/profile")
    public String getProfile(){   
        return "user/user_profile";
    }


    @RequestMapping(value = "/change-password",method = RequestMethod.POST)
    public String changePassword(@RequestParam("oldPassword")String oldPassword,
    @RequestParam("newPassword")String newPassword,Principal p,HttpSession session) {

        String username=p.getName();
        User user=this.repo.findByName(username);
        //TODO: process POST request
        if(this.encoder.matches(oldPassword, user.getPassword())){
            user.setPassword(this.encoder.encode(newPassword));
            this.repo.save(user);
            session.setAttribute("message",new Message("Password Updated...","alert-success"));
        }
        else{
            session.setAttribute("message",new Message("Wrong Password...","alert-danger"));
            return "redirect:/user/setting";

        }
        
        return "redirect:/user/dashboard";
    }
    
 
   



   
    






}