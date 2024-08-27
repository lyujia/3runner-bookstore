package com.nhnacademy.bookstore.book.tag.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.book.tag.dto.request.CreateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.DeleteTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.UpdateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;
import com.nhnacademy.bookstore.book.tag.exception.DeleteTagRequestFormException;
import com.nhnacademy.bookstore.book.tag.service.TagService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;

import jakarta.validation.Valid;

/**
 * 태그들을 만들고 제거하고 수정하는 컨트롤러
 *
 * @author 정주혁
 */
@RestController
@RequestMapping("/bookstore/tags")
//@RequiredArgsConstructor
public class TagController {
	@Autowired
	private TagService tagService;

	/**
	 * @author 한민기
	 * @return 모든 태그 가져오기
	 */
	@GetMapping
	public ApiResponse<List<TagResponse>> getAllTags() {
		return ApiResponse.success(tagService.getAllTags());
	}

	/**
	 * Tag 저장
	 *
	 * @param createTagRequest 생성할 태그의 이름
	 * @return ApiResponse<Void> 성공시 success헤더만 보냄
	 */
	@PostMapping
	public ApiResponse<Void> createTag(@Valid @ModelAttribute CreateTagRequest createTagRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult,
			new DeleteTagRequestFormException(bindingResult.getFieldErrors().toString()));
		tagService.createTag(createTagRequest);
		return new ApiResponse<>(new ApiResponse.Header(true, 201));
	}

	/**
	 * Tag 제거
	 *
	 * @param deleteTagRequest 제거할 태그의 id
	 * @return ApiResponse<Void> 성공시 success헤더만 보냄
	 */
	@DeleteMapping
	public ApiResponse<Void> deleteTag(@Valid @ModelAttribute DeleteTagRequest deleteTagRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult,
			new DeleteTagRequestFormException(bindingResult.getFieldErrors().toString()));
		tagService.deleteTag(deleteTagRequest);
		return new ApiResponse<>(new ApiResponse.Header(true, HttpStatus.NO_CONTENT.value()),
			new ApiResponse.Body<>(null));

	}

	/**
	 * Tag 업데이트
	 *
	 * @param updateTagRequest 업데이트 할 테그의 id 및 이름
	 * @return 성공시 success헤더만 보냄
	 */
	@PutMapping
	public ApiResponse<Long> updateTag(@Valid @RequestBody UpdateTagRequest updateTagRequest,
		BindingResult bindingResult) {
		ValidationUtils.validateBindingResult(bindingResult,
			new DeleteTagRequestFormException(bindingResult.getFieldErrors().toString()));
		Long id = tagService.updateTag(updateTagRequest);
		return ApiResponse.success(id);
	}

	/**
	 * 관리자 페이지에서 태그 목록 불러오기
	 * @param page 페이지
	 * @param size 사이즈
	 * @return 관리자 페이지의 해당 페이지 태그 목록
	 */
	@GetMapping("/admin")
	public ApiResponse<Page<TagResponse>> readAllAdminTags(@RequestParam("page") int page,
		@RequestParam("size") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<TagResponse> response = tagService.getAllAdminTags(pageable);
		return ApiResponse.success(response);
	}
}
