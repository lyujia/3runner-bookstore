package com.nhnacademy.bookstore.purchase.purchasebook.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.MemberType;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.impl.PurchaseBookCustomRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(PurchaseBookCustomRepositoryImpl.class)
public class PurchaseBookCustomRepositoryImplTest {

	@Autowired
	private PurchaseBookCustomRepository purchaseBookCustomRepository;

	@PersistenceContext
	private EntityManager entityManager;

	private Book book;
	private Purchase purchase;
	private PurchaseBook purchaseBook;
	private BookImage bookImage;
	private TotalImage totalImage;

	@BeforeEach
	void setUp() {
		book = new Book();
		book.setTitle("Sample Book");
		book.setAuthor("Author");
		book.setPrice(1000);
		book.setSellingPrice(900);
		book.setPacking(true);
		book.setPublisher("Publisher");
		book.setIsbn("1234567890");
		entityManager.persist(book);
		entityManager.flush(); // Ensure book entity is persisted and the ID is generated

		purchase = new Purchase();
		purchase.setOrderNumber(UUID.randomUUID());
		purchase.setStatus(PurchaseStatus.COMPLETED);
		purchase.setRoad("우리집");
		purchase.setMemberType(MemberType.MEMBER);
		entityManager.persist(purchase);
		entityManager.flush(); // Ensure purchase entity is persisted and the ID is generated

		purchaseBook = new PurchaseBook();
		purchaseBook.setBook(book); // Set the book entity that is already persisted
		purchaseBook.setPurchase(purchase); // Set the purchase entity that is already persisted
		purchaseBook.setQuantity(2);
		purchaseBook.setPrice(1800);
		entityManager.persist(purchaseBook);
		entityManager.flush(); // Ensure purchaseBook entity is persisted

		totalImage = new TotalImage("example.com/image.jpg");
		entityManager.persist(totalImage);
		entityManager.flush(); // Ensure totalImage entity is persisted

		bookImage = new BookImage(BookImageType.DETAIL, book, totalImage);
		entityManager.persist(bookImage);
		entityManager.flush(); // Ensure bookImage entity is persisted

		entityManager.clear(); // Clear the persistence context to avoid unintended interactions
	}
	@Test
	void testReadBookPurchaseResponses() {
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readBookPurchaseResponses(purchase.getId());
		assertThat(responses).isNotEmpty();
		assertThat(responses.getFirst().readBookByPurchase().title()).isEqualTo("Sample Book");
	}

	@Test
	void testReadGuestBookPurchaseResponses() {
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readGuestBookPurchaseResponses(purchase.getOrderNumber().toString());
		assertThat(responses).isNotEmpty();
		assertThat(responses.getFirst().readBookByPurchase().title()).isEqualTo("Sample Book");
	}

	@Test
	void testReadPurchaseBookResponse() {
		ReadPurchaseBookResponse response = purchaseBookCustomRepository.readPurchaseBookResponse(purchaseBook.getId());
		assertThat(response).isNotNull();
		assertThat(response.readBookByPurchase().title()).isEqualTo("Sample Book");
	}

	@Test
	void testReadBookPurchaseResponses_NoResults() {
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readBookPurchaseResponses(999L);
		assertThat(responses).isEmpty();
	}


	@Test
	void testReadBookPurchaseResponses_WithMultipleBooks() {
		Book anotherBook = new Book();
		anotherBook.setTitle("Another Book");
		anotherBook.setAuthor("Another Author");
		anotherBook.setPrice(2000);
		anotherBook.setSellingPrice(1800);
		anotherBook.setPacking(true);
		anotherBook.setPublisher("Another Publisher");
		anotherBook.setIsbn("0987654321");
		entityManager.persist(anotherBook);
		entityManager.flush(); // Ensure the new book entity is persisted before continuing

		PurchaseBook anotherPurchaseBook = new PurchaseBook();
		anotherPurchaseBook.setBook(anotherBook); // Set the another book entity that is already persisted
		anotherPurchaseBook.setPurchase(purchase); // Set the purchase entity that is already persisted
		anotherPurchaseBook.setQuantity(1);
		anotherPurchaseBook.setPrice(1800);
		entityManager.persist(anotherPurchaseBook);

		entityManager.flush();
		entityManager.clear();

		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readBookPurchaseResponses(purchase.getId());
		assertThat(responses).hasSize(2);
		assertThat(responses.get(1).readBookByPurchase().title()).isEqualTo("Another Book");
	}
}
