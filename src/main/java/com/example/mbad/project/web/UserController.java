package com.example.mbad.project.web;

import com.example.mbad.project.model.User;
import com.example.mbad.project.service.UserService;
import com.example.mbad.project.web.forms.LoginForm;
import com.example.mbad.project.web.forms.RegisterForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

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

    @GetMapping("/my-profile")
    public String myProfile(Model model, Principal principal,
                            @RequestParam(required = false, defaultValue = "rentals") String tab) {

        String username = principal.getName();

        User user = userService.findByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("activeTab", tab); // Przekazujemy do widoku, która zakładka jest aktywna

        return "user/profile";
    }
}