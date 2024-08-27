package com.nhnacademy.bookstore.purchase.payment.service.impl;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.payment.Payment;
import com.nhnacademy.bookstore.entity.pointpolicy.PointPolicy;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.member.pointrecord.service.PointRecordService;
import com.nhnacademy.bookstore.purchase.bookcart.dto.request.ReadAllBookCartMemberRequest;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadAllBookCartMemberResponse;
import com.nhnacademy.bookstore.purchase.bookcart.service.BookCartMemberService;
import com.nhnacademy.bookstore.purchase.coupon.service.CouponMemberService;
import com.nhnacademy.bookstore.purchase.payment.dto.CreatePaymentMemberRequest;
import com.nhnacademy.bookstore.purchase.payment.repository.PaymentRepository;
import com.nhnacademy.bookstore.purchase.pointpolicy.exception.PointPolicyDoesNotExistException;
import com.nhnacademy.bookstore.purchase.pointpolicy.repository.PointPolicyRepository;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseMemberService;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.service.PurchaseBookService;
import com.nhnacademy.bookstore.purchase.purchasecoupon.service.PurchaseCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PaymentMemberServiceImplTest {

    @InjectMocks
    private PaymentMemberServiceImpl paymentMemberService;

    @Mock
    private BookCartMemberService bookCartMemberService;

    @Mock
    private PurchaseMemberService purchaseMemberService;

    @Mock
    private PurchaseBookService purchaseBookService;

    @Mock
    private PointRecordService pointRecordService;

    @Mock
    private PurchaseCouponService purchaseCouponService;

    @Mock
    private CouponMemberService couponMemberService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPayment() {
        CreatePaymentMemberRequest request = CreatePaymentMemberRequest.builder()
                .orderId("orderId")
                .road("road")
                .amount(10000)
                .shippingDate(ZonedDateTime.now())
                .isPacking(true)
                .paymentKey("paymentKey")
                .memberId(1L)
                .discountedPoint(1000)
                .couponFormId(1L)
                .discountedPrice(5000)
                .build();

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setPolicyName("적립률");
        pointPolicy.setPolicyValue(10);

        Long purchaseId = 1L;
        given(pointPolicyRepository.findByPolicyName("적립률")).willReturn(Optional.of(pointPolicy));
        given(purchaseMemberService.createPurchase(any(CreatePurchaseRequest.class), eq(1L))).willReturn(purchaseId);
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(mock(Purchase.class)));
        List<ReadAllBookCartMemberResponse> bookCartResponses = List.of(
                ReadAllBookCartMemberResponse.builder()
                        .bookCartId(1L)
                        .bookId(1L)
                        .price(1000)
                        .url("url")
                        .title("title")
                        .quantity(2)
                        .leftQuantity(10)
                        .build()
        );

        given(bookCartMemberService.readAllCartMember(any(ReadAllBookCartMemberRequest.class))).willReturn(bookCartResponses);

        Book book = new Book();
        book.setId(1L);
        book.setQuantity(10);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        Long result = paymentMemberService.payment(request);

        assertEquals(purchaseId, result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(purchaseBookService, times(1)).createPurchaseBook(any(CreatePurchaseBookRequest.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(pointRecordService, times(2)).save(anyLong(), anyString(), eq(1L), eq(purchaseId));
        verify(couponMemberService, times(1)).useCoupons(1L, 1L);
        verify(purchaseCouponService, times(1)).create(purchaseId, 1L, 5000);
    }

    @Test
    void testPayment_PointPolicyDoesNotExist() {
        CreatePaymentMemberRequest request = CreatePaymentMemberRequest.builder()
                .orderId("orderId")
                .road("road")
                .amount(10000)
                .shippingDate(ZonedDateTime.now())
                .isPacking(true)
                .paymentKey("paymentKey")
                .memberId(1L)
                .discountedPoint(1000)
                .couponFormId(1L)
                .discountedPrice(5000)
                .build();

        given(pointPolicyRepository.findByPolicyName("적립률")).willReturn(Optional.empty());

        assertThrows(PointPolicyDoesNotExistException.class, () -> paymentMemberService.payment(request));
    }

    @Test
    void testPayment_BookDoesNotExist() {
        CreatePaymentMemberRequest request = CreatePaymentMemberRequest.builder()
                .orderId("orderId")
                .road("road")
                .amount(10000)
                .shippingDate(ZonedDateTime.now())
                .isPacking(true)
                .paymentKey("paymentKey")
                .memberId(1L)
                .discountedPoint(1000)
                .couponFormId(1L)
                .discountedPrice(5000)
                .build();

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setPolicyName("적립률");
        pointPolicy.setPolicyValue(10);

        Long purchaseId = 1L;
        given(pointPolicyRepository.findByPolicyName("적립률")).willReturn(Optional.of(pointPolicy));
        given(purchaseMemberService.createPurchase(any(CreatePurchaseRequest.class), eq(1L))).willReturn(purchaseId);
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(mock(Purchase.class)));
        List<ReadAllBookCartMemberResponse> bookCartResponses = List.of(
                ReadAllBookCartMemberResponse.builder()
                        .bookCartId(1L)
                        .bookId(1L)
                        .price(1000)
                        .url("url")
                        .title("title")
                        .quantity(2)
                        .leftQuantity(10)
                        .build()
        );
        given(bookCartMemberService.readAllCartMember(any(ReadAllBookCartMemberRequest.class))).willReturn(bookCartResponses);

        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(BookDoesNotExistException.class, () -> paymentMemberService.payment(request));
    }

    @Test
    void testPayment_InsufficientStock() {
        CreatePaymentMemberRequest request = CreatePaymentMemberRequest.builder()
                .orderId("orderId")
                .road("road")
                .amount(10000)
                .shippingDate(ZonedDateTime.now())
                .isPacking(true)
                .paymentKey("paymentKey")
                .memberId(1L)
                .discountedPoint(1000)
                .couponFormId(1L)
                .discountedPrice(5000)
                .build();

        PointPolicy pointPolicy = new PointPolicy();
        pointPolicy.setPolicyName("적립률");
        pointPolicy.setPolicyValue(10);

        Long purchaseId = 1L;
        given(pointPolicyRepository.findByPolicyName("적립률")).willReturn(Optional.of(pointPolicy));
        given(purchaseMemberService.createPurchase(any(CreatePurchaseRequest.class), eq(1L))).willReturn(purchaseId);
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(mock(Purchase.class)));
        List<ReadAllBookCartMemberResponse> bookCartResponses = List.of(
                ReadAllBookCartMemberResponse.builder()
                        .bookCartId(1L)
                        .bookId(1L)
                        .price(1000)
                        .url("url")
                        .title("title")
                        .quantity(11)
                        .leftQuantity(10)
                        .build()
        );
        given(bookCartMemberService.readAllCartMember(any(ReadAllBookCartMemberRequest.class))).willReturn(bookCartResponses);

        Book book = new Book();
        book.setId(1L);
        book.setQuantity(10);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        assertThrows(RuntimeException.class, () -> paymentMemberService.payment(request));
    }
}