package com.nhnacademy.bookstore.purchase.coupon.feign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.request.*;
import com.nhnacademy.bookstore.purchase.coupon.feign.dto.response.*;
import com.nhnacademy.bookstore.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * 쿠폰 API 클라이언트.
 *
 * @author 김병우
 */
@FeignClient(name = "couponFormControllerClient", url = "http://${feign.coupon.url}")
public interface CouponControllerClient {

    @PostMapping("/coupon/members/forms")
    ApiResponse<List<ReadCouponFormResponse>> readCouponForm(@RequestBody ReadCouponFormRequest readCouponFormRequest);

    @PostMapping("/coupon/forms")
    ApiResponse<Long> createCouponForm(@RequestBody CreateCouponFormRequest createCouponFormRequest);

    @PostMapping("/coupon/types/fixes")
    ApiResponse<Long> createFixedCouponPolicy(@RequestBody CreateFixedCouponRequest createFixedCouponRequest);

    @PostMapping("/coupon/types/ratios")
    ApiResponse<Long> createRatioCouponPolicy(@RequestBody CreateRatioCouponRequest createRatioCouponRequest);

    @PostMapping("/coupon/usages/categories")
    ApiResponse<Long> createCategoryCouponPolicy(@RequestBody CreateCategoryCouponRequest createCategoryCouponRequest);

    @PostMapping("/coupon/usages/books")
    ApiResponse<Long> createBookCouponPolicy(@RequestBody CreateBookCouponRequest createBookCouponRequest);

    @GetMapping("/coupon/types")
    ApiResponse<List<ReadCouponTypeResponse>> readAllTypes();

    @GetMapping("/coupon/types/fixes/{couponTypeId}")
    ApiResponse<ReadFixedCouponResponse> readFixedType(@PathVariable Long couponTypeId);

    @GetMapping("/coupon/types/ratios/{couponTypeId}")
    ApiResponse<ReadRatioCouponResponse> readRatioType(@PathVariable Long couponTypeId);

    @GetMapping("/coupon/usages")
    ApiResponse<List<ReadCouponUsageResponse>> readAllUsages();

    @GetMapping("/coupon/usages/categories/{couponTypeId}")
    ApiResponse<List<Long>> readCategoryUsages(@PathVariable Long couponTypeId);

    @GetMapping("/coupon/usages/books/{couponTypeId}")
    ApiResponse<List<Long>> readBookUsages(@PathVariable Long couponTypeId);

    @GetMapping("/coupon/forms")
    ApiResponse<List<ReadCouponFormResponse>> readAllCouponForms();
}
