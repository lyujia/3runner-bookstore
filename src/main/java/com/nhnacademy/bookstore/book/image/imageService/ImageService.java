package com.nhnacademy.bookstore.book.image.imageService;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String createImage(MultipartFile file, String storagePlace);
    S3Object readImage(String fileName);
}
