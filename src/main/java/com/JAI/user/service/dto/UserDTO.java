package com.JAI.user.service.dto;

import com.JAI.user.domain.Role;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO {
    private UUID userId;

    private String nickname;

    private Role role;

    private String email;
}
