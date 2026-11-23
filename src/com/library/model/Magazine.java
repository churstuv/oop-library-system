package com.library.model;

public class Magazine extends LibraryItem {

    // ===== 필드 =====
    private static final int MAX_LATE_FEE = 1000;       // 모든 Magazine 객체가 공유하는 값.
    // 은닉성

    // ===== 생성자 =====
    public Magazine(String id, String title, String author) {
        super(id, title, author);   // 그대로 부모 클래스에 대입.
    }


    // ===== 추상 클래스 구현 & 조회 =====
    @Override
    public boolean canBeLoaned() {      // 대출 가능한 자료인지 반환
        return true;        // Magazine: 대출 가능함.
    }

    @Override
    public int getLoanPeriodDays() {    // 대출 기간(일) 반환
        return 7;          // Magazine: 대출 기간 7일.
    }

    @Override
    public int getLateFeePerDay() {     // 하위 클래스가 일일 연체료 결정
        return 50;         // Magazine: 일일 연체료 50원.
    }


    // ===== 오버라이딩: 일반 =====
    @Override
    public int calculateLateFee(int overdueDays) {      // 실제 연체료 계산.
        if (overdueDays * getLateFeePerDay() >= MAX_LATE_FEE) {     // AI: Math.min을 사용해 if-else를 단축할 수 있지만 쉬운 코드를 위해 하지 않았다.
            return MAX_LATE_FEE;
        }
        return overdueDays * getLateFeePerDay();
    }

}

