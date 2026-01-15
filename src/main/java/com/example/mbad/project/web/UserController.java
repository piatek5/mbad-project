package com.example.mbad.project.web;

import com.example.mbad.project.service.UserService;
import com.example.mbad.project.web.forms.LoginForm;
import com.example.mbad.project.web.forms.RegisterForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(Model model) {

        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {

        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    // 2. Obsłuż wysłanie formularza
    @PostMapping("/register")
    public String registerProcess(@ModelAttribute RegisterForm registerForm) {

        userService.registerUser(registerForm);

        return "redirect:/login";
    }
}