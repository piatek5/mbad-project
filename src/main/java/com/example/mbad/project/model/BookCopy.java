package com.example.mbad.project.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BookCopy {

    @Id
    @GeneratedValue
    private Integer id;

    private Long barcode;

    @ColumnDefault("true")
    private Boolean usable;

    @ColumnDefault("true")
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}
