package com.nhnacademy.bookstore.entity.address;


import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.member.address.dto.request.CreateAddressRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Member member;

    @Size(min = 1, max = 20)
    @NotNull
    private String name;
    @Size(min = 1, max = 100)
    @NotNull
    private String country;
    @Size(min = 1, max = 100)
    @NotNull
    private String city;
    @Size(min = 1, max = 100)
    @NotNull
    @Size(min = 1, max = 100)
    private String state;
    @NotNull
    @Size(min = 1, max = 100)
    private String road;
    @Size(min = 1, max = 20)
    @NotNull
    private String postalCode;

    public Address(CreateAddressRequest request, Member member) {
        this.setMember(member);
        this.setName(request.name());
        this.setCountry(request.country());
        this.setCity(request.city());
        this.setState(request.state());
        this.setRoad(request.road());
        this.setPostalCode(request.postalCode());
    }
}