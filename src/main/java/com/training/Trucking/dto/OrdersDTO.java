package com.training.Trucking.dto;

import com.training.Trucking.entity.Order;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrdersDTO {
    private List<Order> ordersList;
}
