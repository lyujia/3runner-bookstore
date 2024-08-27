package com.nhnacademy.bookstore.global.elastic.book.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import com.nhnacademy.bookstore.global.elastic.document.book.BookDocument;

public interface ElasticSearchCustomBookRepository {
	// List<BookDocument> searchBooks(String keyword);

	SearchHits<BookDocument> searchProductsByProductName(String keyword, Pageable pageable);
}
