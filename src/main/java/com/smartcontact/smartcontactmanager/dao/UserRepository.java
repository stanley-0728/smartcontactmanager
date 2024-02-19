package com.smartcontact.smartcontactmanager.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontact.smartcontactmanager.entities.User;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query("select u from User u where u.email = :email")
    Optional<User> getUserByUserName(@Param("email")String email);

    User  findByName(String name);


    

}
