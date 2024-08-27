package com.nhnacademy.bookstore.book.simplereview.repository;

import com.nhnacademy.bookstore.entity.simplereview.SimpleReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleReviewRepository extends JpaRepository<SimpleReview, Long> {
}
