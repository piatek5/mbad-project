package com.example.mbad.project.web.forms;

import jakarta.validation.constraints.AssertTrue;
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

    @Positive(message = "Rok musi być dodatni")
    private Integer minYear;

    @Positive(message = "Rok musi być dodatni")
    private Integer maxYear;

    @AssertTrue(message = "Rok 'do' musi być późniejszy lub równy rokowi 'od'")
    private boolean isYearRangeValid() {
        // 1. Jeśli którykolwiek jest nullem, nie ma co porównywać -> uznajemy za poprawne
        if (minYear == null || maxYear == null) {
            return true;
        }

        // 2. Właściwe sprawdzenie logiczne
        return minYear <= maxYear;
    }

    public boolean hasFilters() {
        return (title != null && !title.isBlank()) ||
                (author != null && !author.isBlank()) ||
                (isbn != null && !isbn.isBlank()) ||
                minYear != null ||
                maxYear != null;
    }
}