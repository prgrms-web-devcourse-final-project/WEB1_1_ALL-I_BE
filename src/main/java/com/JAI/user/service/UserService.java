package com.JAI.user.service;

import com.JAI.user.domain.User;

import java.util.UUID;

public interface UserService {
    public User getUserById(UUID userId);
}
