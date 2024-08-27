package com.nhnacademy.bookstore.member.auth.repository;

import com.nhnacademy.bookstore.entity.auth.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    public Auth findByName(String name);//일단 아무것도 안필요할듯..
}