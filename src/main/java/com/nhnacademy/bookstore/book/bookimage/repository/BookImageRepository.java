package com.nhnacademy.bookstore.book.bookimage.repository;

import com.nhnacademy.bookstore.entity.bookimage.BookImage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
}
