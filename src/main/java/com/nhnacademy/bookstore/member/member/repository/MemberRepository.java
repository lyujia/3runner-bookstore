package com.nhnacademy.bookstore.member.member.repository;

import com.nhnacademy.bookstore.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    //R1: 멤버 이메일과 페스워드로 멤버 반환
    Optional<Member> findByEmail(String email);
    //이메일을 가진 멤버가 이미 존재하는지 확인할때 필요
}