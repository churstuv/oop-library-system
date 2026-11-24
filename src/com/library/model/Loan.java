// 한 번의 대출 기록을 나타낸다. 대출 기간과 연체 계산의 책임을 가진다.

package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        this.dueDate = loanDate.plusDays(item.getLoanPeriodDays());     // AI: 날짜 더하는 함수 = plusDays
    }


    // ===== 조회 =====
    public LibraryItem getItem() { return this.item; }
    public Member getMember() { return this.member; }
    public LocalDate getLoanDate() { return this.loanDate; }
    public LocalDate getDueDate() { return this.dueDate; }
    public LocalDate getReturnDate() { return this.returnDate; }


    // ===== 판단 =====
    public boolean isReturned() {
        return returnDate != null;  // 반납일 업데이트(not null) 되면 반납된거임.
    }

    public boolean isOverdue(LocalDate today) {
        return (isReturned()) && (today.isAfter(dueDate));  // today.isAfter(dueDate) : today가 dueDate보다 이후의 날짜인가?
    }


    // ===== 계산 =====
    public long getOverdueDays(LocalDate date) {
        if (date.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, date);  // AI: ChronoUnit.DAYS.between() : 일수 관점에서 두 날짜의 차이 일수를 구하기.
        }
        return 0;   // 아니면 0 반환.
    }

    public int calculateFee(LocalDate returnDate) {
        return item.calculateLateFee((int) getOverdueDays(returnDate)); // calculateLateFee의 매개변수는 int형이다. 형 변환 캐스팅하기.
    }


    // ===== 변경 =====
    public void markReturned(LocalDate date) {
        if (isReturned()) {
            throw new IllegalStateException("이미 반납된 대출입니다.");   // 예외 클래스 만들고 던지기: IllegalStateException
        }
        this.returnDate = date;
    }

}
