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

import java.time.LocalTime;
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
    private LocalTime endDatetime;

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
    private User(String nickname, Role role, String email, String password, Provider provider, String imageUrl, LocalTime endDatetime) {
        this.nickname = nickname;
        this.role = role;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.imageUrl = imageUrl;
        this.endDatetime = endDatetime;
    }

    //로그인 용 빌더
    @Builder
    private User(String email, String password, Role role){
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User create(String nickname, String email, String password, Provider provider) {
        return User.builder()
                .nickname(nickname)
                .role(Role.ROLE_USER)    //기본값
                .email(email)
                .password(password)
                .provider(provider)
                .endDatetime(LocalTime.of(11,59,0))
                .build();
    }

    public static User createLoginInfo(String email, Role role) {
        return User.builder()
                .email(email)
                .role(role)
                .build();
    }
}
