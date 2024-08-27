package com.nhnacademy.bookstore.entity.bookimage;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Table(name = "book_image", indexes = {
	@Index(name = "idx_book_id", columnList = "book_id")
})

public class BookImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	private BookImageType type;

	@Setter
	@ManyToOne
	private Book book;

	@OneToOne(cascade = CascadeType.ALL)
	private TotalImage totalImage;

	public BookImage(BookImageType type, Book book, TotalImage totalImage) {
		this.type = type;
		this.book = book;
		this.totalImage = totalImage;
	}

	public BookImage(BookImageType type, TotalImage totalImage) {
		this.type = type;
		this.totalImage = totalImage;
	}

}
