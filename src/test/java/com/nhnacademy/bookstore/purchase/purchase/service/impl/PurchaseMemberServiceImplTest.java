package com.nhnacademy.bookstore.purchase.purchase.service.impl;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.member.member.service.MemberService;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseMemberRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseAlreadyExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseNoAuthorizationException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PurchaseMemberServiceImplTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private PurchaseMemberServiceImpl purchaseMemberService;

	private Member member;
	private Purchase purchase;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		member = new Member();
		member.setId(1L);

		purchase = new Purchase();
		purchase.setId(1L);
		purchase.setOrderNumber(UUID.randomUUID());
		purchase.setStatus(PurchaseStatus.COMPLETED);
		purchase.setDeliveryPrice(5000);
		purchase.setTotalPrice(15000);
		purchase.setCreatedAt(ZonedDateTime.now());
		purchase.setRoad("우리집");
		purchase.setPassword("password");
		purchase.setMemberType(MemberType.MEMBER);
		purchase.setMember(member);
	}

	@Test
	void testCreatePurchase() {
		CreatePurchaseRequest request = CreatePurchaseRequest.builder()
			.orderId(UUID.randomUUID().toString())
			.deliveryPrice(3000)
			.totalPrice(10000)
			.password("member")
			.road("우리집")
			.build();

		when(memberService.readById(anyLong())).thenReturn(member);
		when(purchaseRepository.existsPurchaseByOrderNumber(any(UUID.class))).thenReturn(false);
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		Long id = purchaseMemberService.createPurchase(request, 1L);

	}

	@Test
	void testCreatePurchase_ThrowsPurchaseAlreadyExistException() {
		CreatePurchaseRequest request = CreatePurchaseRequest.builder()
			.orderId(UUID.randomUUID().toString())
			.deliveryPrice(3000)
			.totalPrice(10000)
			.password("member")
			.road("우리집")
			.build();

		when(memberService.readById(anyLong())).thenReturn(member);
		when(purchaseRepository.existsPurchaseByOrderNumber(any(UUID.class))).thenReturn(true);

		assertThrows(PurchaseAlreadyExistException.class, () -> {
			purchaseMemberService.createPurchase(request, 1L);
		});
	}

	@Test
	void testUpdatePurchase() {
		UpdatePurchaseMemberRequest request = new UpdatePurchaseMemberRequest("COMPLETED");

		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		Long id = purchaseMemberService.updatePurchase(request, 1L, 1L);

		assertThat(id).isEqualTo(purchase.getId());
		assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.COMPLETED);
	}

	@Test
	void testUpdatePurchase_ThrowsPurchaseDoesNotExistException() {
		UpdatePurchaseMemberRequest request = new UpdatePurchaseMemberRequest("COMPLETED");

		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(PurchaseDoesNotExistException.class, () -> {
			purchaseMemberService.updatePurchase(request, 1L, 1L);
		});
	}

	@Test
	void testUpdatePurchase_ThrowsPurchaseNoAuthorizationException() {
		UpdatePurchaseMemberRequest request = new UpdatePurchaseMemberRequest("COMPLETED");
		Member anotherMember = new Member();
		anotherMember.setId(2L);
		purchase.setMember(anotherMember);

		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));

		assertThrows(PurchaseNoAuthorizationException.class, () -> {
			purchaseMemberService.updatePurchase(request, 1L, 1L);
		});
	}

	@Test
	void testReadPurchase() {
		when(memberService.readById(anyLong())).thenReturn(member);
		when(purchaseRepository.findByMember(any(Member.class))).thenReturn(List.of(purchase));
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));

		ReadPurchaseResponse response = purchaseMemberService.readPurchase(1L, 1L);

		assertThat(response.id()).isEqualTo(purchase.getId());
	}

	@Test
	void testReadPurchase_ThrowsPurchaseDoesNotExistException() {
		when(memberService.readById(anyLong())).thenReturn(member);
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(PurchaseDoesNotExistException.class, () -> {
			purchaseMemberService.readPurchase(1L, 1L);
		});
	}

	@Test
	void testReadPurchase_ThrowsPurchaseNoAuthorizationException() {
		when(memberService.readById(anyLong())).thenReturn(member);
		when(purchaseRepository.findByMember(any(Member.class))).thenReturn(new ArrayList<>());
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));

		assertThrows(PurchaseNoAuthorizationException.class, () -> {
			purchaseMemberService.readPurchase(1L, 1L);
		});
	}

	@Test
	void testDeletePurchase() {
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));
		doNothing().when(purchaseRepository).delete(any(Purchase.class));

		purchaseMemberService.deletePurchase(1L, 1L);

		verify(purchaseRepository, times(1)).delete(purchase);
	}

	@Test
	void testDeletePurchase_ThrowsPurchaseDoesNotExistException() {
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(PurchaseDoesNotExistException.class, () -> {
			purchaseMemberService.deletePurchase(1L, 1L);
		});
	}

	@Test
	void testDeletePurchase_ThrowsPurchaseNoAuthorizationException() {
		Member anotherMember = new Member();
		anotherMember.setId(2L);
		purchase.setMember(anotherMember);

		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));

		assertThrows(PurchaseNoAuthorizationException.class, () -> {
			purchaseMemberService.deletePurchase(1L, 1L);
		});
	}
}
