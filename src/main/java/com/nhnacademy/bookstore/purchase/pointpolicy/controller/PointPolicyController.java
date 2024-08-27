package com.nhnacademy.bookstore.purchase.pointpolicy.controller;

import com.nhnacademy.bookstore.purchase.pointpolicy.dto.PointPolicyResponseRequest;
import com.nhnacademy.bookstore.purchase.pointpolicy.service.PointPolicyService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 포인트 정책 컨트롤러.
 *
 * @author 김병우
 */
@RestController
@RequiredArgsConstructor
public class PointPolicyController {
    private final PointPolicyService pointPolicyService;

    /**
     * 정책 저장,업데이트
     *
     * @param pointPolicyResponseRequest 요청 dto
     * @return 포인트 아이디
     */
    @PostMapping("/bookstore/points/policies")
    public ApiResponse<Long> saveOrUpdatePolicy(
            @RequestBody PointPolicyResponseRequest pointPolicyResponseRequest) {

        return ApiResponse.success(pointPolicyService.update(pointPolicyResponseRequest.policyKey(), pointPolicyResponseRequest.policyValue()));
    }

    /**
     * 포인트 정책 전체 읽기.
     *
     * @return 요청리스트
     */
    @GetMapping("/bookstore/points/policies")
    public ApiResponse<List<PointPolicyResponseRequest>> readPolicy() {
        return ApiResponse.success(pointPolicyService.readAll());
    }

    /**
     * 포인트 정책 하나 읽기.
     *
     * @param policyKey 키
     * @return 포인트 정책
     */
    @GetMapping("/bookstore/points/policies/{policyKey}")
    public ApiResponse<PointPolicyResponseRequest> readOne(@PathVariable String policyKey) {
        return ApiResponse.success(pointPolicyService.read(policyKey));
    }

}
