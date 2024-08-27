package com.nhnacademy.bookstore.book.tag.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;

public interface TagCustomRepository {

	Page<TagResponse> readAdminBookList(Pageable pageable);
}
