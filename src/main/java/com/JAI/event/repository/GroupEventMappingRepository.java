package com.JAI.event.repository;

import com.JAI.event.domain.GroupEventMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GroupEventMappingRepository extends JpaRepository<GroupEventMapping, UUID> {
}
