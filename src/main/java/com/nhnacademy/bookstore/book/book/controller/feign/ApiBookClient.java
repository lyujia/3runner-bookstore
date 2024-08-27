package com.nhnacademy.bookstore.book.book.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 한민기
 * 알라딘에서 해당 책의 정보를 가져오기 위한 Feign Client
 *
 */
@FeignClient(name = "ApiBookClient", url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx")
public interface ApiBookClient {

	@GetMapping
	String getBook(@RequestParam String ttbkey,
		@RequestParam String ItemIdType,
		@RequestParam String ItemId,
		@RequestParam String QueryType,
		@RequestParam String output);
}
