package org.example.concertTicketing.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
// 자정에 초기화 시켜주는 클래스
@Component
@RequiredArgsConstructor
public class RedisScheduler {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(RedisScheduler.class);

    private static final String VIEW_COUNT_KEY = "concert:viewcount";
    private static final String RANK_KEY = "concert:rank";

    // 매일 자정(00:00:00)에 실행
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 */1 * * * *") // 매 1분마다 실행 (테스트용)
    public void resetViewCountAndRank() {
        redisTemplate.delete(VIEW_COUNT_KEY);
        redisTemplate.delete(RANK_KEY);
        log.info("Redis 조회수와 랭킹 초기화 완료 - 자정 스케줄러 실행");
    }
}
