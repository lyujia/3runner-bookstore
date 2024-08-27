package com.nhnacademy.bookstore.member.address.service.impl;

import com.nhnacademy.bookstore.entity.address.Address;
import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.address.dto.request.UpdateAddressRequest;
import com.nhnacademy.bookstore.member.address.exception.AddressFullException;
import com.nhnacademy.bookstore.member.address.exception.AddressNotExistsException;
import com.nhnacademy.bookstore.member.address.repository.AddressRepository;
import com.nhnacademy.bookstore.member.address.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The type Address service.
 *
 * @author 오연수, 유지아
 */
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    /**
     * Save.
     *
     * @param address the address -Address값을 받아 repository에 저장한다.  null -컨트롤러에서 전체 주소조회해서 반환하기 때문에 void반환으로 해둠.
     * @author 유지아, 오연수 Save. -주소를 받아 저장한다.
     */
    public void save(Address address, Member member) {
        if (this.readAll(member).size() > 9) {
            throw new AddressFullException();
        }
        addressRepository.save(address);
    }

    /**
     * Find all list.
     *
     * @param member the member -Member요소를 가져온다.
     * @return the list -Address 리스트를 반환해준다.
     * @author 유지아  Find all list. -유저를 받아 유저에 저장된 주소들을 반환한다.
     */
    public List<Address> readAll(Member member) {
        return addressRepository.findByMember(member);
    }

    /**
     * Update address.
     *
     * @param addressId            주소 id
     * @param updateAddressRequest name, country, city, state, road, postalCode
     * @return the address
     * @author 오연수
     */
    public Address updateAddress(Long addressId, UpdateAddressRequest updateAddressRequest) {
        Address address = addressRepository.findById(addressId).orElseThrow(AddressNotExistsException::new);

        address.setName(updateAddressRequest.name());
        address.setCountry(updateAddressRequest.country());
        address.setCity(updateAddressRequest.city());
        address.setState(updateAddressRequest.state());
        address.setRoad(updateAddressRequest.road());
        address.setPostalCode(updateAddressRequest.postalCode());

        return addressRepository.save(address);
    }

    /**
     * Delete address.
     *
     * @param addressId 주소 id
     * @author 오연수
     */
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
