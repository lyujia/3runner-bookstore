package com.nhnacademy.bookstore.member.auth.service.impl;

import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.member.auth.repository.AuthRepository;
import com.nhnacademy.bookstore.member.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;

    /**
     * @Author -유지아.
     * Save. -권한을 받아 저장한다.
     *
     * @param auth the auth -권한 자체를 받는다.(string값을 받는걸로 바꾸는게 좋을듯)
     */
    public void save(Auth auth){
        authRepository.save(auth);
    }

    /**
     * @Author -유지아.
     * Get auth auth. -권한의 이름으로 권한을 반환한다.
     *
     * @param name the name -권한의 이름값을 받는다.
     * @return the auth -Auth를 반환한다.
     */
    public Auth getAuth(String name){
        return authRepository.findByName(name);
    }
}


