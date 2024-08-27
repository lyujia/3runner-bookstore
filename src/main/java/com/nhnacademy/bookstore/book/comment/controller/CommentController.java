package com.nhnacademy.bookstore.book.comment.controller;

import com.nhnacademy.bookstore.book.comment.dto.request.CreateCommentRequest;
import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import com.nhnacademy.bookstore.book.comment.exception.CreateCommentFromException;
import com.nhnacademy.bookstore.book.comment.service.CommentService;
import com.nhnacademy.bookstore.util.ApiResponse;
import com.nhnacademy.bookstore.util.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 댓글 컨트롤러입니다.
 *
 * @author 김은비
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookstore/books/reviews")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 생성 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param memberId 사용자 아이디
     * @param request  댓글 내용 dto
     * @return ApiResponse<>
     */
    @PostMapping("/{reviewId}")
    public ApiResponse<Void> createComment(@PathVariable Long reviewId, @RequestHeader("Member-id") Long memberId,
                                           @Valid @RequestBody CreateCommentRequest request,
                                           BindingResult bindingResult) {
        ValidationUtils.validateBindingResult(bindingResult, new CreateCommentFromException());
        commentService.createComment(reviewId, memberId, request);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }

    /**
     * 댓글 수정 메서드입니다.
     *
     * @param commentId 댓글 아이디
     * @param memberId  사용자 아이디
     * @param request   댓글 내용 dto
     * @return ApiResponse<>
     */
    @PutMapping("/{commentId}")
    public ApiResponse<Void> updateComment(@PathVariable long commentId, @RequestHeader("Member-id") Long memberId, @RequestBody CreateCommentRequest request) {
        commentService.updateComment(commentId, memberId, request);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }

    /**
     * 댓글 삭제 메서드입니다.
     * 댓글의 상태를 변환합니다.
     *
     * @param commentId 댓글 아이디
     * @param memberId  사용자 아이디
     * @return ApiResponse<>
     */
    @DeleteMapping("/{commentId}/delete")
    public ApiResponse<Void> deleteComment(@PathVariable long commentId, @RequestHeader("Member-id") Long memberId) {
        commentService.deleteComment(commentId, memberId);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }

    /**
     * 리뷰 아이디로 댓글 리스트를 조회하는 메서드입니다.
     *
     * @param reviewId 리뷰 아이디
     * @param page     페이지
     * @param size     사이즈
     * @return 댓글 리스트
     */
    @GetMapping("/{reviewId}/comments")
    public ApiResponse<Page<CommentResponse>> readAllCommentsByReviewId(@PathVariable long reviewId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> commentList = commentService.readAllCommentsByReviewId(reviewId, pageable);
        return ApiResponse.success(commentList);
    }

    /**
     * 사용자가 자신의 댓글을 조회하는 메서드입니다.
     *
     * @param memberId 사용자 아이디
     * @param page     페이지
     * @param size     사이즈
     * @return 댓글 리스트
     */
    @GetMapping("/member/comments")
    public ApiResponse<Page<CommentResponse>> readAllCommentsByMemberId(@RequestHeader(value = "Member-id") Long memberId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CommentResponse> commentList = commentService.readAllCommentsByMemberId(memberId, pageable);
        return ApiResponse.success(commentList);
    }
}
