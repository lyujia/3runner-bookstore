package com.nhnacademy.bookstore.book.bookcategory.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.CreateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.UpdateBookCategoryRequest;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;

public interface BookCategoryService {
	void createBookCategory(CreateBookCategoryRequest dto);

	void updateBookCategory(long bookCategoryId, UpdateBookCategoryRequest dto);

	void deletedBookCategory(Long id);

	List<CategoryParentWithChildrenResponse> readBookWithCategoryList(Long bookId);

	Page<BookListResponse> readCategoriesWithBookList(List<Long> categoryList, Pageable pageable);

	List<CategoryParentWithChildrenResponse> allCategoryList();

	List<String> readBookCategoryNames(Long bookId);
}

