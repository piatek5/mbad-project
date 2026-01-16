package com.example.mbad.project.web.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {

    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Size(min = 3, max = 50, message = "Nazwa musi mieć od 3 do 50 znaków")
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 6, message = "Hasło musi mieć minimum 6 znaków")
    private String password;

    @NotBlank(message = "E-mail jest wymagany")
    @Email(message = "Niepoprawny format adresu email")
    private String email;

    @Pattern(regexp = "\\d{9}", message = "Telefon musi składać się z 9 cyfr (np. 123456789)")
    private String phone;
}