package com.nhnacademy.bookstore.book.review.repository.impl;

import com.nhnacademy.bookstore.book.review.dto.response.ReviewAdminListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewDetailResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import com.nhnacademy.bookstore.book.review.repository.ReviewCustomRepository;
import com.nhnacademy.bookstore.entity.book.QBook;
import com.nhnacademy.bookstore.entity.member.QMember;
import com.nhnacademy.bookstore.entity.purchase.QPurchase;
import com.nhnacademy.bookstore.entity.purchasebook.QPurchaseBook;
import com.nhnacademy.bookstore.entity.review.QReview;
import com.nhnacademy.bookstore.entity.review.enums.ReviewStatus;
import com.nhnacademy.bookstore.entity.reviewimage.QReviewImage;
import com.nhnacademy.bookstore.entity.reviewlike.QReviewLike;
import com.nhnacademy.bookstore.entity.totalimage.QTotalImage;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 리뷰 커스텀 인터페이스 구현체입니다.
 *
 * @author 김은비
 */
@Repository
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private static final QMember qMember = QMember.member;
    private static final QPurchase qPurchase = QPurchase.purchase;
    private static final QPurchaseBook qPurchaseBook = QPurchaseBook.purchaseBook;
    private static final QReview qReview = QReview.review;
    private static final QBook qBook = QBook.book;
    private static final QTotalImage qTotalImage = QTotalImage.totalImage;
    private static final QReviewImage qReviewImage = QReviewImage.reviewImage;
    private static final QReviewLike qReviewLike = QReviewLike.reviewLike;

    public ReviewCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 사용자가 자신이 구매한 도서인지 확인하는 메서드입니다.
     * 주문 상태가 확정인지 같이 확인합니다.
     *
     * @param purchaseBookId 주문-도서 아이디
     * @param memberId       멤버 아이디
     * @return 존재하면 true
     */
    @Override
    public boolean existByPurchaseBook(long purchaseBookId, long memberId) {
        Integer count = jpaQueryFactory
                .selectOne()
                .from(qPurchaseBook)
                .join(qPurchaseBook.purchase, qPurchase)
                .where(qPurchaseBook.id.eq(purchaseBookId)
                        .and(qPurchase.member.id.eq(memberId)))
//                        .and(qPurchase.status.eq(PurchaseStatus.CONFIRMATION)))
                .fetchFirst();
        return count != null && count > 0;
    }

    /**
     * 리뷰 상세 조회 쿼리입니다.
     *
     * @param reviewId 리뷰 아이디
     * @return 조회된 리뷰
     */
    @Override
    public ReviewDetailResponse getReviewDetail(long reviewId) {
        return jpaQueryFactory
                .select(Projections.constructor(ReviewDetailResponse.class,
                        qBook.id,
                        qBook.title,
                        qReview.id,
                        qReview.title,
                        qReview.content,
                        qReview.rating,
                        qMember.email,
                        qReview.createdAt,
                        qReview.updated,
                        qReview.updatedAt
                ))
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .join(qPurchaseBook.book, qBook)
                .join(qPurchaseBook.purchase, qPurchase)
                .join(qPurchase.member, qMember)
                .where(qReview.id.eq(reviewId))
                .fetchOne();
    }

    /**
     * 리뷰 전체 조회 쿼리입니다.
     * 관리자가 리뷰를 삭제하기 위해 필요한 메서드입니다.
     *
     * @param pageable 페이지 객체
     * @return 리뷰 페이지
     */
    @Override
    public Page<ReviewAdminListResponse> getReviewList(Pageable pageable) {
        List<ReviewAdminListResponse> reviewListResponses = jpaQueryFactory
                .select(Projections.constructor(ReviewAdminListResponse.class,
                        qReview.id,
                        qReview.title,
                        qTotalImage.url,
                        qReview.rating,
                        qMember.email,
                        qReview.createdAt,
                        qReview.deletedAt,
                        qReview.deletedReason,
                        JPAExpressions.select(qReviewLike.count())
                                .from(qReviewLike)
                                .where(qReviewLike.review.id.eq(qReview.id))
                ))
                .distinct()
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .join(qPurchaseBook.book, qBook)
                .join(qPurchaseBook.purchase, qPurchase)
                .join(qPurchase.member, qMember)
                .leftJoin(qTotalImage).on(qTotalImage.id.eq(
                        JPAExpressions.select(qReviewImage.totalImage.id.min())
                                .from(qReviewImage)
                                .where(qReviewImage.review.id.eq(qReview.id))
                ))
                .leftJoin(qReviewLike).on(qReviewLike.review.id.eq(qReview.id))
                .groupBy(qReview.id, qReview.title, qTotalImage.url, qReview.rating, qMember.email, qReview.createdAt, qReview.deletedAt)
                .orderBy(getSort(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = Optional.ofNullable(jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(reviewListResponses, pageable, totalCount);
    }

    /**
     * 책 아이디로 리뷰 조회하는 쿼리입니다.
     * 삭제된 리뷰는 보이지 않습니다.
     *
     * @param bookId   책 아이디
     * @param pageable 페이지 객체
     * @return 리뷰 페이지
     */
    @Override
    public Page<ReviewListResponse> getReviewsByBookId(long bookId, Pageable pageable) {
        List<ReviewListResponse> reviewListResponses = jpaQueryFactory
                .select(Projections.constructor(ReviewListResponse.class,
                        qReview.id,
                        qReview.title,
                        qTotalImage.url,
                        qReview.rating,
                        qMember.email,
                        qReview.createdAt,
                        JPAExpressions.select(qReviewLike.count())
                                .from(qReviewLike)
                                .where(qReviewLike.review.id.eq(qReview.id))
                ))
                .distinct()
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .join(qPurchaseBook.book, qBook)
                .join(qPurchaseBook.purchase, qPurchase)
                .join(qPurchase.member, qMember)
                .leftJoin(qTotalImage).on(qTotalImage.id.eq(
                        JPAExpressions.select(qReviewImage.totalImage.id.min())
                                .from(qReviewImage)
                                .where(qReviewImage.review.id.eq(qReview.id))
                ))
                .leftJoin(qReviewLike).on(qReviewLike.review.id.eq(qReview.id))
                .groupBy(qReview.id, qReview.title, qTotalImage.url, qReview.rating, qMember.email, qReview.createdAt)
                .orderBy(getSort(pageable.getSort()))
                .where(qPurchaseBook.book.id.eq(bookId)
                        .and(qReview.reviewStatus.eq(ReviewStatus.ON)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .where(qPurchaseBook.book.id.eq(bookId).and(qReview.reviewStatus.eq(ReviewStatus.ON)))
                .fetchOne()).orElse(0L);
        return new PageImpl<>(reviewListResponses, pageable, total);
    }


    private OrderSpecifier<?>[] getSort(Sort sort) {
        List<OrderSpecifier<?>> orders = sort.stream()
                .map(order -> {
                    String property = order.getProperty();
                    boolean isAscending = order.isAscending();

                    return switch (property) {
                        case "createdAt" -> new OrderSpecifier<>(
                                isAscending ? Order.ASC : Order.DESC,
                                qReview.createdAt);
                        case "likes" -> new OrderSpecifier<>(
                                isAscending ? Order.ASC : Order.DESC,
                                qReviewLike.count());
                        case "title" -> new OrderSpecifier<>(
                                isAscending ? Order.ASC : Order.DESC,
                                qReview.title);
                        default -> throw new IllegalArgumentException("정렬 기준이 잘못되었습니다!!: " + property);
                    };
                })
                .collect(Collectors.toList());

        // 고유 정렬 키 추가
        orders.add(new OrderSpecifier<>(Order.ASC, qReview.id));

        return orders.toArray(new OrderSpecifier[0]);
    }


    /**
     * 사용자 아이디로 리뷰 조회하는 쿼리입니다.
     * 삭제된 리뷰는 보이지 않습니다.
     *
     * @param memberId 멤버 아이디
     * @param pageable 페이지 객체
     * @return 리뷰 페이지
     */
    @Override
    public Page<ReviewListResponse> getReviewsByUserId(long memberId, Pageable pageable) {
        List<ReviewListResponse> reviewListResponses = jpaQueryFactory
                .select(Projections.constructor(ReviewListResponse.class,
                        qReview.id,
                        qReview.title,
                        qTotalImage.url,
                        qReview.rating,
                        qMember.email,
                        qReview.createdAt,
                        JPAExpressions.select(qReviewLike.count())
                                .from(qReviewLike)
                                .where(qReviewLike.review.id.eq(qReview.id))
                ))
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .join(qPurchaseBook.purchase, qPurchase)
                .join(qPurchase.member, qMember)
                .leftJoin(qTotalImage).on(qTotalImage.id.eq(
                        JPAExpressions.select(qReviewImage.totalImage.id.min())
                                .from(qReviewImage)
                                .where(qReviewImage.review.id.eq(qReview.id))
                ))
                .where(qPurchase.member.id.eq(memberId)
                        .and(qReview.reviewStatus.eq(ReviewStatus.ON)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .join(qPurchaseBook.purchase, qPurchase)
                .where(qPurchase.member.id.eq(memberId).and(qReview.reviewStatus.eq(ReviewStatus.ON)))
                .fetchOne()).orElse(0L);
        return new PageImpl<>(reviewListResponses, pageable, total);
    }

    @Override
    public Double getAverageRatingByBookId(long bookId) {
        return jpaQueryFactory
                .select(qReview.rating.avg())
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .where(qPurchaseBook.book.id.eq(bookId)
                        .and(qReview.reviewStatus.eq(ReviewStatus.ON)))
                .fetchOne();
    }

    @Override
    public Long countReviewsByBookId(long bookId) {
        return jpaQueryFactory
                .select(qReview.count())
                .from(qReview)
                .join(qReview.purchaseBook, qPurchaseBook)
                .where(qPurchaseBook.book.id.eq(bookId)
                        .and(qReview.reviewStatus.eq(ReviewStatus.ON)))
                .fetchOne();
    }
}
