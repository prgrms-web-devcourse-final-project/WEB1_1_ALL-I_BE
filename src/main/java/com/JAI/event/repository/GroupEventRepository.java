package com.JAI.event.repository;

import com.JAI.event.domain.GroupEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupEventRepository extends JpaRepository<GroupEvent, UUID> {
}
