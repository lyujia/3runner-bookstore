package com.nhnacademy.bookstore.purchase.bookcart.repository;

import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;

import java.util.List;

/**
 * 도서장바구니 레디스 저장소 인터페이스.
 *
 * @author 김병우
 */
public interface BookCartRedisRepository {
    Long create(String hashName, Long id, ReadBookCartGuestResponse readBookCartGuestResponse);

    Long update(String hashName, Long id, int quantity);

    Long delete(String hashName, Long id);

    void deleteAll(String hashName);

    List<ReadBookCartGuestResponse> readAllHashName(String hashName);

    boolean isHit(String hashName);

    boolean isMiss(String hashName);

    void loadData(List<ReadBookCartGuestResponse> bookCartGuestResponses,String hashName);
}
