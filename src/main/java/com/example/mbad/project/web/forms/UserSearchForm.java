package com.example.mbad.project.web.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchForm {

    private String username;

    private String email;

    private String phone;

    public boolean hasFilters() {
        return (username != null && !username.isBlank()) ||
                (email != null && !email.isBlank()) ||
                (phone != null && !phone.isBlank());
    }
}
