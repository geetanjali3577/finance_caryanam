package com.finserv.serviceImpl;

import com.finserv.dto.PersonalInfoRequestDTO;
import com.finserv.dto.PersonalInfoResponseDTO;
import com.finserv.dto.UserResponseDTO;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import com.finserv.exception.BadRequestException;
import com.finserv.repository.PersonalInfoRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.PersonalInfoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalInfoServiceImpl
        implements PersonalInfoService {

    private final PersonalInfoRepository personalInfoRepository;

    private final UserRepository userRepository;

    @Override
    public PersonalInfoResponseDTO savePersonalInfo(PersonalInfoRequestDTO dto) {

        // =========================
        // 1. USER VALIDATION
        // =========================
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BadRequestException("User Not Found"));

        // =========================
        // 2. DUPLICATE CHECK
        // =========================
        if (personalInfoRepository.existsByUser_UserId(dto.getUserId())) {
            throw new BadRequestException("Personal Info Already Exists");
        }

        // =========================
        // 3. CREATE ENTITY
        // =========================
        PersonalInfo personalInfo = new PersonalInfo();

        // LINK USER
        personalInfo.setUser(user);

        // AUTO FETCH FROM USER
        personalInfo.setFullName(user.getFullName());
        personalInfo.setEmail(user.getEmail());
        personalInfo.setMobileNumber(user.getMobileNumber());

        // REQUEST DATA
        personalInfo.setAddress(dto.getAddress());
        personalInfo.setCity(dto.getCity());
        personalInfo.setState(dto.getState());
        personalInfo.setPincode(dto.getPincode());
        personalInfo.setLoanAmount(dto.getLoanAmount());

        personalInfo.setCreatedAt(LocalDateTime.now());

        // =========================
        // 4. SAVE TO DB
        // =========================
        PersonalInfo savedInfo = personalInfoRepository.save(personalInfo);

        // =========================
        // 5. RESPONSE DTO
        // =========================
        PersonalInfoResponseDTO response = new PersonalInfoResponseDTO();

        response.setPersonalInfoId(savedInfo.getPersonalInfoId());


        response.setUserId(savedInfo.getUser().getUserId());

        response.setFullName(savedInfo.getFullName());
        response.setEmail(savedInfo.getEmail());
        response.setMobileNumber(savedInfo.getMobileNumber());

        response.setAddress(savedInfo.getAddress());
        response.setCity(savedInfo.getCity());
        response.setState(savedInfo.getState());
        response.setPincode(savedInfo.getPincode());
        response.setLoanAmount(savedInfo.getLoanAmount());
        response.setCreatedAt(savedInfo.getCreatedAt());

        return response;

    }


    //UPDATED INFO


    @Override
    public PersonalInfoResponseDTO updatePersonalInfo(Long userId, PersonalInfoRequestDTO dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        PersonalInfo info = user.getPersonalInfo();

        if (info == null) {
            info = new PersonalInfo();
            info.setUser(user);
            user.setPersonalInfo(info);
        }

        // ======================
        // PERSONAL INFO UPDATE
        // ======================
        if (dto.getAddress() != null) {
            info.setAddress(dto.getAddress());
        }

        if (dto.getCity() != null) {
            info.setCity(dto.getCity());
        }

        if (dto.getState() != null) {
            info.setState(dto.getState());
        }

        if (dto.getPincode() != null) {
            info.setPincode(dto.getPincode());
        }

        if (dto.getLoanAmount() != null) {
            info.setLoanAmount(dto.getLoanAmount());
        }


        if (dto.getMobileNumber() != null && !dto.getMobileNumber().isEmpty()) {
            info.setMobileNumber(dto.getMobileNumber());
            user.setMobileNumber(dto.getMobileNumber());
        }

        PersonalInfo saved = personalInfoRepository.save(info);
        userRepository.save(user);

        // ======================
        // RESPONSE
        // ======================
        PersonalInfoResponseDTO response = new PersonalInfoResponseDTO();

        response.setPersonalInfoId(saved.getPersonalInfoId());
        response.setUserId(user.getUserId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setMobileNumber(saved.getMobileNumber());
        response.setAddress(saved.getAddress());
        response.setCity(saved.getCity());
        response.setState(saved.getState());
        response.setPincode(saved.getPincode());
        response.setLoanAmount(saved.getLoanAmount());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }


    @Override
    public List<PersonalInfoResponseDTO> getAllPersonalInfo() {

        List<PersonalInfo> list =
                personalInfoRepository
                        .findAllPaidPersonalInfo();

        return list.stream().map(info -> {

            PersonalInfoResponseDTO dto = new PersonalInfoResponseDTO();

            dto.setPersonalInfoId(info.getPersonalInfoId());

            // USER INFO (JOIN)
            if (info.getUser() != null) {
                dto.setUserId(info.getUser().getUserId());
                dto.setFullName(info.getUser().getFullName());
                dto.setEmail(info.getUser().getEmail());
            }

            // PERSONAL INFO
            dto.setMobileNumber(info.getMobileNumber());
            dto.setAddress(info.getAddress());
            dto.setCity(info.getCity());
            dto.setState(info.getState());
            dto.setPincode(info.getPincode());
            dto.setLoanAmount(info.getLoanAmount());
            dto.setCreatedAt(info.getCreatedAt());

            return dto;

        }).toList();
    }


    @Override
    public PersonalInfoResponseDTO getByUserId(Long userId) {

        PersonalInfo info = personalInfoRepository
                .findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Personal Info Not Found"));

        PersonalInfoResponseDTO dto = new PersonalInfoResponseDTO();

        dto.setPersonalInfoId(info.getPersonalInfoId());
        dto.setUserId(info.getUser().getUserId());

        dto.setFullName(info.getFullName());
        dto.setEmail(info.getEmail());
        dto.setMobileNumber(info.getMobileNumber());

        dto.setAddress(info.getAddress());
        dto.setCity(info.getCity());
        dto.setState(info.getState());
        dto.setPincode(info.getPincode());
        dto.setLoanAmount(info.getLoanAmount());
        dto.setCreatedAt(info.getCreatedAt());

        return dto;
    }

}