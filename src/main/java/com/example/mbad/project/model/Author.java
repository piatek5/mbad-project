package com.example.mbad.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String birthYear;

    private String deathYear;

    @ColumnDefault("'Nie wprowadzono opisu.'")
    private String description;

    @ManyToMany(mappedBy = "authors")
    private List<Book> books;
}
