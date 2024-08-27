package com.nhnacademy.bookstore.book.booktag.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.tag.repository.TagRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booktag.BookTag;
import com.nhnacademy.bookstore.entity.tag.Tag;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class BookTagRepositoryTest {

	@Autowired
	private BookTagRepository bookTagRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private TagRepository tagRepository;

	private Book book;
	private Tag tag;

	@BeforeEach
	public void setUp() {

		book = new Book("Sample Book", "Sample Description", ZonedDateTime.now(), 100, 50, 80, 500, true, "12346789",
			"John Doe", "Sample Publisher", null, null, null);

		bookRepository.save(book);

		tag = new Tag();
		tag.setName("Sample Tag");
		tagRepository.save(tag);

		BookTag bookTag = new BookTag(book, tag);
		bookTagRepository.save(bookTag);
	}

	@Test
	public void testFindAllBookIdByTagId() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Book> books = bookTagRepository.findAllBookIdByTagId(tag.getId(), pageable);

		assertThat(books).isNotEmpty();
		assertThat(books.getContent().getFirst().getTitle()).isEqualTo(book.getTitle());
	}

	@Test
	public void testFindAllTagIdByBookId() {
		List<Tag> tags = bookTagRepository.findAllTagIdByBookId(book.getId());

		assertThat(tags).isNotEmpty();
		assertThat(tags.iterator().next().getName()).isEqualTo(tag.getName());
	}
}
