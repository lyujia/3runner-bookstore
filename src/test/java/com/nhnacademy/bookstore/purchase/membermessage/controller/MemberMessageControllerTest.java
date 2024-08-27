package com.nhnacademy.bookstore.purchase.membermessage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.membermessage.dto.ReadMemberMessageResponse;
import com.nhnacademy.bookstore.purchase.membermessage.dto.UpdateMemberMessageRequest;
import com.nhnacademy.bookstore.purchase.membermessage.service.MemberMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(MemberMessageController.class)
class MemberMessageControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberMessageService memberMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("맴버 메시지 전체 읽기")
    @Test
    void testReadAllById() throws Exception {
        Long memberId = 1L;
        ReadMemberMessageResponse response = new ReadMemberMessageResponse(
                1L,
                "Test Message",
                null,
                ZonedDateTime.now()
        );
        Page<ReadMemberMessageResponse> pageResponse = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(memberMessageService.readAll(memberId, 0, 10)).willReturn(pageResponse);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/messages")
                        .header("Member-Id", memberId)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-all-messages",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content[].id").type(JsonFieldType.NUMBER).description("메시지 ID"),
                                fieldWithPath("body.data.content[].message").type(JsonFieldType.STRING).description("메시지 내용"),
                                fieldWithPath("body.data.content[].viewAt").type(JsonFieldType.STRING).optional().description("메시지 조회 시간"),
                                fieldWithPath("body.data.content[].sendAt").type(JsonFieldType.NULL).description("메시지 발송 시간"),
                                fieldWithPath("body.data.pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                fieldWithPath("body.data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("body.data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("body.data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않은 여부"),
                                fieldWithPath("body.data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬된 여부"),
                                fieldWithPath("body.data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("body.data.pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("body.data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징된 여부"),
                                fieldWithPath("body.data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징되지 않은 여부"),
                                fieldWithPath("body.data.totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("body.data.totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                fieldWithPath("body.data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("body.data.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("body.data.number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("body.data.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("body.data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않은 여부"),
                                fieldWithPath("body.data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬된 여부"),
                                fieldWithPath("body.data.numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 요소 수"),
                                fieldWithPath("body.data.first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("body.data.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비어 있는지 여부")
                        )
                ));
    }

    @DisplayName("읽지 않은 메시지 수 조회")
    @Test
    void testReadUnreadedMessage() throws Exception {
        Long memberId = 1L;
        Long unreadCount = 5L;

        given(memberMessageService.countUnreadMessage(memberId)).willReturn(unreadCount);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/messages/counts")
                        .header("Member-Id", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-unreaded-messages",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("읽지 않은 메시지 수")
                        )
                ));
    }

    @DisplayName("메시지 읽음 처리")
    @Test
    void testUpdateMessage() throws Exception {
        Long memberMessageId = 1L;
        UpdateMemberMessageRequest request = new UpdateMemberMessageRequest(memberMessageId);

        String jsonRequest = objectMapper.writeValueAsString(request);

        this.mockMvc.perform(put("/bookstore/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("update-message",
                        requestFields(
                                fieldWithPath("memberMessageId").type(JsonFieldType.NUMBER).description("맴버 메시지 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NULL).description("응답 데이터 (없음)")
                        )
                ));
    }
}