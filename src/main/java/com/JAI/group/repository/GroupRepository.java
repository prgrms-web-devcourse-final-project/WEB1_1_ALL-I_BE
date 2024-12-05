package com.JAI.group.repository;

import com.JAI.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    @Query("SELECT g " +
            "FROM Group g " +
            "JOIN GroupSetting gs ON gs.group.groupId = g.groupId " +
            "WHERE gs.user.userId = :userId")
    public List<Group> findGroupByUserId(UUID userId);
}
