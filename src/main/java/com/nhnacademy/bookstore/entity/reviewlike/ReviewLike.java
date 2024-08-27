package com.nhnacademy.bookstore.entity.reviewlike;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.review.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private ZonedDateTime createdAt;

    @ManyToOne
    @Setter
    private Member member;

    @ManyToOne
    @Setter
    private Review review;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    /**
     * 리뷰 좋아요 생성 메서드입니다.
     *
     * @param member 멤버
     * @param review 리뷰
     * @return 생성된 좋아요 객체
     */
    public static ReviewLike createReviewLike(Member member, Review review) {
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReview(review);
        reviewLike.setMember(member);
        reviewLike.createdAt = ZonedDateTime.now();
        return reviewLike;
    }
}
