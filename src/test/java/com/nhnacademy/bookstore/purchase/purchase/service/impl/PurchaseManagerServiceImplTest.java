package com.nhnacademy.bookstore.purchase.purchase.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;

public class PurchaseManagerServiceImplTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@InjectMocks
	private PurchaseManagerServiceImpl purchaseManagerService;

	private Purchase purchase;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

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
	}

	@Test
	void testReadPurchaseAll() {
		List<Purchase> purchases = new ArrayList<>();
		purchases.add(purchase);
		when(purchaseRepository.findAll()).thenReturn(purchases);

		Pageable pageable = PageRequest.of(0, 10);
		Page<ReadPurchaseResponse> responsePage = purchaseManagerService.readPurchaseAll(pageable);

		assertThat(responsePage).isNotEmpty();
		assertThat(responsePage.getContent().get(0).id()).isEqualTo(purchase.getId());
	}

	@Test
	void testUpdatePurchaseStatus() {
		when(purchaseRepository.findPurchaseByOrderNumber(eq(purchase.getOrderNumber()))).thenReturn(Optional.of(purchase));
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		Long id = purchaseManagerService.updatePurchaseStatus(purchase.getOrderNumber().toString(), "DELIVERY_START");

		assertThat(id).isEqualTo(purchase.getId());
		assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.DELIVERY_START);
	}

	@Test
	void testUpdatePurchaseStatus_ThrowsPurchaseDoesNotExistException() {
		when(purchaseRepository.findPurchaseByOrderNumber(any(UUID.class))).thenReturn(Optional.empty());

		assertThrows(PurchaseDoesNotExistException.class, () -> {
			purchaseManagerService.updatePurchaseStatus(purchase.getOrderNumber().toString(), "DELIVERY_START");
		});
	}

	@Test
	void testUpdateOrderStatus() {
		List<Purchase> purchases = new ArrayList<>();
		purchases.add(purchase);
		ZonedDateTime tenDaysAgo = ZonedDateTime.now().minusDays(10);
		purchase.setShippingDate(tenDaysAgo);

		when(purchaseRepository.findByShippingDateBefore(any(ZonedDateTime.class))).thenReturn(purchases);

		purchaseManagerService.updateOrderStatus();

		assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.CONFIRMATION);
		verify(purchaseRepository, times(1)).save(purchase);
	}

	@Test
	void testUpdatePurchaseStatus_DeliveryStartUpdatesShippingDate() {
		when(purchaseRepository.findPurchaseByOrderNumber(eq(purchase.getOrderNumber()))).thenReturn(Optional.of(purchase));
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		Long id = purchaseManagerService.updatePurchaseStatus(purchase.getOrderNumber().toString(), "DELIVERY_START");

		assertThat(id).isEqualTo(purchase.getId());
		assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.DELIVERY_START);
		assertThat(purchase.getShippingDate()).isNotNull();
	}

	@Test
	void testUpdatePurchaseStatus_StatusNotDeliveryStartDoesNotUpdateShippingDate() {
		when(purchaseRepository.findPurchaseByOrderNumber(eq(purchase.getOrderNumber()))).thenReturn(Optional.of(purchase));
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		Long id = purchaseManagerService.updatePurchaseStatus(purchase.getOrderNumber().toString(), "COMPLETED");

		assertThat(id).isEqualTo(purchase.getId());
		assertThat(purchase.getStatus()).isEqualTo(PurchaseStatus.COMPLETED);
		assertThat(purchase.getShippingDate()).isNull();
	}
}
