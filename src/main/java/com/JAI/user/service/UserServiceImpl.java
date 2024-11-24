package com.JAI.user.service;

import com.JAI.user.controller.request.UserJoinReq;
import com.JAI.user.domain.Provider;
import com.JAI.user.domain.User;
import com.JAI.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public void join(UserJoinReq userJoinReq) {

        String email = userJoinReq.email();
        String password = userJoinReq.password();
        String nickname = userJoinReq.nickname();

        //이메일 중복 확인
        if(userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
        //닉네임 중복 확인 --> 이걸 예외 처리하는게 맞나?
        if(userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        //비밀번호 암호화?
        User user = User.create(
                nickname,
                email,
                bCryptPasswordEncoder.encode(password),
                Provider.ORIGIN
        );
        //Repo에 저장
        userRepository.save(user);
    }
}
