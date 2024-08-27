package com.nhnacademy.bookstore.entity.comment;

import com.nhnacademy.bookstore.entity.comment.enums.CommentStatus;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.review.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String content;

    @NotNull
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime deletedAt;

    @NotNull
    private CommentStatus status;

    @ManyToOne
    @NotNull
    @Setter
    private Review review;

    @ManyToOne
    @Setter
    private Member member;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * 댓글 삭제 메서드입니다.
     *
     * @author 김은비
     */
    public void deletedComment() {
        this.deletedAt = ZonedDateTime.now();
        this.status = CommentStatus.DELETE;
    }

    /**
     * 댓글 생성 메서드입니다.
     *
     * @param content 댓글 내용
     * @param review  댓글이 달린 리뷰
     * @param member  댓글 작성자
     * @return 생성된 Comment 객체
     */
    public static Comment createComment(String content, Review review, Member member) {
        Comment comment = new Comment();
        comment.content = content;
        comment.review = review;
        comment.member = member;
        comment.status = CommentStatus.ON;
        comment.createdAt = ZonedDateTime.now();
        return comment;
    }
}
