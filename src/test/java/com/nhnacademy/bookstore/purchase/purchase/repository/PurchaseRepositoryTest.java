package com.nhnacademy.bookstore.purchase.purchase.repository;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseRepositoryTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	private Purchase purchase;
	private UUID orderNumber;
	private Member member;

	@BeforeEach
	void setUp() {
		orderNumber = UUID.randomUUID();
		member = new Member();
		member.setId(1L);
		member.setName("John Doe");

		purchase = new Purchase();
		purchase.setOrderNumber(orderNumber);
		purchase.setStatus(PurchaseStatus.COMPLETED);
		purchase.setDeliveryPrice(1000);
		purchase.setTotalPrice(5000);
		purchase.setCreatedAt(ZonedDateTime.now());
		purchase.setRoad("123 Main St");
		purchase.setPassword("password");
		purchase.setShippingDate(ZonedDateTime.now());
		purchase.setIsPacking(true);
		purchase.setMemberType(MemberType.NONMEMBER);
		purchase.setMember(member);
	}

	@Test
	void testExistsPurchaseByOrderNumber() {
		when(purchaseRepository.existsPurchaseByOrderNumber(orderNumber)).thenReturn(true);

		Boolean exists = purchaseRepository.existsPurchaseByOrderNumber(orderNumber);

		assertTrue(exists);
	}

	@Test
	void testFindByMember() {
		when(purchaseRepository.findByMember(member)).thenReturn(Arrays.asList(purchase));

		List<Purchase> purchases = purchaseRepository.findByMember(member);

		assertThat(purchases).isNotEmpty();
		assertThat(purchases.get(0).getOrderNumber()).isEqualTo(orderNumber);
	}

	@Test
	void testFindPurchaseByOrderNumber() {
		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.of(purchase));

		Optional<Purchase> foundPurchase = purchaseRepository.findPurchaseByOrderNumber(orderNumber);

		assertTrue(foundPurchase.isPresent());
		assertThat(foundPurchase.get().getOrderNumber()).isEqualTo(orderNumber);
	}

	@Test
	void testFindByShippingDateBefore() {
		ZonedDateTime shippingDate = ZonedDateTime.now().plusDays(1);
		purchase.setShippingDate(shippingDate.minusDays(2));

		when(purchaseRepository.findByShippingDateBefore(shippingDate)).thenReturn(Arrays.asList(purchase));

		List<Purchase> purchases = purchaseRepository.findByShippingDateBefore(shippingDate);

		assertThat(purchases).isNotEmpty();
		assertThat(purchases.get(0).getOrderNumber()).isEqualTo(orderNumber);
	}
}
