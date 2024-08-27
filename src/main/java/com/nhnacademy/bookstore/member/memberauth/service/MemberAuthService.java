package com.nhnacademy.bookstore.member.memberauth.service;

import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.entity.member.Member;

import java.util.List;

public interface MemberAuthService {
    public List<Auth> readAllAuths(Long memberId);
    public void saveAuth(Member member, Auth auth);

}
