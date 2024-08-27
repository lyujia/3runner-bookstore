package com.nhnacademy.bookstore.member.member.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.nhnacademy.bookstore.member.auth.repository.AuthRepository;
import com.nhnacademy.bookstore.member.member.dto.request.UpdatePasswordRequest;
import com.nhnacademy.bookstore.member.member.exception.GeneralNotPayco;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.member.memberauth.repository.MemberAuthRepository;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponMemberService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.member.enums.AuthProvider;
import com.nhnacademy.bookstore.entity.member.enums.Grade;
import com.nhnacademy.bookstore.entity.member.enums.Status;
import com.nhnacademy.bookstore.entity.memberauth.MemberAuth;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UpdateMemberRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UserProfile;
import com.nhnacademy.bookstore.member.member.exception.AlreadyExistsEmailException;
import com.nhnacademy.bookstore.member.member.exception.LoginFailException;
import com.nhnacademy.bookstore.member.member.exception.LoginOauthEmailException;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.member.service.MemberService;
import com.nhnacademy.bookstore.member.memberauth.dto.response.MemberAuthResponse;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;

import lombok.RequiredArgsConstructor;

/**
 * The type Member service.
 *
 * @author 오연수, 유지아
 */
@Transactional
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
	private final MemberRepository memberRepository;
	private final PurchaseRepository purchaseRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthRepository authRepository;
	private final MemberAuthRepository memberAuthRepository;
	/**
	 * 웰컴 쿠폰 구현 서비스, 포인트 서비스
	 */
	private final CouponMemberService couponMemberService;
	private final MemberPointService memberPointService;


	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Member saveOrGetPaycoMember(UserProfile userProfile) {
		Optional<Member> optionalMember = memberRepository.findByEmail(userProfile.getEmail());
		if (optionalMember.isPresent()) {
			if(optionalMember.get().getAuthProvider() == AuthProvider.PAYCO) {
				updateStatus(optionalMember.get().getId(),Status.Active);
				updateLastLogin(optionalMember.get().getId(),ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
				return optionalMember.get();//존재하는경우, 그냥 멤버를 가져온다.
			}else{
				throw new GeneralNotPayco();
			}
		} else {

			Member member = new Member();
			member.setEmail(userProfile.getEmail());
			member.setPassword(passwordEncoder.encode(userProfile.getId()));
			member.setGrade(Grade.General);
			member.setName(userProfile.getName()!=null? userProfile.getName() : "Payco");
			member.setPhone(userProfile.getMobile()!=null? userProfile.getMobile() : "EmptyNumber");
			member.setPoint(5000L);
			member.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
			member.setAuthProvider(AuthProvider.PAYCO);
			member.setLastLoginDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
			member.setStatus(Status.Active);
			memberRepository.save(member);
			//없는경우 새로 가져온다.

			couponMemberService.issueWelcomeCoupon(member);
			memberPointService.welcomePoint(member);
			return member;
		}
	}

	/**
	 * 멤버 아이디를 통해 멤버 찾기
	 * @param id
	 * @return
	 */
	@Override
	public MemberAuthResponse readByIdForSecurity(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(() -> new MemberNotExistsException());
		List<MemberAuth> memberAuthList = member.getMemberAuthList();
		List<String> authList = new ArrayList<>();
		for (MemberAuth memberAuth : memberAuthList) {
			authList.add(memberAuth.getAuth().getName());
		}

		return MemberAuthResponse.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.password(member.getPassword())
			.auth(authList)
			.build();
	}

	/**
	 * Save member.
	 *
	 * @param request the member -Member값을 받아온다.
	 * @return the member -저장 후 member값을 그대로 반환한다.
	 * @author 유지아 Save member. -멤버값을 받아와 저장한다.(이메일 중복하는걸로 확인하면 좋을듯)
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Member save(CreateMemberRequest request) {
		CreateMemberRequest encodedRequest = new CreateMemberRequest(request.email(),
			passwordEncoder.encode(request.password()), request.name(), request.phone(), request.age(),
			request.birthday());
		Member member = new Member(encodedRequest);
		Optional<Member> findmember = memberRepository.findByEmail(member.getEmail());
		if (findmember.isPresent()) {
			throw new AlreadyExistsEmailException();
		}
		memberRepository.save(member);

		couponMemberService.issueWelcomeCoupon(member);
		memberPointService.welcomePoint(member);

		return member;
	}

	/**
	 * Find by id member.
	 *
	 * @param id the id -long형인 memberid를 받는다.
	 * @return the member -member 반환
	 * @author 유지아 Find by id member. -memberid를 받아 멤버자체를 가져온다.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Member readById(Long id) {
		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent()) {
			return member.get();
		} else {
			throw new MemberNotExistsException();
		}
	}

	/**
	 * Find by email and password member.
	 *
	 * @param email    the email -string 이메일 값을 받는다.
	 * @param password the password -string 비밀번호 값을 받는다.
	 * @return the member -해당하는 member를 반환한다.
	 * @author 유지아 Find by email and password member. -이메일과 패스워드 값으로 조회한다.
	 */
	public Member readByEmailAndPassword(String email, String password) {
		Optional<Member> member = memberRepository.findByEmail(email);

		if (member.isPresent()) {
			if (member.get().getAuthProvider() != AuthProvider.GENERAL) {
				throw new LoginOauthEmailException(member.get().getAuthProvider());
			}
			if (passwordEncoder.matches(password, member.get().getPassword())) {
				return member.get();
			}
		}
		throw new LoginFailException();
	}

	public Member readByEmail(String email) {
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isPresent()) {
			return member.get();
		}
		throw new LoginFailException();
	}

	/**
	 * 멤버 업데이트
	 *
	 * @param memberId            the member id
	 * @param updateMemberRequest password, name, age, phone, email, birthday
	 * @return the member
	 * @author 오연수
	 */
	public Member updateMember(Long memberId, UpdateMemberRequest updateMemberRequest) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
		LocalDate birthday = LocalDate.parse(updateMemberRequest.birthday());
		ZonedDateTime zonedBirthday = birthday.atStartOfDay(ZoneId.systemDefault());
		member.setName(updateMemberRequest.name());
		member.setAge(updateMemberRequest.age());
		member.setPhone(updateMemberRequest.phone());
		member.setBirthday(zonedBirthday);
		member.setModifiedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));

		return memberRepository.save(member);
	}

	/**
	 * 멤버 탈퇴
	 *
	 * @param memberId the member id
	 * @author 오연수
	 */
	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);

		member.setStatus(Status.Withdrawn);
		member.setDeletedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
		member.setPhone("EmptyNumber");
		member.setName("EmptyName");
		member.setPassword("EmptyPassword");
		member.setBirthday(null);
		member.setAge(0);



		memberRepository.save(member);
	}

	/**
	 * Update member's status(활성, 탈퇴, 휴면).
	 *
	 * @param memberId the member id
	 * @param status   the status
	 * @return the member
	 * @author 오연수
	 */
	public Member updateStatus(Long memberId, Status status) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
		member.setStatus(status);
		member.setModifiedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
		return memberRepository.save(member);
	}

	/**
	 * Update member's grade(general, royal, gold, platinum).
	 *
	 * @param memberId the member id
	 * @param grade    the grade
	 * @return the member
	 * @author 오연수
	 */
	public Member updateGrade(Long memberId, Grade grade) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
		member.setGrade(grade);
		member.setModifiedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
		return memberRepository.save(member);
	}

	@Override
	public Member updateLastLogin(Long memberId, ZonedDateTime lastLogin) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
		member.setLastLoginDate(lastLogin);
		return memberRepository.save(member);
	}

	/**
	 * 주문 리스트 조회 멤버.
	 *
	 * @param memberId 맴버아이디
	 * @return 리스트
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<ReadPurchaseResponse> getPurchasesByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(MemberNotExistsException::new);
		return purchaseRepository.findByMember(member)
			.stream()
			.map(purchase -> ReadPurchaseResponse.builder()
				.id(purchase.getId())
				.orderNumber(purchase.getOrderNumber())
				.status(purchase.getStatus())
				.deliveryPrice(purchase.getDeliveryPrice())
				.totalPrice(purchase.getTotalPrice())
				.createdAt(purchase.getCreatedAt())
				.road(purchase.getRoad())
				.password(purchase.getPassword())
				.memberType(purchase.getMemberType())
				.build()
			).collect(Collectors.toList());
	}

	public Member updatePassword(Long memberId, UpdatePasswordRequest updatePasswordRequest) {
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
		member.setPassword(passwordEncoder.encode(updatePasswordRequest.password()));
		member.setModifiedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
		return memberRepository.save(member);
	}
	public Boolean isCorrectPassword(Long memberId, String password){
		Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
		return passwordEncoder.matches(password, member.getPassword());

	}
}