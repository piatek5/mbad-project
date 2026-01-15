package com.example.mbad.project.web.forms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookAddForm {

    private String title;

    private String isbn;

    private Integer publicationYear;

    private String description;

    private Long publisherId;

    private List<Long> authorIds;

    private List<Long> categoryIds;
}
