package com.nhnacademy.bookstore.member.member.service;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.dto.response.GetMemberResponse;
import com.nhnacademy.bookstore.member.member.dto.response.ReadMemberResponse;

import java.util.List;

/**
 * 맴버포인트서비스 인터페이스.
 *
 * @author 김병우
 */
public interface MemberPointService {
    Long updatePoint(Long memberId, Long usePoint);
    List<ReadMemberResponse> readAll();
    void welcomePoint(Member member);
}
