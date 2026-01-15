package com.example.mbad.project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class QueueEntry extends CirculationEntry {

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
