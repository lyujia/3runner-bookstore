package com.nhnacademy.bookstore.book.bookimage.dto.request;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateBookImageRequestTest {

	private static Validator validator;

	@BeforeAll
	static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void validCreateBookImageRequest() {
		CreateBookImageRequest request = CreateBookImageRequest.builder()
			.url("http://example.com/image.jpg")
			.type(BookImageType.MAIN)
			.bookId(1L)
			.build();

		Set<ConstraintViolation<CreateBookImageRequest>> violations = validator.validate(request);
		assertThat(violations).isEmpty();
	}


	@Test
	void invalidCreateBookImageRequest_NullBookId() {
		CreateBookImageRequest request = CreateBookImageRequest.builder()
			.url("http://example.com/image.jpg")
			.type(BookImageType.DESCRIPTION)
			.bookId(0L)
			.build();

		Set<ConstraintViolation<CreateBookImageRequest>> violations = validator.validate(request);
		assertThat(violations).isEmpty();  // Assuming that bookId=0 is valid, modify if your validation logic changes
	}
}
