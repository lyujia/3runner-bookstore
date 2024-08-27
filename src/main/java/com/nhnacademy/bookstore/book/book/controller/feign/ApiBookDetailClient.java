package com.nhnacademy.bookstore.book.book.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ApiBookDetailClient", url = "https://www.aladin.co.kr/shop/wproduct.aspx")
public interface ApiBookDetailClient {
	@GetMapping
	String AladinDetailView(@RequestParam Long ItemId);
}

