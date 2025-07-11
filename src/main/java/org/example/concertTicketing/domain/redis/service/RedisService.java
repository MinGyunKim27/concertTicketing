package org.example.concertTicketing.domain.redis.service;

import lombok.RequiredArgsConstructor;
import org.example.concertTicketing.domain.redis.repository.RedisRepository;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisRepository RedisRepository;

    public boolean lock(String key, String value, long timeoutMillis) {
        return Boolean.TRUE.equals(RedisRepository.tryLock(key, value, timeoutMillis));
    }

    public void unlock(String key, String value) {
        RedisRepository.releaseLock(key, value);
    }

    public void lockSeats(Long userId, Long concertId, List<Long> seatIds) {
        String value = String.valueOf(userId);
        for (Long seatId: seatIds) {
            String key = "lock:concert:" + concertId + ":seat:" + seatId;
            boolean locked = lock(key, value, 5 * 60 * 1000);
            if (!locked) {
                throw new IllegalStateException("이미 선택된 좌석입니다 : " + seatId);
            }
        }
    }
}
