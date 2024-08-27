package com.nhnacademy.bookstore.purchase.refundrecord.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;

public interface RefundRecordRepository extends JpaRepository<RefundRecord, Long> {

}
