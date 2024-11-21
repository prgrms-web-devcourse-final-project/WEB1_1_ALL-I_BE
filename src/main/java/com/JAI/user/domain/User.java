package com.JAI.user.domain;

import com.JAI.alarm.domain.Alarm;
import com.JAI.category.domain.Category;
import com.JAI.event.domain.PersonalEvent;
import com.JAI.group.domain.GroupSetting;
import com.JAI.todo.domain.PersonalTodo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime endDatetime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "alarm_id")
    private List<Alarm> alarms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupSetting> groupSettings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalEvent> personalEvents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonalTodo> personalTodos;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories;

    @Builder
    private User(String nickname, Role role, String email, String password, Provider provider, String imageUrl, LocalDateTime endDatetime) {
        this.nickname = nickname;
        this.role = role;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.imageUrl = imageUrl;
        this.endDatetime = endDatetime;
    }

    public static User create(String nickname, String email, String password, Provider provider, LocalDateTime endDatetime) {
        return User.builder()
                .nickname(nickname)
                .role(Role.USER)    //기본값
                .email(email)
                .password(password)
                .provider(provider)
                .endDatetime(endDatetime)
                .build();
    }
}
