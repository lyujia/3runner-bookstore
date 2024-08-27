package com.nhnacademy.bookstore.bookLike.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.booklike.repository.BookLikeRepository;
import com.nhnacademy.bookstore.book.booklike.repository.impl.BookLikeCustomRepositoryImpl;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booklike.BookLike;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;

import jakarta.persistence.EntityManager;

/**
 * 도서-좋아요 repo 테스트입니다.
 *
 * @author 김은비
 */
@DataJpaTest
@Import(BookLikeCustomRepositoryImpl.class)
public class BookLikeRepositoryTest {
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BookLikeRepository bookLikeRepository;

	private Member member2;
	private Book book1;

	@BeforeEach
	public void setUp() {
		Member member1 = new Member(CreateMemberRequest.builder()
			.password("111111")
			.name("1")
			.age(1)
			.phone("1")
			.birthday("2024-05-28")
			.email("dfdaf@nav.com")
			.build());
		entityManager.persist(member1);

		member2 = new Member(CreateMemberRequest.builder()
			.password("111111")
			.name("1")
			.age(1)
			.phone("1")
			.birthday("2024-05-28")
			.email("dfdaf2@nav.com")
			.build());
		entityManager.persist(member2);

        book1 = new Book(
                "책1",
                "책1입니다.",
                ZonedDateTime.now(),
                1000,
                10,
                900,
                0,
                true,
                "작가",
                "123456789",
                "출판사",
                null,
                null,
                null
        );
        entityManager.persist(book1);

        Book book2 = new Book(
                "책2",
                "책2입니다.",
                ZonedDateTime.now(),
                1000,
                10,
                900,
                0,
                true,
                "작가r",
                "1234567891",
                "출판사",
                null,
                null,
                null
        );
        entityManager.persist(book2);

		BookLike bookLike1 = new BookLike();
		bookLike1.setBook(book1);
		bookLike1.setMember(member1);
		entityManager.persist(bookLike1);

		BookLike bookLike2 = new BookLike();
		bookLike2.setBook(book2);
		bookLike2.setMember(member2);
		entityManager.persist(bookLike2);

		BookLike bookLike3 = new BookLike();
		bookLike3.setBook(book1);
		bookLike3.setMember(member2);
		entityManager.persist(bookLike3);

		entityManager.flush();
	}

	@Test
	public void testFindBookLikeByMemberId() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<BookListResponse> bookListResponsePage = bookLikeRepository.findBookLikeByMemberId(member2.getId(),
			pageable);

		assertThat(bookListResponsePage.getContent()).hasSize(2);
		List<BookListResponse> content = bookListResponsePage.getContent();
		assertThat(content).extracting(BookListResponse::title).containsExactlyInAnyOrder("책1", "책2");
	}

    @Test
    void testCountLikeByBookId() {
        long count = bookLikeRepository.countLikeByBookId(book1.getId());
        assertThat(count).isEqualTo(2);
    }
}
