package com.JAI.event.repository;

import com.JAI.event.domain.PersonalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PersonalEventRepository extends JpaRepository<PersonalEvent, UUID> {
    List<PersonalEvent> findAllByStartDateBetweenAndUser_UserId(LocalDate startDate, LocalDate endDate, UUID userId);
}
