package com.AbsoluteValue.RESTful.common.auth.repository;

import com.AbsoluteValue.RESTful.common.auth.token.RefreshTokenState;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
@RequiredArgsConstructor
public class RedisRefreshTokenStore implements RefreshTokenStore {

    private static final String KEY_PREFIX = "restful:refresh:";
    private static final DefaultRedisScript<Long> ROTATE_SCRIPT = script("""
            local current = redis.call('GET', KEYS[1])
            if not current then return 0 end
            if current ~= ARGV[1] then
                redis.call('DEL', KEYS[1])
                return 0
            end
            redis.call('SET', KEYS[1], ARGV[2], 'PXAT', ARGV[3])
            return 1
            """);
    private static final DefaultRedisScript<Long> REVOKE_SCRIPT = script("""
            if redis.call('GET', KEYS[1]) ~= ARGV[1] then return 0 end
            redis.call('DEL', KEYS[1])
            return 1
            """);

    private final StringRedisTemplate redisTemplate;
    private final Clock clock;

    @Override
    public void save(RefreshTokenState token) {
        Duration lifetime = Duration.between(clock.instant(), token.expiresAt());
        if (lifetime.isNegative() || lifetime.isZero()) {
            throw new IllegalStateException("Refresh token has already expired");
        }
        if (!Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(
                key(token.familyId()), value(token), lifetime))) {
            throw new IllegalStateException("Refresh token family already exists");
        }
    }

    @Override
    public boolean rotate(RefreshTokenState presented, RefreshTokenState replacement) {
        return Long.valueOf(1).equals(redisTemplate.execute(
                ROTATE_SCRIPT,
                List.of(key(presented.familyId())),
                value(presented),
                value(replacement),
                Long.toString(replacement.expiresAt().toEpochMilli())
        ));
    }

    @Override
    public boolean revoke(RefreshTokenState presented) {
        return Long.valueOf(1).equals(redisTemplate.execute(
                REVOKE_SCRIPT,
                List.of(key(presented.familyId())),
                value(presented)
        ));
    }

    private static String key(String familyId) {
        return KEY_PREFIX + familyId;
    }

    private static String value(RefreshTokenState token) {
        String userId = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(token.userId().getBytes(StandardCharsets.UTF_8));
        return token.tokenId() + ":" + userId + ":" + token.tokenHash();
    }

    private static DefaultRedisScript<Long> script(String source) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(source);
        script.setResultType(Long.class);
        return script;
    }
}
