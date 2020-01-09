package com.example.carrent.model;

import java.util.Date;

import lombok.Data;

@Data
public class Car {
    private int id;
    private String carType;
    private String carNumber;
    private User user;
    private String startTime;
    private String endTime;
    private String imagePath;
    private int state;
}
