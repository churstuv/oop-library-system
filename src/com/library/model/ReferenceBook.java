/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * ReferenceBook - 참고도서: 열람 전용(대출 불가)
 */
package com.library.model;

public class ReferenceBook extends LibraryItem {
    public ReferenceBook(String id, String title, String author) {
        super(id, title, author);
    }

    @Override public boolean canBeLoaned() { return false; }
    @Override public int getLoanPeriodDays() { return 0; }
    @Override public int getLateFeePerDay() { return 0; }
    @Override public String getTypeName() { return "참고도서"; }
}
