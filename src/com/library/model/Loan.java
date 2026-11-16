/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * Loan - 대출 기록 한 건 (대출일, 반납 예정일, 반납일)
 */
package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private final LibraryItem item;
    private final Member member;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate; // null이면 미반납

    public Loan(LibraryItem item, Member member, LocalDate loanDate) {
        this.item = item;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(item.getLoanPeriodDays());
    }

    public LibraryItem getItem() { return item; }
    public Member getMember() { return member; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        return !isReturned() && today.isAfter(dueDate);
    }

    public int getOverdueDays(LocalDate date) {
        long days = ChronoUnit.DAYS.between(dueDate, date);
        return (int) Math.max(0, days);
    }

    /** 다형성: 자료 종류마다 다른 calculateLateFee()가 호출된다. */
    public int calculateFee(LocalDate date) {
        return item.calculateLateFee(getOverdueDays(date));
    }

    public void markReturned(LocalDate date) {
        if (isReturned()) {
            throw new IllegalStateException("이미 반납된 대출입니다.");
        }
        this.returnDate = date;
    }
}
