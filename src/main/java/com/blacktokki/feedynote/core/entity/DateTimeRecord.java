package com.blacktokki.feedynote.core.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;




@MappedSuperclass
public abstract class DateTimeRecord {
    @CreationTimestamp
    @Column(name = "dt_created")
    private ZonedDateTime created;
    
    @UpdateTimestamp
    @Column(name = "dt_updated")
    private ZonedDateTime updated;
}
