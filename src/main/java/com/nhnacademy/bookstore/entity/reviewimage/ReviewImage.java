package com.nhnacademy.bookstore.entity.reviewimage;

import com.nhnacademy.bookstore.entity.review.Review;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@ManyToOne(cascade = CascadeType.ALL)
	private Review review;

	@OneToOne(cascade = CascadeType.ALL)
	private TotalImage totalImage;

	public ReviewImage(Review review, TotalImage totalImage) {
		this.review = review;
		this.totalImage = totalImage;
	}
}
