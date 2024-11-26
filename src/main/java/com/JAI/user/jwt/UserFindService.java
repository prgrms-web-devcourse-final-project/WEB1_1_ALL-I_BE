package com.JAI.user.jwt;

import com.JAI.user.domain.User;
import com.JAI.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFindService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) throws Exception{
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("User not found"));
    }
}
