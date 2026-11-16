/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * Book - 도서: 14일, 100원/일
 */
package com.library.model;

public class Book extends LibraryItem {
    public Book(String id, String title, String author) {
        super(id, title, author);
    }

    @Override public boolean canBeLoaned() { return true; }
    @Override public int getLoanPeriodDays() { return 14; }
    @Override public int getLateFeePerDay() { return 100; }
    @Override public String getTypeName() { return "도서"; }
}
