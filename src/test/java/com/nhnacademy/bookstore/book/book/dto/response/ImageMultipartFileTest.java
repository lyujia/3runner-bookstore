package com.nhnacademy.bookstore.book.book.dto.response;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageMultipartFileTest {
	private byte[] testBytes;
	private ImageMultipartFile imageMultipartFile;

	@BeforeEach
	void setUp() {
		testBytes = "test image content".getBytes();
		imageMultipartFile = new ImageMultipartFile(testBytes);
	}

	@Test
	void testIsEmpty() {
		ImageMultipartFile emptyFile = new ImageMultipartFile(new byte[0]);
		assertTrue(emptyFile.isEmpty());

		assertFalse(imageMultipartFile.isEmpty());
	}

	@Test
	void testGetSize() {
		assertEquals(testBytes.length, imageMultipartFile.getSize());
	}

	@Test
	void testTransferTo() throws IOException {
		File tempFile = File.createTempFile("test", ".jpg");
		tempFile.deleteOnExit();

		imageMultipartFile.transferTo(tempFile);

		assertTrue(tempFile.exists());
		assertEquals(testBytes.length, tempFile.length());

		// Clean up the temporary file
		tempFile.delete();
	}

	@Test
	void testGetName() {
		assertEquals("image", imageMultipartFile.getName());
	}

	@Test
	void testGetOriginalFilename() {
		assertEquals("image.jpg", imageMultipartFile.getOriginalFilename());
	}

	@Test
	void testGetContentType() {
		assertEquals("image/jpeg", imageMultipartFile.getContentType());
	}

	@Test
	void testGetBytes() throws IOException {
		assertArrayEquals(testBytes, imageMultipartFile.getBytes());
	}

	@Test
	void testGetInputStream() throws IOException {
		InputStream inputStream = imageMultipartFile.getInputStream();
		assertNotNull(inputStream);
		assertEquals(ByteArrayInputStream.class, inputStream.getClass());
	}
}
