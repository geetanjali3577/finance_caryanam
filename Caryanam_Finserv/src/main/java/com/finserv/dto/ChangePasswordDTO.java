package com.finserv.dto;
import lombok.Data;
@Data
public class ChangePasswordDTO {

    private String email;

    private String oldPassword;

    private String newPassword;

    private String confirmPassword;


}