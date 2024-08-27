package com.nhnacademy.bookstore.entity.member;

import com.nhnacademy.bookstore.entity.address.Address;
import com.nhnacademy.bookstore.entity.member.enums.AuthProvider;
import com.nhnacademy.bookstore.entity.member.enums.Grade;
import com.nhnacademy.bookstore.entity.member.enums.Status;
import com.nhnacademy.bookstore.entity.memberauth.MemberAuth;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.member.member.dto.request.CreateMemberRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 6, max = 255)
    private String password;

    @NotNull
    private Long point;

    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    private int age;

    @NotNull
    @Size(min = 1, max = 11)
    private String phone;

    @NotNull
    @Column(unique = true)
    private String email;

    private ZonedDateTime birthday;

    @NotNull
    private Grade grade;

    @NotNull
    private Status status;

    private ZonedDateTime lastLoginDate;

    @NotNull
    private ZonedDateTime createdAt;

    private ZonedDateTime modifiedAt;
    private ZonedDateTime deletedAt;

    private AuthProvider authProvider;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addressList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberAuth> memberAuthList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointRecord> pointRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Purchase> purchaseList = new ArrayList<>();

    public Member(CreateMemberRequest request) {
        LocalDate birthday = LocalDate.parse(request.birthday());
        ZonedDateTime zonedBirthday = birthday.atStartOfDay(ZoneId.systemDefault());
        this.setPassword(request.password());
        this.setPoint(5000L);
        this.setName(request.name());
        this.setAge(request.age());
        this.setStatus(Status.Active);
        this.setPhone(request.phone());
        this.setEmail(request.email());
        this.setBirthday(zonedBirthday);
        this.setGrade(Grade.General);
        this.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        this.setAuthProvider(AuthProvider.GENERAL);
        this.setLastLoginDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
    }

    /**
     * 멤버가 관리자인지 판단하는 메서드입니다.
     *
     * @return true or false
     * @author 김은비
     */
    public boolean isAdmin() {
        return memberAuthList.stream()
                .anyMatch(memberAuth -> memberAuth.getAuth().getName().equals("ADMIN"));
    }

    /**
     * 멤버 권한을 추가하는 메서드입니다.
     *
     * @param memberAuth 추가할 멤버 권한
     * @author 김은비
     */
    public void addMemberAuth(MemberAuth memberAuth) {
        if (memberAuthList.stream().noneMatch(auth -> auth.getAuth().getName().equals(memberAuth.getAuth().getName()))) {
            memberAuthList.add(memberAuth);
            memberAuth.setMember(this);
        }
    }
}
