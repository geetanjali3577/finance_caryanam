
package com.finserv.entity;
import com.finserv.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "adminReg")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;

    private String fullName;

    private String mobileNumber;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}