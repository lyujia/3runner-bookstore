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
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseGuestService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 비회원 주문 서비스.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PurchaseGuestServiceImpl implements PurchaseGuestService {
	private final PurchaseRepository purchaseRepository;
	private final PasswordEncoder encoder;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createPurchase(CreatePurchaseRequest createPurchaseRequest) {
		Purchase purchase = new Purchase(
			UUID.fromString(createPurchaseRequest.orderId()),
			PurchaseStatus.COMPLETED,
			createPurchaseRequest.deliveryPrice(),
			createPurchaseRequest.totalPrice(),
			ZonedDateTime.now(),
			createPurchaseRequest.road(),
			encoder.encode(createPurchaseRequest.password()),
			createPurchaseRequest.shippingDate(),
			createPurchaseRequest.isPacking(),
			MemberType.NONMEMBER,
			null

		);

		if (purchaseRepository.existsPurchaseByOrderNumber(purchase.getOrderNumber())) {
			throw new PurchaseAlreadyExistException("주문 번호가 중복되었습니다.");
		}

		purchaseRepository.save(purchase);
		return purchase.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updatePurchase(UpdatePurchaseGuestRequest updatePurchaseGuestRequest) {
		Purchase purchase = purchaseRepository.findPurchaseByOrderNumber(
			updatePurchaseGuestRequest.orderNumber()).orElseThrow(()->new PurchaseDoesNotExistException("해당주문이 없습니다."));

		purchase.setStatus(updatePurchaseGuestRequest.purchaseStatus());

		purchaseRepository.save(purchase);

		return purchase.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadPurchaseResponse readPurchase(UUID orderNumber, String password) {
		if (Boolean.FALSE.equals(validateGuest(orderNumber, password))) {
			return null;
		}
		Optional<Purchase> purchaseOptional = purchaseRepository.findPurchaseByOrderNumber(orderNumber);

		Purchase purchase = validateGuest(purchaseOptional, password);

		return ReadPurchaseResponse.builder()
			.id(purchase.getId())
			.orderNumber(orderNumber)
			.status(purchase.getStatus())
			.deliveryPrice(purchase.getDeliveryPrice())
			.totalPrice(purchase.getTotalPrice())
			.createdAt(purchase.getCreatedAt())
			.road(purchase.getRoad())
			.password(purchase.getPassword())
			.memberType(purchase.getMemberType())
			.isPacking(purchase.getIsPacking())
			.shippingDate(purchase.getShippingDate())
			.build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePurchase(UUID orderNumber, String password) {
		Optional<Purchase> purchaseOptional = purchaseRepository.findPurchaseByOrderNumber(orderNumber);
		Purchase purchase = validateGuest(purchaseOptional, password);
		purchaseRepository.delete(purchase);
	}

	/**
	 * 비회원 주문 번호, 비밀번호 검증
	 *
	 * @param purchaseOptional 주문
	 * @param password 비밀번호
	 * @return 주문
	 */
	private Purchase validateGuest(Optional<Purchase> purchaseOptional, String password) {
		if (purchaseOptional.isEmpty()) {
			throw new PurchaseDoesNotExistException("주문이 없습니다.");
		}

		Purchase purchase = purchaseOptional.get();
		if (!encoder.matches(password, purchase.getPassword())) {
			throw new PurchaseNoAuthorizationException("권한이 없습니다.");
		}
		return purchase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean validateGuest(UUID orderNumber, String password) {
		Purchase purchase = purchaseRepository.findPurchaseByOrderNumber(orderNumber).orElse(null);
		if (purchase == null) {
			return false;
		}
		return encoder.matches(password, purchase.getPassword());

	}
}
