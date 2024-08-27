package com.nhnacademy.bookstore.purchase.purchase.service.impl;

import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.UpdatePurchaseGuestRequest;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseAlreadyExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseNoAuthorizationException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchase.service.impl.PurchaseGuestServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PurchaseGuestServiceImplTest {

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private PasswordEncoder encoder;

	@InjectMocks
	private PurchaseGuestServiceImpl purchaseGuestService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createPurchase_Success() {
		CreatePurchaseRequest request = CreatePurchaseRequest.builder()
			.orderId("123e4567-e89b-12d3-a456-426614174000")
			.deliveryPrice(5000)
			.totalPrice(10000)
			.road("123 Street")
			.password("password")
			.shippingDate(ZonedDateTime.now())
			.isPacking(true)
			.build();

		Purchase purchase = new Purchase(
			UUID.fromString(request.orderId()),
			PurchaseStatus.COMPLETED,
			request.deliveryPrice(),
			request.totalPrice(),
			ZonedDateTime.now(),
			request.road(),
			encoder.encode(request.password()),
			request.shippingDate(),
			request.isPacking(),
			MemberType.NONMEMBER,
			null
		);

		when(purchaseRepository.existsPurchaseByOrderNumber(purchase.getOrderNumber())).thenReturn(false);
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);
		when(encoder.encode(anyString())).thenReturn("encodedPassword");

		Long purchaseId = purchaseGuestService.createPurchase(request);

		assertNotNull(purchaseId);
		verify(purchaseRepository, times(1)).existsPurchaseByOrderNumber(purchase.getOrderNumber());
		verify(purchaseRepository, times(1)).save(any(Purchase.class));
	}

	@Test
	void createPurchase_PurchaseAlreadyExistException() {
		CreatePurchaseRequest request = CreatePurchaseRequest.builder()
			.orderId("123e4567-e89b-12d3-a456-426614174000")
			.deliveryPrice(5000)
			.totalPrice(10000)
			.road("123 Street")
			.password("password")
			.shippingDate(ZonedDateTime.now())
			.isPacking(true)
			.build();

		when(purchaseRepository.existsPurchaseByOrderNumber(any(UUID.class))).thenReturn(true);

		assertThrows(PurchaseAlreadyExistException.class, () -> purchaseGuestService.createPurchase(request));
	}

	@Test
	void updatePurchase_Success() {
		UpdatePurchaseGuestRequest request =  UpdatePurchaseGuestRequest.builder()
			.orderNumber(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
			.purchaseStatus(PurchaseStatus.COMPLETED)
			.password("password")
			.build();

		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setOrderNumber(request.orderNumber());
		purchase.setStatus(PurchaseStatus.COMPLETED);

		when(purchaseRepository.findPurchaseByOrderNumber(request.orderNumber())).thenReturn(Optional.of(purchase));
		when(purchaseRepository.save(any(Purchase.class))).thenReturn(purchase);

		Long purchaseId = purchaseGuestService.updatePurchase(request);

		assertNotNull(purchaseId);
		assertEquals(PurchaseStatus.COMPLETED, purchase.getStatus());
		verify(purchaseRepository, times(1)).findPurchaseByOrderNumber(request.orderNumber());
		verify(purchaseRepository, times(1)).save(any(Purchase.class));
	}

	@Test
	void updatePurchase_PurchaseDoesNotExistException() {
		UpdatePurchaseGuestRequest request =  UpdatePurchaseGuestRequest.builder()
			.orderNumber(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
			.purchaseStatus(PurchaseStatus.COMPLETED)
			.password("password")
			.build();

		when(purchaseRepository.findPurchaseByOrderNumber(request.orderNumber())).thenReturn(Optional.empty());

		assertThrows(PurchaseDoesNotExistException.class, () -> purchaseGuestService.updatePurchase(request));
	}

	@Test
	void readPurchase_Success() {
		UUID orderNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		String password = "password";
		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setOrderNumber(orderNumber);
		purchase.setPassword("encodedPassword");

		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.of(purchase));
		when(encoder.matches(password, purchase.getPassword())).thenReturn(true);

		ReadPurchaseResponse response = purchaseGuestService.readPurchase(orderNumber, password);

		assertNotNull(response);
		assertEquals(orderNumber, response.orderNumber());
		verify(purchaseRepository, times(2)).findPurchaseByOrderNumber(orderNumber); // validateGuest + readPurchase
	}




	@Test
	void deletePurchase_Success() {
		UUID orderNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		String password = "password";
		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setOrderNumber(orderNumber);
		purchase.setPassword("encodedPassword");

		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.of(purchase));
		when(encoder.matches(password, purchase.getPassword())).thenReturn(true);

		purchaseGuestService.deletePurchase(orderNumber, password);

		verify(purchaseRepository, times(1)).findPurchaseByOrderNumber(orderNumber);
		verify(purchaseRepository, times(1)).delete(purchase);
	}

	@Test
	void deletePurchase_PurchaseDoesNotExistException() {
		UUID orderNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		String password = "password";

		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.empty());

		assertThrows(PurchaseDoesNotExistException.class,
			() -> purchaseGuestService.deletePurchase(orderNumber, password));
	}

	@Test
	void deletePurchase_PurchaseNoAuthorizationException() {
		UUID orderNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		String password = "password";
		Purchase purchase = new Purchase();
		purchase.setId(1L);
		purchase.setOrderNumber(orderNumber);
		purchase.setPassword("encodedPassword");

		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.of(purchase));
		when(encoder.matches(password, purchase.getPassword())).thenReturn(false);

		assertThrows(PurchaseNoAuthorizationException.class,
			() -> purchaseGuestService.deletePurchase(orderNumber, password));
	}

	@Test
	void validateGuest_Success() {
		UUID orderNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		String password = "password";
		Purchase purchase = new Purchase();
		purchase.setOrderNumber(orderNumber);
		purchase.setPassword("encodedPassword");

		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.of(purchase));
		when(encoder.matches(password, purchase.getPassword())).thenReturn(true);

		Boolean isValid = purchaseGuestService.validateGuest(orderNumber, password);

		assertTrue(isValid);
		verify(purchaseRepository, times(1)).findPurchaseByOrderNumber(orderNumber);
	}

	@Test
	void validateGuest_Failure() {
		UUID orderNumber = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		String password = "password";

		when(purchaseRepository.findPurchaseByOrderNumber(orderNumber)).thenReturn(Optional.empty());

		Boolean isValid = purchaseGuestService.validateGuest(orderNumber, password);

		assertFalse(isValid);
		verify(purchaseRepository, times(1)).findPurchaseByOrderNumber(orderNumber);
	}
}
