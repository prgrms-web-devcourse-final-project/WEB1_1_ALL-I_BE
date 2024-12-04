package com.JAI.group.repository;

import com.JAI.group.domain.GroupInvitation;
import com.JAI.group.domain.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, UUID> {
    boolean existsByUser_UserIdAndGroup_GroupIdAndStatus(UUID userId, UUID groupId, InvitationStatus status);
}
