package com.smartcontact.smartcontactmanager.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smartcontact.smartcontactmanager.dao.ContactRepository;
import com.smartcontact.smartcontactmanager.dao.UserRepository;
import com.smartcontact.smartcontactmanager.entities.Contact;
import com.smartcontact.smartcontactmanager.entities.User;

@RestController
public class SearchController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query")String query,Principal p){
        String username=p.getName();
        User user=this.userRepository.findByName(username);
        List<Contact> contacts=this.contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
    
}
