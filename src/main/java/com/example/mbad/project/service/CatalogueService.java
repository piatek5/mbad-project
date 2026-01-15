package com.example.mbad.project.service;

import com.example.mbad.project.model.Author;
import com.example.mbad.project.model.Book;
import com.example.mbad.project.model.Category;
import com.example.mbad.project.model.Publisher;
import com.example.mbad.project.repository.*;
import com.example.mbad.project.web.forms.BookAddForm;
import com.example.mbad.project.web.forms.BookSearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogueService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    private final BookRepository bookService;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public List<Publisher> getPublishers() {
        return publisherRepository.findAll();
    }

    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String title, String author, Integer minYear, Integer maxYear, String isbn) {
        return bookRepository.findWithFilters(title, author, minYear, maxYear, isbn);
    }

    public List<Book> searchBooks(BookSearchForm searchForm) {
        return bookRepository.findWithFilters(searchForm.getTitle(), searchForm.getAuthor(), searchForm.getMinYear(), searchForm.getMaxYear(), searchForm.getIsbn());
    }

    // W BookService.java

    public void addNewBook(BookAddForm form) {
        // 1. Pobieramy obiekty na podstawie ID z formularza
        Publisher publisher = publisherRepository.findById(form.getPublisherId())
                .orElseThrow(() -> new RuntimeException("Brak wydawcy"));

        List<Author> authors = authorRepository.findAllById(form.getAuthorIds());
        List<Category> categories = categoryRepository.findAllById(form.getCategoryIds());

        // 2. Tworzymy nową książkę
        Book book = Book.builder()
                .title(form.getTitle())
                .isbn(form.getIsbn())
                .publicationYear(form.getPublicationYear())
                .description(form.getDescription())
                .publisher(publisher)
                .authors(new HashSet<>(authors))     // Set zamiast List (zależy jak masz w Encji)
                .categories(new HashSet<>(categories))
                .build();

        // 3. Zapisujemy
        bookRepository.save(book);

        // 4. (Opcjonalnie) Automatycznie tworzymy 1 egzemplarz (BookCopy),
        // żeby książka była od razu widoczna jako "dostępna"
    }
}
