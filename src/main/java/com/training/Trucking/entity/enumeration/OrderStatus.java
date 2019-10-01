package com.training.Trucking.entity.enumeration;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum OrderStatus {
    CREATED, PROCESSING, COMPLETED, REJECTED;
}
