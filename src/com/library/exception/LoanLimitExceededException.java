/*
 * LoanLimitExceededException - 대출 권수 초과 예외
 * 회원의 현재 대출 권수가 최대 대출 권수(일반 5권, 학생 3권)에 도달한 상태에서
 * 대출을 시도하면 LoanService.borrow()에서 발생한다.
 */
package com.library.exception;

public class LoanLimitExceededException {
}
