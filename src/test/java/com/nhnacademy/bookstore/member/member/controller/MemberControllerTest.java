package com.nhnacademy.bookstore.member.member.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.entity.auth.Auth;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.member.enums.Status;
import com.nhnacademy.bookstore.member.auth.service.impl.AuthServiceImpl;
import com.nhnacademy.bookstore.member.member.dto.request.*;
import com.nhnacademy.bookstore.member.member.service.impl.MemberServiceImpl;
import com.nhnacademy.bookstore.member.memberauth.service.impl.MemberAuthServiceImpl;
import com.nhnacademy.bookstore.member.pointrecord.service.impl.PointRecordServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends BaseDocumentTest {

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private PointRecordServiceImpl pointRecordService;

    @MockBean
    private AuthServiceImpl authService;

    @MockBean
    private MemberAuthServiceImpl memberAuthService;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @Autowired
    private MockMvc mockMvc;

    @DisplayName("새로운 회원 생성")
    @Test
    void createMember() throws Exception {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .age(25)
                .phone("010-0000-0000")
                .birthday("2022-04-22")
                .build();

        Auth auth = new Auth(1L,"USER");

        when(authService.getAuth(any())).thenReturn(auth);
        when(memberService.save(any())).thenReturn(new Member());

        doNothing().when(memberAuthService).saveAuth(any(),any());


        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"password\": \"password\", \"name\": \"Test User\", \"age\": 25 ,\"phone\":\"01000000000\",\"birthday\": \"2022-04-22\"}"))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,
                        "회원의 정보를 받아 등록하는 API",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description("전화번호"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("birthday").type(JsonFieldType.STRING).description("생일")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NULL).description("null 반환")
                        )
                ));

    }

    @Test
    void readById() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setName("Test User");
        member.setEmail("test@example.com");
        member.setAge(25);

        when(memberService.readById(anyLong())).thenReturn(member);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/members")
                        .header("Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data.name").value("Test User"))
                .andDo(document(snippetPath,
                        "멤버의 정보를 가져오는 API",
                        requestHeaders(
                                headerWithName("Member-Id").description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.password").type(JsonFieldType.NULL).description("비밀번호 (null 값)"),
                                fieldWithPath("body.data.point").type(JsonFieldType.NULL).description("포인트 (null 값)"),
                                fieldWithPath("body.data.name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("body.data.age").type(JsonFieldType.NUMBER).description("나이"),
                                fieldWithPath("body.data.phone").type(JsonFieldType.NULL).description("핸드폰 (null 값)"),
                                fieldWithPath("body.data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("body.data.grade").type(JsonFieldType.NULL).description("등급 (null 값)"),
                                fieldWithPath("body.data.birthday").type(JsonFieldType.NULL).description("생일 (null 값)"),
                                fieldWithPath("body.data.lastLoginDate").type(JsonFieldType.NULL).description("마지막 로그인 날짜 (null 값)"),
                                fieldWithPath("body.data.createdAt").type(JsonFieldType.NULL).description("생성 날짜 (null 값)"),
                                fieldWithPath("body.data.modifiedAt").type(JsonFieldType.NULL).description("수정 날짜 (null 값)")
                        ))
                );
    }



//    @Test
//    void readByEmailAndPassword() throws Exception {
//        Member member = new Member();
//        member.setId(1L);
//        member.setName("Test User");
//        member.setEmail("test@example.com");
//        member.setPassword("password");
//        member.setAge(25);
//
//        when(memberService.readByEmailAndPassword(anyString(), anyString())).thenReturn(member);
//
//        mockMvc.perform(post("/bookstore/members/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"email\": \"test@example.com\", \"password\": \"password\" }"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.body.name").value("Test User"));
//    }
    @Test
    void readAuths() throws Exception {
        Auth auth = new Auth();
        auth.setName("USER");

        when(memberAuthService.readAllAuths(anyLong())).thenReturn(List.of(auth));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/members/auths")
                        .header("Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data[0].auth").value("USER"))
                .andDo(document(snippetPath,"멤버의 권한이 담긴 리스트를 반환하는 API 문서화",
                        requestHeaders(headerWithName("Member-Id").description("멤버 아이디")),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.ARRAY).description("권한 리스트")
                                        .attributes(key("constraints").value("리스트 형태의 권한 데이터")),
                                fieldWithPath("body.data[].auth").type(JsonFieldType.STRING).description("권한")
                        )));

    }

    @Test
    void updateMember() throws Exception {
        // UpdateMemberRequest 객체를 빌더 패턴을 사용하여 생성합니다.
        UpdateMemberRequest request = UpdateMemberRequest.builder()
                .name("Updated User")
                .phone(null) // 또는 비워둘 수 있지만, 문서화에서는 null을 명시적으로 표현하는 것이 좋습니다.
                .birthday(null) // 또는 비워둘 수 있지만, 문서화에서는 null을 명시적으로 표현하는 것이 좋습니다.
                .build();

        // 업데이트된 멤버 객체를 생성합니다.
        Member updatedMember = new Member();
        updatedMember.setId(1L);
        updatedMember.setName("Updated User");

        // 멤버 서비스의 updateMember 메서드가 호출될 때 업데이트된 멤버 객체를 반환하도록 설정합니다.
        when(memberService.updateMember(anyLong(), any(UpdateMemberRequest.class))).thenReturn(updatedMember);

        // MockMvc를 사용하여 API 호출을 수행합니다.
        mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)) // JSON 형태로 변환하여 body에 포함합니다.
                        .header("Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data.name").value("Updated User"))
                .andDo(document(snippetPath,
                        "업데이트 정보를 받아 멤버를 업데이트 하는 API",
                        requestHeaders(
                                headerWithName("Member-Id").description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("age").type(JsonFieldType.NUMBER).optional().description("나이"),
                                fieldWithPath("phone").type(JsonFieldType.STRING).optional().description("핸드폰 번호"),
                                fieldWithPath("birthday").type(JsonFieldType.STRING).optional().description("생일")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.id").type(JsonFieldType.STRING).description("멤버 아이디"),
                                fieldWithPath("body.data.name").type(JsonFieldType.STRING).description("멤버 이름")
                        ))
                );
    }

    @Test
    void deleteMember() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/members")
                        .header("Member-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("delete-member",
                        requestHeaders(
                                headerWithName("Member-Id").description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        ))
                );
    }



    @Test
    void oauthMember() throws Exception {
        UserProfile userProfile = new UserProfile();
        userProfile.setEmail("test@example.com");
        userProfile.setName("Test User");

        Auth auth = new Auth();
        auth.setName("USER");

        Member member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setName("Test User");

        when(authService.getAuth(anyString())).thenReturn(auth);
        when(memberService.saveOrGetPaycoMember(any(UserProfile.class))).thenReturn(member);
        when(memberAuthService.readAllAuths(anyLong())).thenReturn(Collections.singletonList(auth));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members/oauth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"name\": \"Test User\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andDo(document("oauth-member",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름")
                        ),
                        responseFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).optional().description("비밀번호"),
                                fieldWithPath("auth").type(JsonFieldType.ARRAY).description("권한 리스트"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버아이디"),
                                subsectionWithPath("auth[]").ignored()// 다른 필요한 응답 필드들을 여기에 추가할 수 있습니다
                        )
                ));
    }

    @Test
    void emailExists() throws Exception {
        when(memberService.readByEmail(anyString())).thenReturn(new Member());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/members/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data").value(true))
                .andDo(document("check-email-exists",
                        queryParameters(
                                parameterWithName("email").description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.BOOLEAN).description("이메일 존재 여부")
                        )
                ));
    }

    @Test
    void updatePassword() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest("newpassword");

        mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/members/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"newPassword\": \"newpassword\" }")
                        .header("Member-Id", 1L))
                .andExpect(status().isOk())
                .andDo(document("update-password",
                        requestHeaders(
                                headerWithName("Member-Id").description("회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로운 비밀번호")
                        )
                ));
    }

    @Test
    void isPasswordMatch() throws Exception {
        PasswordCorrectRequest request = new PasswordCorrectRequest("newpassword");

        when(memberService.isCorrectPassword(anyLong(), anyString())).thenReturn(true);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"password\": \"newpassword\" }")
                        .header("Member-Id", "1"))
                .andExpect(status().isOk())
                .andDo(document("check-password-match",
                        requestHeaders(
                                headerWithName("Member-Id").description("멤버 아이디")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("입력한 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        ))
                );
    }
    @DisplayName("Update last login for active member")
    @Test
    void lastLoginUpdate() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setStatus(Status.Active);

        when(memberService.readById(anyLong())).thenReturn(member);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/members/lastLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"memberId\": 1 }"))
                .andExpect(status().isOk())
                .andDo(document("last-login-update",
                        requestFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

    @DisplayName("Awake dormant member by email")
    @Test
    void dormantAwake() throws Exception {
        Member member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");

        when(memberService.readByEmail(anyString())).thenReturn(member);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/members/lastLogin/dormantAwake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\" }"))
                .andExpect(status().isOk())
                .andDo(document("dormant-awake",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("멤버 이메일")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        )
                ));
    }

}
