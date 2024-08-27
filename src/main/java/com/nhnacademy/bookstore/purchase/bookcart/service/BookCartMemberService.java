package com.nhnacademy.bookstore.purchase.bookcart.service;

import java.util.List;

import com.nhnacademy.bookstore.purchase.bookcart.dto.request.*;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadAllBookCartMemberResponse;

/**
 * 북카트 회원 서비스 인터페이스.
 *
 * @author 정주혁
 * fix : 김병우
 */
public interface BookCartMemberService {
	/**
	 * 회원 북카트 읽기.
	 *
	 * @param readAllCartMemberRequest 북카트 요청 dto.
	 * @return 북카트 응답 dto.
	 */
	List<ReadAllBookCartMemberResponse> readAllCartMember(ReadAllBookCartMemberRequest readAllCartMemberRequest);

	/**
	 * 북카트 회원 생성.
	 *
	 * @param createBookCartRequest 북카트 생성 요청 dto.
	 * @return 북카트 아이디
	 */
	Long createBookCartMember(CreateBookCartRequest createBookCartRequest);

	/**
	 * 북카트 회원 업데이트.
	 *
	 * @param updateBookCartRequest 북카트 업데이트 요청 dto.
	 * @param memberId 맴버아이디
	 * @return 북카트 아이디
	 */
	Long updateBookCartMember(UpdateBookCartRequest updateBookCartRequest, Long memberId);

	/**
	 * 북카트 회원 삭제.
	 *
	 * @param deleteBookCartMemberRequest 북카트 삭제 요청 dto.
	 * @param memberId 맴버아이디
	 * @return 북카트아이디
	 */
	Long deleteBookCartMember(DeleteBookCartRequest deleteBookCartMemberRequest, Long memberId);

	/**
	 * 북카트 전체 삭제
	 *
	 * @param memberId 맴버아이디
	 * @return 카트아이디
	 */
	Long deleteAllBookCart(Long memberId);
}
