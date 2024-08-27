package com.nhnacademy.bookstore.book.image.imageService.Impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.book.image.imageService.ImageService;

/**
 * @author 한민기
 */
@SpringBootTest
class ImageServiceImplTest {

	@Autowired
	private ImageService imageService;

	/**
	 * uploadImage 성공시 -> object storage 컨테이너에 /test/test.png 저장
	 * @throws IOException 파일 변형 실패시
	 */
	@DisplayName("uploadImage 확인")
	@Test
	@Order(1)
	void uploadImage() throws IOException {
		String testFileName = "test.png";

		ResourceLoader resourceLoader = new DefaultResourceLoader();

		Resource resource = resourceLoader.getResource(testFileName);
		InputStream inputStream = resource.getInputStream();

		MultipartFile multipartFile = new MockMultipartFile("file",
			testFileName, "image/png", inputStream.readAllBytes());

		imageService.createImage(multipartFile, "test");

	}

	/**
	 * 미리 저장해둔 파일 확인
	 */
	@DisplayName("downloadImage 확인")
	@Test
	@Order(2)
	void downloadImage() {
		S3Object object = imageService.readImage("test/test.png");
		assertNotNull(object);
	}

	@DisplayName("존재하지 않는 파일을 불러올시 ")
	@Test
	void NotFindDownloadImage() {
		assertThrows(NotFindImageException.class, () -> {
			imageService.readImage("test/notExist.png");
		});

	}
}