package com.example.mbad.project.web.forms;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookAddForm {

    @NotBlank(message = "Tytuł nie może być pusty")
    @Size(max = 255, message = "Tytuł jest zbyt długi")
    private String title;

    @NotBlank(message = "ISBN jest wymagany")
    @Pattern(regexp = "\\d{13}", message = "ISBN musi składać się z dokładnie 13 cyfr")
    private String isbn;

    @NotNull(message = "Rok wydania jest wymagany")
    @Min(value = 1000, message = "Rok nie może być mniejszy niż 1000")
    private Integer publicationYear;

    @AssertTrue(message = "Rok wydania nie może być z przyszłości")
    private boolean isPublicationYearValid() {
        // Jeśli rok jest null, zwracamy true (bo @NotNull wyżej zajmie się błędem null)
        if (publicationYear == null) {
            return true;
        }

        // Sprawdzamy: rok wpisany <= rok obecny
        return publicationYear <= java.time.Year.now().getValue();
    }

    @Size(max = 2000, message = "Opis jest zbyt długi")
    private String description;

    @NotNull(message = "Musisz wybrać wydawcę")
    private Long publisherId;

    @NotEmpty(message = "Książka musi mieć co najmniej jednego autora")
    private List<Long> authorIds;

    @NotEmpty(message = "Książka musi mieć co najmniej jedną kategorię")
    private List<Long> categoryIds;
}