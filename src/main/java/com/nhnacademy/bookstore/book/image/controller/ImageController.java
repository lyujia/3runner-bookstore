package com.nhnacademy.bookstore.book.image.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.book.image.imageService.ImageService;
import com.nhnacademy.bookstore.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Objects;
import java.util.UUID;

/**
 * @author 한민기
 *
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookstore/images")
public class ImageController {

    private final ImageService imageService;

    /**
     * front (toast ui)-> this -> nhn cloud (object storage)
     * front 에서 보여주는 이미지를 서버에 저장
     * @param image 받아올 이미지
     * @param type 파일을 저장할 위치 -> ex) book, review
     * @return 저장할 파일명 (새로운 UUID.확장자)
     */
    @PostMapping("/{type}/upload")
    public ApiResponse<String> uploadImage(@RequestParam MultipartFile image, @PathVariable String type){
        String result  = imageService.createImage(image, type);
        return ApiResponse.success(result);
    }

    /**
     * front (fileName) -> this -> nhn cloud (object storage) -> this -> front (image)
     * front 에서 요청한 이미지를 서버에서 받아서 보내기
     * @param fileName 보여줄 파일의 이름
     * @param type 파일을 보여줄 위치 -> ex) book, review
     * @return 서버에서 가져온 파일
     */
    @GetMapping("/{type}/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("fileName") String fileName, @PathVariable String type)  {
        byte[] content;
        try {
            S3Object s3Object = imageService.readImage(type + "/" + fileName);
            content = s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new NotFindImageException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);


        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

}
