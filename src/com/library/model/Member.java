// 추상 클래스: 회원의 공통 정보와 대출 목록을 관리한다. 대출 목록은 외부에 읽기 전용으로만 노출한다(은닉성).

package com.library.model;
import java.util.List;
import java.time.LocalDate;

public abstract class Member {

    // ===== 필드 =====
    private final String memberId;
    private final String name;
    private final List<Loan> activeLoans;


    // ===== 생성자 =====
    public Member(String memberId, String name, List<Loan> activeLoans) {
        this.memberId = memberId;
        this.name = name;
        this.activeLoans = activeLoans;
    }


    // ===== 조회 =====
    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public List<Loan> getActiveLoans() { // 수정 불가 리스트 (Collections.unmodifiableList) 반환
        return this.activeLoans;
    }


    // ===== 판단 =====
    public boolean canBorrowMore() {
        if (activeLoans.size() < getMaxLoanCount())
            return true;
    }

    public boolean hasOverdue(LocalDate today) {
    }


    // ===== 추상 메소드 =====
    public abstract int getMaxLoanCount(); // 유형별 최대 대출 권수 (다형성)
    public abstract String getMemberType(); // 유형 이름 반환 (출력용)


    // ===== 변경 =====
    public void addLoan(Loan loan) { // 대출 목록에 추가 (LoanService가 호출)

    }

    public void removeLoan(Loan loan) { // 반납된 대출을 목록에서 제거

    }

}
