/*
 * OverdueMemberException - 연체 회원 대출 차단 예외
 * 연체 중인 대출이 있는 회원이 새로 대출을 시도하면
 * LoanService.borrow()에서 발생한다. (대출 검사 1순위)
 *
 * 예외 객체가 "누가, 몇 건 연체 중인지"를 필드로 들고 있어서,
 * 메시지 문자열을 파싱하지 않고도 호출한 쪽이 상황을 알 수 있다.
 */
package com.library.exception;

public class OverdueMemberException extends LibraryException {
    private final String memberId;
    private final int overdueCount;

    public OverdueMemberException(String memberId, String memberName, int overdueCount) {
        super(String.format("%s(%s) 회원은 연체 중인 자료가 %d건 있어 대출할 수 없습니다.%n먼저 연체 자료를 반납해 주세요.",
                memberName, memberId, overdueCount));
        this.memberId = memberId;
        this.overdueCount = overdueCount;
    }

    public String getMemberId() {
        return memberId;
    }

    public int getOverdueCount() {
        return overdueCount;
    }
}
