package com.nhnacademy.bookstore.book.bookimage.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.bookimage.dto.request.CreateBookImageRequest;
import com.nhnacademy.bookstore.book.bookimage.repository.BookImageRepository;
import com.nhnacademy.bookstore.book.bookimage.service.BookImageService;
import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookImageServiceImpl implements BookImageService {

	private final BookImageRepository bookImageRepository;
	private final BookRepository bookRepository;

	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void createBookImage(List<String> imageList, long bookId, BookImageType bookImageType) {
		List<CreateBookImageRequest> createBookImageRequestList = new ArrayList<>();
		for (String image : imageList) {
			createBookImageRequestList.add(new CreateBookImageRequest(image, bookImageType, bookId));
		}
		createBookImage(createBookImageRequestList);
	}

	/**
	 * List 로 받아와서 한꺼번에 추가
	 * book Id 가 같기 떄문에 한번만 받아와 다 추가
	 * @param bookImageRequestList 추가할 book 과 Image
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void createBookImage(List<CreateBookImageRequest> bookImageRequestList) {
		if (Objects.isNull(bookImageRequestList) || bookImageRequestList.isEmpty()) {
			return;
		}

		Book book = bookRepository.findById(bookImageRequestList.getFirst().bookId()).orElseThrow(
			NotFindImageException::new);
		for (CreateBookImageRequest bookImageRequest : bookImageRequestList) {

			TotalImage totalImage = new TotalImage(bookImageRequest.url());
			BookImage bookImageEntity = new BookImage(
				bookImageRequest.type(),
				book,
				totalImage
			);

			bookImageRepository.save(bookImageEntity);
		}
	}

	/**
	 * Book Image 다대다 연결을 위한 함수
	 * @param bookImageRequest  bookImageRequestDto
	 */
	@Override
	public void createBookImage(CreateBookImageRequest bookImageRequest) {
		Optional<Book> book = bookRepository.findById(bookImageRequest.bookId());
		if (book.isEmpty()) {
			throw new NotFindImageException();
		}
		TotalImage totalImage = new TotalImage(bookImageRequest.url());
		BookImage bookImageEntity = new BookImage(
			bookImageRequest.type(),
			book.get(),
			totalImage
		);

		bookImageRepository.save(bookImageEntity);
	}

	/**
	 *  책 수정시 이미지 수정.
	 * @param imageMain 메인 이미지
	 * @param imageList    수정된 이미지 리스트
	 * @param bookId 책 아이디
	 */
	@Transactional(propagation = Propagation.MANDATORY)
	@Override
	public void updateBookImage(String imageMain, List<String> imageList, long bookId) {
		Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookDoesNotExistException("존재하지 않는 책입니다."));

		List<BookImage> deleteBookImage = book.getBookImageList();

		BookImage mainBookImage = null;
		for (BookImage bookImage : deleteBookImage) {
			if (bookImage.getType().equals(BookImageType.MAIN))
				mainBookImage = bookImage;
		}
		List<String> addImageStr = new ArrayList<>();

		imageList.add(imageMain);
		deleteBookImage.removeIf(bookImage ->
			imageList.contains(bookImage.getTotalImage().getUrl())
		);

		for (BookImage image : deleteBookImage) {
			if (!imageList.contains(image.getTotalImage().getUrl())) {
				addImageStr.add(image.getTotalImage().getUrl());
			}
		}

		bookImageRepository.deleteAll(deleteBookImage);

		if (Objects.nonNull(mainBookImage) && !mainBookImage.getTotalImage().getUrl().equals(imageMain)) {
			TotalImage mainTotalImage = new TotalImage(imageMain);
			mainBookImage = new BookImage(BookImageType.MAIN, book, mainTotalImage);
			bookImageRepository.save(mainBookImage);
		}

		List<BookImage> bookImageList = new ArrayList<>();
		for (String image : addImageStr) {
			TotalImage totalImage = new TotalImage(image);
			bookImageList.add(new BookImage(BookImageType.DESCRIPTION, book, totalImage));
		}
		if (!bookImageList.isEmpty()) {
			bookImageRepository.saveAll(bookImageList);
		}

	}
}
