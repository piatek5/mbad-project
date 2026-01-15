package com.example.mbad.project.web.forms;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchForm {

    private String isbn;

    private String title;

    private String author;

    @Positive
    private Integer minYear;

    @Positive
    private Integer maxYear;

    public boolean hasFilters() {
        return (title != null && !title.isBlank()) ||
                (author != null && !author.isBlank()) ||
                (isbn != null && !isbn.isBlank()) ||
                minYear != null ||
                maxYear != null;
    }
}
