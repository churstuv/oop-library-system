/*
 * ReturnReceipt - 반납 처리 결과 (영수증)
 * LoanService.returnItem()이 반납한 대출 기록, 연체 일수, 연체료를 한 번에 돌려주기 위해 사용한다.
 *
 * [설계 이유]
 *  - record: 값만 담는 불변 객체를 짧게 만든다. 생성 후 값이 바뀌지 않아 안전하다(은닉성).
 *  - 연체료를 반납 "전에" 계산해 이 객체에 담아 두므로, 화면은 계산 방법을 몰라도 결과만 보여주면 된다.
 */
package com.library.service;

import com.library.model.Loan;

public record ReturnReceipt(Loan loan, int overdueDays, int lateFee) {

    public boolean isOverdue() {
        return overdueDays > 0;
    }
}
