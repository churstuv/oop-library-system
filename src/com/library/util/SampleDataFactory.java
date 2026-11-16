/*
 * SampleDataFactory - 시연용 초기 데이터 생성
 * 시연용 자료(B001~B003, M001~M002, R001)와 회원(G001, S001)을 등록하고,
 * 대출·반납 기록을 실제 서비스 흐름(LoanService)을 거쳐 만든다.
 * 날짜는 오늘 기준 상대값으로 만들어 발표 날짜와 무관하게 연체·이력이 보이도록 한다.
 * 주요 메소드: load(CatalogService, MemberService, LoanService, LibraryClock)
 *
 * [설계 이유]
 *  - Loan 객체를 직접 만들어 끼워 넣지 않고, LibraryClock을 과거로 돌린 뒤 borrow()/returnItem()을 호출한다.
 *    실제 업무 규칙을 그대로 거치므로 자료 상태·회원 대출 목록·이력이 항상 서로 맞는다.
 *  - 마지막에 resetToday()로 날짜를 반드시 되돌린다(finally). 실패해도 시스템 날짜가 과거에 머물지 않는다.
 *  - 객체를 만들 필요가 없는 유틸리티라 생성자를 private으로 막았다.
 *
 * 결과 (오늘 = T)
 *  - B002: G001이 T-30 대출(예정 T-16) → T-10 반납, 6일 연체 후 반납 (이력용)
 *  - M001: S001이 T-30 대출(예정 T-23) → 오늘 기준 23일 연체, 50원×23=1,150 → 상한 1,000원
 *  - B001: G001이 T-3 대출 (현재 대출 중, 예정 T+11)
 */
package com.library.util;

import com.library.exception.LibraryException;
import com.library.model.Book;
import com.library.model.GeneralMember;
import com.library.model.Magazine;
import com.library.model.ReferenceBook;
import com.library.model.StudentMember;
import com.library.service.CatalogService;
import com.library.service.LibraryClock;
import com.library.service.LoanService;
import com.library.service.MemberService;

import java.time.LocalDate;

public final class SampleDataFactory {

    private SampleDataFactory() {
    }

    public static void load(CatalogService catalog, MemberService members,
                            LoanService loans, LibraryClock clock) {
        addItems(catalog);
        addMembers(members);
        addLoanRecords(loans, clock);
    }

    private static void addItems(CatalogService catalog) {
        catalog.addItem(new Book("B001", "자바 프로그래밍 입문", "김도서"));
        catalog.addItem(new Book("B002", "객체지향 설계", "이설계"));
        catalog.addItem(new Book("B003", "알고리즘 기초", "박알고"));
        catalog.addItem(new Magazine("M001", "IT 트렌드 월간지", "편집부"));
        catalog.addItem(new Magazine("M002", "과학 소식", "편집부"));
        catalog.addItem(new ReferenceBook("R001", "표준 국어사전", "편집부"));
    }

    private static void addMembers(MemberService members) {
        members.registerMember(new GeneralMember("G001", "홍길동"));
        members.registerMember(new StudentMember("S001", "김학생"));
    }

    private static void addLoanRecords(LoanService loans, LibraryClock clock) {
        LocalDate today = clock.today();
        try {
            clock.setToday(today.minusDays(30));
            loans.borrow("G001", "B002");
            loans.borrow("S001", "M001");

            clock.setToday(today.minusDays(10));
            loans.returnItem("B002");

            clock.setToday(today.minusDays(3));
            loans.borrow("G001", "B001");
        } catch (LibraryException e) {
            // 고정된 시연 데이터이므로 여기서 실패하면 코드 오류다. 조용히 넘기지 않고 바로 알린다.
            throw new IllegalStateException("시연 데이터 생성 실패: " + e.getMessage(), e);
        } finally {
            clock.setToday(today);
        }
    }
}
