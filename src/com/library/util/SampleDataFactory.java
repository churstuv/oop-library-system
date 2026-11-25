// SampleDataFactory - 시연용 초기 데이터 생성
// 대출·반납 기록을 실제 LoanService 흐름으로 쌓고, 날짜는 오늘 기준 상대값으로 만든다.

package com.library.util;

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

public class SampleDataFactory {

    private SampleDataFactory() {
    }

    public static void load(CatalogService catalog, MemberService members,
                            LoanService loanService, LibraryClock clock) {
        LocalDate today = clock.today();
        try {
            catalog.addItem(new Book("B001", "자바 프로그래밍 입문", "김도서"));
            catalog.addItem(new Book("B002", "객체지향 설계", "이설계"));
            catalog.addItem(new Book("B003", "알고리즘 기초", "박알고"));
            catalog.addItem(new Magazine("M001", "IT 트렌드 월간지", "편집부"));
            catalog.addItem(new Magazine("M002", "과학 소식", "편집부"));
            catalog.addItem(new ReferenceBook("R001", "표준 국어사전", "편집부"));

            members.registerMember(new GeneralMember("G001", "홍길동"));
            members.registerMember(new StudentMember("S001", "김학생"));

            // G001: B002를 30일 전 대출, 10일 전 반납 (예정일 16일 전 → 6일 연체 후 반납, 이력용)
            clock.setToday(today.minusDays(30));
            loanService.borrow("G001", "B002");
            clock.setToday(today.minusDays(10));
            loanService.returnItem("B002");

            // G001: B001을 3일 전 대출 (현재 대출 중)
            clock.setToday(today.minusDays(3));
            loanService.borrow("G001", "B001");

            // S001: M001을 30일 전 대출 → 예정일 23일 전, 현재 23일 연체 중
            clock.setToday(today.minusDays(30));
            loanService.borrow("S001", "M001");
        } finally {
            clock.setToday(today);  // 원래 날짜로 복구
        }
    }
}
