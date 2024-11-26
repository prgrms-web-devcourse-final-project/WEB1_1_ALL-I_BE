package com.JAI.chatbot;

import com.JAI.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RedisChatbotUtil {

    private final RedisUtil redisUtil;

    private static final String TOKEN_PREFIX = "chatbot:input:";

    public void saveRefreshToken(String email, String refreshToken, long ttlSeconds) {
        redisUtil.save(TOKEN_PREFIX + email, refreshToken, ttlSeconds);
    }

    public String getRefreshToken(String email) {
        return redisUtil.get(TOKEN_PREFIX + email);
    }

    public void deleteRefreshToken(String email) {
        redisUtil.delete(TOKEN_PREFIX + email);
    }

    public Long getRefreshTokenTTL(String email) {
        return redisUtil.getTTL(TOKEN_PREFIX + email);
    }

}

