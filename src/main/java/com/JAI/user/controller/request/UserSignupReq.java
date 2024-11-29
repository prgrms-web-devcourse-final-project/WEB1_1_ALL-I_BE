package com.JAI.user.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserSignupReq {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "형식이 이메일이어야 합니다.")
    String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$",
            message = "비밀번호는 8자 이상이어야 하며, 숫자, 영문자, 특수 문자를 각각 최소 한 개 이상 포함하여야 합니다.")
    String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    String nickname;
}
