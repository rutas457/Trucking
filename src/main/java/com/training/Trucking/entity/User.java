package com.training.Trucking.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String surname;

    private String email;
    private String password;

    private String phoneNumber;
    private Boolean isBanned;
    private Boolean enabled;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable
    private Collection<Role> roles;

    @OneToOne(cascade = CascadeType.ALL)
    private UserAccount userAccount;
}
