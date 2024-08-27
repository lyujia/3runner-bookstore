package com.nhnacademy.bookstore.entity.bookcategory;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.category.Category;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@Setter
	private Book book;

	@ManyToOne
	@Setter
	private Category category;

	public static BookCategory create(Book book, Category category) {
		BookCategory bookCategory = BookCategory.builder()
			.book(book)
			.category(category)
			.build();
		book.addBookCategory(bookCategory);  // 양방향 설정
		return bookCategory;
	}
}
