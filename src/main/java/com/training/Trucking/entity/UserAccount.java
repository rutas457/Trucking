package com.training.Trucking.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Audited
    private Long balance;

    @OneToOne
    private User user;

    public UserAccount(Long balance, User user) {
        this.balance = balance;
        this.user = user;
    }
}
