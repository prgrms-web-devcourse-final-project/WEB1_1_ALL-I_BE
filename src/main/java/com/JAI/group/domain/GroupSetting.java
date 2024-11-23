package com.JAI.group.domain;

import com.JAI.event.domain.GroupEventMapping;
import com.JAI.todo.domain.GroupTodoMapping;
import com.JAI.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "group_setting")
public class GroupSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_setting_id", columnDefinition = "BINARY(16)")
    private UUID groupSettingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private GroupRole role;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Group group;

    @OneToMany(mappedBy = "groupSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEventMapping> groupEventMapping;

    @OneToMany(mappedBy = "groupSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupTodoMapping> groupTodoMapping;

    @Builder
    private GroupSetting (GroupRole role, User user, Group group) {
        this.role = role;
        this.user = user;
        this.group = group;
    }

    public static GroupSetting create(GroupRole role, User users, Group group) {
        return GroupSetting.builder()
                .role(role)
                .user(users)
                .group(group)
                .build();
    }
}
