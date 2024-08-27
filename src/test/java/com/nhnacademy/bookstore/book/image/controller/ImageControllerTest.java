package com.nhnacademy.bookstore.book.image.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.book.image.imageService.ImageService;

@WebMvcTest(ImageController.class)
class ImageControllerTest {

	@MockBean
	private ImageService imageService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void uploadImage() throws Exception {
		MockMultipartFile mockFile = new MockMultipartFile("image", "testImage.png", "image/png",
			"test data".getBytes());

		String imageStr = "testUUID.png";
		when(imageService.createImage(any(MultipartFile.class), any())).thenReturn(imageStr);
		mockMvc.perform(multipart("/bookstore/images/book/upload")
				.file(mockFile))
			.andExpect(status().isOk());
		//                .andExpect(content().json("{\"success\":true,\"data\":\"testUUID.png\"}"));

	}

	@Test
	void downloadFile_shouldReturnFileContent() throws Exception {
		String fileName = "testUUID.png";
		String type = "book";
		byte[] fileContent = "test file content".getBytes();
		S3Object s3Object = new S3Object();
		s3Object.setObjectContent(new S3ObjectInputStream(new ByteArrayInputStream(fileContent), null));

		when(imageService.readImage(type + "/" + fileName)).thenReturn(s3Object);

		mockMvc.perform(get("/bookstore/images/book/download")
				.param("fileName", fileName))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
			.andExpect(content().bytes(fileContent));
	}

	@Test
	void downloadFile_shouldThrowNotFindImageException_whenImageNotFound() throws Exception {
		String fileName = "testUUID.png";
		String type = "book";

		when(imageService.readImage(type + "/" + fileName)).thenThrow(new NotFindImageException());

		mockMvc.perform(get("/bookstore/images/book/download")
				.param("fileName", fileName))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void downloadFile_shouldThrowNotFindImageException_whenIOExceptionOccurs() throws Exception {
		String fileName = "testUUID.png";
		String type = "book";

		S3Object s3Object = Mockito.mock(S3Object.class);
		InputStream mockInputStream = Mockito.mock(S3ObjectInputStream.class);
		when(s3Object.getObjectContent()).thenReturn((S3ObjectInputStream)mockInputStream);
		when(mockInputStream.readAllBytes()).thenThrow(new IOException());

		when(imageService.readImage(type + "/" + fileName)).thenReturn(s3Object);

		mockMvc.perform(get("/bookstore/images/book/download")
				.param("fileName", fileName))
			.andExpect(status().isNotFound())
			.andDo(print());  // Print request and response
	}
}