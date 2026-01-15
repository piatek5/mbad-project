package com.example.mbad.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "web_user")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Length(min = 5, max = 25)
    private String username;

    @NotBlank
    private String password;

    private String phone;

    @NotBlank
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Rental> rentals;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user")
    private List<QueueEntry> queueEntries;
}
