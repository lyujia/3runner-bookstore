package com.nhnacademy.bookstore.purchase.payment.service.impl;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.payment.Payment;
import com.nhnacademy.bookstore.entity.payment.enums.PaymentStatus;
import com.nhnacademy.bookstore.purchase.bookcart.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.bookcart.service.BookCartGuestService;
import com.nhnacademy.bookstore.purchase.payment.dto.CreatePaymentGuestRequest;
import com.nhnacademy.bookstore.purchase.payment.repository.PaymentRepository;
import com.nhnacademy.bookstore.purchase.payment.service.PaymentGuestService;
import com.nhnacademy.bookstore.purchase.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchase.service.PurchaseGuestService;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.service.PurchaseBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PaymentGuestServiceImpl implements PaymentGuestService {
    private final BookCartGuestService bookCartGuestService;
    private final PurchaseGuestService purchaseGuestService;
    private final PurchaseBookService purchaseBookService;
    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long payment(CreatePaymentGuestRequest createPaymentGuestRequest) {

        Long purchaseId = purchaseGuestService.createPurchase(
                CreatePurchaseRequest.builder()
                        .orderId(createPaymentGuestRequest.orderId())
                        .road(createPaymentGuestRequest.road())
                        .password(createPaymentGuestRequest.password())
                        .isPacking(createPaymentGuestRequest.isPacking())
                        .shippingDate(createPaymentGuestRequest.shippingDate())
                        .totalPrice(createPaymentGuestRequest.amount())
                        .deliveryPrice(3000).build()
        );


        paymentRepository.save(
                new Payment(
                        createPaymentGuestRequest.orderId(),
                        createPaymentGuestRequest.amount(),
                        0,
                        createPaymentGuestRequest.paymentKey(),
                        PaymentStatus.SUCCESS,
                        purchaseRepository.findById(purchaseId).orElseThrow(()->new PurchaseDoesNotExistException("주문이 없습니다."))
                )
        );


        List<ReadBookCartGuestResponse> bookCartGuestResponseList = bookCartGuestService.readAllBookCart(createPaymentGuestRequest.cartId());

        for (ReadBookCartGuestResponse bookCartGuestResponse : bookCartGuestResponseList) {
            purchaseBookService.createPurchaseBook(
                    CreatePurchaseBookRequest.builder()
                            .bookId(bookCartGuestResponse.bookId())
                            .purchaseId(purchaseId)
                            .price(bookCartGuestResponse.price())
                            .quantity(bookCartGuestResponse.quantity())
                            .build()
            );

            Book book = bookRepository.findById(bookCartGuestResponse.bookId())
                    .orElseThrow(()-> new BookDoesNotExistException("북아이디 : " + bookCartGuestResponse.bookId() + "가 없습니다."));

            if (book.getQuantity() - bookCartGuestResponse.quantity() >= 0 ) {
                book.setQuantity(book.getQuantity()-bookCartGuestResponse.quantity());
            } else {
                throw new RuntimeException("북아이디 : " + bookCartGuestResponse.bookId() + "가 현재 재고가 없습니다.");
            }

            bookRepository.save(book);
        }

        bookCartGuestService.deleteAllBookCart(createPaymentGuestRequest.cartId());

        return purchaseId;
    }
}
