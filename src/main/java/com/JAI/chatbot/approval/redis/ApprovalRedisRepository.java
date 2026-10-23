package com.JAI.chatbot.approval.redis;

import com.JAI.chatbot.controller.dto.ChatbotRedisDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * A bounded lease reduces concurrent DB work; MySQL UNIQUE is authoritative.
 * Redis Lua keys contain the same {token} hash tag for Redis Cluster safety.
 */
@Component
@RequiredArgsConstructor
public class ApprovalRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public static final long LEASE_MILLIS = 120_000L;

    private static final DefaultRedisScript<List> CLAIM_SCRIPT = script("""
        local payload = redis.call('GET', KEYS[1])
        if not payload then return {'MISSING'} end
        local data = cjson.decode(payload)
        if data.userId ~= ARGV[1] then return {'FORBIDDEN'} end
        if data.token ~= ARGV[4] then return {'MISSING'} end
        if data.approvalState ~= 'READY' then return {'NOT_READY'} end
        if not data.chatbotReqDTO or data.chatbotReqDTO == cjson.null then return {'NOT_READY'} end
        local intent = data.chatbotReqDTO.intention
        if intent == 'EVENT' then
          if not data.chatbotEventRespDTO or cjson.null == data.chatbotEventRespDTO then
            return {'NOT_READY'}
          end
        elseif intent == 'TODO' or intent == 'PLAN_RECOMMENDATION' then
          if not data.chatbotTodoRespDTO or cjson.null == data.chatbotTodoRespDTO then
            return {'NOT_READY'}
          end
        else
          return {'NOT_READY'}
        end
        local ok = redis.call('SET', KEYS[2], ARGV[2], 'NX', 'PX', ARGV[3])
        if not ok then return {'BUSY'} end
        return {'ACQUIRED', payload}
        """);

    private static final DefaultRedisScript<Long> RELEASE_SCRIPT = longScript("""
        if redis.call('GET', KEYS[1]) ~= ARGV[1] then return 0 end
        return redis.call('DEL', KEYS[1])
        """);

    private static DefaultRedisScript<List> script(String lua) {
        DefaultRedisScript<List> result = new DefaultRedisScript<>();
        result.setScriptText(lua);
        result.setResultType(List.class);
        return result;
    }

    private static DefaultRedisScript<Long> longScript(String lua) {
        DefaultRedisScript<Long> result = new DefaultRedisScript<>();
        result.setScriptText(lua);
        result.setResultType(Long.class);
        return result;
    }

    public enum Status { ACQUIRED, BUSY, MISSING, FORBIDDEN, NOT_READY }

    public record Claim(Status status, ChatbotRedisDataDTO proposal) {}

    public Claim claim(String token, UUID userId, UUID leaseOwner) {
        List<?> raw = redisTemplate.execute(CLAIM_SCRIPT,
                List.of(proposalKey(token), leaseKey(token)),
                userId.toString(), leaseOwner.toString(), String.valueOf(LEASE_MILLIS), token);
        if (raw == null || raw.isEmpty()) {
            throw new IllegalStateException("Redis approval claim returned no result");
        }
        Status status = Status.valueOf(raw.get(0).toString());
        if (status != Status.ACQUIRED) return new Claim(status, null);
        try {
            return new Claim(status, objectMapper.readValue(raw.get(1).toString(), ChatbotRedisDataDTO.class));
        } catch (JsonProcessingException e) {
            // The lease will expire, and never authorizes a DB commit without valid data.
            release(token, leaseOwner);
            throw new IllegalStateException("Invalid approval proposal", e);
        }
    }

    public boolean cleanupAfterCommit(String token, UUID leaseOwner) {
        // Inline Long result: cleanup cannot erase a newly acquired lease.
        Long result = redisTemplate.execute(CLEANUP_LONG_SCRIPT,
                List.of(proposalKey(token), leaseKey(token)), leaseOwner.toString());
        return result != null && result == 1L;
    }

    private static final DefaultRedisScript<Long> CLEANUP_LONG_SCRIPT = longScript("""
        if redis.call('GET', KEYS[2]) ~= ARGV[1] then return 0 end
        redis.call('DEL', KEYS[1], KEYS[2])
        return 1
        """);

    public void release(String token, UUID leaseOwner) {
        redisTemplate.execute(RELEASE_SCRIPT, List.of(leaseKey(token)), leaseOwner.toString());
    }

    public static String proposalKey(String token) {
        return "approval:{" + token + "}:proposal";
    }

    private static String leaseKey(String token) {
        return "approval:{" + token + "}:lease";
    }
}
