package com.nhnacademy.bookstore.book.image.imageService.Impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.nhnacademy.bookstore.book.image.exception.FailUploadImageException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

public class MockImageServiceImplTest {
    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createImage_shouldThrowFailUploadImageException_whenIOExceptionOccurs() throws IOException {
        // Given
        String storagePlace = "test";
        String originalFilename = "testImage.png";
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);

        // When
        // Mock the behavior of multipartFile.getBytes() to throw IOException
        doThrow(IOException.class).when(multipartFile).getBytes();

        // Then
        assertThrows(FailUploadImageException.class, () -> imageService.createImage(multipartFile, storagePlace));
    }

}
