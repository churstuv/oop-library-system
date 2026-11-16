/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * LibraryItem - 모든 자료의 공통 부모(추상 클래스)
 * 상태(status)는 private이며 markOnLoan()/markAvailable()로만 바뀐다.
 */
package com.library.model;

public abstract class LibraryItem implements Loanable {
    private final String id;
    private final String title;
    private final String author;
    private ItemStatus status = ItemStatus.AVAILABLE;

    protected LibraryItem(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public ItemStatus getStatus() { return status; }

    public boolean isAvailable() {
        return status == ItemStatus.AVAILABLE;
    }

    public void markOnLoan() {
        if (status == ItemStatus.ON_LOAN) {
            throw new IllegalStateException("이미 대출 중인 자료입니다: " + id);
        }
        status = ItemStatus.ON_LOAN;
    }

    public void markAvailable() {
        status = ItemStatus.AVAILABLE;
    }

    /** 하루 연체료 */
    public abstract int getLateFeePerDay();

    /** 화면 표시용 종류 이름 */
    public abstract String getTypeName();

    /** 기본 연체료 계산. 하위 클래스가 재정의할 수 있다(Magazine 상한). */
    public int calculateLateFee(int overdueDays) {
        return Math.max(0, overdueDays) * getLateFeePerDay();
    }

    @Override
    public String toString() {
        return "[" + getTypeName() + "] " + title + " (" + id + ")";
    }
}
