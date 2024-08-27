package com.nhnacademy.bookstore.entity.book;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.booktag.BookTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "book", indexes = {
	@Index(name = "idx_published_date", columnList = "published_date"),
	@Index(name = "idx_price", columnList = "price"),
	@Index(name = "idx_view_count", columnList = "view_count")
})
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter
	private long id;

	@Size(min = 1, max = 50)
	@NotNull
	private String title;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String description;

	private ZonedDateTime publishedDate;

	@NotNull
	@Min(0)
	private int price;

	@NotNull
	@Min(0)
	private int quantity;

	@NotNull
	@Min(0)
	private int sellingPrice;

	@NotNull
	@Min(0)
	@Column(columnDefinition = "int default 0")
	private int viewCount;

	@NotNull
	private boolean packing;

	@NotNull
	@Size(min = 1, max = 50)
	private String author;

	@NotNull
	@Column(unique = true)
	@Size(min = 1, max = 20)
	private String isbn;

	@NotNull
	@Size(min = 1, max = 50)
	private String publisher;

	@NotNull
	private ZonedDateTime createdAt;

	//    public Book(long id, String title, String description, ZonedDateTime publishedDate, )
	//연결

	@Setter
	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookCategory> bookCategoryList = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookTag> bookTagList = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookImage> bookImageList = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createdAt = ZonedDateTime.now();
	}

	public Book(String title, String description, ZonedDateTime publishedDate, int price,
		int quantity, int sellingPrice, int viewCount, boolean packing, String author, String isbn,
		String publisher, List<BookCategory> bookCategoryList, List<BookTag> bookTagList,
		List<BookImage> bookImageList) {
		this.title = title;
		this.description = description;
		this.publishedDate = publishedDate;
		this.price = price;
		this.quantity = quantity;
		this.sellingPrice = sellingPrice;
		this.viewCount = viewCount;
		this.packing = packing;
		this.author = author;
		this.isbn = isbn;
		this.publisher = publisher;
		this.bookCategoryList = bookCategoryList != null ? bookCategoryList : new ArrayList<>();
		this.bookTagList = bookTagList != null ? bookTagList : new ArrayList<>();
		this.bookImageList = bookImageList != null ? bookImageList : new ArrayList<>();
	}

	/**
	 * 도서 카테고리 추가 메서드.
	 *
	 * @param bookCategory 도서-카테고리
	 */
	public void addBookCategory(BookCategory bookCategory) {
		this.bookCategoryList.add(bookCategory);
		bookCategory.setBook(this);
	}

	/**
	 * 도서 카테고리 삭제 메서드.
	 *
	 * @param bookCategory 도서-카테고리
	 */
	public void removeBookCategory(BookCategory bookCategory) {
		this.bookCategoryList.remove(bookCategory);
		bookCategory.setBook(null); // 양방향 해제
	}

	/**
	 * 이미지 추가 메서드
	 */
	public void addBookImage(BookImage bookImage) {
		this.bookImageList.add(bookImage);
		bookImage.setBook(this);
	}

	public void addBookTag(BookTag bookTag) {
		this.bookTagList.add(bookTag);
		bookTag.setBook(this);
	}

	public void viewBook() {
		viewCount++;
	}
}
