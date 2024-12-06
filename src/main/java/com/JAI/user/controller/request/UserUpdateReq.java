package com.JAI.user.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateReq {
    @NotBlank(message = "닉네임 입력은 필수입니다.")
    String nickname;

    String imageUrl;

    @NotNull(message = "시간 입력은 필수입니다.")
    LocalTime endTime;
}
