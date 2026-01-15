package com.example.mbad.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/", "/login", "/register", "/books").permitAll() // Co jest dostępne bez logowania
                        .anyRequest().authenticated() // Reszta wymaga logowania
                )
                .formLogin(form -> form
                        .loginPage("/login") // Widok dla GET
                        .loginProcessingUrl("/login") // Widok dla POST
                        .defaultSuccessUrl("/", true) // Gdzie przekierować po sukcesie
                        .failureUrl("/login?error=true") // Gdzie po błędzie
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}