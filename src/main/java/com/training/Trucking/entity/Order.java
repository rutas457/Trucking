package com.training.Trucking.entity;

import com.training.Trucking.entity.enumeration.CargoType;
import com.training.Trucking.entity.enumeration.OrderStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "user_order")
public class Order {

    private static final int tonPerKm = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cargoName;

    @ManyToOne
    private Route route;
    private Long price;

    @Enumerated(EnumType.STRING)
    private OrderStatus state;

    @Enumerated(EnumType.STRING)
    private CargoType cargoType;

    private Integer weight;
    private LocalDate shippingStart;
    private LocalDate shippingEnd;

    private Boolean paid;

    @ManyToOne
    private User user;

    public long calculateOrderPrice() {
        return Double.valueOf(route.getDistanceInKm() * weight * tonPerKm * cargoType.getPriceCoefficient()).longValue();
    }

}
