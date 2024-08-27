package com.nhnacademy.bookstore.member.pointrecord.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.bookstore.entity.member.Member;
import com.nhnacademy.bookstore.entity.pointrecord.PointRecord;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.member.member.service.MemberPointService;
import com.nhnacademy.bookstore.member.pointrecord.exception.NoBuyPointRecordException;
import com.nhnacademy.bookstore.member.pointrecord.repository.PointRecordRepository;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.repository.PurchaseRepository;

@ExtendWith(MockitoExtension.class)
public class PointRecordServiceImplTest {

    @InjectMocks
    private PointRecordServiceImpl pointRecordService;

    @Mock
    private MemberPointService memberPointService;

    @Mock
    private PointRecordRepository pointRecordRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Test
    public void testSave_Success() {
        Long memberId = 1L;
        Long purchaseId = 1L;
        Long usePoint = 100L;
        String content = "buy point";

        Member member = new Member();
        member.setId(memberId);

        Purchase purchase = new Purchase();
        purchase.setId(purchaseId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));

        Long savedId = pointRecordService.save(usePoint, content, memberId, purchaseId);

        verify(pointRecordRepository, times(1)).save(any(PointRecord.class));
        verify(memberPointService, times(1)).updatePoint(memberId, usePoint);
    }

    @Test
    public void testSave_MemberNotExists() {
        Long memberId = 1L;
        Long purchaseId = 1L;
        Long usePoint = 100L;
        String content = "buy point";

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> {
            pointRecordService.save(usePoint, content, memberId, purchaseId);
        });

        verify(pointRecordRepository, times(0)).save(any(PointRecord.class));
        verify(memberPointService, times(0)).updatePoint(anyLong(), anyLong());
    }

    @Test
    public void testSave_PurchaseNotExists() {
        Long memberId = 1L;
        Long purchaseId = 1L;
        Long usePoint = 100L;
        String content = "buy point";

        Member member = new Member();
        member.setId(memberId);

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        assertThrows(PurchaseDoesNotExistException.class, () -> {
            pointRecordService.save(usePoint, content, memberId, purchaseId);
        });

        verify(pointRecordRepository, times(0)).save(any(PointRecord.class));
        verify(memberPointService, times(0)).updatePoint(anyLong(), anyLong());
    }

    @Test
    public void testReadByMemberId_MemberNotExists() {
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotExistsException.class, () -> {
            pointRecordService.readByMemberId(memberId, pageable);
        });

        verify(memberRepository, times(1)).findById(memberId);
        verify(pointRecordRepository, times(0)).findAllByMember(any(Member.class), any(Pageable.class));
    }

    @Test
    public void testRefundByPurchaseId_Success() {
        Long purchaseId = 1L;
        Purchase purchase = new Purchase();
        purchase.setId(purchaseId);

        Member member = new Member();
        member.setId(1L);

        PointRecord pointRecord = new PointRecord(100L, "buy point", member, purchase);
        List<PointRecord> pointRecords = Arrays.asList(pointRecord);

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(pointRecordRepository.findAllByPurchase(purchase)).thenReturn(pointRecords);

        Long refundedRecordId = pointRecordService.refundByPurchaseId(purchaseId);

        verify(pointRecordRepository, times(1)).save(any(PointRecord.class));
        verify(memberPointService, times(1)).updatePoint(member.getId(), -100L);
    }

    @Test
    public void testRefundByPurchaseId_PurchaseNotExists() {
        Long purchaseId = 1L;

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());

        assertThrows(PurchaseDoesNotExistException.class, () -> {
            pointRecordService.refundByPurchaseId(purchaseId);
        });

        verify(pointRecordRepository, times(0)).save(any(PointRecord.class));
        verify(memberPointService, times(0)).updatePoint(anyLong(), anyLong());
    }

    @Test
    public void testRefundByPurchaseId_NoBuyPointRecord() {
        Long purchaseId = 1L;
        Purchase purchase = new Purchase();
        purchase.setId(purchaseId);

        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        when(pointRecordRepository.findAllByPurchase(purchase)).thenReturn(Arrays.asList());

        assertThrows(NoBuyPointRecordException.class, () -> {
            pointRecordService.refundByPurchaseId(purchaseId);
        });

        verify(pointRecordRepository, times(0)).save(any(PointRecord.class));
        verify(memberPointService, times(0)).updatePoint(anyLong(), anyLong());
    }
}