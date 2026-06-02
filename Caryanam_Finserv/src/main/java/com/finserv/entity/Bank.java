package com.finserv.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "banks")
@Data
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;

    private String bankName;

    private String representativeName;

    private String contactNumber;

    private String email;


}