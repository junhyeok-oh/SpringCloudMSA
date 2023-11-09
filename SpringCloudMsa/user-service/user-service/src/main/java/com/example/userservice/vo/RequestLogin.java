package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
//유저가 전달한 데이터를 저장하는(로그인 입력했을 때 저장하는)
public class RequestLogin {
    @NotNull(message = "Email can not be null")
    @Size(min = 2, message = "Email not be less than two characters")
    @Email
    private String email;

    @NotNull(message = "Password cannot be null")
    @Size(min = 8, message = "Password must be equal or greater than 8 characters")
    private String password;
}
