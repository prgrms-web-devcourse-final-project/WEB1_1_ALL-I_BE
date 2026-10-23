package com.JAI.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

//Redis 공통 유틸
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    //레디스 저장
    public void save(String key, String value, long ttlSeconds){
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    // 원자적인 SET NX EX: READY 스냅샷을 한 번만 확정합니다.
    public boolean saveIfAbsent(String key, String value, long ttlSeconds) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS));
    }

    //레디스 조회
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    //레디스 삭제
    public void delete(String key){
        redisTemplate.delete(key);
    }

    //TTL 조회
    public Long getTTL(String key){
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    //키 존재 여부 확인
    public boolean hasKey(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
