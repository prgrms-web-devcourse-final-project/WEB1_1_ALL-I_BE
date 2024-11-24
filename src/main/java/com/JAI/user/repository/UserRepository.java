package com.JAI.user.repository;

import com.JAI.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);
}
