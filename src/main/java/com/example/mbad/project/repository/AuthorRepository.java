package com.example.mbad.project.repository;

import com.example.mbad.project.model.Author;
import com.example.mbad.project.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}