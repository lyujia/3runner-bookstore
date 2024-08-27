package com.nhnacademy.bookstore.member.pointrecord.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.member.pointrecord.dto.request.ReadPointRecordRequest;
import com.nhnacademy.bookstore.member.pointrecord.dto.response.ReadPointRecordResponse;
import com.nhnacademy.bookstore.member.pointrecord.service.PointRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@WebMvcTest(PointRecordController.class)
class PointRecordControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointRecordService pointRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("맴버의 포인트 내역 출력")
    @Test
    void testReadPointRecord() throws Exception {
        Long memberId = 1L;
        ReadPointRecordRequest readPointRecordRequest = new ReadPointRecordRequest(0, 10, "createdAt");

        ReadPointRecordResponse response1 = new ReadPointRecordResponse(1L, 100L, "2023-07-18", "test content 1");
        ReadPointRecordResponse response2 = new ReadPointRecordResponse(2L, 200L, "2023-07-19", "test content 2");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt"));
        Page<ReadPointRecordResponse> pageResponse = new PageImpl<>(Arrays.asList(response1, response2), pageable, 2);

        given(pointRecordService.readByMemberId(memberId, pageable)).willReturn(pageResponse);

        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members/points")
                        .header("Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(readPointRecordRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-point-record",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("sort").type(JsonFieldType.STRING).optional().description("정렬 기준")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.content[].recordId").type(JsonFieldType.NUMBER).description("레코드 ID"),
                                fieldWithPath("body.data.content[].usePoint").type(JsonFieldType.NUMBER).description("사용된 포인트"),
                                fieldWithPath("body.data.content[].createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                fieldWithPath("body.data.content[].content").type(JsonFieldType.STRING).description("내용"),
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
}