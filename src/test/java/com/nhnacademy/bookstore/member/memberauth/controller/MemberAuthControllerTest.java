package com.nhnacademy.bookstore.member.memberauth.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.member.memberauth.dto.response.MemberAuthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;


import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.member.service.MemberService;
import com.nhnacademy.bookstore.member.memberauth.service.MemberAuthService;


/**
 * MemberAuthController 에 대한 테스트입니다.
 *
 * @author 오연수
 */
@WebMvcTest(controllers = MemberAuthController.class)
public class MemberAuthControllerTest extends BaseDocumentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberAuthService memberAuthService;

    @MockBean
    private MemberService memberService;

    @DisplayName("존재하는 멤버 이메일을 통해 MemberAuth 를 가져오는 경우")
    @Test
    void testGetMemberAuth_Success() throws Exception {
        String email = "test@example.com";
        String password = "password";
        Long memberId = 1L;

        Member member = new Member();
        member.setId(memberId);
        member.setEmail(email);
        member.setPassword(password);

        Auth auth1 = new Auth();
        auth1.setName("USER");
        Auth auth2 = new Auth();
        auth2.setName("ADMIN");

        List<Auth> authList = List.of(auth1, auth2);

        when(memberService.readByEmail(anyString())).thenReturn(member);
        when(memberAuthService.readAllAuths(memberId)).thenReturn(authList);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.header.resultCode").value(200))
                .andExpect(jsonPath("$.header.successful").value(true))
                .andExpect(jsonPath("$.body.data.email").value(email))
                .andExpect(jsonPath("$.body.data.password").value(password))
                .andExpect(jsonPath("$.body.data.auth").isArray())
                .andExpect(jsonPath("$.body.data.auth[0]").value("USER"))
                .andExpect(jsonPath("$.body.data.memberId").value(memberId))
                .andDo(document(snippetPath, "존재하는 멤버 이메일로 권한을 가져오는 API",
                        requestHeaders(
                                headerWithName("Content-Type").description("Content Type")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("body.data.password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("body.data.auth").type(JsonFieldType.ARRAY).description("권한 리스트"),
                                fieldWithPath("body.data.memberId").type(JsonFieldType.NUMBER).description("멤버 아이디")
                        )
                ));
    }

    @DisplayName("없는 멤버 이메일을 통해 MemberAuth 를 가져오는 경우")
    @Test
    void testGetMemberAuth_MemberNotFound() throws Exception {
        when(memberService.readByEmail(anyString())).thenReturn(null);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"unknown@example.com\"}"))
                .andExpect(status().isOk())
                .andDo(document("get-member-auth-not-found"));
    }
    @DisplayName("Member login with token and header")
    @Test
    void testLogin() throws Exception {
        Long memberId = 1L;
        String token = "testToken";
        MemberAuthResponse memberAuthResponse = MemberAuthResponse.builder()
                .email("test@example.com")
                .password("password")
                .auth(List.of("USER", "ADMIN"))
                .memberId(memberId)
                .build();

        when(memberService.readByIdForSecurity(anyLong())).thenReturn(memberAuthResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/token/login/{token}", token)
                        .header("Member-Id", memberId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.header.resultCode").value(200))
                .andExpect(jsonPath("$.header.successful").value(true))
                .andExpect(jsonPath("$.body.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.body.data.password").value("password"))
                .andExpect(jsonPath("$.body.data.auth[0]").value("USER"))
                .andExpect(jsonPath("$.body.data.auth[1]").value("ADMIN"))
                .andExpect(jsonPath("$.body.data.memberId").value(memberId))
                .andDo(document("login",
                        pathParameters(
                                parameterWithName("token").description("The authentication token")
                        ),
                        requestHeaders(
                                headerWithName("Member-Id").description("The ID of the member")
                        ),
                        responseFields(
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("Indicates if the request was successful"),
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("The result code"),
                                fieldWithPath("body.data.email").type(JsonFieldType.STRING).description("The email of the member"),
                                fieldWithPath("body.data.password").type(JsonFieldType.STRING).description("The password of the member"),
                                fieldWithPath("body.data.auth").type(JsonFieldType.ARRAY).description("The list of authorities for the member"),
                                fieldWithPath("body.data.memberId").type(JsonFieldType.NUMBER).description("The ID of the member")
                        )
                ));
    }
}
