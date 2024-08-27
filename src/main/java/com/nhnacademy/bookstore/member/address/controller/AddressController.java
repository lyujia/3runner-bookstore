package com.nhnacademy.bookstore.member.address.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.nhnacademy.bookstore.member.address.exception.AddressFormErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.bookstore.entity.address.Address;
import com.nhnacademy.bookstore.entity.member.Member;

import com.nhnacademy.bookstore.member.address.dto.request.CreateAddressRequest;
import com.nhnacademy.bookstore.member.address.dto.request.UpdateAddressRequest;
import com.nhnacademy.bookstore.member.address.dto.response.AddressResponse;
import com.nhnacademy.bookstore.member.address.dto.response.UpdateAddressResponse;
import com.nhnacademy.bookstore.member.address.service.impl.AddressServiceImpl;
import com.nhnacademy.bookstore.member.member.service.impl.MemberServiceImpl;
import com.nhnacademy.bookstore.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * The type Address controller.
 *
 * @author 오연수, 유지아
 */
@RestController
@RequiredArgsConstructor
public class AddressController {
    private final MemberServiceImpl memberService;
    private final AddressServiceImpl addressServiceImpl;

    /**
     * Create address response entity.
     * 주소만든다음 가지고있는 모든 주소를 반환한다.
     * @param request the request
     * @author 유지아
     * @return the response entity
     *
     */
    @PostMapping("/bookstore/members/addresses")
    public ApiResponse<Long> createAddress(@RequestBody CreateAddressRequest request,
                                           BindingResult bindingResult,
                                           @RequestHeader(value = "Member-Id") Long memberId) {
        if (bindingResult.hasErrors()) {
            throw new AddressFormErrorException(bindingResult.getFieldErrors().toString());
        }

        Member member = memberService.readById(memberId);
        Address address = new Address(request, member);
        addressServiceImpl.save(address, member);
        return ApiResponse.createSuccess(address.getId());
    }


    /**
     * Find all addresses response entity.
     * 멤버아이디에 따른 모든 주소를 가져온다.
     * @author 유지아
     * @param memberId the member id
     * @return the response entity
     */
    @GetMapping("/bookstore/members/addresses")
    public ApiResponse<List<AddressResponse>> readAllAddresses(@RequestHeader("Member-Id") Long memberId) {

        Member member = memberService.readById(memberId);
        List<Address> addresses = addressServiceImpl.readAll(member);
        return new ApiResponse<>(new ApiResponse.Header(true, 200),
                new ApiResponse.Body<>(
                        addresses.stream()
                                .map(a -> AddressResponse.builder()
                                        .addressId(a.getId())
                                        .name(a.getName())
                                        .country(a.getCountry())
                                        .city(a.getCity())
                                        .state(a.getState())
                                        .road(a.getRoad())
                                        .postalCode(a.getPostalCode())
                                        .build()
                                ).toList()));
    }

    /**
     * 주소 업데이트
     *
     * @param addressId            the address id
     * @param updateAddressRequest name, country, city, state, road, postalCode
     * @return the api response - UpdateAddressResponse DTO
     * @author 오연수
     */
    @PutMapping("/bookstore/members/addresses/{addressId}")
    public ApiResponse<UpdateAddressResponse> updateAddress(@RequestBody @Valid UpdateAddressRequest updateAddressRequest, @PathVariable(name = "addressId") Long addressId) {
        Address address = addressServiceImpl.updateAddress(addressId, updateAddressRequest);
        UpdateAddressResponse updateAddressResponse = UpdateAddressResponse.builder()
                .id(addressId)
                .name(address.getName())
                .build();
        return new ApiResponse<>(new ApiResponse.Header(true,200),new ApiResponse.Body<>(updateAddressResponse));

    }


    /**
     * 주소 삭제
     *
     * @param addressId the address id
     * @return the api response - Void
     * @author 오연수
     */
    @DeleteMapping("/bookstore/members/addresses/{addressId}")
    public ApiResponse<Void> deleteAddress(@PathVariable(name = "addressId") Long addressId) {

        addressServiceImpl.deleteAddress(addressId);
        return new ApiResponse<>(new ApiResponse.Header(true, HttpStatus.NO_CONTENT.value()));
    }
}
