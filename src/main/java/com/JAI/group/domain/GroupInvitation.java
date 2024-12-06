package com.JAI.group.domain;

import com.JAI.alarm.domain.Alarm;
import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "group_invitation")
public class GroupInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_invitation_id", columnDefinition = "BINARY(16)")
    private UUID groupInvitationId;


    @Column(name = "status", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToOne (mappedBy = "groupInvitation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Alarm alarm;


    @Builder
    private GroupInvitation(UUID groupInvitationId, InvitationStatus status, Group group, User user) {
        this.groupInvitationId = groupInvitationId;
        this.status = status;
        this.group = group;
        this.user = user;
    }

    public static GroupInvitation create(Group group, User user) {
        return GroupInvitation.builder()
                .status(InvitationStatus.PENDING) //default
                .group(group)
                .user(user)
                .build();
    }

    public void updateStatus(InvitationStatus status) {
        this.status = status;
    }

}
