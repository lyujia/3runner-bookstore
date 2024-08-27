package com.nhnacademy.bookstore.book.booklike.controller;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.booklike.service.BookLikeService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * 도서 좋아요 컨트롤러입니다.
 *
 * @author 김은비
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore")
public class BookLikeController {
    private final BookLikeService bookLikeService;

    @GetMapping("/mypage/books/likes")
    public ApiResponse<Page<BookListResponse>> readAllBookLikesByMemberId(
            @RequestHeader(value = "Member-Id") Long memberId,
            @RequestParam int page,
            @RequestParam int size
    ) {
        log.info("book like controller : memberId={}, page={}, size={}", memberId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<BookListResponse> bookResponse = bookLikeService.findBookLikeByMemberId(memberId, pageable);
        return ApiResponse.success(bookResponse);
    }

    @GetMapping("/{bookId}/likes/status")
    public ApiResponse<Boolean> isBookLikedByMember(@PathVariable("bookId") Long bookId, @RequestHeader("Member-Id") Long memberId) {
        try {
            boolean isLiked = bookLikeService.isBookLikedByMember(bookId, memberId);
            return ApiResponse.success(isLiked);
        } catch (Exception e) {
            log.error("Error checking like status for book ID: " + bookId + " and member ID: " + memberId, e);
            return ApiResponse.fail(500, new ApiResponse.Body<>(false));
        }
    }

    /**
     * 좋아요 생성 메서드입니다.
     *
     * @param bookId   도서 아이디
     * @param memberId 멤버 아이디
     */
    @PostMapping("/{bookId}/like")
    public ApiResponse<Void> createBookLike(@PathVariable Long bookId, @RequestHeader("Member-Id") Long memberId) {
        log.info("deleteBookLike: bookId={}, memberId={}", bookId, memberId);
        bookLikeService.createBookLike(bookId, memberId);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }

    /**
     * 도서 좋아요 카운트 메서드입니다.
     *
     * @param bookId 도서 아이디
     * @return 카운트
     */
    @GetMapping("/{bookId}/likes")
    public ApiResponse<Long> countLikeByBookId(@PathVariable Long bookId) {
        long cnt = bookLikeService.countLikeByBookId(bookId);
        return ApiResponse.success(cnt);
    }

    /**
     * 도서 좋아요 삭제 메서드입니다.
     *
     * @param bookId   도서 아이디
     * @param memberId 멤버 아이디
     */
    @DeleteMapping("/{bookId}/like/delete")
    public ApiResponse<Void> deleteBookLike(@PathVariable Long bookId, @RequestHeader("Member-Id") Long memberId) {
        bookLikeService.deleteBookLike(bookId, memberId);
        return new ApiResponse<>(new ApiResponse.Header(true, 200));
    }
}
