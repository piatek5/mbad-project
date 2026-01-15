package com.example.mbad.project.web;

import com.example.mbad.project.model.Book;
import com.example.mbad.project.service.CatalogueService;
import com.example.mbad.project.web.forms.BookSearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CatalogueController {

    private final CatalogueService catalogueService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(
                String.class,
                new StringTrimmerEditor(true) // true => "" -> null
        );
    }

    @GetMapping("/books")
    public String listBooks(
            @ModelAttribute("searchForm") BookSearchForm searchForm, Model model) {

        // Jeśli formularz został jakkolwiek wypełniony
        if (searchForm.hasFilters()) {
            // Cała lista książek po przefiltrowaniu
            List<Book> foundBooks = catalogueService.searchBooks(searchForm);

            model.addAttribute("books", foundBooks);
            model.addAttribute("isSearchMode", true);
        } else {

            model.addAttribute("books", List.of());
            model.addAttribute("isSearchMode", false);
        }

        return "catalogue";
    }

    @GetMapping("/")
    public String welcome() {
        return "index";
    }
}
