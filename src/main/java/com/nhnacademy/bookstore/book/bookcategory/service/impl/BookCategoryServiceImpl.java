package com.nhnacademy.bookstore.book.bookcategory.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.book.dto.response.BookListResponse;
import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.CreateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.dto.request.UpdateBookCategoryRequest;
import com.nhnacademy.bookstore.book.bookcategory.exception.BookCategoryAlreadyExistsException;
import com.nhnacademy.bookstore.book.bookcategory.exception.BookCategoryNotFoundException;
import com.nhnacademy.bookstore.book.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookstore.book.bookcategory.service.BookCategoryService;
import com.nhnacademy.bookstore.book.category.dto.response.BookDetailCategoryResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.exception.CategoryNotFoundException;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.category.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 도서-카테고리 service
 *
 * @author 김은비
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;

	/**
	 * 도서-카테고리 생성 메서드
	 *
	 * @param dto 생성 내용
	 */
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void createBookCategory(CreateBookCategoryRequest dto) {
		Book book = getBookById(dto.bookId());
		List<Category> categories = getCategoriesByIds(dto.categoryIds());

		for (Category category : categories) {
			if (bookCategoryRepository.existsByBookAndCategory(book, category)) {
				throw new BookCategoryAlreadyExistsException("이미 등록된 카테고리입니다.");
			}
		}

		for (Category category : categories) {
			BookCategory bookCategory = BookCategory.create(book, category);
			book.addBookCategory(bookCategory);
		}

		bookRepository.save(book);
	}

	/**
	 * 도서-카테고리 수정 메서드
	 *
	 * @param dto 수정 내용
	 */
	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void updateBookCategory(long bookId, UpdateBookCategoryRequest dto) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookDoesNotExistException("존재하지 않는 도서입니다."));
		List<Category> categories = categoryRepository.findAllById(dto.categoryIds());
		log.info("Requested categories: {}", dto.categoryIds());
		log.info("Found categories: {}", categories.stream().map(Category::getId).collect(Collectors.toList()));
		if (categories.size() != dto.categoryIds().size()) {
			throw new CategoryNotFoundException("존재하지 않는 카테고리입니다.");
		}

		// 기존의 모든 카테고리 삭제
		book.getBookCategoryList().clear();
		log.info("book categories : {} ", book.getBookCategoryList().size());

		for (Category category : categories) {
			BookCategory bookCategory = new BookCategory();
			bookCategory.setBook(book);
			bookCategory.setCategory(category);
			book.addBookCategory(bookCategory);
			log.info("카테고리 추가: {} to book: {}", category.getId(), bookId);
		}

		bookRepository.save(book);
		log.info("Updated book categories for bookId: {}", bookId);
		log.info("categories : {}", book.getBookCategoryList().size());
	}

	@Override
	public void deletedBookCategory(Long id) {
		BookCategory bookCategory = bookCategoryRepository.findById(id)
			.orElseThrow(() -> new BookCategoryNotFoundException("도서에 등록되지 않은 카테고리입니다."));

		Book book = bookCategory.getBook();
		book.removeBookCategory(bookCategory);

		bookCategoryRepository.deleteById(id);
	}

	/**
	 * 도서에 해당하는 카테고리 목록 불러오는 메서드
	 *
	 * @param bookId 책 아이디
	 * @return 책에 해당하는 카테고리 list
	 */
	@Override
	public List<CategoryParentWithChildrenResponse> readBookWithCategoryList(Long bookId) {
		return categoryChildrenMade(bookCategoryRepository.bookWithCategoryList(bookId));
	}

	/**
	 * 카테고리에 해당하는 도서 목록 불러오는 메서드
	 *
	 * @param categoryIds 카테고리 아이디 목록
	 * @param pageable    페이지
	 * @return 카테고리에 해당하는 도서 list
	 */
	@Override
	public Page<BookListResponse> readCategoriesWithBookList(List<Long> categoryIds,
		Pageable pageable) {
		List<Category> categories = categoryRepository.findAllById(categoryIds);
		if (categories.size() != categoryIds.size()) {
			throw new CategoryNotFoundException("존재하지 않는 카테고리가 있습니다.");
		}
		return bookCategoryRepository.categoriesWithBookList(categoryIds, pageable);
	}

	private Book getBookById(Long bookId) {
		return bookRepository.findById(bookId)
			.orElseThrow(() -> new BookDoesNotExistException("존재하지 않는 책입니다."));
	}

	private List<Category> getCategoriesByIds(List<Long> categoryIds) {
		List<Category> categories = categoryRepository.findAllById(categoryIds);
		if (categories.size() != categoryIds.size()) {
			throw new CategoryNotFoundException("존재하지 않는 카테고리입니다.");
		}
		return categories;
	}

	public List<CategoryParentWithChildrenResponse> allCategoryList() {
		List<Category> categoryList = categoryRepository.findAll();

		return categoryChildrenMadeCategory(categoryList);
	}

	/**
	 * 책에 소속된 카테고리 이름만 가져오는 함수
	 * @param bookId 책
	 * @return 카테고리 이름 리스트
	 */
	@Override
	public List<String> readBookCategoryNames(Long bookId) {
		List<BookCategory> categorieList = bookCategoryRepository.findByBookId(bookId);

		List<String> categoryNameList = new ArrayList<>();
		categorieList.forEach(category -> categoryNameList.add(category.getCategory().getName()));
		return categoryNameList;
	}

	/**
	 * @author 한민기
	 *
	 * @param categoryList
	 * @return
	 */
	private List<CategoryParentWithChildrenResponse> categoryChildrenMade(
		List<BookDetailCategoryResponse> categoryList) {

		Map<Long, CategoryParentWithChildrenResponse> categoryMap = new HashMap<>();

		List<CategoryParentWithChildrenResponse> rootList = new ArrayList<>();

		for (BookDetailCategoryResponse category : categoryList) {
			categoryMap.put(category.id(), CategoryParentWithChildrenResponse.builder()
				.id(category.id())
				.name(category.name())
				.childrenList(new ArrayList<>())
				.build());
		}

		for (BookDetailCategoryResponse category : categoryList) {
			CategoryParentWithChildrenResponse date = categoryMap.get(category.id());
			if (category.parentId() == null) {
				rootList.add(date);
			} else {
				CategoryParentWithChildrenResponse parent = categoryMap.get(category.parentId());
				parent.getChildrenList().add(date);
			}
		}
		return rootList;

	}

	/**
	 * @author 한민기
	 *
	 * @param categoryList
	 * @return
	 */
	private List<CategoryParentWithChildrenResponse> categoryChildrenMadeCategory(
		List<Category> categoryList) {

		Map<Long, CategoryParentWithChildrenResponse> categoryMap = new HashMap<>();

		List<CategoryParentWithChildrenResponse> rootList = new ArrayList<>();

		for (Category category : categoryList) {
			categoryMap.put(category.getId(), CategoryParentWithChildrenResponse.builder()
				.id(category.getId())
				.name(category.getName())
				.childrenList(new ArrayList<>())
				.build());
		}

		for (Category category : categoryList) {
			CategoryParentWithChildrenResponse date = categoryMap.get(category.getId());
			if (category.getParent() == null) {
				rootList.add(date);
			} else {
				CategoryParentWithChildrenResponse parent = categoryMap.get(category.getParent().getId());
				parent.getChildrenList().add(date);
			}
		}
		return rootList;
	}

}
