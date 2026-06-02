package com.finserv.repository;

import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInfo, Long> {

    boolean existsByUser_UserId(Long userId);


    Optional<PersonalInfo> findByUser_UserId(Long userId);
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.personalInfo WHERE u.userId = :id")
    Optional<User> findByIdWithPersonalInfo(Long id);
    @Query("SELECT p FROM PersonalInfo p JOIN FETCH p.user")
    List<PersonalInfo> findAllWithUser();
}