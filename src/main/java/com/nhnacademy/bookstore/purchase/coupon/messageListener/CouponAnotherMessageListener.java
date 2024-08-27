package com.nhnacademy.bookstore.purchase.coupon.messageListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.entity.membermessage.MemberMessage;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.coupon.dto.CouponResponse;
import com.nhnacademy.bookstore.purchase.coupon.repository.CouponCustomRepository;
import com.nhnacademy.bookstore.purchase.membermessage.dto.CouponFormDto;
import com.nhnacademy.bookstore.purchase.membermessage.service.MemberMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 만료3일남은 쿠폰 리스너.
 *
 * @author 김병우
 */
@Component
@RequiredArgsConstructor
public class CouponAnotherMessageListener {
    private static final String queueName2 = "3RUNNER-COUPON-EXPIRED-IN-THREE-DAY";
    private final CouponCustomRepository couponCustomRepository;
    private final MemberMessageService memberMessageService;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    /**
     * 만료기한 3일 남은 쿠폰 맴버들 메시지 큐 전송.
     *
     * @param couponFormDtosJson 쿠폰폼 dto
     */
    @RabbitListener(queues = queueName2)
    public void receiveMessage(String couponFormDtosJson) throws JsonProcessingException {
        List<CouponFormDto> couponFormDtos = objectMapper
                .readValue(couponFormDtosJson, objectMapper.getTypeFactory().constructCollectionType(List.class, CouponFormDto.class));
        List<Long> couponFormIds = new ArrayList<>();
        for (CouponFormDto dto : couponFormDtos) {
            couponFormIds.add(dto.id());
        }

        List<CouponResponse> responses = couponCustomRepository.findMemberIdsByCouponFormIds(couponFormIds);
        for (CouponResponse response : responses) {
            String name = "기본쿠폰";
            for (CouponFormDto couponFormDto : couponFormDtos) {
                if (couponFormDto.id() == response.couponId()) {
                    name = couponFormDto.name();
                }
            }
            memberMessageService.createMessage(
                    new MemberMessage("회원님 쿠폰 " + name + "(" + response.couponId() + ") 가 만료 3일 남았습니다.",
                            memberRepository
                                    .findById(response.memberId())
                                    .orElseThrow(MemberNotExistsException::new))
            );
        }
    }
}