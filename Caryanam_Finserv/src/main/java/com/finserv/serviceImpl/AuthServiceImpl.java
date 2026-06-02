package com.finserv.serviceImpl;

import com.finserv.configuration.JwtUtil;
import com.finserv.dto.LoginRequestDTO;
import com.finserv.dto.LoginResponseDTO;
import com.finserv.entity.Admin;
import com.finserv.entity.Dealer;
import com.finserv.entity.User;
import com.finserv.enums.DealerStatus;
import com.finserv.repository.AdminRepository;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final AdminRepository adminRepository;

    // TOKEN BLACKLIST
    private static final Set<String> tokenBlacklist = new HashSet<>();

    // =====================================================
    // LOGIN
    // =====================================================
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        // =====================================
        // EMAIL EXISTS CHECK
        // =====================================

        boolean emailExists =
                userRepository.existsByEmail(request.getEmail())
                        || dealerRepository.existsByEmail(request.getEmail())
                        || adminRepository.existsByEmail(request.getEmail());

        if (!emailExists) {

            throw new RuntimeException(
                    "Account not found. Please register first."
            );
        }

        // =====================================
        // PASSWORD AUTHENTICATION
        // =====================================

        try {

            authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Invalid Password"
            );
        }

        // =====================================
        // USER LOGIN
        // =====================================

        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElse(null);

        if (user != null) {

            String token =
                    jwtUtil.generateToken(

                            user.getUserId(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getRole(),
                            user.getApplicationId(),
                            null
                    );

            return new LoginResponseDTO(token);
        }

        // =====================================
        // DEALER LOGIN
        // =====================================

        Dealer dealer =
                dealerRepository
                        .findByEmail(request.getEmail())
                        .orElse(null);

        if (dealer != null) {

            // DEALER STATUS CHECK
            if (dealer.getStatus() == DealerStatus.INACTIVE) {

                throw new RuntimeException(
                        "Dealer account is inactive. Please contact admin."
                );
            }

            String token =
                    jwtUtil.generateToken(

                            dealer.getDealerId(),
                            dealer.getFullName(),
                            dealer.getEmail(),
                            dealer.getRole(),
                            null,
                            dealer.getDealerCode()
                    );

            return new LoginResponseDTO(token);
        }

        // =====================================
        // ADMIN LOGIN
        // =====================================

        Admin admin =
                adminRepository
                        .findByEmail(request.getEmail())
                        .orElse(null);

        if (admin != null) {

            String token =
                    jwtUtil.generateToken(

                            admin.getAdminId(),
                            admin.getFullName(),
                            admin.getEmail(),
                            admin.getRole(),
                            null,
                            null
                    );

            return new LoginResponseDTO(token);
        }

        throw new RuntimeException(
                "Login Failed"
        );
    }

    // =====================================================
    // LOGOUT
    // =====================================================
    @Override
    public void logout(String token) {

        if (token == null || token.isBlank()) {

            throw new RuntimeException(
                    "Authorization Token is Missing"
            );
        }

        if (token.startsWith("Bearer ")) {

            token = token.substring(7);
        }

        tokenBlacklist.add(token);

        System.out.println("Logout Successfully");
    }

    // =====================================================
    // CHECK TOKEN
    // =====================================================
    public static boolean isTokenBlacklisted(String token) {

        return tokenBlacklist.contains(token);
    }
}