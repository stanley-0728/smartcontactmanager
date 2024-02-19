package com.smartcontact.smartcontactmanager.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.smartcontact.smartcontactmanager.entities.User;
import com.smartcontact.smartcontactmanager.dao.UserRepository;
@Component
public class UserInfoUserDetailService implements UserDetailsService{
    @Autowired
    private UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userInfo = repository.getUserByUserName(email);
        return userInfo.map(UserInfoUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("user not found " + email));

    }
    
}
