package com.example.ecommerceuserservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInRequestDTO {
    private String email;
    private String password;
}
