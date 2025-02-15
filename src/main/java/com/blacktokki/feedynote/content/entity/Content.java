package com.blacktokki.feedynote.content.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

import com.blacktokki.feedynote.account.entity.User;

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

    @Column(name = "co_input", nullable = true, columnDefinition = "TEXT")
    private String input;

    @Column(name = "co_title", nullable = true, columnDefinition = "TEXT")
    private String title;

    @Column(name = "co_description", nullable = true, columnDefinition = "TEXT")
    private String description;

    @Column(name = "co_image_url", nullable = true, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "co_updated")
    private ZonedDateTime updated;

    @Column(name = "co_deleted")
    private ZonedDateTime deleted;

    @PrePersist
    @PreUpdate
    public void preUpdate(){
        if (!ContentType.FEEDCONTENT.equals(this.type)){
            this.updated = ZonedDateTime.now();
        }
    }
}