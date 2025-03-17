package com.blacktokki.notebook.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.blacktokki.notebook.account.entity.User;

@Entity
@Table(name="content")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    @Id
    @Column(name = "co_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "us_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "us_id", nullable = true)
    private Long userId;

    @Column(name = "co_parent_id", nullable = true)
    private Long parentId;

    @Convert(converter = EnumConverter.class)
    @Column(name = "co_type", nullable= false)
    private ContentType type;
 
    @Column(name = "co_order", nullable= false)
    private Integer order;

    @Column(name = "co_title", nullable = true, columnDefinition = "TEXT")
    private String title;

    @Column(name = "co_description", nullable = true, columnDefinition = "TEXT")
    private String description;

    @Convert(converter = ContentOption.Converter.class)
    @Column(name = "co_option", nullable= false)
    private ContentOption.Map option;

    @UpdateTimestamp
    @Column(name = "co_updated")
    private ZonedDateTime updated;

    @Column(name = "co_deleted")
    private ZonedDateTime deleted;
}