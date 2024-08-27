package com.nhnacademy.bookstore.entity.purchase.enums;

public enum PurchaseStatus {
    /**
     * 주문 확인중, 주문 완료, 출고, 배달 진행중,
     * 배달 완료, 환불 요청, 환불 완료, 주문 확정
     */
    PROCESSING, COMPLETED, DELIVERY_START, DELIVERY_PROGRESS,
    DELIVERY_COMPLETED, REFUNDED_REQUEST, CONFIRMATION, REFUNDED_COMPLETED ;

    public static PurchaseStatus fromString(String status) {
        if (status == null) {
            throw new IllegalArgumentException("비어있습니다.");
        }
        // Enum values are returned in uppercase, so we convert the input to uppercase
        try {
            return PurchaseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("없는 값입니다.: " + status, e);
        }
    }
}