package com.training.Trucking.dto;

import com.training.Trucking.entity.enumeration.CargoType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Getter
@Setter
public class OrderDTO {

    private String cargoName;
    private String fromCity;
    private String toCity;

    private String shippingStart;
    private String shippingEnd;

    private CargoType cargoType;

    @Min(value = 1, message = "{cargo.weight}")
    @Max(value = 50, message = "{cargo.weight}")
    private int weight;
}
