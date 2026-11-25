package com.library.model;

public class ReferenceBook extends LibraryItem {

    // ===== 필드 =====
    // 필요 없다.


    // ===== 생성자 =====
    public ReferenceBook(String id, String title, String author) {
        super(id, title, author);   // 그대로 부모 클래스에 대입.
    }


    // ===== 추상 클래스 구현 & 조회 =====
    @Override
    public boolean canBeLoaned() {      // 대출 가능한 자료인지 반환
        return false;        // ReferenceBook: 대출 불가능함.
    }

    @Override
    public int getLoanPeriodDays() {    // 대출 기간(일) 반환
        return 0;          // ReferenceBook: 대출 기간 0일.
    }

    @Override
    public int getLateFeePerDay() {     // 하위 클래스가 일일 연체료 결정
        return 0;         // ReferenceBook: 일일 연체료 0원.
    }


    @Override
    public String getTypeName() {       // 자료 종류 이름 반환 (출력용)
        return "참고도서";
    }


    // ===== 오버라이딩: 일반 =====
    // Reference Book은 부모 클래스의 것을 그대로 쓴다.

}
