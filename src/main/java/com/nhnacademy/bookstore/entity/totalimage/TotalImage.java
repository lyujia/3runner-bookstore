package com.nhnacademy.bookstore.entity.totalimage;

import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.reviewimage.ReviewImage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TotalImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Getter
	@NotBlank
	@Column(unique = true)
	@Size(max = 40)
	private String url;

	@OneToOne(mappedBy = "totalImage", cascade = CascadeType.ALL)
	private BookImage bookImage;

	@OneToOne(mappedBy = "totalImage", cascade = CascadeType.ALL)
	private ReviewImage reviewImage;

	public TotalImage(String url) {
		this.url = url;
	}
}
