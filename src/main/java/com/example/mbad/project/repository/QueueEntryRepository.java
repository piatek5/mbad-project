package com.example.mbad.project.repository;

import com.example.mbad.project.model.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, Long> {

}
