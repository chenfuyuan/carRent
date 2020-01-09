package com.example.carrent.vo;

import com.example.carrent.model.Car;
import com.example.carrent.model.User;

import lombok.Data;

@Data
public class SubCarVo {
    private String phone;
    private String subDateTime;
    private String token;
    private Car car;
}
