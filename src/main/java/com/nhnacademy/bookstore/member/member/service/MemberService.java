package com.nhnacademy.bookstore.member.member.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.member.enums.Grade;
import com.nhnacademy.bookstore.entity.member.enums.Status;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UpdateMemberRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UpdatePasswordRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UserProfile;
import com.nhnacademy.bookstore.member.memberauth.dto.response.MemberAuthResponse;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;

@Service
public interface MemberService {
	Member save(CreateMemberRequest createMemberRequest);

	Member readById(Long id);

	Member readByEmailAndPassword(String email, String password);

	Member updateMember(Long memberId, UpdateMemberRequest updateMemberRequest);

	Member readByEmail(String email);

	void deleteMember(Long memberId);

	Member updateStatus(Long memberId, Status status);

	Member updateGrade(Long memberId, Grade grade);

	Member updateLastLogin(Long memberId, ZonedDateTime lastLogin);

	List<ReadPurchaseResponse> getPurchasesByMemberId(Long memberId);


	Member updatePassword(Long memberId, UpdatePasswordRequest updatePasswordRequest);
	Boolean isCorrectPassword(Long memberId, String password);
	Member saveOrGetPaycoMember(UserProfile userProfile);

	MemberAuthResponse readByIdForSecurity(Long id);
}
