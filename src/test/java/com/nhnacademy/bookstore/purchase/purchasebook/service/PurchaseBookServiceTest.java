package com.nhnacademy.bookstore.purchase.purchasebook.service;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.request.UpdatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.purchasebook.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.ImPossibleAccessPurchaseBookException;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsBook;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsPurchase;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.NotExistsPurchaseBook;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookCustomRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.repository.PurchaseBookRepository;
import com.nhnacademy.bookstore.purchase.purchasebook.service.impl.PurchaseBookServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PurchaseBookServiceTest {

	@Mock
	private PurchaseBookRepository purchaseBookRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private PurchaseBookCustomRepository purchaseBookCustomRepository;

	@InjectMocks
	private PurchaseBookServiceImpl purchaseBookService;

	private Book book;
	private Purchase purchase;
	private PurchaseBook purchaseBook;
	private Member member;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		member = new Member();
		member.setId(1L);

		book = new Book();
		book.setId(1L);
		book.setTitle("Sample Book");
		book.setAuthor("Author");
		book.setPrice(1000);
		book.setSellingPrice(900);
		book.setPacking(true);
		book.setPublisher("Publisher");
		book.setIsbn("1234567890");

		purchase = new Purchase();
		purchase.setId(1L);
		purchase.setOrderNumber(UUID.randomUUID());
		purchase.setStatus(PurchaseStatus.COMPLETED);
		purchase.setRoad("우리집");
		purchase.setMember(member);

		purchaseBook = new PurchaseBook();
		purchaseBook.setBook(book);
		purchaseBook.setPurchase(purchase);
		purchaseBook.setQuantity(2);
		purchaseBook.setPrice(1800);
	}

	@Test
	void testReadBookByPurchaseResponses() {
		List<PurchaseBook> purchaseBooks = new ArrayList<>();
		purchaseBooks.add(purchaseBook);
		purchase.setPurchaseBookList(purchaseBooks);

		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));
		when(purchaseBookCustomRepository.readBookPurchaseResponses(anyLong())).thenReturn(new ArrayList<>());

		List<ReadPurchaseBookResponse> responses = purchaseBookService.readBookByPurchaseResponses(1L, 1L);
		assertThat(responses).isEmpty();
	}

	@Test
	void testReadBookByPurchaseResponses_ThrowsNotExistsPurchase() {
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(NotExistsPurchase.class, () -> {
			purchaseBookService.readBookByPurchaseResponses(1L, 1L);
		});
	}

	@Test
	void testReadBookByPurchaseResponses_ThrowsImPossibleAccessPurchaseBookException() {
		List<PurchaseBook> purchaseBooks = new ArrayList<>();
		purchaseBooks.add(purchaseBook);
		purchase.setPurchaseBookList(purchaseBooks);

		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));
		purchaseBook.getPurchase().getMember().setId(2L); // Set different member ID to cause exception

		assertThrows(ImPossibleAccessPurchaseBookException.class, () -> {
			purchaseBookService.readBookByPurchaseResponses(1L, 1L);
		});
	}

	@Test
	void testReadGuestBookByPurchaseResponses() {
		when(purchaseBookCustomRepository.readGuestBookPurchaseResponses(anyString())).thenReturn(new ArrayList<>());

		List<ReadPurchaseBookResponse> responses = purchaseBookService.readGuestBookByPurchaseResponses("some-uuid");
		assertThat(responses).isEmpty();
	}

	@Test
	void testUpdatePurchaseBook() {
		UpdatePurchaseBookRequest request = new UpdatePurchaseBookRequest(1L, 1, 3, 2700);

		when(purchaseBookRepository.findByPurchaseIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.of(purchaseBook));

		Long id = purchaseBookService.updatePurchaseBook(request);

		assertThat(id).isEqualTo(purchaseBook.getId());
		assertThat(purchaseBook.getQuantity()).isEqualTo(request.quantity());
		assertThat(purchaseBook.getPrice()).isEqualTo(request.price());
	}

	@Test
	void testUpdatePurchaseBook_ThrowsNotExistsPurchaseBook() {
		UpdatePurchaseBookRequest request =  UpdatePurchaseBookRequest.builder().bookId( 1L).quantity(1).price(1000).purchaseId(1L).build();

		when(purchaseBookRepository.findByPurchaseIdAndBookId(anyLong(), anyLong())).thenReturn(Optional.empty());

		assertThrows(NotExistsPurchaseBook.class, () -> {
			purchaseBookService.updatePurchaseBook(request);
		});
	}

	@Test
	void testCreatePurchaseBook() {
		CreatePurchaseBookRequest request =  CreatePurchaseBookRequest.builder().bookId(1L).purchaseId(1L).price(1000).quantity(5).build();

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.of(purchase));
		when(purchaseBookRepository.save(any(PurchaseBook.class))).thenReturn(purchaseBook);

		Long id = purchaseBookService.createPurchaseBook(request);

		assertThat(id).isEqualTo(purchaseBook.getId());
	}

	@Test
	void testCreatePurchaseBook_ThrowsNotExistsBook() {
		CreatePurchaseBookRequest request =  CreatePurchaseBookRequest.builder().bookId(1L).purchaseId(1L).price(1000).quantity(5).build();

		when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(NotExistsBook.class, () -> {
			purchaseBookService.createPurchaseBook(request);
		});
	}

	@Test
	void testCreatePurchaseBook_ThrowsNotExistsPurchase() {
		CreatePurchaseBookRequest request =  CreatePurchaseBookRequest.builder().bookId(1L).purchaseId(1L).price(1000).quantity(5).build();

		when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
		when(purchaseRepository.findById(anyLong())).thenReturn(Optional.empty());

		assertThrows(NotExistsPurchase.class, () -> {
			purchaseBookService.createPurchaseBook(request);
		});
	}

	@Test
	void testDeletePurchaseBook() {
		doNothing().when(purchaseBookRepository).deleteById(anyLong());

		purchaseBookService.deletePurchaseBook(1L);

		verify(purchaseBookRepository, times(1)).deleteById(eq(1L));
	}
}
