/*
 * LoanService - 대출·반납 흐름 조율
 * 대출(F4), 반납(F5), 회원 대출 현황·이력 조회(F6), 연체 목록 조회(F7)를 담당한다.
 * 대출 검사 순서: 연체 여부 -> 대출 권수 -> 대출 가능 자료 -> 자료 상태
 * 반납 시에는 반납 처리 전에 연체료를 먼저 계산한다.
 * 주요 메소드: borrow(memberId, itemId), returnItem(itemId),
 *            getActiveLoans(), getLoanHistory(), getOverdueLoans()
 */
package com.library.service;

public class LoanService {
}
