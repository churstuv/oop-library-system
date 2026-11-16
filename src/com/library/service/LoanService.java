/*
 * LoanService - 대출·반납 흐름 조율
 * 대출(F4), 반납(F5), 회원 대출 현황·이력 조회(F6), 연체 목록 조회(F7)를 담당한다.
 * 대출 검사 순서: 연체 여부 -> 대출 권수 -> 대출 가능 자료 -> 자료 상태
 * 반납 시에는 반납 처리 전에 연체료를 먼저 계산한다.
 * 주요 메소드: borrow(memberId, itemId), returnItem(itemId),
 *            getActiveLoans(), getLoanHistory(), getOverdueLoans()
 *
 * [설계 이유]
 *  - 객체 간 협력의 "조율자": 직접 규칙을 계산하지 않고, 규칙을 가진 객체에게 물어본다.
 *      연체 여부   → Member.hasOverdue()
 *      대출 권수   → Member.canBorrowMore()   (내부에서 getMaxLoanCount() 다형성)
 *      대출 가능   → Loanable.canBeLoaned()   (참고도서만 false)
 *      연체료      → Loan.calculateFee()      (내부에서 item.calculateLateFee() 다형성)
 *    그래서 이 클래스에는 instanceof나 "도서면 14일" 같은 분기가 하나도 없다.
 *    자료·회원 종류가 늘어나도 LoanService는 수정하지 않는다(개방-폐쇄 원칙).
 *  - 의존성 주입: 필요한 서비스와 LibraryClock을 생성자로 받는다. 직접 new 하지 않으므로
 *    테스트에서 날짜를 고정한 LibraryClock을 넣어 연체 상황을 쉽게 재현할 수 있다.
 *  - 검사 순서를 이렇게 정한 이유: 회원 자격(연체·권수)을 먼저 확인해야
 *    "어떤 자료를 고르든 지금은 대출할 수 없다"는 근본 원인을 먼저 안내할 수 있다.
 */
package com.library.service;

import com.library.exception.ItemNotAvailableException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.OverdueMemberException;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LoanService {
    private final CatalogService catalogService;
    private final MemberService memberService;
    private final LibraryClock clock;

    // 반납된 기록까지 포함한 전체 대출 기록 (이력 조회용)
    private final List<Loan> loans = new ArrayList<>();

    public LoanService(CatalogService catalogService, MemberService memberService, LibraryClock clock) {
        this.catalogService = Objects.requireNonNull(catalogService);
        this.memberService = Objects.requireNonNull(memberService);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * 대출 (F4)
     * @return 생성된 대출 기록 (반납 예정일 안내에 사용)
     * @throws OverdueMemberException     연체 중인 회원
     * @throws LoanLimitExceededException 최대 대출 권수 도달
     * @throws ItemNotAvailableException  참고도서이거나 이미 대출 중인 자료
     * @throws IllegalArgumentException   존재하지 않는 회원·자료 ID
     */
    public Loan borrow(String memberId, String itemId)
            throws OverdueMemberException, LoanLimitExceededException, ItemNotAvailableException {
        Member member = getMemberOrThrow(memberId);
        LibraryItem item = getItemOrThrow(itemId);
        LocalDate today = clock.today();

        // 1) 연체 여부
        if (member.hasOverdue(today)) {
            throw new OverdueMemberException(member.getId(), member.getName(), countOverdue(member, today));
        }
        // 2) 대출 권수
        if (!member.canBorrowMore()) {
            throw new LoanLimitExceededException(member.getId(), member.getName(),
                    member.getMemberType(), member.getMaxLoanCount());
        }
        // 3) 대출 가능한 자료인지 (참고도서 차단)
        if (!item.canBeLoaned()) {
            throw new ItemNotAvailableException(item.getId(), item.getTitle(),
                    ItemNotAvailableException.Reason.NOT_LOANABLE);
        }
        // 4) 자료 상태 (이미 대출 중인지)
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(item.getId(), item.getTitle(),
                    ItemNotAvailableException.Reason.ALREADY_ON_LOAN);
        }

        // 모든 검사를 통과한 뒤에만 상태를 바꾼다. (중간에 실패해도 데이터가 어긋나지 않음)
        Loan loan = new Loan(item, member, today); // dueDate = today + 대출 기간
        item.markOnLoan();
        member.addLoan(loan);
        loans.add(loan);
        return loan;
    }

    /**
     * 반납 (F5)
     * 연체료는 반납 처리 "전에" 계산한다. (반납 후에는 미반납 대출이 아니게 되므로)
     * @return 반납 결과 (연체 일수, 연체료 포함)
     * @throws IllegalArgumentException 존재하지 않는 자료이거나 대출 중이 아닌 자료
     */
    public ReturnReceipt returnItem(String itemId) {
        LibraryItem item = getItemOrThrow(itemId);
        Loan loan = findActiveLoanByItem(item.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "「" + item.getTitle() + "」(" + item.getId() + ")은(는) 대출 중인 자료가 아닙니다."));
        LocalDate today = clock.today();

        int overdueDays = loan.getOverdueDays(today);
        int fee = loan.calculateFee(today); // 다형성: 잡지는 상한 1,000원 적용

        loan.markReturned(today);
        item.markAvailable();
        loan.getMember().removeLoan(loan);
        return new ReturnReceipt(loan, overdueDays, fee);
    }

    /** 회원의 현재 대출 목록 (미반납만, F6) */
    public List<Loan> getActiveLoans(String memberId) {
        return getMemberOrThrow(memberId).getActiveLoans();
    }

    /** 회원의 전체 대출 이력 (반납 완료 포함, 대출일 순, F6) */
    public List<Loan> getLoanHistory(String memberId) {
        Member member = getMemberOrThrow(memberId);
        List<Loan> history = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.getMember() == member) {
                history.add(loan);
            }
        }
        history.sort(Comparator.comparing(Loan::getLoanDate));
        return history;
    }

    /** 현재 연체 중인 대출 목록 (연체 일수가 많은 순, F7) */
    public List<Loan> getOverdueLoans() {
        LocalDate today = clock.today();
        List<Loan> overdue = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isOverdue(today)) {
                overdue.add(loan);
            }
        }
        overdue.sort(Comparator.comparingInt((Loan loan) -> loan.getOverdueDays(today)).reversed());
        return overdue;
    }

    /** 연체 대출의 오늘 기준 예상 연체료 (연체 목록 화면용) */
    public int getExpectedFee(Loan loan) {
        return loan.calculateFee(clock.today());
    }

    /** 시스템 기준 오늘 날짜 (화면 표시용) */
    public LocalDate today() {
        return clock.today();
    }

    // ---- 내부 도우미 ----

    private Optional<Loan> findActiveLoanByItem(String itemId) {
        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getItem().getId().equalsIgnoreCase(itemId)) {
                return Optional.of(loan);
            }
        }
        return Optional.empty();
    }

    private int countOverdue(Member member, LocalDate today) {
        int count = 0;
        for (Loan loan : member.getActiveLoans()) {
            if (loan.isOverdue(today)) {
                count++;
            }
        }
        return count;
    }

    private Member getMemberOrThrow(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("회원 ID를 입력해 주세요.");
        }
        return memberService.findMember(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID입니다: " + memberId.trim()));
    }

    private LibraryItem getItemOrThrow(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("자료 ID를 입력해 주세요.");
        }
        return catalogService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자료 ID입니다: " + itemId.trim()));
    }
}
