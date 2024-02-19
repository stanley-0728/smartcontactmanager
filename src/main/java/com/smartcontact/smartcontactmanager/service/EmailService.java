package com.smartcontact.smartcontactmanager.service;

import java.util.Properties;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    public boolean sendEmail(String subject,String message,String to){
        boolean f=false;

        String from="sumanth.james23@gmail.com";


        String host="smtp.gmail.com";

        // Get System Properties
        Properties properties =System.getProperties();
        System.out.println(properties);

        // Set Properties
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", 465);
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.auth", true);

        // Set username and password in session
       Session session=Session.getInstance(properties, new Authenticator() {
           protected PasswordAuthentication getPasswordAuthentication(){
            return new PasswordAuthentication("suvarna.sbhavana.mec19@itbhu.ac.in",
            "stanika@143");
           }
       });

       session.setDebug(true);

    //    Compose The message(text,multiparrt)

    MimeMessage m=new MimeMessage(session);
    try{
        // from email
            m.setFrom(from);
            // add recipient
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // add subject
            m.setSubject(subject);
            // add contect
            m.setContent(message,"text/html");;
            // send message
            Transport.send(m);
          f=true;
        }
    catch(Exception e){
        return f;
    }
    return f;

    }
    
}
