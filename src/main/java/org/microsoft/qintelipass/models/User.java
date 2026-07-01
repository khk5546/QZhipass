package org.microsoft.qintelipass.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.microsoft.qintelipass.enums.UserStatus;

import java.time.OffsetDateTime;

@Setter
@Getter
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", updatable = false, nullable = false)
    private Long id;
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "passwordHash")
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;
    @Column(name = "username", nullable = false)
    private String name;
    @CreationTimestamp
    @Column(name = "joinedAt", nullable = false, updatable = false)
    private OffsetDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = UserStatus.NORMAL;
        }

    }
}
