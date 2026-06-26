package com.finserv.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {

        http

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/favicon.ico","/webhook","/api/payment/**").permitAll()

                                .requestMatchers(
                                                "/api/auth/**",
                                                "/api/user/register",
                                                "/api/user/send-otp",
                                                "/api/user/verify-otp",
                                        "/api/user/register/send-otp",
                                        "/api/user/register/verify-otp",
                                        "/api/dealer/register/send-otp",
                                        "/api/dealer/register/verify-otp",




                                                "/api/user/reset-password",
                                                "/api/dealer/register",
                                                "/api/dealer/send-otp",
                                                "/api/dealer/verify-otp",
                                                "/api/dealer/reset-password",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/v3/api-docs/**"
                                        ).permitAll()
                                .requestMatchers(
                                        "/api/auth/**",
                                        "/api/documents/download-all/**","/api/documents/zip/**"
                                          ).permitAll()

                                .requestMatchers(HttpMethod.PUT, "/api/user/payment-success/**").permitAll()
                                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                        // Admin APIs
                                        .requestMatchers(
                                                "/api/user/all",
                                                "/api/user/search",
                                                "/api/user/assign-bank/**",
                                                "/api/user/payment-success/**",
                                                "/api/dealer/all",
                                                "/api/dealer/search/dealer-code",
                                                "/api/admin/current",
                                                "/api/admin/me",
                                                "/api/admin/profile",
                                                "/api/admin/dashboard",
                                                "/api/admin/banks/**",
                                                "/api/personal-info/all",
                                                "/api/documents/status/**",
                                                "/api/documents/pending",
                                                "/api/documents/verified",
                                                "/api/documents/*/remarks"
                                        ).hasRole("ADMIN")
                                       .requestMatchers("/api/user/dealer/**")
                                        .permitAll()
                                        // User + Admin
                                        .requestMatchers(

                                                "/api/user/**",
                                                "/api/user/update/**",
                                                "/api/user/search/email",
                                                "/api/user/change-password",
                                                "/api/user/delete/{userId}"
                                        ).hasAnyRole("USER", "ADMIN")

                                        // Personal Info
                                .requestMatchers(

                                        "/api/personal-info/save",
                                        "/api/personal-info/**",
                                        "/api/personal-info/update/**"
                                ).hasAnyRole("USER","DEALER","ADMIN")

                                .requestMatchers("/api/personal-info/all",
                                        "/api/dealer/change-password",
                                        "/api/dealer/{dealerCode}/user/{userId}",
                                        "/api/dealer/delete/{userId}"
                                )
                                .hasAnyRole("DEALER","ADMIN")

                                        // Documents
                                        .requestMatchers(
                                                "/api/documents/upload"
                                        ).hasAnyRole("USER", "DEALER")

                                        .requestMatchers(
                                                "/api/documents/count/**",
                                                "/api/documents/download/**",
                                                "/api/documents/preview/**",
                                                "/api/documents/user/**"
                                        ).hasAnyRole("USER", "DEALER", "ADMIN")

                                        // Notifications
                                        .requestMatchers("/api/notifications/**")
                                        .hasAnyRole("USER", "DEALER", "ADMIN")
                                        .requestMatchers("/api/v1/whatsapp/**")
                                .hasRole("ADMIN")
                                        // Dealer
                                        .requestMatchers("/api/dealer/**","/api/dealer/all",
                                                 "/api/dealer/search/dealer-code")
                                        .hasAnyRole("DEALER", "ADMIN")

                                        // Chatbot
                                        .requestMatchers("/api/chatbot/**")
                                        .hasAnyRole("USER", "DEALER", "ADMIN")

                                .requestMatchers(
                                        "/api/user/all",
                                        "/api/user/search"
                                ).hasAnyRole("DEALER","ADMIN")

                                .requestMatchers(
                                        "/api/users/delete/**" ,
                                        "/api/user/assign-bank/**"

                                ).hasRole("ADMIN")

                                        .anyRequest().authenticated()
                                )


                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:63342",
                "https://v1.vahanfinserv.com, " ,
                "https://vahanfinserv.com"
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}