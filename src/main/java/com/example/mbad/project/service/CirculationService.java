package com.example.mbad.project.service;

import com.example.mbad.project.repository.QueueEntryRepository;
import com.example.mbad.project.repository.RentalRepository;
import com.example.mbad.project.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CirculationService {

    private final RentalRepository rentalRepository;
    private final ReservationRepository reservationRepository;
    private final QueueEntryRepository queueEntryRepository;

}
