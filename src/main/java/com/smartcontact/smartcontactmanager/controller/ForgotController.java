package com.smartcontact.smartcontactmanager.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartcontact.smartcontactmanager.dao.UserRepository;
import com.smartcontact.smartcontactmanager.entities.User;
import com.smartcontact.smartcontactmanager.helper.Message;
import com.smartcontact.smartcontactmanager.service.EmailService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ForgotController {

    @Autowired
    private UserRepository repo;
    @Autowired
    private EmailService service;
    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/forgot-password")
    public String openEmailForm(){
        return "forgot_email_form";
    }

    @PostMapping("/forgot-password")
    public String sendOtp(@RequestParam("email")String email,HttpSession session) {
        //TODO: process POST request
        System.out.println(email);
        //generating 4 digits otp

        Random random = new Random();   
        int otp = random.nextInt(99999 - 1000) + 1000;
        String subject="SCM Password Reset";
        String message=""
                        + "<div style='border:3px solid black; padding:20px'>"
                        +"<h1>"
                        +"OTP is "
                        +"<b>"+otp
                        +"</n>"
                        +"</b>"
                        +"</div>";
        String to=email;
        boolean f=this.service.sendEmail(subject, message, to);

        if(f){
            session.setAttribute("myotp",otp);
            session.setAttribute("email", email);
            return "verify_otp";
        }
        else{
            session.setAttribute("message",new Message("Check Your email Id!...", "alert-danger") );
            return "forgot_email_form";

        }
    }

    @PostMapping("/verify-otp")
    public String postMethodName(@RequestParam("otp") int otp,HttpSession session) {
        //TODO: process POST 
        int myotp=(int)session.getAttribute("myotp");
        String email =(String)session.getAttribute("email");
        if(myotp==otp){
                if(this.repo.getUserByUserName(email).isPresent()){

                    return "password_change_form";
            }
            else{
                session.setAttribute("message",new Message("No such user exists","alert-danger"));
                return "forgot_email_form";

            }

        }else{

            session.setAttribute("message", new Message("You have entered wrong otp","alert-danger"));
            return "verify_otp";
        }
    }

    @PostMapping("/change-password")
    public String postMethodName(@RequestParam("newPassword")String newPasssword,
    HttpSession session,RedirectAttributes r) {
        //TODO: process POST request
        String email=(String)session.getAttribute("email");
        User user=this.repo.getUserByUserName(email).get();
        user.setPassword(this.encoder.encode(newPasssword));
        this.repo.save(user);
        r.addFlashAttribute("update", "Password Changed ");

        
        return "redirect:/signIn";
    }
    
    
    
    
}
