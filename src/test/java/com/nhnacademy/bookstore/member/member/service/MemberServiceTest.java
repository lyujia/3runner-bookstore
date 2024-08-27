package com.nhnacademy.bookstore.member.member.service;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.member.enums.AuthProvider;
import com.nhnacademy.bookstore.entity.member.enums.Grade;
import com.nhnacademy.bookstore.entity.member.enums.Status;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UpdateMemberRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UpdatePasswordRequest;
import com.nhnacademy.bookstore.member.member.dto.request.UserProfile;
import com.nhnacademy.bookstore.member.member.exception.AlreadyExistsEmailException;
import com.nhnacademy.bookstore.member.member.exception.GeneralNotPayco;
import com.nhnacademy.bookstore.member.member.exception.LoginFailException;
import com.nhnacademy.bookstore.member.member.exception.LoginOauthEmailException;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.member.member.service.impl.MemberServiceImpl;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponMemberService;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CouponMemberService couponMemberService;

    @Mock
    private MemberPointService memberPointService;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private Member member2;
    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPassword("encodedPassword");
        member.setAuthProvider(AuthProvider.GENERAL);

        member2 = new Member();
        member2.setId(2L);
        member2.setEmail("test2@example.com");
        member2.setPassword("encodedPassword");
        member2.setAuthProvider(AuthProvider.PAYCO);
        member.setStatus(Status.Active);
        member.setLastLoginDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
    }


    @Test
    void testSaveOrGetPaycoMember_NewMember() {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("new@example.com");
        userProfile.setId("payco-id");
        userProfile.setName("New User");

        when(memberRepository.findByEmail(userProfile.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userProfile.getId())).thenReturn("encodedPassword");

        Member result = memberService.saveOrGetPaycoMember(userProfile);

        assertNotNull(result);
        verify(memberRepository, times(1)).findByEmail(userProfile.getEmail());
        verify(memberRepository, times(1)).save(ArgumentMatchers.any(Member.class));
    }

    @Test
    void testSave() {
        CreateMemberRequest request = new CreateMemberRequest("new@example.com", "password", "New User", "1234567890", 25, "1996-01-01");

        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        Member result = memberService.save(request);

        assertNotNull(result);
        verify(memberRepository, times(1)).findByEmail(request.email());
        verify(memberRepository, times(1)).save(ArgumentMatchers.any(Member.class));
    }

    @Test
    void testReadById() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.readById(1L);

        assertEquals(member, result);
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void testReadByEmailAndPassword_Success() {
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password", member.getPassword())).thenReturn(true);

        Member result = memberService.readByEmailAndPassword("test@example.com", "password");

        assertEquals(member, result);
        verify(memberRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testReadByEmailAndPassword_Failure() {
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrongPassword", member.getPassword())).thenReturn(false);

        assertThrows(LoginFailException.class, () -> {
            memberService.readByEmailAndPassword("test@example.com", "wrongPassword");
        });
    }



    @Test
    void testDeleteMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.deleteMember(1L);

        assertEquals(Status.Withdrawn, member.getStatus());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdateStatus() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        Member result = memberService.updateStatus(1L, Status.Inactive);

        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdateGrade() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.updateGrade(1L, Grade.Royal);

        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdateLastLogin() {
        ZonedDateTime lastLogin = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.updateLastLogin(1L, lastLogin);

        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testUpdatePassword() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest("newPassword");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(passwordEncoder.encode(updatePasswordRequest.password())).thenReturn("encodedNewPassword");

        Member result = memberService.updatePassword(1L, updatePasswordRequest);

        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void testIsCorrectPassword() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("password", member.getPassword())).thenReturn(true);

        Boolean result = memberService.isCorrectPassword(1L, "password");

        assertTrue(result);
    }
    @Test
    void testReadByEmail_Success() {
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

        Member result = memberService.readByEmail("test@example.com");

        assertEquals(member, result);
        verify(memberRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testReadByEmail_Failure() {
        when(memberRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(LoginFailException.class, () -> {
            memberService.readByEmail("nonexistent@example.com");
        });

        verify(memberRepository, times(1)).findByEmail("nonexistent@example.com");
    }
    @Test
    void testUpdateMember() {
        UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                .name("Updated Name")
                .age(30)
                .phone("010-1234-5678")
                .birthday("2022-04-22")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member updatedMember = memberService.updateMember(1L, updateMemberRequest);

        assertNotNull(updatedMember);
        assertEquals("Updated Name", updatedMember.getName());
        assertEquals(30, updatedMember.getAge());
        assertEquals("010-1234-5678", updatedMember.getPhone());

        verify(memberRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).save(member);
    }


    @Test
    void testSaveOrGetPaycoMember_ExistingNonPaycoMember() {
        member.setAuthProvider(AuthProvider.GENERAL);
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("payco@example.com");
        userProfile.setId("payco-id");
        userProfile.setName("Payco User");

        when(memberRepository.findByEmail(userProfile.getEmail())).thenReturn(Optional.of(member));

        assertThrows(GeneralNotPayco.class, () -> memberService.saveOrGetPaycoMember(userProfile));
    }
    @Test
    void testSave_AlreadyExistsEmail() {
        CreateMemberRequest request = new CreateMemberRequest("test@example.com", "password", "Test User", "010-0000-0000", 25, "1996-01-01");

        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.of(member));

        assertThrows(AlreadyExistsEmailException.class, () -> memberService.save(request));

        verify(memberRepository, times(1)).findByEmail(request.email());
        verify(memberRepository, never()).save(any(Member.class));
        verify(couponMemberService, never()).issueWelcomeCoupon(any(Member.class));
        verify(memberPointService, never()).welcomePoint(any(Member.class));
    }

}
