package org.example.concertTicketing.domain.concert;

import org.example.concertTicketing.domain.concert.dto.response.ConcertResponseDto;
import org.example.concertTicketing.domain.concert.entity.Concert;
import org.example.concertTicketing.domain.concert.repository.ConcertRepository;
import org.example.concertTicketing.domain.concert.service.ConcertService;
import org.example.concertTicketing.domain.seat.repository.SeatRepository;
import org.example.concertTicketing.domain.venue.entity.Venue;
import org.example.concertTicketing.domain.venue.repository.VenueRepository;
import org.example.concertTicketing.scheduler.RedisScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ConcertServiceTest {
    @InjectMocks
    ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private VenueRepository venueRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private HashOperations<String, Object, Object> hashOperations;
    @Mock
    private SetOperations<String, Object> setOperations;
    @Autowired
    private RedisScheduler redisScheduler; // 테스트 클래스에 주입
    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

    }

    @Test
    void 캐시에없으면_DB에서_조회하고_캐시에_저장된다() {
        // given
        Long concertId = 1L;
        String userId = "user-1";
        String redisKey = "concert:" + concertId;

        Venue venue = Venue.builder().name("예술의전당").build();
        Concert concert = Concert.builder()
                .id(concertId)
                .title("아리아나그란데 콘서트")
                .date(LocalDateTime.now())
                .venue(venue)
                .build();

        when(redisTemplate.opsForValue().get(redisKey)).thenReturn(null);
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        when(seatRepository.countByVenue(venue)).thenReturn(100L);
        when(redisTemplate.opsForHash().get("concert:viewcount", concertId.toString())).thenReturn(5);

        // when
        ConcertResponseDto result = concertService.getConcert(concertId, userId);

        // then
        assertThat(result.getConcertName()).isEqualTo("아리아나그란데 콘서트");
        assertThat(result.getRemainingTickets()).isEqualTo(100);
        assertThat(result.getViewCount()).isEqualTo(5);

        verify(redisTemplate.opsForValue(), times(1)).set(eq(redisKey), any(ConcertResponseDto.class), any());

    }

    @Test
    void 캐시에_있으면_DB호출이_없어야함() {
        Long concertId = 1L;
        String concertKey = "concert:" + concertId;

        // 캐시에 미리 저장할 DTO 준비
        ConcertResponseDto cachedDto = ConcertResponseDto.builder()
                .id(concertId)
                .concertName("Cached Concert")
                .date(LocalDateTime.now())
                .venue("Cached Venue")
                .remainingTickets(100L)
                .viewCount(50)
                .build();

        // redisTemplate.opsForValue()의 get, set 동작 mocking
        ValueOperations<String, Object> valueOps = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(concertKey)).thenReturn(cachedDto);

        // DB 호출 시 예외 던지기 (DB 호출이 안 되야 하므로)
        lenient().when(concertRepository.findById(anyLong()))
                .thenThrow(new RuntimeException("DB 호출 금지"));

        // 실제 테스트 대상 호출
        ConcertResponseDto result = concertService.getConcert(concertId, "userIp");

        // 결과 검증
        assertEquals(cachedDto.getId(), result.getId());
        assertEquals(cachedDto.getConcertName(), result.getConcertName());

        // verify로 DB 호출 안 한 거 확인
        verify(concertRepository, never()).findById(anyLong());
    }
    @Test
    void 조회시_조회수_증가_확인() {
        Long concertId = 1L;
        String userIdOrIp = "userIp";

        ConcertService spyService = Mockito.spy(concertService);

        // Redis와 DB mocking
        ValueOperations<String, Object> valueOps = Mockito.mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get("concert:" + concertId)).thenReturn(null);

        Concert concert = Concert.builder()
                .id(concertId)
                .title("Test Concert")
                .date(LocalDateTime.now())
                .venue(Venue.builder().name("Test Venue").build())
                .build();
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(seatRepository.countByVenue(concert.getVenue())).thenReturn(100L);

        // boolean 반환 메서드 mocking
        when(spyService.incrementViewCount(concertId, userIdOrIp)).thenReturn(true);
        doReturn(42).when(spyService).getViewCount(concertId);

        ConcertResponseDto dto = spyService.getConcert(concertId, userIdOrIp);

        verify(spyService).incrementViewCount(concertId, userIdOrIp);
        verify(spyService).getViewCount(concertId);

        assertEquals(42, dto.getViewCount());
        assertEquals(concertId, dto.getId());
        assertEquals("Test Concert", dto.getConcertName());
    }
    @Test
    void 동일_사용자_IP는_조회수_한번만_증가함() {
        Long concertId = 1L;
        String userIdOrIp = "192.168.1.1";
        String userKey = "concert:view:user:" + concertId;

        SetOperations<String, Object> setOps = Mockito.mock(SetOperations.class);
        HashOperations<String, Object, Object> hashOps = Mockito.mock(HashOperations.class);
        ZSetOperations<String, Object> zSetOps = Mockito.mock(ZSetOperations.class);

        when(redisTemplate.opsForSet()).thenReturn(setOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);

        // 첫 호출 - 신규 추가 (조회수 증가)
        when(setOps.add(userKey, userIdOrIp)).thenReturn(1L);
        when(hashOps.increment(anyString(), anyString(), anyLong())).thenReturn(1L);
        when(zSetOps.incrementScore(anyString(), anyString(), anyDouble())).thenReturn(1.0);

        boolean firstIncrement = concertService.incrementViewCount(concertId, userIdOrIp);
        assertTrue(firstIncrement);

        // 두 번째 호출 - 이미 존재 (조회수 증가 안 됨)
        when(setOps.add(userKey, userIdOrIp)).thenReturn(0L);

        boolean secondIncrement = concertService.incrementViewCount(concertId, userIdOrIp);
        assertFalse(secondIncrement);

        verify(setOps, times(2)).add(userKey, userIdOrIp);
        verify(hashOps, times(1)).increment(anyString(), anyString(), anyLong());
        verify(zSetOps, times(1)).incrementScore(anyString(), anyString(), anyDouble());
        verify(redisTemplate, times(1)).expire(anyString(), any(Duration.class));
    }
}