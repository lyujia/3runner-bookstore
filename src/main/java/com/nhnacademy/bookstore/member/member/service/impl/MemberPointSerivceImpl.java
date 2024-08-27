package com.nhnacademy.bookstore.member.member.service.impl;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.pointpolicy.PointPolicy;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.member.member.dto.response.ReadMemberResponse;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.member.pointrecord.repository.PointRecordRepository;
import com.nhnacademy.bookstore.purchase.pointpolicy.exception.PointPolicyDoesNotExistException;
import com.nhnacademy.bookstore.purchase.pointpolicy.repository.PointPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 맴버 포인트 서비스 구현체.
 *
 * @author 김병우
 */
@Transactional
@Service
@RequiredArgsConstructor
public class MemberPointSerivceImpl implements MemberPointService {
    private final MemberRepository memberRepository;
    private final PointPolicyRepository pointPolicyRepository;
    private final PointRecordRepository pointRecordRepository;

    /**
     * 맴버 포인트 업데이트.
     *
     * @param memberId 맴버아이디
     * @param usePoint 사용포인트
     * @return 맴버아이디
     */
    @Override
    public Long updatePoint(Long memberId, Long usePoint) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new);
        Long currentPoint = member.getPoint();
        member.setPoint(currentPoint + usePoint);

        memberRepository.save(member);

        return memberId;
    }

    @Override
    public List<ReadMemberResponse> readAll() {
        return memberRepository
                .findAll()
                .stream()
                .map(m -> ReadMemberResponse.builder()
                        .memberId(m.getId())
                        .name(m.getName())
                        .phone(m.getPhone())
                        .age(m.getAge())
                        .email(m.getEmail())
                        .build()
                ).toList();
    }

    @Override
    public void welcomePoint(Member member) {
        PointPolicy pointPolicy = pointPolicyRepository
                .findByPolicyName("회원가입포인트").orElseThrow(()->new PointPolicyDoesNotExistException("포인트 정책이 없습니다"));
        final long POINT_RATE = pointPolicy.getPolicyValue();

        //포인트 적립
        pointRecordRepository.save(new PointRecord(
                POINT_RATE,
                "회원가입 적립",
                member,
                null)
        );

    }
}
