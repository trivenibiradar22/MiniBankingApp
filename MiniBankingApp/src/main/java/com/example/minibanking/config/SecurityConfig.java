
package com.example.minibanking.config;

import com.example.minibanking.repository.AccountRepository;
import com.example.minibanking.security.JwtAuthenticationFilter;
import com.example.minibanking.security.JwtService;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtService jwtService,
            AccountRepository repository) {
        return new JwtAuthenticationFilter(jwtService, repository);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                    "/",
                    "/login.html",
                    "/register.html",
                    "/index.html",
                    "/customer.html",
                    "/style.css",
                    "/script.js",
                    "/customer.js",
                    "/favicon.ico"
                ).permitAll()

                // Public APIs
                .requestMatchers(
                    "/api/accounts/register",
                    "/api/accounts/login"
                ).permitAll()

                // Admin APIs
                .requestMatchers(
                    "/api/accounts/all",
                    "/api/admin/**"
                ).hasRole("ADMIN")

                // Customer APIs
             // Admin and Customer Profile APIs
                .requestMatchers("/api/profile/**").hasAnyRole("USER", "ADMIN")

                // Customer APIs
                .requestMatchers(
                    "/api/accounts/balance",
                    "/api/accounts/deposit",
                    "/api/accounts/withdraw",
                    "/api/transactions/**"
                ).hasRole("USER")

                // All remaining requests
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
