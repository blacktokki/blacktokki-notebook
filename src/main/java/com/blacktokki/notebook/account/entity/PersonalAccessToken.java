package com.blacktokki.notebook.account.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "personal_access_token")
public class PersonalAccessToken {
    @Id
    @Column(name = "pa_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "us_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "us_id", nullable = true)
    private Long userId;

    @Column(name = "pa_name", nullable = false)
    private String name;

    @Column(name = "pa_token", nullable = false)
    private String token;

    @Column(name = "pa_expired", nullable = false)
    private LocalDateTime expired;

    @CreationTimestamp
    @Column(name = "pa_created", nullable = false)
    private LocalDateTime created;

    @Builder
    public PersonalAccessToken(Long userId, String name, String token, LocalDateTime expirationDate) {
        this.userId = userId;
        this.name = name;
        this.token = token;
        this.expired = expirationDate;
    }
}