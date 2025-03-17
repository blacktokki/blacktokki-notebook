package com.blacktokki.notebook.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import org.hibernate.annotations.Subselect;

@Entity
@Subselect("select * from db1_account.user")
public class User {
    @Id
    @Column(name = "us_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "us_name")
    private String name;
    
    @Column(name = "us_username")
    private String username;
    
    @Column(name = "us_password")
    private String password;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}