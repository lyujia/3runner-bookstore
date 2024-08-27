package com.nhnacademy.bookstore.purchase.bookcart.service;

import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import java.util.List;

/**
 * 도서장바구니 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface BookCartGuestService {

    /**
     * 북카트 생성.
     *
     * @param bookId 북아이디
     * @param cartId 카트아이디
     * @param quantity 수량
     * @return 북카트아이디
     */
    Long createBookCart(Long bookId, Long cartId, int quantity);

    /**
     * 북카트 업데이트.
     *
     * @param bookId 북아이디
     * @param cartId 카트아이디
     * @param quantity 수량
     * @return 북카트아이디
     */
    Long updateBookCart(Long bookId, Long cartId, int quantity);

    /**
     * 북카트 읽기.
     *
     * @param cartId 카트아이디
     * @return 응답Dto
     */
    List<ReadBookCartGuestResponse> readAllBookCart(Long cartId);

    /**
     * 북카트 삭제.
     *
     * @param bookCartId 북카트 아이디
     * @param cartId 카트 아이디
     * @return 북카트아이디
     */
    Long deleteBookCart(Long bookCartId, Long cartId);

    /**
     * 카트 삭제
     *
     * @param cartId 카트아이디
     * @return 북카트아이디
     */
    Long deleteAllBookCart(Long cartId);
}
