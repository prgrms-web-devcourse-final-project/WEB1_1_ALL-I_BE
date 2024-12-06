package com.JAI.user.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserInfoRes {

    String email;

    String nickname;

    String imageUrl;

    LocalTime endTime;

}
