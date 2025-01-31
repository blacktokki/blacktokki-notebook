package com.blacktokki.spreadocs.core.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;





@MappedSuperclass

public abstract class ActiveRecord extends DateTimeRecord {
    @Column(name = "ar_active_start", nullable = true)
    private Date activeStart;

    @Column(name = "ar_active_end", nullable = true)
    private Date activeEnd;
}
