package com.example.mbad.project.web;

import com.example.mbad.project.model.Book;
import com.example.mbad.project.model.Rental;
import com.example.mbad.project.model.User;
import com.example.mbad.project.service.CatalogueService;
import com.example.mbad.project.service.CirculationService;
import com.example.mbad.project.service.UserService;
import com.example.mbad.project.web.forms.BookAddForm;
import com.example.mbad.project.web.forms.BookSearchForm;
import com.example.mbad.project.web.forms.UserSearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CirculationService circulationService;
    private final CatalogueService catalogueService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(
                String.class,
                new StringTrimmerEditor(true) // true => "" -> null
        );
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("usersCount", userService.countAll());
        model.addAttribute("activeRentalsCount", circulationService.activeRentalsCount());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(Model model,
            @ModelAttribute("searchForm") UserSearchForm searchForm) {

        // Jeśli formularz został jakkolwiek wypełniony
        if (searchForm.hasFilters()) {
            // Cała lista książek po przefiltrowaniu
            List<User> users = userService.searchUsers(searchForm);

            model.addAttribute("users", users);
            model.addAttribute("isSearchMode", true);

        } else {

            model.addAttribute("users", List.of());
            model.addAttribute("isSearchMode", false);
        }

        return "admin/users";
    }

    @GetMapping("/books")
    public String manageBooks(Model model,
            @ModelAttribute("searchForm") BookSearchForm searchForm) {

        // Jeśli formularz został jakkolwiek wypełniony-
        if (searchForm.hasFilters()) {

            List<Book> books = catalogueService.searchBooks(searchForm);

            model.addAttribute("books", books);
            model.addAttribute("isSearchMode", true);

        } else {

            model.addAttribute("books", List.of());
            model.addAttribute("isSearchMode", false);
        }

        model.addAttribute("bookForm", new BookAddForm());

        // Słowniki do Selectów
        model.addAttribute("allAuthors", catalogueService.getAuthors());
        model.addAttribute("allCategories", catalogueService.getCategories());
        model.addAttribute("allPublishers", catalogueService.getPublishers());

        return "admin/books";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute BookAddForm bookForm) {
        catalogueService.addNewBook(bookForm);
        return "redirect:/admin/books";
    }

    @GetMapping("/users/{id}/rentals")
    public String userRentals(@PathVariable Long id, Model model) {
        // Pobieramy usera (żeby wyświetlić jego imię w nagłówku)
        User user = userService.getUserById(id);

        // Pobieramy jego historię
        List<Rental> rentals = circulationService.getUserHistory(id);

        model.addAttribute("user", user);
        model.addAttribute("rentals", rentals);

        return "admin/user-rentals"; // Nowy widok
    }
}