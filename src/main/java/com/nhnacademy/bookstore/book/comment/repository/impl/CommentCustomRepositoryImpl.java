package com.nhnacademy.bookstore.book.comment.repository.impl;

import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import com.nhnacademy.bookstore.book.comment.repository.CommentCustomRepository;
import com.nhnacademy.bookstore.entity.comment.QComment;
import com.nhnacademy.bookstore.entity.comment.enums.CommentStatus;
import com.nhnacademy.bookstore.entity.member.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 리뷰 댓글 기능 커스텀 레포지토리 구현체입니다.
 *
 * @author 김은비
 */
public class CommentCustomRepositoryImpl implements CommentCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private static final QComment qComment = QComment.comment;
    private static final QMember qMember = QMember.member;

    public CommentCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 리뷰 아이디로 댓글 조회하는 쿼리입니다.
     * 삭제된 댓글은 보이지 않습니다.
     *
     * @param reviewId 리뷰 아이디
     * @param pageable 페이지 객체
     * @return 댓글 리스트
     */
    @Override
    public Page<CommentResponse> readAllCommentsByReviewId(long reviewId, Pageable pageable) {
        List<CommentResponse> commentResponses = jpaQueryFactory.select(
                        Projections.constructor(CommentResponse.class,
                                qComment.id,
                                qComment.content,
                                qMember.email,
                                qComment.createdAt))
                .from(qComment)
                .join(qComment.member, qMember)
                .where(qComment.review.id.eq(reviewId).and(qComment.status.eq(CommentStatus.ON)))
                .orderBy(qComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = Optional.ofNullable(jpaQueryFactory
                .select(qComment.count())
                .from(qComment)
                .where(qComment.review.id.eq(reviewId).and(qComment.status.eq(CommentStatus.ON)))
                .fetchOne()).orElse(0L);
        return new PageImpl<>(commentResponses, pageable, totalCount);
    }

    /**
     * 사용자 아이디로 댓글 조회하는 메서드입니다.
     * 삭제된 댓글은 보이지 않습니다.
     *
     * @param memberId 사용자 아이디
     * @param pageable 페이지 객체
     * @return 댓글 리스트
     */
    @Override
    public Page<CommentResponse> readAllCommentByMemberId(long memberId, Pageable pageable) {
        List<CommentResponse> commentResponses = jpaQueryFactory.select(
                        Projections.constructor(CommentResponse.class,
                                qComment.id,
                                qComment.content,
                                qMember.email,
                                qComment.createdAt))
                .from(qComment)
                .join(qComment.member, qMember)
                .where(qComment.member.id.eq(memberId).and(qComment.status.eq(CommentStatus.ON)))
                .orderBy(qComment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long totalCount = Optional.ofNullable(jpaQueryFactory
                .select(qComment.count())
                .from(qComment)
                .where(qComment.member.id.eq(memberId).and(qComment.status.eq(CommentStatus.ON)))
                .fetchOne()).orElse(0L);
        return new PageImpl<>(commentResponses, pageable, totalCount);
    }
}
