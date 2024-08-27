package com.nhnacademy.bookstore.book.comment.service;

import com.nhnacademy.bookstore.book.comment.dto.request.CreateCommentRequest;
import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 댓글 서비스 인터페이스입니다.
 *
 * @author 김은비
 */
public interface CommentService {
    void createComment(long reviewId, long memberId, CreateCommentRequest createCommentRequest);

    void updateComment(long commentId, long memberId, CreateCommentRequest createCommentRequest);

    void deleteComment(long commentId, long memberId);

    Page<CommentResponse> readAllCommentsByReviewId(Long reviewId, Pageable pageable);

    Page<CommentResponse> readAllCommentsByMemberId(Long memberId, Pageable pageable);
}
