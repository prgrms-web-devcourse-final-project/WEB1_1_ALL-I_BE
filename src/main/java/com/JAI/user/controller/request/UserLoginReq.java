package com.JAI.user.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserLoginReq {
    @NotBlank(message = "이메일을 입력하셔야 합니다.")
    String email;
    @NotBlank(message = "비밀번호를 입력하셔야 합니다.")
    String password;
}
