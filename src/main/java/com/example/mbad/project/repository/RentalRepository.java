package com.example.mbad.project.repository;

import com.example.mbad.project.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    public long countByEndDateIsNull();
}
