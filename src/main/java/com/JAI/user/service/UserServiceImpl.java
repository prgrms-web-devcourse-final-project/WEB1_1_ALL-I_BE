package com.JAI.user.service;

import com.JAI.user.domain.User;
import com.JAI.user.exception.UserNotFoundException;
import com.JAI.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("해당 ID를 가진 사용자를 찾을 수 없습니다.", userId));
    }
}
