package com.nhnacademy.bookstore.purchase.membermessage.service;

import com.nhnacademy.bookstore.entity.membermessage.MemberMessage;
import com.nhnacademy.bookstore.purchase.membermessage.dto.ReadMemberMessageResponse;
import org.springframework.data.domain.Page;

/**
 * 맴버메시지 서비스 인터페이스.
 *
 * @author 김병우
 */
public interface MemberMessageService {

    /**
     * 맴버메시지 생성.
     *
     * @param memberMessage 맴버메시지
     * @return 멤버메시지아이디
     */
    Long createMessage(MemberMessage memberMessage);

    /**
     * 맴버메시지 전부 읽기.
     *
     * @param memberId 맴버아이디
     * @param page 페이지
     * @param size 페이지 사이즈
     * @return 맴버메시지
     */
    Page<ReadMemberMessageResponse> readAll(Long memberId, int page, int size);

    /**
     * 안읽은메시지 수
     *
     * @param memberId 맴버 아이디
     * @return 롱
     */
    Long countUnreadMessage(Long memberId);

    /**
     * 메시지 읽기.
     *
     * @param memberMessageId 맴버 메시지 아이디
     */
    void readAlarm(Long memberMessageId);
}
