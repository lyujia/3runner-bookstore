package com.nhnacademy.bookstore.purchase.pointpolicy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.purchase.pointpolicy.dto.PointPolicyResponseRequest;
import com.nhnacademy.bookstore.purchase.pointpolicy.service.PointPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(PointPolicyController.class)
class PointPolicyControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointPolicyService pointPolicyService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("포인트 정책 저장 또는 업데이트 API")
    @Test
    void testSaveOrUpdatePolicy() throws Exception {
        PointPolicyResponseRequest request = new PointPolicyResponseRequest("TestPolicy", 10);
        Long policyId = 1L;

        given(pointPolicyService.update(anyString(), anyInt())).willReturn(policyId);

        this.mockMvc.perform(post("/bookstore/points/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("save-or-update-policy",
                        requestFields(
                                fieldWithPath("policyKey").type(JsonFieldType.STRING).description("정책 키"),
                                fieldWithPath("policyValue").type(JsonFieldType.NUMBER).description("정책 값")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NUMBER).description("정책 ID")
                        )
                ));
    }

    @DisplayName("모든 포인트 정책 조회 API")
    @Test
    void testReadPolicy() throws Exception {
        PointPolicyResponseRequest response1 = new PointPolicyResponseRequest("Policy1", 10);
        PointPolicyResponseRequest response2 = new PointPolicyResponseRequest("Policy2", 20);
        List<PointPolicyResponseRequest> responses = Arrays.asList(response1, response2);

        given(pointPolicyService.readAll()).willReturn(responses);

        this.mockMvc.perform(get("/bookstore/points/policies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-all-policies",
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].policyKey").type(JsonFieldType.STRING).description("정책 키"),
                                fieldWithPath("body.data[].policyValue").type(JsonFieldType.NUMBER).description("정책 값")
                        )
                ));
    }

    @DisplayName("단일 포인트 정책 조회 API")
    @Test
    void testReadOne() throws Exception {
        String policyKey = "TestPolicy";
        PointPolicyResponseRequest response = new PointPolicyResponseRequest(policyKey, 10);

        given(pointPolicyService.read(anyString())).willReturn(response);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/points/policies/{policyKey}", policyKey)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("read-one-policy",
                        pathParameters(
                                parameterWithName("policyKey").description("정책 키")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.policyKey").type(JsonFieldType.STRING).description("정책 키"),
                                fieldWithPath("body.data.policyValue").type(JsonFieldType.NUMBER).description("정책 값")
                        )
                ));
    }
}