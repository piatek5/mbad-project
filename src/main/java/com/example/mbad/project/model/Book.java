package com.example.mbad.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String isbn;

    @NotBlank
    private String title;

    @Positive
    private Integer publicationYear;

    @ColumnDefault("'Nie wprowadzono opisu.'")
    @Column(length = 2000)
    private String description;

    @OneToMany(mappedBy = "book")
    private List<BookCopy> bookCopies;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany
    @JoinTable(
            name = "books_authors",
            joinColumns = {
                    @JoinColumn(name = "book_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "author_id")
            }
    )
    private Set<Author> authors;

    @ManyToMany
    @JoinTable(
            name = "books_categories",
            joinColumns = {
                    @JoinColumn(name = "book_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "category_id")
            }
    )
    private Set<Category> categories;

    @Formula("(SELECT COUNT(*) FROM book_copy bc WHERE bc.book_id = id AND bc.available = true)")
    private Integer availableCopiesCount;
}
