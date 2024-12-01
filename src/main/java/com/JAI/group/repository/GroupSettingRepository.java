package com.JAI.group.repository;

import com.JAI.group.domain.Group;
import com.JAI.group.domain.GroupSetting;
import com.JAI.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupSettingRepository extends JpaRepository<GroupSetting, UUID> {
    List<GroupSetting> findByUser(User user);
    Optional<GroupSetting> findByUserAndGroup(User user, Group group);
}
