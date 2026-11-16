/*
 * LoanLimitExceededException - 대출 권수 초과 예외
 * 회원의 현재 대출 권수가 최대 대출 권수(일반 5권, 학생 3권)에 도달한 상태에서
 * 대출을 시도하면 LoanService.borrow()에서 발생한다. (대출 검사 2순위)
 *
 * 최대 권수는 Member.getMaxLoanCount()(다형성)에서 받아오므로
 * 회원 유형이 늘어나도 이 예외 클래스는 수정할 필요가 없다.
 */
package com.library.exception;

public class LoanLimitExceededException extends LibraryException {
    private final String memberId;
    private final int maxLoanCount;

    public LoanLimitExceededException(String memberId, String memberName, String memberType, int maxLoanCount) {
        super(String.format("%s(%s) 회원은 %s 회원 최대 대출 권수(%d권)에 도달했습니다.",
                memberName, memberId, memberType, maxLoanCount));
        this.memberId = memberId;
        this.maxLoanCount = maxLoanCount;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getMaxLoanCount() {
        return maxLoanCount;
    }
}
