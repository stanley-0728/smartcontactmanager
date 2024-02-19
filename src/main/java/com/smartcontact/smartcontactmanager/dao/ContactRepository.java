package com.smartcontact.smartcontactmanager.dao;

import com.smartcontact.smartcontactmanager.entities.Contact;
import com.smartcontact.smartcontactmanager.entities.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface  ContactRepository extends JpaRepository<Contact,Integer> {
    @Query("from Contact as c where c.user.id= :userId")
    Page<Contact> findContactsByUserId(@Param("userId") int userId,Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from Contact c where c.Id =:id")
    public void deleteContactById(@Param("id") Integer id);

    public List<Contact> findByNameContainingAndUser(String name ,User user);

}
