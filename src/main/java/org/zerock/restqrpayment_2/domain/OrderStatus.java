package org.zerock.restqrpayment_2.domain;

public enum OrderStatus {
    PENDING,    // 주문 대기
    ACCEPTED,   // 주문 접수
    COMPLETED,  // 완료
    CANCELLED   // 취소
}
