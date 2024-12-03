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

    @Column(name = "description", nullable = true, length = 255)
    private String description;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToOne (mappedBy = "groupInvitation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Alarm alarm;


    @Builder
    private GroupInvitation(InvitationStatus status, String description) {
        this.status = status;
        this.description = description;
    }

    public static GroupInvitation create(InvitationStatus status, String description) {
        return GroupInvitation.builder()
                .status(status)
                .description(description)
                .build();
    }
}
