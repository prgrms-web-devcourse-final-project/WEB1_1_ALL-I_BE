package com.JAI.group.repository;

import com.JAI.group.domain.GroupSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupSettingRepository extends JpaRepository<GroupSetting, UUID> {
}
