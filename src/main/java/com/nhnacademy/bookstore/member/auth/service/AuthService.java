package com.nhnacademy.bookstore.member.auth.service;

import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.member.auth.repository.AuthRepository;

public interface AuthService {
    public void save(Auth auth);
    public Auth getAuth(String name);
}


/**
 * @Author -유지아.
 * Get auth auth. -권한의 이름으로 권한을 반환한다.
 *
 * @param name the name -권한의 이름값을 받는다.
 * @return the auth -Auth를 반환한다.
 */
