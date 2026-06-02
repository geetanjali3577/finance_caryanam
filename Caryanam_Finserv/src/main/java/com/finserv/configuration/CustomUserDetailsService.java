package com.finserv.configuration;

import java.util.List;


import com.finserv.entity.Admin;

import com.finserv.entity.Dealer;
import com.finserv.repository.AdminRepository;

import com.finserv.repository.DealerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.finserv.entity.User;
import com.finserv.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DealerRepository dealerRepository;



    @Override
    public CustomUserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // USER
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {

            String role = user.getRole() != null
                    ? user.getRole().name()
                    : "USER";

            return new CustomUserDetails(
                    user.getUserId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
        }

        // ADMIN
        Admin admin = adminRepository.findByEmail(email).orElse(null);

        if (admin != null) {

            String role = admin.getRole() != null
                    ? admin.getRole().name()
                    : "ADMIN";

            return new CustomUserDetails(
                    admin.getAdminId(),
                    admin.getFullName(),
                    admin.getEmail(),
                    admin.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
        }

        // DEALER
        Dealer dealer = dealerRepository.findByEmail(email).orElse(null);

        if (dealer != null) {

            String role = dealer.getRole() != null
                    ? dealer.getRole().name()
                    : "DEALER";

            return new CustomUserDetails(
                    dealer.getDealerId(),
                    dealer.getFullName(),
                    dealer.getEmail(),
                    dealer.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
        }

        throw new UsernameNotFoundException(
                "User not found with email: " + email
        );
    }
}