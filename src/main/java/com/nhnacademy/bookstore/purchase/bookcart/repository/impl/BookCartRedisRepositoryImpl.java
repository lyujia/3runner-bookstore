package com.nhnacademy.bookstore.purchase.bookcart.repository.impl;

import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.bookcart.repository.BookCartRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 도서장바구니 레디스 저장소 구현체.
 *
 * @author 김병우
 */
@Repository
@RequiredArgsConstructor
public class BookCartRedisRepositoryImpl implements BookCartRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Long create(String hashName, Long id, ReadBookCartGuestResponse readBookCartGuestResponse) {
        redisTemplate.opsForHash().put(hashName + ":", id.toString(), readBookCartGuestResponse);
        redisTemplate.expire(hashName + ":", 1, TimeUnit.HOURS);
        return id;
    }

    @Override
    public Long update(String hashName, Long id, int quantity) {
        ReadBookCartGuestResponse response = (ReadBookCartGuestResponse)redisTemplate.opsForHash().get(hashName + ":", id.toString());

        ReadBookCartGuestResponse updatedResponse = ReadBookCartGuestResponse.builder()
                .bookCartId(response.bookCartId())
                .bookId(response.bookId())
                .price(response.price())
                .url(response.url())
                .title(response.title())
                .quantity(quantity)
                .leftQuantity(response.leftQuantity())
                .build();

        redisTemplate.opsForHash().put(hashName + ":", id.toString(), updatedResponse);
        redisTemplate.expire(hashName + ":", 1, TimeUnit.HOURS);
        return id;
    }


    @Override
    public Long delete(String hashName, Long id) {
        redisTemplate.opsForHash().delete(hashName + ":", id.toString());
        return id;
    }

    @Override
    public void deleteAll(String hashName) {
        redisTemplate.delete(hashName + ":");
    }


    @Override
    public List<ReadBookCartGuestResponse> readAllHashName(String hashName) {
        List<Object> bookCartList = redisTemplate.opsForHash().values(hashName + ":");

        return  bookCartList
                .stream()
                .map(ReadBookCartGuestResponse.class::cast)
                .toList();
    }

    @Override
    public boolean isHit(String hashName) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(hashName + ":"));
    }

    @Override
    public boolean isMiss(String hashName) {
        return Boolean.FALSE.equals(redisTemplate.hasKey(hashName + ":"));
    }

    @Override
    public void loadData(List<ReadBookCartGuestResponse> bookCartGuestResponses, String hashName) {
        for (ReadBookCartGuestResponse o : bookCartGuestResponses) {
            if (Objects.nonNull(o)) {
                redisTemplate.opsForHash().put(hashName + ":", o.bookCartId().toString(), o);
            }
        }
        redisTemplate.expire(hashName + ":", 1, TimeUnit.HOURS);
    }
}
