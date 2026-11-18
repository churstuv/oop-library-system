// 한 번의 대출 기록을 나타낸다. 대출 기간과 연체 계산의 책임을 가진다.

package com.library.model;

public class Loan {

    // ===== 필드 =====
    // ===== 생성자 =====
    // ===== 조회 =====
    // ===== 상태 변경 =====
    // ===== 추상 메소드 =====
    private final LibraryItem item;
    private final Member member;
    private final LocalDate loanDate;
    private final LocalDate dueDate;

}
