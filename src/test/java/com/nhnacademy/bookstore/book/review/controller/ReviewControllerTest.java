package com.nhnacademy.bookstore.book.review.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.review.dto.request.CreateReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.request.DeleteReviewRequest;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewAdminListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.ReviewListResponse;
import com.nhnacademy.bookstore.book.review.dto.response.UserReadReviewResponse;
import com.nhnacademy.bookstore.book.review.service.ReviewService;
import com.nhnacademy.bookstore.book.reviewimage.service.ReviewImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Collections;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest extends BaseDocumentTest {

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewImageService reviewImageService;

    @DisplayName("리뷰 생성 테스트")
    @Test
    void createReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest("좋은 책입니다", "추천합니다", 5, Collections.emptyList());

        when(reviewService.createReview(anyLong(), anyLong(), any(CreateReviewRequest.class))).thenReturn(1L);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/{purchaseBookId}/create", 1L)
                        .header("Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"좋은 책입니다\",\"content\":\"추천합니다\",\"ratings\":5,\"imageList\":[]}"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 생성 API",
                        pathParameters(
                                parameterWithName("purchaseBookId").description("구매한 책 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("생성된 리뷰 ID")
                        )));

        verify(reviewService, times(1)).createReview(anyLong(), anyLong(), any(CreateReviewRequest.class));
        verify(reviewImageService, times(1)).createReviewImage(anyList(), anyLong());
    }


    @DisplayName("리뷰 수정 테스트")
    @Test
    void updateReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest("좋은 책입니다", "추천합니다", 5, Collections.emptyList());

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/review/{reviewId}", 1L)
                        .header("Member-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"좋은 책입니다\",\"content\":\"추천합니다\",\"ratings\":5,\"imageList\":[]}"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 수정 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("수정된 리뷰 ID")
                        )));

        verify(reviewService, times(1)).updateReview(anyLong(), anyLong(), any(CreateReviewRequest.class));
        verify(reviewImageService, times(1)).createReviewImage(anyList(), anyLong());
    }

    @DisplayName("리뷰 전체 조회 테스트")
    @Test
    void readAllReviews() throws Exception {
        Page<ReviewAdminListResponse> response = new PageImpl<>(Collections.emptyList());

        when(reviewService.readAllReviews(any(Pageable.class))).thenReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/reviews")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 전체 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 리뷰 수"),
                                fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 리뷰 수"),
                                fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어 있는지 여부"),
                                fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬이 비어 있는지 여부"),
                                fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬이 되었는지 여부"),
                                fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬이 안 되었는지 여부"),
                                fieldWithPath("body.data.pageable").type(JsonFieldType.STRING).description("페이징 정보")
                        )));

        verify(reviewService, times(1)).readAllReviews(any(Pageable.class));
    }

    @DisplayName("리뷰 상세 조회 테스트")
    @Test
    void readReviewDetail() throws Exception {
        UserReadReviewResponse response = new UserReadReviewResponse(1L, "책 제목", 1L, "리뷰 제목", "리뷰 내용", 5.0, "member@example.com", null, false, null, 0L);

        when(reviewService.readDetailUserReview(anyLong())).thenReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/reviews/{reviewId}", 1L))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 상세 조회 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.reviewId").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                fieldWithPath("body.data.bookId").type(JsonFieldType.NUMBER).description("책 ID"),
                                fieldWithPath("body.data.bookTitle").type(JsonFieldType.STRING).description("책 제목"),
                                fieldWithPath("body.data.reviewTitle").type(JsonFieldType.STRING).description("리뷰 제목"),
                                fieldWithPath("body.data.reviewContent").type(JsonFieldType.STRING).description("리뷰 내용"),
                                fieldWithPath("body.data.ratings").type(JsonFieldType.NUMBER).description("평점"),
                                fieldWithPath("body.data.memberEmail").type(JsonFieldType.STRING).description("작성자 이메일"),
                                fieldWithPath("body.data.createdAt").type(JsonFieldType.NULL).description("생성 일시"),
                                fieldWithPath("body.data.updated").type(JsonFieldType.BOOLEAN).description("업데이트 여부"),
                                fieldWithPath("body.data.updatedAt").type(JsonFieldType.NULL).description("업데이트 일시"),
                                fieldWithPath("body.data.reviewLike").type(JsonFieldType.NUMBER).description("리뷰 좋아요 수")
                        )));

        verify(reviewService, times(1)).readDetailUserReview(anyLong());
    }

    @DisplayName("책 아이디로 리뷰 조회 테스트")
    @Test
    void readReviewsByBookId() throws Exception {
        Page<ReviewListResponse> response = new PageImpl<>(Collections.emptyList());

        when(reviewService.readAllReviewsByBookId(anyLong(), any(Pageable.class))).thenReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/{bookId}/reviews", 1L)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "likes,desc"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "책 아이디로 리뷰 조회 API",
                        pathParameters(
                                parameterWithName("bookId").description("책 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 리뷰 수"),
                                fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 리뷰 수"),
                                fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어 있는지 여부"),
                                fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
                                fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                fieldWithPath("body.data.pageable").type(JsonFieldType.STRING).description("페이지 정보")
                        )));

        verify(reviewService, times(1)).readAllReviewsByBookId(anyLong(), any(Pageable.class));
    }


    @DisplayName("멤버 아이디로 리뷰 조회 테스트")
    @Test
    void readReviewsByMemberId() throws Exception {
        Page<ReviewListResponse> response = new PageImpl<>(Collections.emptyList());

        when(reviewService.readAllReviewsByMemberId(anyLong(), any(Pageable.class))).thenReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/reviews/member")
                        .header("Member-Id", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "멤버 아이디로 리뷰 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("리뷰 목록"),
                                fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 리뷰 수"),
                                fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 리뷰 수"),
                                fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어 있는지 여부"),
                                fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
                                fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                fieldWithPath("body.data.pageable").type(JsonFieldType.STRING).description("페이지 정보")
                        )));

        verify(reviewService, times(1)).readAllReviewsByMemberId(anyLong(), any(Pageable.class));
    }


    @DisplayName("책 아이디로 리뷰 평점 평균 조회 테스트")
    @Test
    void getAverageReviewsByBookId() throws Exception {
        when(reviewService.getAverageRating(anyLong())).thenReturn(4.5);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/{bookId}/reviews/avg", 1L))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "책 아이디로 리뷰 평점 평균 조회 API",
                        pathParameters(
                                parameterWithName("bookId").description("책 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("평균 평점")
                        )));

        verify(reviewService, times(1)).getAverageRating(anyLong());
    }

    @DisplayName("책 아이디로 리뷰 갯수 조회 테스트")
    @Test
    void getCountReviewsByBookId() throws Exception {
        when(reviewService.reviewCount(anyLong())).thenReturn(10L);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/{bookId}/reviews/count", 1L))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "책 아이디로 리뷰 갯수 조회 API",
                        pathParameters(
                                parameterWithName("bookId").description("책 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("리뷰 갯수")
                        )));

        verify(reviewService, times(1)).reviewCount(anyLong());
    }

    @DisplayName("리뷰 삭제 테스트")
    @Test
    void deleteReview() throws Exception {
        DeleteReviewRequest request = new DeleteReviewRequest("부적절한 내용");

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/reviews/{reviewId}/delete", 1L)
                        .header("Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deletedReason\":\"부적절한 내용\"}"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "리뷰 삭제 API",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )));

        verify(reviewService, times(1)).deleteReview(anyLong(), anyLong(), any(DeleteReviewRequest.class));
    }
}
