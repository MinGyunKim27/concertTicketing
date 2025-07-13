package org.example.concertTicketing.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.concertTicketing.domain.common.annotation.MultiLock;
import org.example.concertTicketing.domain.ticket.dto.request.TicketReserveRequestDto;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class MultiLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(multiLockAnn)")
    public Object applyMultiLock(ProceedingJoinPoint joinPoint, MultiLock multiLockAnn) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        String prefix = multiLockAnn.prefix();
        long timeout = multiLockAnn.timeout();

        Long concertId = extractConcertId(args);
        System.out.println("concertId: " + concertId);
        List<Long> seatIds = extractSeatIds(args);

        if (concertId == null || seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("concertId 또는 seatIds를 추출할 수 없습니다.");
        }

        // 좌석별 락 객체 생성
        List<RLock> locks = seatIds.stream()
                .map(seatId -> redissonClient.getLock(prefix + ":" + concertId + ":seat:" + seatId))
                .toList();

        RLock multiLock = new RedissonMultiLock(locks.toArray(new RLock[0]));

        boolean locked = false;
        try {
            locked = multiLock.tryLock(0, timeout, TimeUnit.SECONDS);
            if (!locked) {
                throw new IllegalStateException("좌석 중 일부가 현재 예약 중입니다.");
            }

            // 락 획득 후 비즈니스 로직 실행
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 처리 중 인터럽트 발생", e);
        } finally {
            if (locked) {
                multiLock.unlock();
            }
        }
    }


    private Long extractConcertId(Object[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Long && i == 1) {
                return (Long) args[i]; // 두 번째 Long = concertId
            }
        }
        return null;
    }

    private List<Long> extractSeatIds(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg instanceof TicketReserveRequestDto)
                .map(arg -> ((TicketReserveRequestDto) arg).seatIds())
                .findFirst()
                .orElse(null);
    }

}
