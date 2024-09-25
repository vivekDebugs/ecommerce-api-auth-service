package com.example.ecommerceuserservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User extends Base {
    private String email;
    private String password;
}
