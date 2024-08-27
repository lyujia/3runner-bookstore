package com.nhnacademy.bookstore.book.review.repository;

import com.nhnacademy.bookstore.entity.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewCustomRepository {
}
