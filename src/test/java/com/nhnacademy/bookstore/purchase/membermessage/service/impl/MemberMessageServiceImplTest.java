package com.nhnacademy.bookstore.purchase.membermessage.service.impl;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.membermessage.MemberMessage;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.membermessage.dto.ReadMemberMessageResponse;
import com.nhnacademy.bookstore.purchase.membermessage.exception.MemberMessageDoesNotExistException;
import com.nhnacademy.bookstore.purchase.membermessage.repository.MemberMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class MemberMessageServiceImplTest {

    @Mock
    private MemberMessageRepository memberMessageRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberMessageServiceImpl memberMessageService;

    private Member member;
    private MemberMessage memberMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setId(1L);
        member.setBirthday(ZonedDateTime.now());
        member.setPassword("asdaff");
        member.setEmail("test@example.com");
        member.setName("user");

        memberMessage = new MemberMessage();
        memberMessage.setViewAt(ZonedDateTime.now());
        memberMessage.setMember(member);
        memberMessage.setId(1L);
        memberMessage.setMessage("testMesgae");
    }

    @Test
    void testCreateMessage() {
        given(memberMessageRepository.save(any(MemberMessage.class))).willReturn(memberMessage);

        Long messageId = memberMessageService.createMessage(memberMessage);

        assertEquals(memberMessage.getId(), messageId);
        verify(memberMessageRepository).save(memberMessage);
    }

    @Test
    void testReadAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberMessage> memberMessages = new PageImpl<>(List.of(memberMessage));

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(memberMessageRepository.findByMember(any(Member.class), any(Pageable.class))).willReturn(memberMessages);

        Page<ReadMemberMessageResponse> responses = memberMessageService.readAll(member.getId(), 0, 10);

        assertEquals(1, responses.getTotalElements());
        ReadMemberMessageResponse response = responses.getContent().get(0);
        assertEquals(memberMessage.getId(), response.id());
        assertEquals(memberMessage.getMessage(), response.message());
    }

    @Test
    void testCountUnreadMessage() {
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(memberMessageRepository.countByViewAtIsNull(any(Member.class))).willReturn(1L);

        Long count = memberMessageService.countUnreadMessage(member.getId());

        assertEquals(1L, count);
    }

    @Test
    void testReadAlarm() {
        given(memberMessageRepository.findById(anyLong())).willReturn(Optional.of(memberMessage));

        memberMessageService.readAlarm(memberMessage.getId());

        assertNotNull(memberMessage.getViewAt());
        verify(memberMessageRepository).save(memberMessage);
    }

    @Test
    void testReadAlarm_MessageDoesNotExist() {
        given(memberMessageRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(MemberMessageDoesNotExistException.class, () -> memberMessageService.readAlarm(1L));
    }
}