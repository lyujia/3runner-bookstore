package com.nhnacademy.bookstore.member.pointrecord.repository;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA PointRecordRepository 인터페이스
 *
 * @author 김병우
 */
public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {
    List<PointRecord> findAllByPurchase(Purchase purchase);

    Page<PointRecord> findAllByMember(Member member, Pageable pageable);
}
