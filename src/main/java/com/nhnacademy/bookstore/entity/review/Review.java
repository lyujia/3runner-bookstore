package com.nhnacademy.bookstore.entity.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nhnacademy.bookstore.entity.comment.Comment;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.review.enums.ReviewStatus;
import com.nhnacademy.bookstore.entity.reviewimage.ReviewImage;
import com.nhnacademy.bookstore.entity.reviewlike.ReviewLike;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private PurchaseBook purchaseBook;

    @NotNull
    @Size(min = 1, max = 50)
    @Setter
    private String title;

    @NotNull
    @Lob
    @Column(columnDefinition = "TEXT")
    @Setter
    private String content;

    @NotNull
    @Setter
    private double rating;

    @NotNull
    @Setter
    private boolean updated;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private ZonedDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private ZonedDateTime updatedAt;

    @Setter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private ZonedDateTime deletedAt;

    @Setter
    private ReviewStatus reviewStatus;

    @Size(min = 1, max = 500)
    @Setter
    private String deletedReason;

    // 연결

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImageList = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> reviewLikeList = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
        this.updated = true;
    }

    public void addReviewLike(ReviewLike reviewLike) {
        this.reviewLikeList.add(reviewLike);
        reviewLike.setReview(this);
    }

    @Builder
    public Review(PurchaseBook purchaseBook, String title, String content, double rating, List<ReviewImage> reviewImageList,
                  List<Comment> commentList, List<ReviewLike> reviewLikeList) {
        this.purchaseBook = purchaseBook;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.reviewStatus = ReviewStatus.ON;
        this.reviewImageList = reviewImageList != null ? reviewImageList : new ArrayList<>();
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        this.reviewLikeList = reviewLikeList != null ? reviewLikeList : new ArrayList<>();
    }
}
