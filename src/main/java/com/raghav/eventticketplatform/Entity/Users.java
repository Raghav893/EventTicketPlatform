package com.raghav.eventticketplatform.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false,unique = true,length = 50,updatable = false)
    private String username;

    @Column(nullable = false)
    private  String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Roles roles;
    String authProvider = "JWT";

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false , updatable = false)
    LocalDateTime CreatedAt;


}
