package com.nhnacademy.bookstore.purchase.membermessage.service.impl;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.membermessage.MemberMessage;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.membermessage.dto.ReadMemberMessageResponse;
import com.nhnacademy.bookstore.purchase.membermessage.exception.MemberMessageDoesNotExistException;
import com.nhnacademy.bookstore.purchase.membermessage.repository.MemberMessageRepository;
import com.nhnacademy.bookstore.purchase.membermessage.service.MemberMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


/**
 * 맴버메시지 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberMessageServiceImpl implements MemberMessageService {
    private final MemberMessageRepository memberMessageRepository;
    private final MemberRepository memberRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createMessage(MemberMessage memberMessage) {
        memberMessageRepository.save(memberMessage);
        return memberMessage.getId();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ReadMemberMessageResponse> readAll(Long memberId, int page, int size) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);

        Pageable pageable = PageRequest.of(page, size);

        return memberMessageRepository.findByMember(member, pageable)
                .map(m -> ReadMemberMessageResponse.builder()
                        .id(m.getId())
                        .message(m.getMessage())
                        .viewAt(m.getViewAt())
                        .sendAt(m.getSendAt())
                        .build()
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countUnreadMessage(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);

        return memberMessageRepository.countByViewAtIsNull(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readAlarm(Long memberMessageId) {
        MemberMessage memberMessage = memberMessageRepository
                .findById(memberMessageId)
                .orElseThrow(()->new MemberMessageDoesNotExistException(memberMessageId+"맴버메시지 아이디가 존재하지 않습니다."));

        memberMessage.setViewAt(ZonedDateTime.now());
        memberMessageRepository.save(memberMessage);
    }
}
