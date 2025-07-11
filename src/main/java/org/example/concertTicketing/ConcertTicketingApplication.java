package org.example.concertTicketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching //캐시사용시 필요한 어노테이션입니다.
@EnableScheduling // 스케줄러 실행 어노테이션입니다.
public class ConcertTicketingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConcertTicketingApplication.class, args);
    }

}
