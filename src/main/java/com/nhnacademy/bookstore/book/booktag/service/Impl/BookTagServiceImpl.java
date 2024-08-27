package com.nhnacademy.bookstore.book.booktag.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.booktag.dto.request.CreateBookTagListRequest;
import com.nhnacademy.bookstore.book.booktag.dto.request.CreateBookTagRequest;
import com.nhnacademy.bookstore.book.booktag.dto.request.ReadBookIdRequest;
import com.nhnacademy.bookstore.book.booktag.dto.request.ReadTagRequest;
import com.nhnacademy.bookstore.book.booktag.dto.response.ReadBookByTagResponse;
import com.nhnacademy.bookstore.book.booktag.dto.response.ReadTagByBookResponse;
import com.nhnacademy.bookstore.book.booktag.exception.AlreadyExistsBookTagException;
import com.nhnacademy.bookstore.book.booktag.exception.NotExistsBookTagException;
import com.nhnacademy.bookstore.book.booktag.repository.BookTagRepository;
import com.nhnacademy.bookstore.book.booktag.service.BookTagService;
import com.nhnacademy.bookstore.book.tag.repository.TagRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.booktag.BookTag;
import com.nhnacademy.bookstore.entity.tag.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 책에 달린 태그를 검색 하거나 태그로 책을 검색하기 위한 서비스
 *
 * @author 정주혁
 * fix 한민기
 */

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookTagServiceImpl implements BookTagService {

	private final BookTagRepository bookTagRepository;
	private final BookRepository bookRepository;
	private final TagRepository tagRepository;

	/**
	 * 태그가 달린 책들을 불러오기위한 메소드
	 *
	 * @param tagId    해당 태그가 달린 책의 정보들을 가져오기위한 태그 id
	 * @param pageable Page<book>의 pageable 정보
	 * @return Page<ReadBookByTagResponse> 해당 태그가 달린 책의 정보들
	 */
	public Page<ReadBookByTagResponse> readBookByTagId(ReadTagRequest tagId, Pageable pageable) {
		Page<Book> books = bookTagRepository.findAllBookIdByTagId(tagId.tagId(), pageable);

		return books.map(book -> ReadBookByTagResponse.builder()
			.price(book.getPrice()).author(book.getAuthor()).quantity(book.getQuantity())
			.description(book.getDescription()).title(book.getTitle()).packing(book.isPacking())
			.publishedDate(book.getPublishedDate()).creationDate(book.getCreatedAt())
			.view_count(book.getViewCount())
			.sellingPrice(book.getSellingPrice()).build());
	}

	/**
	 * 책에 달린 태그들을 불러오기 위한 메소드
	 *
	 * @param bookId 해당 책에 달린 태그들을 가져오기위한 책 id
	 * @return Set<ReadTagByBookResponse> 해당 책에 달린 태그들
	 */
	public List<ReadTagByBookResponse> readTagByBookId(ReadBookIdRequest bookId) {

		List<Tag> tags = bookTagRepository.findAllTagIdByBookId(bookId.bookId());

		return tags.stream().map(tag -> ReadTagByBookResponse.builder().id(tag.getId()).name(tag.getName()).build())
			.collect(Collectors.toList());
	}

	/**
	 * @author 한민기
	 * @param bookTagRequest 만들 book id 와 tag id
	 * @return bookTag 의 id
	 */
	@Override
	public Long createBookTag(CreateBookTagRequest bookTagRequest) {
		if (bookTagRepository.existsByBookIdAndTagId(bookTagRequest.bookId(), bookTagRequest.tagId())) {
			throw new AlreadyExistsBookTagException("이미 해당 책에 달린 태그가 존재합니다.");
		}
		if (!bookRepository.existsById(bookTagRequest.bookId())) {
			throw new BookDoesNotExistException("책이 없습니다.");
		}
		if (!tagRepository.existsById(bookTagRequest.tagId())) {
			throw new NotExistsBookTagException("태그가 없습니다.");
		}
		BookTag bookTag = new BookTag(bookRepository.findById(bookTagRequest.bookId()).orElse(null),
			tagRepository.findById(bookTagRequest.tagId()).orElse(null));
		return bookTagRepository.save(bookTag).getId();

	}

	/**
	 * @author 한민기
	 * @param createBookTagRequestList 만들 book id 와 tag id	의 리스트 형식
	 */
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void createBookTag(CreateBookTagListRequest createBookTagRequestList) {
		if (!bookRepository.existsById(createBookTagRequestList.bookId())) {
			throw new BookDoesNotExistException("책이 없습니다.");
		}

		for (Long tagId : createBookTagRequestList.tagIdList()) {
			if (bookTagRepository.existsByBookIdAndTagId(createBookTagRequestList.bookId(), tagId)) {
				throw new AlreadyExistsBookTagException("이미 해당 책에 달린 태그가 존재합니다.");
			}
			if (!tagRepository.existsById(tagId)) {
				throw new NotExistsBookTagException("태그가 없습니다.");
			}

			BookTag bookTag = new BookTag(bookRepository.findById(createBookTagRequestList.bookId()).orElse(null),
				tagRepository.findById(tagId).orElse(null));

			bookTagRepository.save(bookTag);
		}
	}

	/**
	 * 기존의 태그를 삭제하고 다시 추가.
	 *
	 * @param createBookTagListRequest 수정할 태그들의 목록
	 */
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void updateBookTag(CreateBookTagListRequest createBookTagListRequest) {
		Book book = bookRepository.findById(createBookTagListRequest.bookId())
			.orElseThrow(() -> new BookDoesNotExistException("존재하지 않는 도서입니다."));

		List<Tag> bookTagList = tagRepository.findAllById(createBookTagListRequest.tagIdList());

		book.getBookTagList().clear();

		bookRepository.save(book);
		for (Tag tag : bookTagList) {
			BookTag bookTag = new BookTag(book, tag);
			book.addBookTag(bookTag);
		}

		bookRepository.save(book);
	}

}
