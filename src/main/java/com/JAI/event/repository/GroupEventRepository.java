package com.JAI.event.repository;

import com.JAI.event.domain.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface GroupEventRepository extends JpaRepository<GroupEvent, UUID> {
    List<GroupEvent> findByGroup_GroupIdAndStartDateBetween(UUID groupId, LocalDate startDate, LocalDate endDate);
}
