package com.nhnacademy.bookstore.book.comment.repository;

import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import com.nhnacademy.bookstore.book.comment.repository.impl.CommentCustomRepositoryImpl;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.comment.Comment;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CommentCustomRepositoryImpl.class)
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private EntityManager entityManager;

    private Member member2;
    private Review review;

    @BeforeEach
    public void setUp() {
        Member member = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf@nav.com")
                .build());
        entityManager.persist(member);

        member2 = new Member(CreateMemberRequest.builder()
                .password("12345677")
                .name("1")
                .age(1)
                .phone("1")
                .birthday("2024-05-28")
                .email("dfdaf2@nav.com")
                .build());
        entityManager.persist(member2);

        Book book = new Book(
                "책1",
                "책1입니다.",
                ZonedDateTime.now(),
                1000,
                10,
                900,
                0,
                true,
                "작가",
                "123456789",
                "출판사",
                null,
                null,
                null
        );
        entityManager.persist(book);

        Purchase purchase = new Purchase(
                UUID.randomUUID(),
                PurchaseStatus.CONFIRMATION,
                100,
                10,
                ZonedDateTime.now(),
                "road",
                "password",
                ZonedDateTime.now(),
                true,
                MemberType.MEMBER,
                null);
        entityManager.persist(purchase);

        PurchaseBook purchaseBook = new PurchaseBook(book, 1, 100, purchase);
        entityManager.persist(purchaseBook);

        review = new Review(
                purchaseBook,
                "리뷰입니다.",
                "아주 추천합니다. 좋은 책입니다.",
                4.5,
                null,
                null,
                null
        );
        entityManager.persist(review);

        Comment comment = Comment.createComment("잘 보고 갑니다!", review, member2);
        entityManager.persist(comment);
    }

    @DisplayName("댓글 저장 테스트")
    @Test
    void saveComment() {
        Comment comment2 = Comment.createComment("좋은 리뷰 잘 보고 갑니다!", review, member2);
        commentRepository.save(comment2);
        assertThat(comment2.getId()).isEqualTo(4L);
    }

    @DisplayName("리뷰 아이디로 댓글 조회 테스트 ")
    @Test
    void readAllCommentsByReviewId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentResponse> commentList = commentRepository.readAllCommentsByReviewId(review.getId(), pageable);

        assertThat(commentList).isNotNull();
        assertThat(commentList.getContent()).hasSize(1);
        assertThat(commentList.getContent().getFirst().commentId()).isEqualTo(2L);
        assertThat(commentList.getContent().getFirst().content()).isEqualTo("잘 보고 갑니다!");
        assertThat(commentList.getContent().getFirst().memberEmail()).isEqualTo("dfdaf2@nav.com");
    }

    @DisplayName("사용자 아이디로 댓글 조회 테스트")
    @Test
    void readAllCommentsByMemberId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CommentResponse> commentList = commentRepository.readAllCommentByMemberId(member2.getId(), pageable);

        assertThat(commentList).isNotNull();
        assertThat(commentList.getContent()).hasSize(1);
        assertThat(commentList.getContent().getFirst().commentId()).isEqualTo(1L);
        assertThat(commentList.getContent().getFirst().content()).isEqualTo("잘 보고 갑니다!");
        assertThat(commentList.getContent().getFirst().memberEmail()).isEqualTo("dfdaf2@nav.com");
    }
}
