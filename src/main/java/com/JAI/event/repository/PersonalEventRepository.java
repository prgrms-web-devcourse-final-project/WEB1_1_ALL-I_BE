package com.JAI.event.repository;

import com.JAI.event.domain.PersonalEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonalEventRepository extends JpaRepository<PersonalEvent, UUID> {
}
