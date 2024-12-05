package com.JAI.event.repository;

import com.JAI.event.domain.GroupEventMapping;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface GroupEventMappingRepository extends JpaRepository<GroupEventMapping, UUID> {
    @Query("DELETE FROM GroupEventMapping gem WHERE gem.groupSetting.groupSettingId = :groupSettingId")
    @Modifying
    @Transactional
    void deleteByGroupSettingId(UUID groupSettingId);
}
