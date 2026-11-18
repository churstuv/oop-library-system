// 인터페이스, 추상성: 대출 가능한 자료가 지켜야 할 규약을 정의한다. 대출 서비스는 구체 타입이 아니라 이 인터페이스를 통해 자료의 대출 가능 여부와 기간을 확인한다.

package com.library.model;

public interface Loanable {

    boolean canBeLoaned();      // 대출 가능한 자료인지 반환
    int getLoanPeriodDays();    // 대출 기간(일) 반환
}
