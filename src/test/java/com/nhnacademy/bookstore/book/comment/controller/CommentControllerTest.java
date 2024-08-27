package com.nhnacademy.bookstore.book.comment.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.book.comment.dto.request.CreateCommentRequest;
import com.nhnacademy.bookstore.book.comment.dto.response.CommentResponse;
import com.nhnacademy.bookstore.book.comment.service.CommentService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest extends BaseDocumentTest {

    @MockBean
    private CommentService commentService;

    @DisplayName("댓글 생성 테스트")
    @Test
    void createComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest("좋은 댓글입니다");

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/books/reviews/{reviewId}", 1L)
                        .header("Member-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"좋은 댓글입니다\"}"))
                .andExpect(status().isOk())
                .andDo(document("comment-create",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )));

        verify(commentService).createComment(anyLong(), anyLong(), any(CreateCommentRequest.class));
    }

    @DisplayName("댓글 수정 테스트")
    @Test
    void updateComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest("수정된 댓글입니다");

        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/books/reviews/{commentId}", 1L)
                        .header("Member-id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"수정된 댓글입니다\"}"))
                .andExpect(status().isOk())
                .andDo(document("comment-update",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )));

        verify(commentService).updateComment(anyLong(), anyLong(), any(CreateCommentRequest.class));
    }

    @DisplayName("댓글 삭제 테스트")
    @Test
    void deleteComment() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/books/reviews/{commentId}/delete", 1L)
                        .header("Member-id", 1L))
                .andExpect(status().isOk())
                .andDo(document("comment-delete",
                        pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )));

        verify(commentService).deleteComment(anyLong(), anyLong());
    }

    @DisplayName("리뷰 아이디로 댓글 조회 테스트")
    @Test
    void readAllCommentsByReviewId() throws Exception {
        Page<CommentResponse> response = new PageImpl<>(Collections.emptyList());

        when(commentService.readAllCommentsByReviewId(anyLong(), any(Pageable.class))).thenReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/reviews/{reviewId}/comments", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document("comment-read-all-by-reviewId",
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("댓글 목록"),
                                fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 댓글 수"),
                                fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 댓글 수"),
                                fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어 있는지 여부"),
                                fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
                                fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                fieldWithPath("body.data.pageable").type(JsonFieldType.STRING).description("페이지 정보")
                        )));

        verify(commentService).readAllCommentsByReviewId(anyLong(), any(Pageable.class));
    }


    @DisplayName("멤버 아이디로 댓글 조회 테스트")
    @Test
    void readAllCommentsByMemberId() throws Exception {
        Page<CommentResponse> response = new PageImpl<>(Collections.emptyList());

        when(commentService.readAllCommentsByMemberId(anyLong(), any(Pageable.class))).thenReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/books/reviews/member/comments")
                        .header("Member-id", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "멤버 아이디로 댓글 조회 API",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content").type(JsonFieldType.ARRAY).description("댓글 목록"),
                                fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 댓글 수"),
                                fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 댓글 수"),
                                fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("비어 있는지 여부"),
                                fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어있는지 여부"),
                                fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬되었는지 여부"),
                                fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않았는지 여부"),
                                fieldWithPath("body.data.pageable").type(JsonFieldType.STRING).description("페이지 정보")
                        )));

        verify(commentService).readAllCommentsByMemberId(anyLong(), any(Pageable.class));
    }


}
