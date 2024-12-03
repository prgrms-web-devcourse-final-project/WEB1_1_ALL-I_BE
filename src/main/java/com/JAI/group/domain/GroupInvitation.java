package com.JAI.group.domain;

import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
// TODO :: 그룹 멤버
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "group_invitation_tb")
public class GroupInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_invitation_id", columnDefinition = "BINARY(16)")
    private UUID groupInvitationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    private GroupInvitation(Status status, Group group, User user) {
        this.status = status;
        this.group = group;
        this.user = user;
    }

    public static GroupInvitation create(Group group, User user) {
        return GroupInvitation.builder()
                .status(Status.PENDING) //default
                .group(group)
                .user(user)
                .build();
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

}
