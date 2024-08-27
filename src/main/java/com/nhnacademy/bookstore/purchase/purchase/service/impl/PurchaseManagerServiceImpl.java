package com.nhnacademy.bookstore.purchase.purchase.service.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.purchase.purchase.dto.response.ReadPurchaseResponse;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseManagerService;

import lombok.RequiredArgsConstructor;

/**
 * 관리자 주문 상태 조회및 수정 서비스
 *
 * @author 정주혁
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseManagerServiceImpl implements PurchaseManagerService {
	private final PurchaseRepository purchaseRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<ReadPurchaseResponse> readPurchaseAll(Pageable pageable) {
		List<Purchase> purchases = purchaseRepository.findAll();
		List<ReadPurchaseResponse> responseList = purchases.stream().map(purchase -> ReadPurchaseResponse.builder()
				.id(purchase.getId())
				.orderNumber(purchase.getOrderNumber())
				.status(purchase.getStatus())
				.deliveryPrice(purchase.getDeliveryPrice()).totalPrice(purchase.getTotalPrice())
				.createdAt(purchase.getCreatedAt())
				.road(purchase.getRoad())
				.password(purchase.getPassword())
				.memberType(purchase.getMemberType())
				.build())
			.toList();
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), responseList.size());
		List<ReadPurchaseResponse> pagedResponse = responseList.subList(start, end);

		return new PageImpl<>(pagedResponse, pageable, responseList.size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updatePurchaseStatus(String purchaseId, String status) {

		Purchase purchase = purchaseRepository.findPurchaseByOrderNumber(UUID.fromString(purchaseId))
			.orElseThrow(() -> new PurchaseDoesNotExistException(""));


		purchase.setStatus(PurchaseStatus.fromString(status));
		if(status.equals("DELIVERY_START")&& purchase.getShippingDate() == null){
			purchase.setShippingDate(ZonedDateTime.now());
		}
		Purchase t= purchaseRepository.save(purchase);

		return t.getId();
	}

	/**
	 * 주문 상태가 출고 날짜 이후 10일이 지나면 자동적으로 주문 확정 변환
	 *
	 */
	@Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
	public void updateOrderStatus() {
		ZonedDateTime tenDaysAgo = ZonedDateTime.now().minusDays(10);
		List<Purchase> purchases = purchaseRepository.findByShippingDateBefore(
			tenDaysAgo);


		for (Purchase purchase : purchases) {
			purchase.setStatus(PurchaseStatus.CONFIRMATION);
			purchaseRepository.save(purchase);
		}
	}
}
