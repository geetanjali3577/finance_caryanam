package com.finserv.service;

import com.finserv.dto.PersonalInfoRequestDTO;
import com.finserv.dto.PersonalInfoResponseDTO;
import com.finserv.dto.UserResponseDTO;
import com.finserv.entity.PersonalInfo;

import java.util.List;

public interface PersonalInfoService {

    Object savePersonalInfo(PersonalInfoRequestDTO dto);

    PersonalInfoResponseDTO updatePersonalInfo(Long userId, PersonalInfoRequestDTO dto);

    List<PersonalInfoResponseDTO> getAllPersonalInfo();



    PersonalInfoResponseDTO getByUserId(Long userId);


}