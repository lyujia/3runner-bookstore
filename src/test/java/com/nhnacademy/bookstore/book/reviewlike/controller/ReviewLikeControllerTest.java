package com.nhnacademy.bookstore.book.reviewlike.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.reviewlike.service.ReviewLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewLikeController.class)
class ReviewLikeControllerTest extends BaseDocumentTest {

    @MockBean
    private ReviewLikeService reviewLikeService;

    @DisplayName("리뷰 좋아요 생성 테스트")
    @Test
    void createReviewLike() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/books/review/{reviewId}/like", 1L)
                        .header("Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 좋아요 생성 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )));

        verify(reviewLikeService, times(1)).createReviewLike(1L, 1L);
    }

    @DisplayName("리뷰 좋아요 삭제 테스트")
    @Test
    void deleteReviewLike() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/books/review/{reviewId}/like", 1L)
                        .header("Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 좋아요 삭제 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )));

        verify(reviewLikeService, times(1)).deleteReviewLike(1L, 1L);
    }

    @DisplayName("리뷰 좋아요 여부 판단 테스트")
    @Test
    void isReviewLikedByMember() throws Exception {
        when(reviewLikeService.isReviewLikedByMember(anyLong(), anyLong())).thenReturn(true);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/review/{reviewId}/like/status", 1L)
                        .header("Member-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data").value(true))
                .andDo(document(snippetPath,
                        "리뷰 좋아요 여부 판단 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("좋아요 여부")
                        )));

        verify(reviewLikeService, times(1)).isReviewLikedByMember(1L, 1L);
    }

    @DisplayName("리뷰 좋아요 카운트 테스트")
    @Test
    void countReviewLike() throws Exception {
        when(reviewLikeService.countReviewLike(anyLong())).thenReturn(5L);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/review/{reviewId}/like/count", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data").value(5L))
                .andDo(document(snippetPath,
                        "리뷰 좋아요 카운트 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("좋아요 수")
                        )));

        verify(reviewLikeService, times(1)).countReviewLike(1L);
    }
}
