package com.nhnacademy.bookstore.member.address.service;

import com.nhnacademy.bookstore.entity.address.Address;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.address.dto.request.UpdateAddressRequest;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    void save(Address address,Member member);

    List<Address> readAll(Member member);

    Address updateAddress(Long addressId, UpdateAddressRequest updateAddressRequest);

    void deleteAddress(Long addressId);
}
