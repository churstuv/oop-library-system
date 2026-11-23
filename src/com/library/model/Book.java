package com.library.model;

public class Book extends LibraryItem {

    // ===== 필드 =====
    // 필요 없다.


    // ===== 생성자 =====
    public Book(String id, String title, String author) {
        super(id, title, author);   // 그대로 부모 클래스에 대입.
    }


    // ===== 추상 클래스 구현 & 조회 =====
    @Override
    public boolean canBeLoaned() {      // 대출 가능한 자료인지 반환
        return true;        // Book: 대출 가능함.
    }

    @Override
    public int getLoanPeriodDays() {    // 대출 기간(일) 반환
        return 14;          // Book: 대출 기간 14일.
    }

    @Override
    public int getLateFeePerDay() {     // 하위 클래스가 일일 연체료 결정
        return 100;         // Book: 일일 연체료 100원.
    }


    // ===== 오버라이딩: 일반 =====
    // Book은 부모 클래스의 것을 그대로 쓴다.
}
