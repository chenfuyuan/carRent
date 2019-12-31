package com.example.carrent.vo;

import lombok.Data;

/**
 * 用于发送短信的数据传递
 */
@Data
public class SendSmsMessage {
    //发送验证码得手机号
    private String phone;
    //短信验证码
    private String authCode;
    //发送短信状态信息
    private String message;
    //是否成功
    private boolean success;

}
