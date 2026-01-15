package com.example.mbad.project.service;

import com.example.mbad.project.model.Book;
import com.example.mbad.project.repository.*;
import com.example.mbad.project.web.forms.BookSearchForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String title, String author, Integer minYear, Integer maxYear) {
        return bookRepository.findWithFilters(title, author, minYear, maxYear);
    }

    public List<Book> searchBooks(BookSearchForm searchForm) {
        return bookRepository.findWithFilters(searchForm.getTitle(), searchForm.getAuthor(), searchForm.getMinYear(), searchForm.getMaxYear());
    }
}
