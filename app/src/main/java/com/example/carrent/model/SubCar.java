package com.example.carrent.model;

import lombok.Data;

@Data
public class SubCar {
    private int id;
    private User user;
    private Car car;
    private String subDateTime;
    private String phone;
    private int state;
}
