package com.nhnacademy.bookstore.member.address.service;

import com.nhnacademy.bookstore.entity.address.Address;
import com.nhnacademy.bookstore.member.address.dto.request.UpdateAddressRequest;
import com.nhnacademy.bookstore.member.address.exception.AddressNotExistsException;
import com.nhnacademy.bookstore.member.address.repository.AddressRepository;
import com.nhnacademy.bookstore.member.address.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressServiceImpl addressServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주소 업데이트 테스트")
    void updateAddressTest() {
        // Given
        Long addressId = 1L;
        UpdateAddressRequest updateAddressRequest = UpdateAddressRequest.builder()
                .name("Updated Name")
                .country("Updated Country")
                .city("Updated City")
                .state("Updated State")
                .road("Updated Road")
                .postalCode("123456")
                .build();

        Address address = Address.builder()
                .name("Old Name")
                .build();

        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        // When
        Address updatedAddress = addressServiceImpl.updateAddress(addressId, updateAddressRequest);

        // Then
        assertEquals("Updated Name", updatedAddress.getName());
        assertEquals("Updated Country", updatedAddress.getCountry());
        assertEquals("Updated City", updatedAddress.getCity());
        assertEquals("Updated State", updatedAddress.getState());
        assertEquals("Updated Road", updatedAddress.getRoad());
        assertEquals("123456", updatedAddress.getPostalCode());

        verify(addressRepository, times(1)).findById(anyLong());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    @DisplayName("주소 업데이트 시 주소가 없는 경우")
    void updateAddress_NotExistsTest() {
        // Given
        Long addressId = 1L;
        UpdateAddressRequest updateAddressRequest = UpdateAddressRequest.builder()
                .name("Updated Name")
                .build();

        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AddressNotExistsException.class, () ->
                addressServiceImpl.updateAddress(addressId, updateAddressRequest)
        );

        verify(addressRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("주소 삭제 테스트")
    void deleteAddressTest() {
        // Given
        Long addressId = 1L;

        // When
        addressServiceImpl.deleteAddress(addressId);

        // Then
        verify(addressRepository, times(1)).deleteById(anyLong());
    }
}
