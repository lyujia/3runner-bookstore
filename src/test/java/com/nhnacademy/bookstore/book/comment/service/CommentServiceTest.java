package com.nhnacademy.bookstore.book.comment.service;

import com.nhnacademy.bookstore.book.comment.dto.request.CreateCommentRequest;
import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import com.nhnacademy.bookstore.book.comment.exception.CommentNotExistsException;
import com.nhnacademy.bookstore.book.comment.exception.UnauthorizedCommentAccessException;
import com.nhnacademy.bookstore.book.comment.repository.CommentRepository;
import com.nhnacademy.bookstore.book.comment.service.impl.CommentServiceImpl;
import com.nhnacademy.bookstore.book.review.exception.ReviewNotExistsException;
import com.nhnacademy.bookstore.book.review.repository.ReviewRepository;
import com.nhnacademy.bookstore.entity.comment.Comment;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("댓글 생성 테스트")
    @Test
    void createComment() {
        // Given
        long reviewId = 1L;
        long memberId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("좋은 댓글입니다");
        Member member = new Member();
        member.setId(memberId);
        Review review = new Review();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When
        commentService.createComment(reviewId, memberId, request);

        // Then
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();
        assertThat(savedComment.getContent()).isEqualTo("좋은 댓글입니다");
        assertThat(savedComment.getReview()).isEqualTo(review);
        assertThat(savedComment.getMember()).isEqualTo(member);
    }

    @DisplayName("댓글 생성 실패 - 존재하지 않는 멤버")
    @Test
    void createComment_MemberNotExists() {
        long reviewId = 1L;
        long memberId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("좋은 댓글입니다");

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> commentService.createComment(reviewId, memberId, request));
    }

    @DisplayName("댓글 생성 실패 - 존재하지 않는 리뷰")
    @Test
    void createComment_ReviewNotExists() {
        long reviewId = 1L;
        long memberId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("좋은 댓글입니다");
        Member member = new Member();
        member.setId(memberId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(ReviewNotExistsException.class, () -> commentService.createComment(reviewId, memberId, request));
    }

    @DisplayName("댓글 수정 테스트")
    @Test
    void updateComment() {
        long commentId = 1L;
        long memberId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("수정된 댓글입니다");
        Member member = new Member();
        member.setId(memberId);
        Comment comment = new Comment();
        comment.setMember(member);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.updateComment(commentId, memberId, request);

        assertThat(comment.getContent()).isEqualTo("수정된 댓글입니다");
    }

    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    @Test
    void updateComment_CommentNotExists() {
        long commentId = 1L;
        long memberId = 1L;
        CreateCommentRequest request = new CreateCommentRequest("수정된 댓글입니다");

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotExistsException.class, () -> commentService.updateComment(commentId, memberId, request));
    }

    @DisplayName("댓글 수정 실패 - 권한 없음")
    @Test
    void updateComment_UnauthorizedAccess() {
        long commentId = 1L;
        long memberId = 1L;
        long otherMemberId = 2L;
        CreateCommentRequest request = new CreateCommentRequest("수정된 댓글입니다");
        Member member = new Member();
        member.setId(otherMemberId);
        Comment comment = new Comment();
        comment.setMember(member);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedCommentAccessException.class, () -> commentService.updateComment(commentId, memberId, request));
    }

    @DisplayName("댓글 삭제 테스트")
    @Test
    void deleteComment() {
        long commentId = 1L;
        long memberId = 1L;
        Member member = new Member();
        member.setId(memberId);
        Comment comment = new Comment();
        comment.setMember(member);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId, memberId);
        verify(commentRepository, times(1)).findById(commentId);
    }

    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
    @Test
    void deleteComment_CommentNotExists() {
        long commentId = 1L;
        long memberId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotExistsException.class, () -> commentService.deleteComment(commentId, memberId));
    }

    @DisplayName("댓글 삭제 실패 - 권한 없음")
    @Test
    void deleteComment_UnauthorizedAccess() {
        long commentId = 1L;
        long memberId = 1L;
        long otherMemberId = 2L;
        Member member = new Member();
        member.setId(otherMemberId);
        Comment comment = new Comment();
        comment.setMember(member);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(UnauthorizedCommentAccessException.class, () -> commentService.deleteComment(commentId, memberId));
    }

    @DisplayName("리뷰 아이디로 댓글 조회 테스트")
    @Test
    void readAllCommentsByReviewId() {
        long reviewId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentResponse> commentPage = new PageImpl<>(Collections.emptyList());

        when(commentRepository.readAllCommentsByReviewId(reviewId, pageable)).thenReturn(commentPage);

        Page<CommentResponse> result = commentService.readAllCommentsByReviewId(reviewId, pageable);

        assertThat(result).isEqualTo(commentPage);
        verify(commentRepository, times(1)).readAllCommentsByReviewId(reviewId, pageable);
    }

    @DisplayName("멤버 아이디로 댓글 조회 테스트")
    @Test
    void readAllCommentsByMemberId() {
        long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentResponse> commentPage = new PageImpl<>(Collections.emptyList());

        when(commentRepository.readAllCommentByMemberId(memberId, pageable)).thenReturn(commentPage);

        Page<CommentResponse> result = commentService.readAllCommentsByMemberId(memberId, pageable);

        assertThat(result).isEqualTo(commentPage);
        verify(commentRepository, times(1)).readAllCommentByMemberId(memberId, pageable);
    }
}
