package com.example.mbad.project.web.forms;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm
{
    @NotBlank(message = "Podaj login")
    private String username;

    @NotBlank(message = "Podaj hasło")
    private String password;
}