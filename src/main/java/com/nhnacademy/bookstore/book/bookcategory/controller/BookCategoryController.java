package com.nhnacademy.bookstore.book.bookcategory.controller;

import com.nhnacademy.bookstore.book.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/books/{bookId}/categories")
public class BookCategoryController {
    private final BookCategoryService bookCategoryService;

    @GetMapping
    public ApiResponse<List<CategoryParentWithChildrenResponse>> readCategories(
            @PathVariable Long bookId) {
        return ApiResponse.success(bookCategoryService.readBookWithCategoryList(bookId));
    }
}
