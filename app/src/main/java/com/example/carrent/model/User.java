package com.example.carrent.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private long id;
    private String name;
    private String phone;
    private String password;
    private String sex;
    private String token;
    private long createTime;
    private long updateTime;
    private String imagePath;
    private int type;
}
