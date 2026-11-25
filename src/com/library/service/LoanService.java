// LoanService - 대출·반납 흐름 조율
// 자료 상태, 회원 상태, 대출 기록을 직접 조작하지 않고 각 객체의 메소드를 호출해 협력한다.

package com.library.service;

import com.library.exception.ItemNotAvailableException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.OverdueMemberException;
import com.library.model.Loan;
import com.library.model.LibraryItem;
import com.library.model.Loanable;
import com.library.model.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class LoanService {

    private final CatalogService catalog;
    private final MemberService members;
    private final LibraryClock clock;
    private final List<Loan> loans = new ArrayList<>();     // 반납 완료 기록 포함

    public LoanService(CatalogService catalog, MemberService members, LibraryClock clock) {
        this.catalog = catalog;
        this.members = members;
        this.clock = clock;
    }

    // 대출 검사 순서: 연체 여부 -> 대출 권수 -> 대출 가능 자료 -> 자료 상태
    public Loan borrow(String memberId, String itemId) {
        Member member = members.findMember(memberId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다: " + memberId));
        LibraryItem item = catalog.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("자료를 찾을 수 없습니다: " + itemId));
        LocalDate today = clock.today();

        if (member.hasOverdue(today)) {
            throw new OverdueMemberException(member.getName() + " 회원은 연체 중인 자료가 있어 대출할 수 없습니다.");
        }
        if (!member.canBorrowMore()) {
            throw new LoanLimitExceededException(member.getName() + " 회원은 최대 "
                    + member.getMaxLoanCount() + "권까지 대출할 수 있습니다.");
        }
        Loanable loanable = item;   // 구체 타입이 아닌 인터페이스로 대출 가능 여부 확인
        if (!loanable.canBeLoaned()) {
            throw new ItemNotAvailableException(item.getTitle() + "은(는) 대출할 수 없는 자료입니다.");
        }
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(item.getTitle() + "은(는) 이미 대출 중입니다.");
        }

        item.markOnLoan();
        Loan loan = new Loan(item, member, today);
        member.addLoan(loan);
        loans.add(loan);
        return loan;
    }

    // 연체료를 먼저 계산한 뒤 반납 처리한다. 반환값은 부과된 연체료(원).
    public int returnItem(String itemId) {
        Loan loan = findActiveLoanByItem(itemId);
        LocalDate today = clock.today();

        int fee = loan.calculateFee(today);
        loan.markReturned(today);
        loan.getItem().markAvailable();
        loan.getMember().removeLoan(loan);
        return fee;
    }

    public List<Loan> getActiveLoans(String memberId) {
        return loans.stream()
                .filter(l -> l.getMember().getMemberId().equals(memberId) && !l.isReturned())
                .collect(Collectors.toList());
    }

    public List<Loan> getLoanHistory(String memberId) {
        return loans.stream()
                .filter(l -> l.getMember().getMemberId().equals(memberId))
                .sorted(Comparator.comparing(Loan::getLoanDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Loan> getOverdueLoans() {
        LocalDate today = clock.today();
        return loans.stream()
                .filter(l -> l.isOverdue(today))
                .collect(Collectors.toList());
    }

    private Loan findActiveLoanByItem(String itemId) {
        return loans.stream()
                .filter(l -> !l.isReturned() && l.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("대출 중인 자료가 아닙니다: " + itemId));
    }
}
