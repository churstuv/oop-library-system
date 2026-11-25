// 추상 클래스

package com.library.model;

public abstract class LibraryItem implements Loanable {

    // ===== 필드 =====
    private final String id;
    private final String title;
    private final String author;
    private ItemStatus status;


    // ===== 생성자 =====
    public LibraryItem(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = ItemStatus.AVAILABLE;     // 항상 처음은 대출 가능. 처음부터 대출 불가는 canBeLoaned로 확인.
    }


    // ===== 조회 =====
    public String getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public ItemStatus getStatus() { return this.status; }
    public boolean isAvailable() { return this.status == ItemStatus.AVAILABLE; }


    // ===== 상태 변경 =====
    public void markOnLoan() { // 비치 중일 때만 ON_LOAN으로 변경, 아니면 IllegalStateException
        if (getStatus() == ItemStatus.AVAILABLE) {
            this.status = ItemStatus.ON_LOAN;
        }
        else {
            throw new IllegalStateException("이미 대출 중입니다.");
        }
    }

    public void markAvailable() { // 상태를 AVAILABLE로 복구
        this.status = ItemStatus.AVAILABLE;
    }


    // ===== 추상 메소드 =====
    public abstract boolean canBeLoaned();      // 하위 클래스가 대출 가능 여부 결정 (Loanable 구현)
    public abstract int getLoanPeriodDays();    // 하위 클래스가 대출 기간 결정 (Loanable 구현)
    public abstract int getLateFeePerDay();     // 하위 클래스가 일일 연체료 결정
    public abstract String getTypeName();       // 자료 종류 이름 반환 (출력용, 다형성)


    // ===== 일반 =====
    public int calculateLateFee(int overdueDays) {
        return overdueDays * getLateFeePerDay();    // 연체 일수가 0 이하면 0 반환. 하위 클래스에서 재정의 가능.
    }
}
