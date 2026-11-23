// 추상 클래스: 회원의 공통 정보와 대출 목록을 관리한다. 대출 목록은 외부에 읽기 전용으로만 노출한다(은닉성).

package com.library.model;

import java.util.List;
import java.util.Collections;
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
        return Collections.unmodifiableList(activeLoans);
    }   // AI: 절대 수정 불가 리스트로 넘겨주기. 은닉성.


    // ===== 판단 =====
    public boolean canBorrowMore() {
        return activeLoans.size() < getMaxLoanCount();      // AI: if-else문 단축화.
    }

    public boolean hasOverdue(LocalDate today) {        // AI: Loan의 전체 구조에서 isOverdue의 구조 재확인.
        return activeLoans.stream()
                .anyMatch(loan -> loan.isOverdue(today));    // AI: 스트림 문법 중 anyMatch 추천. (하나라도 맞으면 true 출력에 적합.) TODO: 이전엔 filter().count()로 구현했음.
    }   // today라는 추가 인자를 받기 때문에 메소드 참조말고 람다식 쓰기.


    // ===== 추상 메소드 =====
    public abstract int getMaxLoanCount(); // 유형별 최대 대출 권수 (다형성)
    public abstract String getMemberType(); // 유형 이름 반환 (출력용)


    // ===== 변경 =====
    public void addLoan(Loan loan) { // 대출 목록에 추가 (LoanService가 호출)
        activeLoans.add(loan);
    }

    public void removeLoan(Loan loan) { // 반납된 대출을 목록에서 제거
        activeLoans.remove(loan);   // AI: 입력값과 동일한 값이 있을 때 remove 한다.
    }

}
