package com.example.carrent.vo;


import com.example.carrent.model.User;

import lombok.Data;

@Data
public class SignInMessage {
    private String message;
    private boolean success;
    private boolean isRememberPassword;
    private User user;
}
