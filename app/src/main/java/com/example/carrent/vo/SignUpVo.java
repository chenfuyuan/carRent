package com.example.carrent.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SignUpVo {
    private String name;
    private String password;
    private String authCode;
    private String phone;
    private String ImagePath;
}
