package com.example.mbad.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Reservation extends CirculationEntry
{
    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookCopy bookCopy;

    private Instant expirationDate;
}
