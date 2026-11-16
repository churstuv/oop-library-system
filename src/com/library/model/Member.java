/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * Member - 회원 공통 부모(추상 클래스)
 * 현재 대출 목록은 읽기 전용으로만 외부에 노출한다.
 */
package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Member {
    private final String id;
    private final String name;
    private final List<Loan> activeLoans = new ArrayList<>();

    protected Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public List<Loan> getActiveLoans() {
        return Collections.unmodifiableList(activeLoans);
    }

    public abstract int getMaxLoanCount();

    public abstract String getMemberType();

    public boolean canBorrowMore() {
        return activeLoans.size() < getMaxLoanCount();
    }

    public boolean hasOverdue(LocalDate today) {
        for (Loan loan : activeLoans) {
            if (loan.isOverdue(today)) {
                return true;
            }
        }
        return false;
    }

    public void addLoan(Loan loan) {
        activeLoans.add(loan);
    }

    public void removeLoan(Loan loan) {
        activeLoans.remove(loan);
    }

    @Override
    public String toString() {
        return name + " (" + id + ", " + getMemberType() + ")";
    }
}
