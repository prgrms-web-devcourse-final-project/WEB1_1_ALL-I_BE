package com.JAI.user.service;

import com.JAI.category.DTO.CategoryCreateReqDTO;
import com.JAI.category.DTO.CategoryResDTO;
import com.JAI.category.domain.Category;
import com.JAI.category.mapper.CategoryConverter;
import com.JAI.category.service.CategoryService;
import com.JAI.user.controller.request.UserSignupReq;
import com.JAI.user.controller.request.UserUpdateReq;
import com.JAI.user.controller.response.UserInfoRes;
import com.JAI.user.controller.response.UserSignupRes;
import com.JAI.user.controller.response.UserUpdateRes;
import com.JAI.user.converter.UserConverter;
import com.JAI.user.domain.User;
import com.JAI.user.exception.UserDuplicatedException;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public UserSignupRes signup(UserSignupReq userSignupReq) {

        //이메일 중복 확인
        if(userRepository.existsByEmail(userSignupReq.getEmail())) {
            throw new UserDuplicatedException("이미 존재하는 회원입니다.");
        }
        //닉네임 중복 확인
        if(userRepository.existsByNickname(userSignupReq.getNickname())) {
            throw new UserDuplicatedException("이미 사용중인 닉네임입니다.");
        }
        //Repo에 저장
        User user = userRepository.save(userConverter.toUserEntity(userSignupReq));

        //기본 카테고리 생성
        CategoryCreateReqDTO categoryCreateReqDTO = CategoryCreateReqDTO.builder()
                .name("카테고리 1")
                .color("#97cdbd")
                .build();

        CategoryResDTO categoryResDTO = categoryService.createCategory(categoryCreateReqDTO, user.getUserId());

        return userConverter.toUserSignupDTO(user.getUserId(), user.getNickname(), categoryResDTO.getCategoryId());
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID를 가진 사용자를 찾을 수 없습니다.", userId));
    }

    @Override
    public UserUpdateRes updateUserInfo(UserUpdateReq req, UUID userId) {
        //유저 아이디 유효한지
        User user = getUserById(userId);

        //닉네임 중복 확인
        if((!user.getNickname().equals(req.getNickname()))&&(userRepository.existsByNickname(req.getNickname()))) {
            throw new UserDuplicatedException("이미 사용중인 닉네임입니다.");
        }

        user.updateUserInfo(req.getNickname(), req.getImageUrl(), req.getEndTime());

        userRepository.save(user);

        return userConverter.toUserUpdateDTO(user);
    }

    @Override
    public UserInfoRes getUserInfo(UUID userId) {
        //유저 아이디 유효한지
        User user = getUserById(userId);

        //엔티티 값 DTO로
        return userConverter.toUserInfoDTO(user);
    }

    @Override
    public String getUserNickname(UUID userId) {
        User user = getUserById(userId);
        String nickname = user.getNickname();
        return nickname;
    }
}
