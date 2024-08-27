package com.nhnacademy.bookstore.purchase.purchasebook.repository;

import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * purchaseBook repository
 * @author 정주혁
 */
public interface PurchaseBookRepository extends JpaRepository<PurchaseBook, Long> {
    Page<PurchaseBook> findAllByPurchaseId(Long purchaseId, Pageable pageable);

    List<PurchaseBook> findAllByPurchaseId(Long purchaseId);

    Optional<PurchaseBook> findByPurchaseIdAndBookId(Long purchaseId, Long bookId);
}
