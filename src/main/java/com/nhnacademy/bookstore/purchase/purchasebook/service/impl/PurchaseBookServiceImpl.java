package com.nhnacademy.bookstore.purchase.purchasebook.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
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
import com.nhnacademy.bookstore.purchase.purchasebook.service.PurchaseBookService;

import lombok.RequiredArgsConstructor;

/**
 * 주문-책 서비스
 *
 * @author 정주혁
 */
@Transactional
@Service
@RequiredArgsConstructor
public class PurchaseBookServiceImpl implements PurchaseBookService {
	private final PurchaseBookRepository purchaseBookRepository;
	private final PurchaseRepository purchaseRepository;
	private final BookRepository bookRepository;
	private final PurchaseBookCustomRepository purchaseBookCustomRepository;



	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadPurchaseBookResponse> readBookByPurchaseResponses(Long purchaseId, Long memberId) {
		Purchase purchase = purchaseRepository.findById(purchaseId).orElseThrow(NotExistsPurchase::new);


		List<PurchaseBook> purchaseBooks = purchase.getPurchaseBookList();
		if(memberId != purchaseBooks.getFirst().getPurchase().getMember().getId()){
			throw new ImPossibleAccessPurchaseBookException();
		}

		return  purchaseBookCustomRepository.readBookPurchaseResponses(purchaseId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadPurchaseBookResponse> readGuestBookByPurchaseResponses(String purchaseId) {

		return  purchaseBookCustomRepository.readGuestBookPurchaseResponses(purchaseId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updatePurchaseBook(UpdatePurchaseBookRequest updatePurchaseBookRequest) {
		PurchaseBook purchaseBook = purchaseBookRepository.findByPurchaseIdAndBookId(
			updatePurchaseBookRequest.purchaseId(), updatePurchaseBookRequest.bookId()).orElse(null);
		if (purchaseBook == null) {
			throw new NotExistsPurchaseBook();
		}
		purchaseBook.setQuantity(updatePurchaseBookRequest.quantity());
		purchaseBook.setPrice(updatePurchaseBookRequest.price());
		purchaseBookRepository.save(purchaseBook);
		return purchaseBook.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createPurchaseBook(CreatePurchaseBookRequest createPurchaseBookRequest) {
		Book book = bookRepository.findById(createPurchaseBookRequest.bookId()).orElseThrow(NotExistsBook::new);
		int price = book.getSellingPrice() * createPurchaseBookRequest.quantity();


		PurchaseBook purchaseBook = new PurchaseBook(
			book,
			createPurchaseBookRequest.quantity(),
			price,
			purchaseRepository.findById(createPurchaseBookRequest.purchaseId()).orElseThrow(NotExistsPurchase::new));

		return purchaseBookRepository.save(purchaseBook).getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deletePurchaseBook(long purchaseBookId) {
		purchaseBookRepository.deleteById(purchaseBookId);
	}

}
