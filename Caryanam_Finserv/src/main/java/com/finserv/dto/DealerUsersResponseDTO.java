package com.finserv.dto;

import lombok.Data;

import java.util.List;

@Data
public class DealerUsersResponseDTO {

    private String dealerCode;
    private Integer totalUsers;
    private List<UserResponseDTO> users;
}