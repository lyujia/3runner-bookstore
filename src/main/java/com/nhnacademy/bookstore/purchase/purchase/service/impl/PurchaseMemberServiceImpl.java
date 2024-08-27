package com.nhnacademy.bookstore.purchase.purchase.service.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.RequiredArgsConstructor;

/**
 * 회원 주문 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseMemberServiceImpl implements PurchaseMemberService {
	private final PurchaseRepository purchaseRepository;
	private final MemberService memberService;
	private final PasswordEncoder passwordEncoder;

	/**
	 * {@inheritDoc}
	 */
    @Override
    public Long createPurchase(CreatePurchaseRequest createPurchaseRequest, Long memberId) {
		Purchase purchase = new Purchase(
			// UUID.fromString(createPurchaseRequest.orderId()),
			UUID.randomUUID(),
			PurchaseStatus.COMPLETED,
			createPurchaseRequest.deliveryPrice(),
			createPurchaseRequest.totalPrice(),
			ZonedDateTime.now(),
			createPurchaseRequest.road(),
			"member has no password",
			createPurchaseRequest.shippingDate(),
			createPurchaseRequest.isPacking(),
			MemberType.MEMBER,
			memberService.readById(memberId)

		);

        if(purchaseRepository.existsPurchaseByOrderNumber(purchase.getOrderNumber())) {
            throw new PurchaseAlreadyExistException("주문 번호가 중복되었습니다.");
        }

        purchaseRepository.save(purchase);
        return purchase.getId();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updatePurchase(UpdatePurchaseMemberRequest updatePurchaseRequest, Long memberId, Long purchaseId) {
		Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new PurchaseDoesNotExistException(""));

		if (!purchase.getMember().getId().equals(memberId)) {
			throw new PurchaseNoAuthorizationException("권한이 없습니다");
		}

		purchase.setStatus(PurchaseStatus.fromString(updatePurchaseRequest.purchaseStatus()));

		purchaseRepository.save(purchase);

		return purchase.getId();
	}


	/**
	 * {@inheritDoc}
	 */
    @Override
    public ReadPurchaseResponse readPurchase(Long MemberId, Long purchaseId) {
        List<Purchase> purchaseList = purchaseRepository.findByMember(memberService.readById(MemberId));
        Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(()-> new PurchaseDoesNotExistException(""));

        if (!purchaseList.contains(purchase)) {
            throw new PurchaseNoAuthorizationException("권한이 없습니다");
        }

        return ReadPurchaseResponse.builder()
                .id(purchase.getId())
                .status(purchase.getStatus())
                .deliveryPrice(purchase.getDeliveryPrice())
                .totalPrice(purchase.getTotalPrice())
                .createdAt(purchase.getCreatedAt())
                .road(purchase.getRoad())
                .password(purchase.getPassword())
                .memberType(purchase.getMemberType())
                .shippingDate(purchase.getShippingDate())
                .isPacking(purchase.getIsPacking())
                .build();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePurchase(Long MemberId, Long purchaseId) {
		Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(() -> new PurchaseDoesNotExistException(""));

		if (!purchase.getMember().getId().equals(MemberId)) {
			throw new PurchaseNoAuthorizationException("권한이 없습니다");
		}

		purchaseRepository.delete(purchase);
	}
}
