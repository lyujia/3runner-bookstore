package com.nhnacademy.bookstore.purchase.membermessage.repository;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.membermessage.MemberMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 맴버메시지 저장소 인터페이스.
 *
 * @author 김병우
 */
public interface MemberMessageRepository extends JpaRepository<MemberMessage, Long> {

    Page<MemberMessage> findByMember(Member member, Pageable pageable);

    @Query("SELECT COUNT(m) FROM MemberMessage m WHERE m.viewAt IS NULL and m.member = :member")
    long countByViewAtIsNull(@Param("member") Member member);
}
