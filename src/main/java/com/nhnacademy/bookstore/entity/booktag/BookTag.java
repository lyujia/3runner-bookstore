package com.nhnacademy.bookstore.entity.booktag;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.tag.Tag;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "book_tag", indexes = {
//	@Index(name = "idx_book_id", columnList = "book_id"),
	@Index(name = "idx_tag_id", columnList = "tag_id")
})
public class BookTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Setter
	@ManyToOne
	private Book book;

	@ManyToOne
	private Tag tag;

	public BookTag(Book book, Tag tag) {
		this.book = book;
		this.tag = tag;
	}
}
