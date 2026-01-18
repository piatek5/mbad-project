package com.example.mbad.project.repository;

import com.example.mbad.project.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            // Tytuł (bez zmian)
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) OR :title IS NULL) AND " +
            "(EXISTS (SELECT a FROM b.authors a WHERE " +
            "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :author, '%')) OR " +
            "LOWER(a.lastName)  LIKE LOWER(CONCAT('%', :author, '%'))" +
            ") OR :author IS NULL) AND " +
            "(b.publicationYear >= :minYear OR :minYear IS NULL) AND " +
            "(b.publicationYear <= :maxYear OR :maxYear IS NULL) AND " +
            "(LOWER(b.isbn) LIKE LOWER(CONCAT('%', :isbn, '%')) OR :isbn IS NULL)")
    List<Book> findWithFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            @Param("isbn") String isbn
    );
}


