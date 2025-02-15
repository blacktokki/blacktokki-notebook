package com.blacktokki.feedynote.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;






@MappedSuperclass
public abstract class AbstractUser extends DateTimeRecord{
    @Id
    @Column(name = "us_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "us_name")
    private String name;
    
    @Column(name = "us_username")
    private String username;
    
    @Column(name = "us_password")
    private String password;

    @Column(name = "us_is_admin")
    private Boolean isAdmin;

    @Column(name = "us_is_guest")
    private Boolean isGuest;

    @Column(name = "us_image_url")
    private String imageUrl;
}
