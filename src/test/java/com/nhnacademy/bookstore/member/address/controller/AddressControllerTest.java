package com.nhnacademy.bookstore.member.address.controller;

import com.nhnacademy.bookstore.BaseDocumentTest;
import com.nhnacademy.bookstore.entity.address.Address;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.address.dto.request.CreateAddressRequest;
import com.nhnacademy.bookstore.member.address.dto.request.UpdateAddressRequest;

import com.nhnacademy.bookstore.member.address.dto.response.UpdateAddressResponse;
import com.nhnacademy.bookstore.member.address.service.impl.AddressServiceImpl;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;

import com.nhnacademy.bookstore.member.member.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
@WebMvcTest(controllers = AddressController.class)
class AddressControllerTest extends BaseDocumentTest {

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private AddressServiceImpl addressServiceImpl;


    @Autowired
    private MockMvc mockMvc;


    @Test
    void createAddress() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest(
                "Home","Country","City","State","PostalCode","CountryCode"
        );

        Member member = new Member(new CreateMemberRequest("email","password","name","010-0000-0000",25,"2024-05-28"));
        member.setId(1L);
        Address address = new Address(request,member);
        when(memberService.readById(anyLong())).thenReturn(member);
        Mockito.doNothing().when(addressServiceImpl).save(any(Address.class),any(Member.class));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/bookstore/members/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Home\", \"country\": \"Country\", \"city\": \"City\", \"state\": \"State\", \"road\": \"Road\", \"postalCode\": \"12345\"}")
                        .header("Member-Id", 1L))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,"주소를 생성하는 api",
                        requestHeaders(
                                headerWithName("Member-Id").description("멤버 아이디")
                        )
                        ,
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("주소이름"),
                                fieldWithPath("country").type(JsonFieldType.STRING).description("나라명"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("도시명"),
                                fieldWithPath("state").type(JsonFieldType.STRING).description("주"),
                                fieldWithPath("road").type(JsonFieldType.STRING).description("도로명"),
                                fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data").type(JsonFieldType.NULL).description("주소 아이디")
                        )
                        ));
    }

    @DisplayName("Read all addresses for a member")
    @Test
    void readAllAddresses() throws Exception {
        Member member = new Member();
        member.setId(1L);

        Address address = new Address(new CreateAddressRequest("name", "country", "city", "state", "road", "postalCode"), member);
        address.setId(1L);

        when(memberService.readById(anyLong())).thenReturn(member);
        when(addressServiceImpl.readAll(any(Member.class))).thenReturn(Arrays.asList(address));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/bookstore/members/addresses")
                        .header("Member-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data[0].addressId").value(1L))
                .andExpect(jsonPath("$.body.data[0].name").value("name"))
                .andDo(document("read-all-addresses", "멤버의 모든 주소를 읽는 API",
                        requestHeaders(
                                headerWithName("Member-Id").description("멤버 아이디")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data[].addressId").type(JsonFieldType.NUMBER).description("주소 아이디"),
                                fieldWithPath("body.data[].name").type(JsonFieldType.STRING).description("주소 이름"),
                                fieldWithPath("body.data[].country").type(JsonFieldType.STRING).description("나라"),
                                fieldWithPath("body.data[].city").type(JsonFieldType.STRING).description("도시"),
                                fieldWithPath("body.data[].state").type(JsonFieldType.STRING).description("주"),
                                fieldWithPath("body.data[].road").type(JsonFieldType.STRING).description("도로명"),
                                fieldWithPath("body.data[].postalCode").type(JsonFieldType.STRING).description("우편번호")
                        )
                ));
    }

    @Test
    void updateAddress() throws Exception {
        Address address = new Address(new CreateAddressRequest("Home","Country","City","State","Road","12345"),
                new Member(new CreateMemberRequest("email","password","name","phone",25,"2024-05-28")));
        address.setId(1L);
        UpdateAddressResponse addressResponse = new UpdateAddressResponse(1L,"name");

        when(addressServiceImpl.updateAddress(anyLong(), any(UpdateAddressRequest.class))).thenReturn(address);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/bookstore/members/addresses/{addressId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Home\", \"country\": \"Country\", \"city\": \"City\", \"state\": \"State\", \"road\": \"Road\", \"postalCode\": \"12345\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.body.data.id").value(1L))
                .andExpect(jsonPath("$.body.data.name").value("Home"))
                .andDo(document(snippetPath,"요청대로 주소를 갱신한다.",
                        pathParameters(
                            parameterWithName("addressId").description("주소 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("주소이름"),
                                fieldWithPath("country").type(JsonFieldType.STRING).description("나라명"),
                                fieldWithPath("city").type(JsonFieldType.STRING).description("도시명"),
                                fieldWithPath("state").type(JsonFieldType.STRING).description("주"),
                                fieldWithPath("road").type(JsonFieldType.STRING).description("도로명"),
                                fieldWithPath("postalCode").type(JsonFieldType.STRING).description("우편번호")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("body.data.id").type(JsonFieldType.NUMBER).description("주소 ID"),
                                fieldWithPath("body.data.name").type(JsonFieldType.STRING).description("주소명")
                        ))
                );
    }

    @Test
    void deleteAddress() throws Exception {
        doNothing().when(addressServiceImpl).deleteAddress(anyLong());
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/bookstore/members/addresses/{addressId}", 1L))
                .andExpect(status().isOk())
                .andDo(document(snippetPath,"주소를 삭제하는 API",
                        pathParameters(
                                parameterWithName("addressId").description("주소 ID")
                        ),
                        responseFields(
                                fieldWithPath("header.resultCode").type(JsonFieldType.NUMBER).description("결과 코드"),
                                fieldWithPath("header.successful").type(JsonFieldType.BOOLEAN).description("성공 여부")
                        ))
                );
    }
}
