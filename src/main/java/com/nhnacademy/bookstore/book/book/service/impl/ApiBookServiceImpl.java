package com.nhnacademy.bookstore.book.book.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.bookstore.book.book.dto.response.AladinDetailResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ApiCreateBookResponse;
import com.nhnacademy.bookstore.book.book.dto.response.DescriptionResponse;
import com.nhnacademy.bookstore.book.book.dto.response.ImageMultipartFile;
import com.nhnacademy.bookstore.book.book.exception.ApiBookResponseException;
import com.nhnacademy.bookstore.book.book.repository.ApiBookRepository;
import com.nhnacademy.bookstore.book.book.repository.BookRedisRepository;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.book.book.service.ApiBookService;
import com.nhnacademy.bookstore.book.bookcategory.repository.BookCategoryRepository;
import com.nhnacademy.bookstore.book.category.exception.CategoryNotFoundException;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.book.image.exception.MultipartFileException;
import com.nhnacademy.bookstore.book.image.imageService.ImageService;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;
import com.nhnacademy.bookstore.entity.bookimage.BookImage;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;
import com.nhnacademy.bookstore.entity.category.Category;
import com.nhnacademy.bookstore.entity.totalimage.TotalImage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 한민기
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiBookServiceImpl implements ApiBookService {

	private final ApiBookRepository apiBookRepository;
	private final BookRepository bookRepository;
	private final CategoryRepository categoryRepository;
	private final BookCategoryRepository bookCategoryRepository;
	private final ImageService imageService;
	private final BookRedisRepository redisRepository;

	private static final String DETAIL_VIEW_FRONT = "https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=";

	private static final String DEFAULT_DESCRIPTION_IMAGE_BEFORE = "<p><br><img src=\"/api/images/book/download?fileName=";
	private static final String DEFAULT_DESCRIPTION_IMAGE_AFTER = "\" alt=\"image alt attribute\" contenteditable=\"false\"><br></p>";


	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void save(String isbnId) {
		ApiCreateBookResponse bookResponse = apiBookRepository.getBookResponse(isbnId);

		AladinDetailResponse detailResponse = getDetailResponse(getDetailApiItemId(bookResponse.link()),
			bookResponse.item().getFirst().cover());

		DescriptionResponse descriptionResponse = getMadeDescription(bookResponse.item().getFirst().description(),
			detailResponse);

		Book book = new Book(
			bookResponse.title().substring(bookResponse.title().indexOf("-") + 2),
			descriptionResponse.description(),
			stringToZonedDateTime(bookResponse.item().getFirst().pubDate()),
			bookResponse.item().getFirst().priceSales(),
			100,
			bookResponse.item().getFirst().priceSales(),
			0,
			true,
			realAuthorName(bookResponse.item().getFirst().author()),
			bookResponse.item().getFirst().isbn13(),
			bookResponse.item().getFirst().publisher(),
			null,
			null,
			null
		);

		List<String> categories = categoryNameStringToList(bookResponse.item().getFirst().categoryName());

		bookRepository.save(book);
		for (String categoryName : categories) {
			Category category = categoryRepository.findByName(categoryName).orElse(null);

			if (Objects.isNull(category)) {
				throw new CategoryNotFoundException(categoryName);
			}
			BookCategory bookCategory = BookCategory.create(book, category);

			bookCategoryRepository.save(bookCategory);
		}

		for (BookImage bookImage : descriptionResponse.bookImageList()) {
			book.addBookImage(bookImage);
		}
		bookRepository.save(book);
		redisRepository.createBook(book);
	}

	/**
	 * @param author Api 에서 받아오는 작가 이름 => 지음 엮은이등이 포함되어있어서 이름만 받도록 수정
	 * @return 작가의 이름
	 */
	public String realAuthorName(String author) {
		if (author.contains("지음")) {
			return author.substring(0, author.indexOf("지음"));
		} else if (author.contains("엮은이")) {
			return author.substring(0, author.indexOf("엮은이"));
		}
		if (author.contains("옮김")) {
			return author.substring(0, author.indexOf("옮김"));
		}
		return author;
	}

	/**
	 * imageUrl 에서 이미지 파일을 MultipartFile로 가져옴
	 *
	 * @param imageUrl api 에서 받은 cover 이미지의 사진
	 * @return multipartFile 형식의 이미지  -> 이걸 나중에 image 등록
	 */
	public MultipartFile downloadImageAsMultipartFile(String imageUrl) {
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			// 2. 이미지를 바이트 배열로 변환
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream is = connection.getInputStream()) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = is.read(buffer)) != -1) {
					baos.write(buffer, 0, len);
				}
			}

			// 3. 바이트 배열을 MultipartFile로 변환
			byte[] imageBytes = baos.toByteArray();

			return new ImageMultipartFile(imageBytes);
		} catch (IOException e) {
			throw new MultipartFileException();
		}

	}

	/**
	 * String -> ZoneDateTime 으로 변경
	 *
	 * @param dateStr 바꿀 date String
	 * @return ZoneDateTime 의 날짜
	 */
	public ZonedDateTime stringToZonedDateTime(String dateStr) {
		if (Objects.isNull(dateStr)) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDateStr = LocalDate.parse(dateStr, formatter);

		return localDateStr.atStartOfDay(ZoneId.systemDefault());
	}

	/**
	 * 하나의 카테고리를 나눠서 넣기
	 * ex) 국내도서>사회과학>비평/칼럼>정치비평/칼럼
	 *
	 * @param categoryName 하나로 길게 되어 있는 카테고리 이름
	 * @return List 로 나눠진 카테고리
	 */
	public List<String> categoryNameStringToList(String categoryName) {
		String[] categoryNameArray = categoryName.split(">");
		return new ArrayList<>(Arrays.asList(categoryNameArray));
	}

	private String getDetailApiItemId(String link) {
		return link.substring(link.indexOf("ItemId=") + 7);
	}

	/**
	 * 알라딘의 책 html에서 상세 정보 받아오기
	 * (있을 경우에만 받아옴) -> 좀 더 좋은 화질의 메인 사진, 유튜브 영상, 설명의 사진
	 * @param itemId 알라딘의 책 아이디
	 */
	public AladinDetailResponse getDetailResponse(String itemId, String imageUrl) {
		List<String> imageUrlList = new ArrayList<>();
		String youTubeStr = null;

		Document doc = null;
		try {
			doc = Jsoup.connect(DETAIL_VIEW_FRONT + itemId).get();
		} catch (IOException e) {
			throw new ApiBookResponseException();
		}

		Elements metaTags = doc.select("meta[property=og:image]");
		Elements image = doc.select("div[id=card_play]");
		Elements youTube = doc.select("iframe[src*=youtube.com]");

		if (!metaTags.isEmpty()) {
			imageUrl = Objects.requireNonNull(metaTags.first()).attr("content");
		}
		if (!image.isEmpty()) {
			String[] imageList = image.toString().split("<img src=\"");
			for (int i = 1; i < imageList.length; i++) {
				imageUrlList.add("https:" + imageList[i].substring(0, imageList[i].indexOf('"')));
			}
		}
		if (!youTube.isEmpty()) {
			youTubeStr = youTube.toString();
		}
		log.info(imageUrlList.toString());

		return AladinDetailResponse.builder()
			.mainImageUrl(imageUrl)
			.youTubeStr(youTubeStr)
			.imageUrlList(imageUrlList)
			.build();
	}

	/**
	 *
	 * 북 설명에서 나온 사진들을 받아서 object storage 에 넣고
	 * 북 설명에 사진을 변환해서 넣기
	 * @param description        이전 설명
	 * @param detailResponse    바꿀 내용들
	 * @return 변경된 책의 내용과 BookImageList
	 */
	private DescriptionResponse getMadeDescription(String description, AladinDetailResponse detailResponse) {

		StringBuilder descriptionBuilder = new StringBuilder(description);
		List<BookImage> bookImageList = new ArrayList<>();

		if (Objects.nonNull(detailResponse.mainImageUrl())) {
			String fileName = imageService.createImage(downloadImageAsMultipartFile(detailResponse.mainImageUrl()),
				"book");
			TotalImage totalImage = new TotalImage(fileName);
			BookImage bookImage = new BookImage(BookImageType.MAIN, totalImage);
			bookImageList.add(bookImage);
		}
		if (!detailResponse.imageUrlList().isEmpty()) {
			for (String imageUrl : detailResponse.imageUrlList()) {
				String fileName = imageService.createImage(downloadImageAsMultipartFile(imageUrl),
					"book");
				TotalImage totalImage = new TotalImage(fileName);
				BookImage bookImage = new BookImage(BookImageType.DESCRIPTION, totalImage);

				descriptionBuilder.append(DEFAULT_DESCRIPTION_IMAGE_BEFORE)
					.append(fileName)
					.append(DEFAULT_DESCRIPTION_IMAGE_AFTER);
				bookImageList.add(bookImage);
			}
		}
		if (Objects.nonNull(detailResponse.youTubeStr())) {
			descriptionBuilder.append("<br>").append(detailResponse.youTubeStr()).append("<br>");
		}
		description = descriptionBuilder.toString();
		return DescriptionResponse.builder()
			.description(description)
			.bookImageList(bookImageList)
			.build();
	}
}
