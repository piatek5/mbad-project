package com.example.mbad.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class Rental extends CirculationEntry {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookCopy bookCopy;

    @PositiveOrZero
    private int renewalCount;
}
