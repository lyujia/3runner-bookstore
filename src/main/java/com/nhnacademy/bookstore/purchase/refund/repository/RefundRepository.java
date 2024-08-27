package com.nhnacademy.bookstore.purchase.refund.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.refund.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {
	@Query("select DISTINCT p from Purchase as p join PurchaseBook as pb on pb.purchase=p join RefundRecord as rr on rr.purchaseBook=pb join Refund as r on rr.refund=r where p.id = :purchaseId")
	List<Purchase> findByRefundId(Long purchaseId);
}
