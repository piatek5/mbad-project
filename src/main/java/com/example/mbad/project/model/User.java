package com.example.mbad.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @NotBlank
    @Length(min = 5, max = 25)
    private String username;

    @NotBlank
    private String password;

    @Column(length = 15)
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

    public List<Rental> getActiveRentals() {
        if (rentals == null) return new ArrayList<>();
        return rentals.stream()
                .filter(r -> r.getEndDate() == null)
                .collect(Collectors.toList());
    }

    public List<Reservation> getActiveReservations() {
        if (reservations == null) return new ArrayList<>();
        return reservations.stream()
                .filter(r -> r.getEndDate() == null) // lub inny warunek aktywności
                .collect(Collectors.toList());
    }
}
