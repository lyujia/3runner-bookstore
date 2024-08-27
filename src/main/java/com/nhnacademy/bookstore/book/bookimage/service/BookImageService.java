package com.nhnacademy.bookstore.book.bookimage.service;

import java.util.List;

import com.nhnacademy.bookstore.book.bookimage.dto.request.CreateBookImageRequest;
import com.nhnacademy.bookstore.entity.bookimage.enums.BookImageType;

public interface BookImageService {

	void createBookImage(List<String> imageList, long bookId, BookImageType bookImageType);

	void createBookImage(List<CreateBookImageRequest> bookImageRequestList);

	void createBookImage(CreateBookImageRequest bookImage);

	void updateBookImage(String imageMain, List<String> imageList, long bookId);
}
