package com.example.mbad.project.repository;

import com.example.mbad.project.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    public long countByEndDateIsNull();

    List<Rental> findAllByUserIdOrderByCreationDateDesc(Long userId);

    // Opcjonalnie: tylko aktywne (niezwrócone)
    List<Rental> findAllByUserIdAndEndDateIsNull(Long userId);
}
