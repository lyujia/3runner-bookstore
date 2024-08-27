package com.nhnacademy.bookstore.purchase.refund.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsPurchase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.payment.Payment;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.refund.Refund;
import com.nhnacademy.bookstore.entity.refund.enums.RefundStatus;
import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.pointrecord.repository.PointRecordRepository;
import com.nhnacademy.bookstore.purchase.payment.repository.PaymentRepository;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookRepository;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundCustomRepository;
import com.nhnacademy.bookstore.purchase.refund.repository.RefundRepository;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.RefundRecordRepository;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RefundServiceImplTest {

    @InjectMocks
    private RefundServiceImpl refundService;

    @Mock
    private RefundRepository refundRepository;
    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RefundCustomRepository refundCustomRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PointRecordRepository pointRecordRepository;
    @Mock
    private PurchaseBookRepository purchaseBookRepository;
    @Mock
    private RefundRecordRepository refundRecordRepository;
    @Mock
    private BookRepository bookRepository;

    @Test
    public void testReadTossOrderId() {
        UUID orderId = UUID.randomUUID();
        Purchase purchase = new Purchase();
        Payment payment = new Payment();
        payment.setTossOrderId("testTossOrderId");

        when(purchaseRepository.findPurchaseByOrderNumber(orderId)).thenReturn(Optional.of(purchase));
        when(paymentRepository.findByPurchase(purchase)).thenReturn(payment);

        String tossOrderId = refundService.readTossOrderId(orderId.toString());

        assertEquals("testTossOrderId", tossOrderId);
        verify(purchaseRepository, times(1)).findPurchaseByOrderNumber(orderId);
        verify(paymentRepository, times(1)).findByPurchase(purchase);
    }

    @Test
    public void testCreateRefund() {
        Long orderId = 1L;
        String refundContent = "Test Refund";
        Integer price = 1000;
        Long memberId = 1L;

        Purchase purchase = new Purchase();
        purchase.setMember(new Member());
        purchase.getMember().setId(memberId);

        when(purchaseRepository.findById(orderId)).thenReturn(Optional.of(purchase));

        Long refundId = refundService.createRefund(orderId, refundContent, price, memberId);

        verify(purchaseRepository, times(1)).findById(orderId);
        verify(refundRepository, times(1)).save(any(Refund.class));
        assertNotNull(refundId);
    }

    @Test
    public void testUpdateRefundRejected() {
        Long refundId = 1L;
        Refund refund = new Refund();
        refund.setRefundStatus(RefundStatus.READY);

        Purchase purchase = new Purchase();
        purchase.setShippingDate(ZonedDateTime.now().minusDays(5));
        refund.setRefundRecordList(Arrays.asList(new RefundRecord()));
        refund.getRefundRecordList().get(0).setPurchaseBook(new PurchaseBook());
        refund.getRefundRecordList().get(0).getPurchaseBook().setPurchase(purchase);

        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));

        Boolean result = refundService.updateRefundRejected(refundId);

        assertTrue(result);
        assertEquals(RefundStatus.FAILED, refund.getRefundStatus());
        verify(refundRepository, times(1)).findById(refundId);
    }

    @Test
    public void testReadRefundListAll() {
        refundService.readRefundListAll();
        verify(refundCustomRepository, times(1)).readRefundAll();
    }

    @Test
    void testReadTossOrderID_Success() {
        Long purchaseId = 1L;
        Purchase purchase = new Purchase();
        Payment payment = new Payment();
        payment.setTossOrderId("testTossOrderId");

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(paymentRepository.findByPurchase(purchase)).thenReturn(payment);

        String tossOrderId = refundService.readTossOrderID(purchaseId);

        assertEquals("testTossOrderId", tossOrderId);
        verify(purchaseRepository, times(1)).findById(purchaseId);
        verify(paymentRepository, times(1)).findByPurchase(purchase);
    }

    @Test
    void testReadTossOrderID_NotExistsPurchase() {
        Long purchaseId = 1L;

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        assertThrows(NotExistsPurchase.class, () -> {
            refundService.readTossOrderID(purchaseId);
        });

        verify(purchaseRepository, times(1)).findById(purchaseId);
        verify(paymentRepository, times(0)).findByPurchase(any(Purchase.class));
    }
    @Test
    public void testUpdateSuccessRefund_Success() {
        Long refundId = 1L;
        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setPrice(1000);
        refund.setRefundStatus(RefundStatus.READY);

        Member member = new Member();
        member.setId(1L);
        member.setPoint(100L);

        PurchaseBook purchaseBook1 = new PurchaseBook();
        purchaseBook1.setQuantity(2);
        purchaseBook1.setPrice(500);
        Book book1 = new Book();
        book1.setQuantity(10);
        purchaseBook1.setBook(book1);

        PurchaseBook purchaseBook2 = new PurchaseBook();
        purchaseBook2.setQuantity(3);
        purchaseBook2.setPrice(500);
        Book book2 = new Book();
        book2.setQuantity(5);
        purchaseBook2.setBook(book2);

        RefundRecord refundRecord1 = new RefundRecord();
        refundRecord1.setQuantity(2);
        refundRecord1.setPrice(500);
        refundRecord1.setPurchaseBook(purchaseBook1);

        RefundRecord refundRecord2 = new RefundRecord();
        refundRecord2.setQuantity(3);
        refundRecord2.setPrice(500);
        refundRecord2.setPurchaseBook(purchaseBook2);

        List<RefundRecord> refundRecordList = Arrays.asList(refundRecord1, refundRecord2);
        refund.setRefundRecordList(refundRecordList);

        Purchase purchase = new Purchase();
        purchase.setMember(member);
        purchase.setPurchaseBookList(Arrays.asList(purchaseBook1, purchaseBook2));
        purchaseBook1.setPurchase(purchase);
        purchaseBook2.setPurchase(purchase);

        refundRecord1.setRefund(refund);
        refundRecord2.setRefund(refund);

        Boolean result = refundService.updateSuccessRefund(refundId);

    }

    @Test
    public void testUpdateSuccessRefund_RefundNotFound() {
        Long refundId = 1L;

        when(refundRepository.findById(refundId)).thenReturn(Optional.empty());

        Boolean result = refundService.updateSuccessRefund(refundId);

        assertFalse(result);
        verify(refundRepository, times(1)).findById(refundId);
        verify(refundRepository, times(0)).save(any(Refund.class));
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
        verify(pointRecordRepository, times(0)).save(any(PointRecord.class));
    }

    @Test
    public void testUpdateSuccessRefund_RefundAlreadySuccess() {
        Long refundId = 1L;
        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setRefundStatus(RefundStatus.SUCCESS);

        when(refundRepository.findById(refundId)).thenReturn(Optional.of(refund));

        Boolean result = refundService.updateSuccessRefund(refundId);

        assertFalse(result);
        verify(refundRepository, times(1)).findById(refundId);
        verify(refundRepository, times(0)).save(any(Refund.class));
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
        verify(pointRecordRepository, times(0)).save(any(PointRecord.class));
    }



    @Test
    public void testUpdateSuccessRefund_RefundAlreadySuccess1() {
        Long refundId = 1L;
        Refund refund = new Refund();
        refund.setId(refundId);
        refund.setRefundStatus(RefundStatus.SUCCESS);


        Boolean result = refundService.updateSuccessRefund(refundId);

    }
}