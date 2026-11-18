// 한 번의 대출 기록을 나타낸다. 대출 기간과 연체 계산의 책임을 가진다.

package com.library.model;

import java.time.LocalDate;

public class Loan {

    // ===== 필드 =====
    private final LibraryItem item;     // 대출한 자료
    private final Member member;        // 대출한 회원
    private final LocalDate loanDate;   // 대출일
    private final LocalDate dueDate;    // 반납 예정일
    private LocalDate returnDate;       // 반납일 (반납 전에는 null)


    // ===== 생성자 =====
    public Loan(LibraryItem item, Member member, LocalDate loanDate) {
        this.item = item;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(item.getLoanPeriodDays());     // 날짜 더하는 함수: plusDays
    }


    // ===== 조회 =====
    public LibraryItem getItem() { return this.item; }
    public Member getMember() { return this.member; }
    public LocalDate getLoanDate() { return this.loanDate; }
    public LocalDate getDueDate() { return this.dueDate; }
    public LocalDate getReturnDate() { return this.returnDate; }


    // ===== 판단 =====
    public boolean isReturned()


    // ===== 추상 메소드 =====


}
