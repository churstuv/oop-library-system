/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * Magazine - 잡지: 7일, 50원/일, 연체료 상한 1,000원
 */
package com.library.model;

public class Magazine extends LibraryItem {
    private static final int MAX_LATE_FEE = 1000;

    public Magazine(String id, String title, String author) {
        super(id, title, author);
    }

    @Override public boolean canBeLoaned() { return true; }
    @Override public int getLoanPeriodDays() { return 7; }
    @Override public int getLateFeePerDay() { return 50; }
    @Override public String getTypeName() { return "잡지"; }

    @Override
    public int calculateLateFee(int overdueDays) {
        return Math.min(super.calculateLateFee(overdueDays), MAX_LATE_FEE);
    }
}
