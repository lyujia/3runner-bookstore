package com.nhnacademy.bookstore.book.tag.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.tag.dto.request.CreateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.DeleteTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.UpdateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;

/**
 * Tag CRUD 서비스
 * @author 정주혁
 */
public interface TagService {

	List<TagResponse> getAllTags();

	Long createTag(CreateTagRequest tag);

	void deleteTag(DeleteTagRequest tag);

	Long updateTag(UpdateTagRequest tag);

	Page<TagResponse> getAllAdminTags(Pageable pageable);
}
