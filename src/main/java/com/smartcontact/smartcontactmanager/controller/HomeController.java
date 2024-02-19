package com.smartcontact.smartcontactmanager.controller;

import java.net.Authenticator.RequestorType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.smartcontact.smartcontactmanager.dao.UserRepository;
import com.smartcontact.smartcontactmanager.entities.User;
import com.smartcontact.smartcontactmanager.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;




@Controller
public class HomeController {
    
  @Autowired
  private UserRepository userRepo;
  @Autowired
  PasswordEncoder encoder;
    @GetMapping("/")

    public String home(Model m ) {
        m.addAttribute("title", "Home-Smart Contact Manager");
      return "home";
    }

    @GetMapping("/about")

    public String about(Model m ) {
        m.addAttribute("title", "About-Smart Contact Manager");
      return "about";
    }

    @GetMapping("/signUp")

    public String signup(@ModelAttribute("user") User user,Model m) {
        m.addAttribute("title", "Register-Smart Contact Manager");
      return "signup";
    }

    @PostMapping("/do_register")
    public String doRegister( @Valid @ModelAttribute("user") User user,
    BindingResult r,
    @RequestParam(value="agreement",defaultValue = "false") boolean agreement,
    RedirectAttributes ra,HttpSession s,Model m) {
      try{
        System.out.println(user);
        if(!agreement){
          throw new Exception("You Have not agreed terms and conditons");
        }
        if(r.hasErrors()){
          return "signup";
      }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(true);
        user.setRole("ROLE_USER");
        user.setImageUrl("contact.png");
          User result=userRepo.save(user);
          ra.addFlashAttribute("user", new User());
          s.setAttribute("message", new Message("Successfully Registred !!","alert-success"));
          return "redirect:/signUp";

      }catch(Exception e){
        m.addAttribute("user", user);
        s.setAttribute("message", new Message("Something went wrong !!"+e.getMessage(),"alert-danger"));
        return "signup";
      }
       
    }

    @GetMapping("/signIn")
    public String signIn(Model model ) {
        return "login";
    }


    }
    
    
    
    
    

