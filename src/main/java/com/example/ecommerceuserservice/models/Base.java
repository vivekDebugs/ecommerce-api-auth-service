package com.example.ecommerceuserservice.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class Base {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
}