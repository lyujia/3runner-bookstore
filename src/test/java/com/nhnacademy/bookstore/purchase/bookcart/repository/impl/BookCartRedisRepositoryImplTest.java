package com.nhnacademy.bookstore.purchase.bookcart.repository.impl;

import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookCartRedisRepositoryImplTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private BookCartRedisRepositoryImpl bookCartRedisRepository;

    @Test
    void create() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String hashName = "cart";
        Long id = 1L;
        ReadBookCartGuestResponse response = ReadBookCartGuestResponse.builder()
                .bookId(1L)
                .url("daf")
                .bookCartId(1L)
                .quantity(1)
                .leftQuantity(3)
                .price(100)
                .title("title")
                .build();

        bookCartRedisRepository.create(hashName, id, response);

        verify(hashOperations, times(1)).put(hashName + ":", id.toString(), response);
    }

    @Test
    void update() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String hashName = "cart";
        Long id = 1L;
        ReadBookCartGuestResponse existingResponse = ReadBookCartGuestResponse.builder()
                .bookId(1L)
                .url("daf")
                .bookCartId(1L)
                .quantity(1)
                .leftQuantity(3)
                .price(100)
                .title("title")
                .build();
        when(hashOperations.get(hashName+ ":", id.toString())).thenReturn(existingResponse);

        bookCartRedisRepository.update(hashName, id, 3);

    }

    @Test
    void delete() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String hashName = "cart";
        Long id = 1L;

        bookCartRedisRepository.delete(hashName, id);

        verify(hashOperations, times(1)).delete(hashName + ":", id.toString());
    }

    @Test
    void readAllHashName() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String hashName = "cart";
        ReadBookCartGuestResponse response1 = ReadBookCartGuestResponse.builder()
                .bookId(1L)
                .url("daf")
                .bookCartId(1L)
                .quantity(1)
                .leftQuantity(3)
                .price(100)
                .title("title")
                .build();
        ReadBookCartGuestResponse response2 = ReadBookCartGuestResponse.builder()
                .bookId(1L)
                .url("daf")
                .bookCartId(1L)
                .quantity(1)
                .leftQuantity(3)
                .price(100)
                .title("title")
                .build();

        when(hashOperations.values(hashName + ":")).thenReturn(List.of(response1, response2));

        List<ReadBookCartGuestResponse> result = bookCartRedisRepository.readAllHashName(hashName);

        assertThat(result).containsExactlyInAnyOrder(response1, response2);
      }

    @Test
    void isHit() {
        String hashName = "cart";

        when(redisTemplate.hasKey(hashName + ":")).thenReturn(true);

        boolean result = bookCartRedisRepository.isHit(hashName);

        assertThat(result).isTrue();
    }

    @Test
    void isMiss() {
        String hashName = "cart";

        when(redisTemplate.hasKey(hashName + ":")).thenReturn(false);

        boolean result = bookCartRedisRepository.isMiss(hashName);

        assertThat(result).isTrue();
    }

    @Test
    void loadData() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        String hashName = "cart";

        ReadBookCartGuestResponse response1 = ReadBookCartGuestResponse.builder()
                .bookId(1L)
                .url("daf")
                .bookCartId(1L)
                .quantity(1)
                .leftQuantity(3)
                .price(100)
                .title("title")
                .build();
        ReadBookCartGuestResponse response2 = ReadBookCartGuestResponse.builder()
                .bookId(1L)
                .url("daf")
                .bookCartId(1L)
                .quantity(1)
                .leftQuantity(3)
                .price(100)
                .title("title")
                .build();

        bookCartRedisRepository.loadData(List.of(response1, response2), hashName);

    }

    @Test
    void deleteAll() {
        String hashName = "cart";

        bookCartRedisRepository.deleteAll(hashName);

        verify(redisTemplate, times(1)).delete(hashName + ":");
    }
}