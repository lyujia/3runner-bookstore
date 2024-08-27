package com.nhnacademy.bookstore.book.book.dto.response;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

/**
 *
 * Image 의 MultipartFile 을 만들때 필요한 함수
 *
 * @author 한민기
 */
@Getter
public class ImageMultipartFile implements MultipartFile {
	String name = "image";
	String originalFilename = "image.jpg";
	String contentType = "image/jpeg";
	byte[] bytes;
	InputStream inputStream;

	public ImageMultipartFile(byte[] bytes) {

		this.bytes = bytes;
		inputStream = new ByteArrayInputStream(bytes);
	}

	@Override
	public boolean isEmpty() {
		return bytes.length == 0;
	}

	@Override
	public long getSize() {
		return bytes.length;
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			fos.write(bytes);
		}
	}
}
