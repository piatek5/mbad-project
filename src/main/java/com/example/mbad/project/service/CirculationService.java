package com.example.mbad.project.service;

import com.example.mbad.project.model.Rental;
import com.example.mbad.project.repository.QueueEntryRepository;
import com.example.mbad.project.repository.RentalRepository;
import com.example.mbad.project.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CirculationService {

    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;
    private final QueueEntryRepository queueEntryRepository;


    public long activeRentalsCount() {
        return rentalRepository.countByEndDateIsNull();
    }

    public List<Rental> getUserHistory(Long userId) {
        return rentalRepository.findAllByUserIdOrderByCreationDateDesc(userId);
    }
}
