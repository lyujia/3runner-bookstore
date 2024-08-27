package com.nhnacademy.bookstore.purchase.membermessage.controller;

import com.nhnacademy.bookstore.purchase.membermessage.dto.ReadMemberMessageResponse;
import com.nhnacademy.bookstore.purchase.membermessage.dto.UpdateMemberMessageRequest;
import com.nhnacademy.bookstore.purchase.membermessage.service.MemberMessageService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 맴버 메시지 컨트롤러.
 *
 * @author 김병우
 */
@RequiredArgsConstructor
@RestController
public class MemberMessageController {
    private final MemberMessageService memberMessageService;

    /**
     * 맴버메시지 읽기.
     *
     * @param memberId 맴버아이디
     * @param page 페이지
     * @param size 사이즈
     * @return 페이지
     */
    @GetMapping("/bookstore/messages")
    public ApiResponse<Page<ReadMemberMessageResponse>> readAllById(
            @RequestHeader("Member-Id") Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.success(memberMessageService.readAll(memberId, page, size));
    }

    /**
     * 안읽은 메시지 수.
     *
     * @param memberId 맴버아이디
     * @return 롱
     */
    @GetMapping("/bookstore/messages/counts")
    public ApiResponse<Long> readUnreadedMessage(@RequestHeader("Member-Id") Long memberId) {
        return ApiResponse.success(memberMessageService.countUnreadMessage(memberId));
    }


    /**
     * 맴버메시지 상태 업데이트
     *
     * @param updateMemberMessageRequest 업데이트 dto
     * @return void
     */
    @PutMapping("/bookstore/messages")
    public ApiResponse<Void> updateMessage(@RequestBody UpdateMemberMessageRequest updateMemberMessageRequest) {
        memberMessageService.readAlarm(updateMemberMessageRequest.memberMessageId());
        return ApiResponse.success(null);
    }
}
