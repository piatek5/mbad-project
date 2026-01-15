package com.example.mbad.project.service;

import com.example.mbad.project.model.User;
import com.example.mbad.project.repository.UserRepository;
import com.example.mbad.project.web.forms.RegisterForm;
import com.example.mbad.project.web.forms.UserSearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    public void registerUser(RegisterForm form) {
        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());

        // Haszowanie hasła przed zapisem
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public int countAll() {
        return userRepository.findAll().size();
    }

    public List<User> searchUsers(UserSearchForm form) {
        return userRepository.searchUsers(form.getUsername(), form.getEmail(), form.getPhone());
    }

}