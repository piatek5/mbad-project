package com.example.mbad.project.repository;

import com.example.mbad.project.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) OR :title IS NULL) AND " +
            "(EXISTS (SELECT a FROM b.authors a WHERE LOWER(a.lastName) LIKE LOWER(CONCAT('%', :author, '%'))) OR :author IS NULL) AND " +
            "(b.publicationYear >= :minYear OR :minYear IS NULL) AND " +
            "(b.publicationYear <= :maxYear OR :maxYear IS NULL)")
    List<Book> findWithFilters(String title, String author, Integer minYear, Integer maxYear);
}


