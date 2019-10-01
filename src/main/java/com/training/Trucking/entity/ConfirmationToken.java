package com.training.Trucking.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ConfirmationToken {
    private static final int EXPIRATION_MIN = 15;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long tokenID;

    @Column
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn
    private User user;

    public ConfirmationToken(User user) {
        this.user = user;
        expiryDate = calculateExpiryDate();
        confirmationToken = UUID.randomUUID().toString();
    }


    public Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EXPIRATION_MIN);
        return new Date(cal.getTime().getTime());
    }

    public boolean isExpired() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        return expiryDate.getTime() - cal.getTime().getTime() <= 0;
    }

}
