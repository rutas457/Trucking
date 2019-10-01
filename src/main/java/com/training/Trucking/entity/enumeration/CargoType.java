package com.training.Trucking.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CargoType {
    FRAGILE(1.2),
    FOOD(1.3),
    DANGEROUS(2),
    STANDARD(1);

    private double priceCoefficient;

}
