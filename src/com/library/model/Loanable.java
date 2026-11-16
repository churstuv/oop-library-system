/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * Loanable - 대출 가능한 자료가 지켜야 할 규약(인터페이스)
 */
package com.library.model;

public interface Loanable {
    /** 대출할 수 있는 자료인지 (참고도서는 false) */
    boolean canBeLoaned();

    /** 대출 기간(일) */
    int getLoanPeriodDays();
}
