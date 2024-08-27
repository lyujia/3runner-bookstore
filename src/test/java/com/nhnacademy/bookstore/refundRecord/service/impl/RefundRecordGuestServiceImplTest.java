package com.nhnacademy.bookstore.refundRecord.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.refund.Refund;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookCustomRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookRepository;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.AlreadyExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.NotExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRedisRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.service.impl.RefundRecordGuestServiceImpl;

@ExtendWith(MockitoExtension.class)
class RefundRecordGuestServiceImplTest {

	@InjectMocks
	private RefundRecordGuestServiceImpl service;

	@Mock
	private RefundRecordRepository refundRecordRepository;

	@Mock
	private RefundRecordRedisRepository refundRecordRedisRepository;

	@Mock
	private PurchaseBookRepository purchaseBookRepository;

	@Mock
	private RefundRepository refundRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private PurchaseBookCustomRepository purchaseBookCustomRepository;



	@Test
	void testCreateAllRefundRecordRedis_alreadyExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(true);

		assertThrows(AlreadyExistsRefundRecordRedis.class, () -> {
			service.createAllRefundRecordRedis("orderNumber");
		});
	}

	@Test
	void testCreateAllRefundRecordRedis_success() {
		lenient().when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);
		when(purchaseBookCustomRepository.readGuestBookPurchaseResponses(anyString()))
			.thenReturn(Collections.singletonList(mock(ReadPurchaseBookResponse.class)));

		Boolean result = service.createAllRefundRecordRedis("orderNumber");

		assertTrue(result);
	}

	@Test
	void testCreateRefundRecordRedis_alreadyExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(true);

		assertThrows(AlreadyExistsRefundRecordRedis.class, () -> {
			service.createRefundRecordRedis("orderNumber", 1L, 100, 1, mock(ReadBookByPurchase.class));
		});
	}

	@Test
	void testCreateRefundRecordRedis_success() {
		lenient().when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);
		when(refundRecordRedisRepository.create(anyString(), anyLong(), any(ReadRefundRecordResponse.class)))
			.thenReturn(1L);

		Long result = service.createRefundRecordRedis("orderNumber", 1L, 100, 1, mock(ReadBookByPurchase.class));

		assertEquals(1L, result);
	}

	@Test
	void testUpdateRefundRecordAllRedis_notExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);

		Boolean result = service.updateRefundRecordAllRedis("orderNumber");

		assertTrue(result);
	}



	@Test
	void testUpdateRefundRecordZeroAllRedis_notExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);

		Boolean result = service.updateRefundRecordZeroAllRedis("orderNumber");

		assertTrue(result);
	}


	@Test
	void testUpdateRefundRecordRedis_notExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);

		assertThrows(NotExistsRefundRecordRedis.class, () -> {
			service.updateRefundRecordRedis("orderNumber", 1L, 1);
		});
	}


	@Test
	void testDeleteRefundRecordRedis_notExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);

		assertThrows(NotExistsRefundRecordRedis.class, () -> {
			service.deleteRefundRecordRedis("orderNumber", 1L);
		});
	}

	@Test
	void testDeleteRefundRecordRedis_success() {
		lenient().when(refundRecordRedisRepository.isHit(anyString())).thenReturn(true);
		when(refundRecordRedisRepository.delete(anyString(), anyLong())).thenReturn(1L);

		Long result = service.deleteRefundRecordRedis("orderNumber", 1L);

		assertEquals(1L, result);
	}

	@Test
	void testReadRefundRecordRedis_notExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);

		assertThrows(NotExistsRefundRecordRedis.class, () -> {
			service.readRefundRecordRedis("orderNumber");
		});
	}

	@Test
	void testReadRefundRecordRedis_success() {
		lenient().when(refundRecordRedisRepository.isHit(anyString())).thenReturn(true);
		when(refundRecordRedisRepository.readAll(anyString()))
			.thenReturn(Collections.singletonList(mock(ReadRefundRecordResponse.class)));

		List<ReadRefundRecordResponse> result = service.readRefundRecordRedis("orderNumber");

		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	@Test
	void testCreateRefundRecord_notExists() {
		when(refundRecordRedisRepository.isHit(anyString())).thenReturn(false);

		Boolean result = service.createRefundRecord("orderNumber", 1L);

		assertFalse(result);
	}

	@Test
	void testCreateRefundRecord_success() {
		lenient().when(refundRecordRedisRepository.isHit(anyString())).thenReturn(true);
		when(refundRepository.findById(anyLong())).thenReturn(Optional.of(mock(Refund.class)));
		when(service.readRefundRecordRedis("orderNumber")).thenReturn(Collections.singletonList(mock(ReadRefundRecordResponse.class)));
		when(purchaseBookRepository.findById(anyLong())).thenReturn(Optional.of(mock(PurchaseBook.class)));

		Boolean result = service.createRefundRecord("orderNumber", 1L);

		assertTrue(result);
	}
}
