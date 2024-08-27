package com.nhnacademy.bookstore.purchase.refund.service.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.coupon.enums.CouponStatus;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.payment.Payment;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.purchasecoupon.PurchaseCoupon;
import com.nhnacademy.bookstore.entity.refund.Refund;
import com.nhnacademy.bookstore.entity.refund.enums.RefundStatus;
import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.pointrecord.repository.PointRecordRepository;
import com.nhnacademy.bookstore.purchase.coupon.repository.CouponRepository;
import com.nhnacademy.bookstore.purchase.payment.repository.PaymentRepository;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsPurchase;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsPurchaseBook;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookRepository;
import com.nhnacademy.bookstore.purchase.purchasecoupon.repository.PurchaseCouponRepository;
import com.nhnacademy.bookstore.purchase.refund.dto.response.ReadRefundResponse;
import com.nhnacademy.bookstore.purchase.refund.exception.ImpossibleAccessRefundException;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundCustomRepository;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundRepository;
import com.nhnacademy.bookstore.purchase.refund.service.RefundService;
import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRedisRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

	private final RefundRepository refundRepository;
	private final PurchaseRepository purchaseRepository;
	private final PaymentRepository paymentRepository;
	private final RefundCustomRepository refundCustomRepository;
	private final MemberRepository memberRepository;
	private final PointRecordRepository pointRecordRepository;
	private final PurchaseCouponRepository purchaseCouponRepository;
	private final PurchaseBookRepository purchaseBookRepository;
	private final RefundRecordRepository refundRecordRepository;
	private final RefundRecordRedisRepository refundRecordRedisRepository;
	private final BookRepository bookRepository;
	private final CouponRepository couponRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String readTossOrderId(String orderId) {
		Purchase purchase = purchaseRepository.findPurchaseByOrderNumber(UUID.fromString(orderId))
			.orElseThrow(NotExistsPurchase::new);
		Payment payment = paymentRepository.findByPurchase(purchase);
		return payment.getTossOrderId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String readTossOrderID(Long purchaseId) {
		Purchase purchase = purchaseRepository.findById(purchaseId)
			.orElseThrow(NotExistsPurchase::new);
		Payment payment = paymentRepository.findByPurchase(purchase);
		return payment.getTossOrderId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createRefund(Long orderId, String refundContent, Integer price, Long memberId) {

		Purchase purchase = purchaseRepository.findById(orderId).orElseThrow(NotExistsPurchase::new);

		if (!Objects.equals(memberId, purchase.getMember().getId())) {
			throw new ImpossibleAccessRefundException();
		}

		Refund refund = new Refund();
		refund.setRefundContent(refundContent);
		refund.setPrice(price);
		refund.setRefundStatus(RefundStatus.READY);
		purchase.setStatus(PurchaseStatus.REFUNDED_REQUEST);

		purchaseRepository.save(purchase);
		refundRepository.save(refund);

		return refund.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean updateSuccessRefund(Long refundId) {

		Refund refund = refundRepository.findById(refundId).orElse(null);
		if (Objects.isNull(refund)) {
			return false;
		}
		if (refund.getRefundStatus().equals(RefundStatus.SUCCESS)) {
			return false;
		}
		List<RefundRecord> refundRecordList = refund.getRefundRecordList();

		Purchase purchase = refund.getRefundRecordList().getFirst().getPurchaseBook().getPurchase();

		int count = 0;

		for (RefundRecord refundRecord : refundRecordList) {
			if (refundRecord.getQuantity() == refundRecord.getPurchaseBook().getQuantity()) {
				count++;
			}
		}

		Member member = purchase.getMember();

		for (RefundRecord refundRecord : refundRecordList) {
			PurchaseBook purchaseBook = refundRecord.getPurchaseBook();
			purchaseBook.setQuantity(purchaseBook.getQuantity() - refundRecord.getQuantity());
			purchaseBook.setPrice(purchaseBook.getPrice() - refundRecord.getPrice());
			Book book = purchaseBook.getBook();
			book.setQuantity(book.getQuantity() + refundRecord.getQuantity());
			bookRepository.save(book);
			purchaseBookRepository.save(purchaseBook);
		}

		member.setPoint(member.getPoint() + refund.getPrice());
		memberRepository.save(member);

		PointRecord pointRecord = new PointRecord((long)refund.getPrice(), "환불", member, purchase);
		pointRecordRepository.save(pointRecord);

		refund.setRefundStatus(RefundStatus.SUCCESS);

		if (count == purchase.getPurchaseBookList().size()) {
			purchase.setStatus(PurchaseStatus.REFUNDED_COMPLETED);
			List<PurchaseCoupon> purchaseCouponList = purchase.getPurchaseCouponList();
			if (!purchaseCouponList.isEmpty() && refundRepository.findByRefundId(purchase.getId()).size() > 1) {
				refund.setPrice(purchase.getTotalPrice() - purchase.getDeliveryPrice());
				for (PurchaseCoupon purchaseCoupon : purchaseCouponList) {
					purchaseCoupon.setStatus((short)0);
					purchaseCouponRepository.save(purchaseCoupon);
				}
			}
		} else {
			purchase.setStatus(PurchaseStatus.DELIVERY_START);
		}

		refundRepository.save(refund);
		purchaseRepository.save(purchase);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean updateRefundRejected(Long refundId) {
		Refund refund = refundRepository.findById(refundId).orElse(null);
		if (Objects.isNull(refund)) {
			return false;
		}
		Purchase purchase = refund.getRefundRecordList().getFirst().getPurchaseBook().getPurchase();

		refund.setRefundStatus(RefundStatus.FAILED);
		ZonedDateTime tenDaysAgo = ZonedDateTime.now().minusDays(10);
		if (purchase.getShippingDate().isBefore(tenDaysAgo)) {
			purchase.setStatus(PurchaseStatus.CONFIRMATION);
		} else {
			purchase.setStatus(PurchaseStatus.DELIVERY_COMPLETED);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadRefundResponse> readRefundListAll() {
		return refundCustomRepository.readRefundAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createRefundCancelPartPayment(Long memberId, Object orderNumber, Integer price) {

		Purchase purchase;
		List<ReadRefundRecordResponse> responses;

		try {
			Integer orderId = Integer.parseInt(orderNumber.toString());
			purchase = purchaseRepository.findById((long)orderId)
				.orElseThrow(NotExistsPurchase::new);
			if (!purchase.getMember().getId().equals(memberId)) {
				throw new ImpossibleAccessRefundException();
			}
			responses = refundRecordRedisRepository.readAll("Refund_member " + orderNumber);
		} catch (NumberFormatException e) {
			purchase = purchaseRepository.findPurchaseByOrderNumber(UUID.fromString(orderNumber.toString()))
				.orElseThrow(NotExistsPurchase::new);
			responses = refundRecordRedisRepository.readAll(orderNumber.toString());

		}

		Payment payment = paymentRepository.findByPurchase(purchase);

		int count = 0;

		Refund refund = new Refund();
		refund.setRefundContent("결제 취소");
		refund.setPrice(price);
		refund.setRefundStatus(RefundStatus.SUCCESS);

		refundRepository.save(refund);

		for (ReadRefundRecordResponse readRefundRecordResponse : responses) {
			PurchaseBook purchaseBook = purchaseBookRepository.findById(readRefundRecordResponse.id()).orElseThrow(
				NotExistsPurchaseBook::new);
			RefundRecord refundRecord = new RefundRecord();
			refundRecord.setRefund(refund);
			refundRecord.setPurchaseBook(purchaseBook);
			refundRecord.setPrice(readRefundRecordResponse.price());
			refundRecord.setQuantity(readRefundRecordResponse.quantity());

			refundRecordRepository.save(refundRecord);
			if (refundRecord.getQuantity() == purchaseBook.getQuantity()) {
				count++;
			}
			purchaseBook.setQuantity(purchaseBook.getQuantity() - readRefundRecordResponse.quantity());
			purchaseBook.setPrice(purchaseBook.getPrice() - readRefundRecordResponse.price());
			Book book = purchaseBook.getBook();
			book.setQuantity(book.getQuantity() + readRefundRecordResponse.quantity());
			bookRepository.save(book);
			purchaseBookRepository.save(purchaseBook);

		}

		if (count == purchase.getPurchaseBookList().size()) { // 모두 환불 완료
			purchase.setStatus(PurchaseStatus.REFUNDED_COMPLETED);

			List<PurchaseCoupon> purchaseCouponList = purchase.getPurchaseCouponList();
			if (!purchaseCouponList.isEmpty()
				&& refundRepository.findByRefundId(purchase.getId()).size() > 1) { // 이번이 최초환불 & 쿠폰이 사용됨
				refund.setPrice(payment.getTossAmount() - purchase.getDeliveryPrice());
				refundRepository.save(refund);
				for (PurchaseCoupon purchaseCoupon : purchaseCouponList) {
					purchaseCoupon.setStatus((short)0);

					Coupon coupon = purchaseCoupon.getCoupon();
					coupon.setCouponStatus(CouponStatus.READY);
					couponRepository.save(coupon);
					purchaseCouponRepository.save(purchaseCoupon);
				}
			}
		} else {
			purchase.setStatus(PurchaseStatus.DELIVERY_START);
		}

		purchaseRepository.save(purchase);
		try {
			Integer orderId = Integer.parseInt(orderNumber.toString());
			refundRecordRedisRepository.deleteAll("Refund_member " + orderId);
		} catch (NumberFormatException e) {
			refundRecordRedisRepository.deleteAll(orderNumber.toString());
		}

		return refund.getId();

	}
}
