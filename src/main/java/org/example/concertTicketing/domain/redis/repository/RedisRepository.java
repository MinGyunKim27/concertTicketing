package org.example.concertTicketing.domain.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final StringRedisTemplate redisTemplate;

    public Boolean tryLock(String key, String value, long timeoutMillis) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(timeoutMillis));
    }

    public void releaseLock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }

}
