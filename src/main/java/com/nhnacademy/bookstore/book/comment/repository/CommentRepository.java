package com.nhnacademy.bookstore.book.comment.repository;

import com.nhnacademy.bookstore.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

}