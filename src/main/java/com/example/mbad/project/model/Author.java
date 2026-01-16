package com.example.mbad.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Positive
    private Integer birthYear;

    @Positive
    private Integer deathYear;

    @ColumnDefault("'Nie wprowadzono opisu.'")
    @Column(length = 2000)
    private String description;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books;
}
