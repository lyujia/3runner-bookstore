package com.nhnacademy.bookstore.member.memberauth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.memberauth.MemberAuth;
import com.nhnacademy.bookstore.member.memberauth.repository.MemberAuthRepository;
import com.nhnacademy.bookstore.member.memberauth.service.impl.MemberAuthServiceImpl;

/**
 * MemberAuthService 에 대한 테스트 입니다.
 *
 * @author 오연수
 */
public class MemberAuthServiceTest {
	@Mock
	private MemberAuthRepository memberAuthRepository;

	@InjectMocks
	private MemberAuthServiceImpl memberAuthService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("멤버의 권한들을 모두 가져오는 경우 테스트")
	void testReadAllAuths() {
		Long memberId = 1L;
		Auth auth1 = new Auth();
		Auth auth2 = new Auth();
		List<Auth> authList = List.of(auth1, auth2);

		when(memberAuthRepository.findByMemberId(memberId)).thenReturn(authList);

		List<Auth> result = memberAuthService.readAllAuths(memberId);

		assertEquals(2, result.size());
		assertEquals(authList, result);
		verify(memberAuthRepository, times(1)).findByMemberId(memberId);
	}

	@Test
	@DisplayName("멤버의 권한을 저장(추가)하는 경우 테스트")
	void testSaveAuth() {
		Member member = new Member();
		Auth auth = new Auth();
		MemberAuth memberAuth = new MemberAuth();
		memberAuth.setMember(member);
		memberAuth.setAuth(auth);

		when(memberAuthRepository.save(any(MemberAuth.class))).thenReturn(memberAuth);

		memberAuthService.saveAuth(member, auth);

		verify(memberAuthRepository, times(1)).save(any(MemberAuth.class));
	}
}
