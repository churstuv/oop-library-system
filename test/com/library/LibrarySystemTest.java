/*
 * LibrarySystemTest - 팀원 B 담당 기능(service·exception·util) 자동 테스트
 *
 * 외부 라이브러리(JUnit) 없이 실행할 수 있도록 main 메소드와 간단한 검사 도우미로 만들었다.
 * 날짜를 고정한 LibraryClock을 주입하므로, 실행하는 날짜와 상관없이 항상 같은 결과가 나온다.
 *
 * 실행: README의 "테스트 실행" 참고
 */
package com.library;

import com.library.exception.ItemNotAvailableException;
import com.library.exception.LibraryException;
import com.library.exception.LoanLimitExceededException;
import com.library.exception.OverdueMemberException;
import com.library.model.Book;
import com.library.model.GeneralMember;
import com.library.model.ItemStatus;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.model.Magazine;
import com.library.model.ReferenceBook;
import com.library.model.StudentMember;
import com.library.service.CatalogService;
import com.library.service.LibraryClock;
import com.library.service.LoanService;
import com.library.service.MemberService;
import com.library.service.ReturnReceipt;
import com.library.util.SampleDataFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class LibrarySystemTest {

    private static final LocalDate T = LocalDate.of(2026, 12, 1); // 테스트 기준 "오늘"

    private static int passed = 0;
    private static int failed = 0;

    // 테스트마다 새 객체를 만든다 (테스트끼리 영향 X)
    private static LibraryClock clock;
    private static CatalogService catalog;
    private static MemberService members;
    private static LoanService loans;

    public static void main(String[] args) {
        System.out.println("== 도서관 대출·반납 관리 시스템 · 팀원 B 기능 테스트 ==\n");

        section("LibraryClock (F8)");
        run("지정한 날짜로 시작하고 setToday로 변경된다", LibrarySystemTest::clockSetToday);
        run("null 날짜는 거부한다", LibrarySystemTest::clockRejectsNull);
        run("날짜 변경 시 리스너에게 알린다", LibrarySystemTest::clockNotifiesListener);
        run("resetToday는 실제 오늘로 되돌린다", LibrarySystemTest::clockReset);

        section("CatalogService (F1, F2)");
        run("자료 등록 후 ID로 조회 (대소문자 무시)", LibrarySystemTest::catalogFindById);
        run("중복 ID 등록은 거부한다", LibrarySystemTest::catalogRejectsDuplicate);
        run("빈 ID·제목 등록은 거부한다", LibrarySystemTest::catalogRejectsBlank);
        run("제목 검색: 대소문자 무시 + 부분 일치", LibrarySystemTest::catalogSearchTitle);
        run("저자 검색: 부분 일치", LibrarySystemTest::catalogSearchAuthor);
        run("빈 키워드는 전체 목록", LibrarySystemTest::catalogBlankKeyword);
        run("외부에서 목록을 수정할 수 없다 (은닉성)", LibrarySystemTest::catalogListIsReadOnly);

        section("MemberService (F3)");
        run("회원 등록 후 조회", LibrarySystemTest::memberRegisterAndFind);
        run("중복 회원 ID는 거부한다", LibrarySystemTest::memberRejectsDuplicate);

        section("LoanService 대출 (F4)");
        run("정상 대출: 상태 변경, 반납 예정일 = 오늘 + 14일", LibrarySystemTest::borrowSuccess);
        run("잡지 반납 예정일 = 오늘 + 7일 (다형성)", LibrarySystemTest::borrowMagazineDueDate);
        run("참고도서는 대출 불가 (NOT_LOANABLE)", LibrarySystemTest::borrowReferenceBook);
        run("이미 대출 중인 자료는 대출 불가 (ALREADY_ON_LOAN)", LibrarySystemTest::borrowAlreadyOnLoan);
        run("학생 회원 4번째 대출은 권수 초과", LibrarySystemTest::borrowStudentLimit);
        run("일반 회원 6번째 대출은 권수 초과", LibrarySystemTest::borrowGeneralLimit);
        run("연체 회원은 대출 차단", LibrarySystemTest::borrowOverdueBlocked);
        run("검사 순서: 연체가 권수보다 먼저", LibrarySystemTest::borrowCheckOrderOverdueFirst);
        run("검사 순서: 권수가 자료 검사보다 먼저", LibrarySystemTest::borrowCheckOrderLimitBeforeItem);
        run("실패한 대출은 아무 상태도 바꾸지 않는다", LibrarySystemTest::borrowFailureLeavesNoTrace);
        run("없는 회원·자료 ID는 IllegalArgumentException", LibrarySystemTest::borrowUnknownIds);
        run("업무 예외는 모두 LibraryException으로 받을 수 있다", LibrarySystemTest::exceptionHierarchy);

        section("LoanService 반납 (F5)");
        run("기한 내 반납: 연체료 0원, 상태 복구", LibrarySystemTest::returnOnTime);
        run("도서 5일 연체: 500원", LibrarySystemTest::returnBookLate);
        run("잡지 23일 연체: 1,150원 → 상한 1,000원", LibrarySystemTest::returnMagazineCap);
        run("반납 예정일 당일은 연체 아님", LibrarySystemTest::returnOnDueDate);
        run("대출 중이 아닌 자료 반납은 거부", LibrarySystemTest::returnNotOnLoan);
        run("연체 자료 반납 후 다시 대출 가능", LibrarySystemTest::returnUnblocksMember);

        section("이력·연체 조회 (F6, F7)");
        run("현재 대출과 전체 이력 구분", LibrarySystemTest::historyAndActive);
        run("연체 목록: 연체 일수 많은 순", LibrarySystemTest::overdueListSorted);

        section("SampleDataFactory + 발표 시연 시나리오");
        run("시연 데이터: 자료 6, 회원 2, 연체 M001(23일, 1,000원)", LibrarySystemTest::sampleDataState);
        run("시연 데이터 적재 후 날짜가 원래대로 돌아온다", LibrarySystemTest::sampleDataRestoresClock);
        run("발표 시나리오 1~9단계 전체", LibrarySystemTest::demoScenario);

        System.out.printf("%n== 결과: %d개 통과, %d개 실패 ==%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // LibraryClock
    // ------------------------------------------------------------------

    static void clockSetToday() {
        LibraryClock c = new LibraryClock(T);
        check(c.today().equals(T), "초기 날짜");
        c.setToday(T.plusDays(15));
        check(c.today().equals(T.plusDays(15)), "변경된 날짜");
    }

    static void clockRejectsNull() {
        LibraryClock c = new LibraryClock(T);
        expect(NullPointerException.class, () -> c.setToday(null));
        check(c.today().equals(T), "실패 후에도 날짜 유지");
    }

    static void clockNotifiesListener() {
        LibraryClock c = new LibraryClock(T);
        LocalDate[] received = new LocalDate[1];
        c.addChangeListener(date -> received[0] = date);
        c.setToday(T.plusDays(3));
        check(T.plusDays(3).equals(received[0]), "리스너가 새 날짜를 받음");
    }

    static void clockReset() {
        LibraryClock c = new LibraryClock(T);
        c.resetToday();
        check(c.today().equals(LocalDate.now()), "실제 오늘");
        check(!c.isAdjusted(), "조정되지 않은 상태");
    }

    // ------------------------------------------------------------------
    // CatalogService
    // ------------------------------------------------------------------

    static void catalogFindById() {
        setUp();
        check(catalog.findById("b001").isPresent(), "소문자 ID로 조회");
        check(catalog.findById(" B001 ").isPresent(), "공백 포함 ID로 조회");
        check(catalog.findById("X999").isEmpty(), "없는 ID는 empty");
    }

    static void catalogRejectsDuplicate() {
        setUp();
        expect(IllegalArgumentException.class, () -> catalog.addItem(new Book("b001", "다른 책", "누군가")));
    }

    static void catalogRejectsBlank() {
        setUp();
        expect(IllegalArgumentException.class, () -> catalog.addItem(new Book(" ", "제목", "저자")));
        expect(IllegalArgumentException.class, () -> catalog.addItem(new Book("B100", "", "저자")));
    }

    static void catalogSearchTitle() {
        setUp();
        catalog.addItem(new Book("B100", "Effective Java", "Bloch"));
        check(catalog.searchByTitle("java").size() == 1, "'java' → Effective Java");
        check(catalog.searchByTitle("자바").size() == 1, "'자바' → 자바 프로그래밍 입문");
        check(catalog.searchByTitle("없는제목").isEmpty(), "결과 없음");
    }

    static void catalogSearchAuthor() {
        setUp();
        check(catalog.searchByAuthor("편집").size() == 3, "편집부 자료 3개");
    }

    static void catalogBlankKeyword() {
        setUp();
        check(catalog.searchByTitle("  ").size() == 6, "전체 6개");
    }

    static void catalogListIsReadOnly() {
        setUp();
        List<LibraryItem> all = catalog.getAllItems();
        expect(UnsupportedOperationException.class, () -> all.add(new Book("B999", "x", "y")));
    }

    // ------------------------------------------------------------------
    // MemberService
    // ------------------------------------------------------------------

    static void memberRegisterAndFind() {
        setUp();
        members.registerMember(new StudentMember("S002", "이학생"));
        check(members.findMember("s002").isPresent(), "등록한 회원 조회");
        check(members.getAllMembers().size() == 3, "회원 수 3");
    }

    static void memberRejectsDuplicate() {
        setUp();
        expect(IllegalArgumentException.class, () -> members.registerMember(new GeneralMember("G001", "중복")));
    }

    // ------------------------------------------------------------------
    // 대출
    // ------------------------------------------------------------------

    static void borrowSuccess() throws Exception {
        setUp();
        Loan loan = loans.borrow("G001", "B003");
        check(loan.getDueDate().equals(T.plusDays(14)), "반납 예정일 T+14");
        check(catalog.findById("B003").orElseThrow().getStatus() == ItemStatus.ON_LOAN, "자료 상태 ON_LOAN");
        check(members.findMember("G001").orElseThrow().getActiveLoans().contains(loan), "회원 대출 목록에 추가");
    }

    static void borrowMagazineDueDate() throws Exception {
        setUp();
        Loan loan = loans.borrow("G001", "M002");
        check(loan.getDueDate().equals(T.plusDays(7)), "잡지 반납 예정일 T+7");
    }

    static void borrowReferenceBook() {
        setUp();
        ItemNotAvailableException e = expect(ItemNotAvailableException.class, () -> loans.borrow("G001", "R001"));
        check(e.getReason() == ItemNotAvailableException.Reason.NOT_LOANABLE, "원인 NOT_LOANABLE");
    }

    static void borrowAlreadyOnLoan() throws Exception {
        setUp();
        loans.borrow("G001", "B003");
        members.registerMember(new GeneralMember("G002", "임꺽정"));
        ItemNotAvailableException e = expect(ItemNotAvailableException.class, () -> loans.borrow("G002", "B003"));
        check(e.getReason() == ItemNotAvailableException.Reason.ALREADY_ON_LOAN, "원인 ALREADY_ON_LOAN");
    }

    static void borrowStudentLimit() throws Exception {
        setUp();
        members.registerMember(new StudentMember("S002", "이학생"));
        addBooks(4);
        loans.borrow("S002", "X1");
        loans.borrow("S002", "X2");
        loans.borrow("S002", "X3");
        LoanLimitExceededException e = expect(LoanLimitExceededException.class, () -> loans.borrow("S002", "X4"));
        check(e.getMaxLoanCount() == 3, "학생 최대 3권");
    }

    static void borrowGeneralLimit() throws Exception {
        setUp();
        members.registerMember(new GeneralMember("G002", "임꺽정"));
        addBooks(6);
        for (int i = 1; i <= 5; i++) {
            loans.borrow("G002", "X" + i);
        }
        LoanLimitExceededException e = expect(LoanLimitExceededException.class, () -> loans.borrow("G002", "X6"));
        check(e.getMaxLoanCount() == 5, "일반 최대 5권");
    }

    static void borrowOverdueBlocked() {
        setUp();
        OverdueMemberException e = expect(OverdueMemberException.class, () -> loans.borrow("S001", "B003"));
        check(e.getOverdueCount() == 1, "연체 1건");
    }

    static void borrowCheckOrderOverdueFirst() throws Exception {
        // 학생 회원이 20일 전에 3권(최대)을 빌려 모두 연체 → 연체·권수 두 조건이 동시에 걸린 상태
        // 이때 연체 예외가 먼저 나와야 한다
        setUp();
        members.registerMember(new StudentMember("S002", "이학생"));
        addBooks(4);
        clock.setToday(T.minusDays(20));
        loans.borrow("S002", "X1");
        loans.borrow("S002", "X2");
        loans.borrow("S002", "X3");
        clock.setToday(T);
        check(!members.findMember("S002").orElseThrow().canBorrowMore(), "권수도 초과 상태");
        expect(OverdueMemberException.class, () -> loans.borrow("S002", "X4"));
    }

    static void borrowCheckOrderLimitBeforeItem() throws Exception {
        // 최대 권수에 도달한 회원이 참고도서를 요청 → 권수 예외가 먼저
        setUp();
        members.registerMember(new StudentMember("S002", "이학생"));
        addBooks(3);
        loans.borrow("S002", "X1");
        loans.borrow("S002", "X2");
        loans.borrow("S002", "X3");
        expect(LoanLimitExceededException.class, () -> loans.borrow("S002", "R001"));
    }

    static void borrowFailureLeavesNoTrace() {
        setUp();
        int before = loans.getLoanHistory("G001").size();
        expect(ItemNotAvailableException.class, () -> loans.borrow("G001", "R001"));
        check(loans.getLoanHistory("G001").size() == before, "이력 변화 없음");
        check(catalog.findById("R001").orElseThrow().isAvailable(), "자료 상태 변화 없음");
    }

    static void borrowUnknownIds() {
        setUp();
        expect(IllegalArgumentException.class, () -> loans.borrow("NOPE", "B003"));
        expect(IllegalArgumentException.class, () -> loans.borrow("G001", "NOPE"));
        expect(IllegalArgumentException.class, () -> loans.borrow("", "B003"));
    }

    static void exceptionHierarchy() {
        setUp();
        // 다형성: 구체 타입을 몰라도 부모 타입으로 처리할 수 있다
        LibraryException e1 = expect(LibraryException.class, () -> loans.borrow("S001", "B003"));
        LibraryException e2 = expect(LibraryException.class, () -> loans.borrow("G001", "R001"));
        check(e1 instanceof OverdueMemberException, "연체 예외");
        check(e2 instanceof ItemNotAvailableException, "대출 불가 예외");
        check(!e1.getMessage().isBlank() && !e2.getMessage().isBlank(), "안내 메시지 존재");
    }

    // ------------------------------------------------------------------
    // 반납
    // ------------------------------------------------------------------

    static void returnOnTime() throws Exception {
        setUp();
        loans.borrow("G001", "B003");
        clock.setToday(T.plusDays(10));
        ReturnReceipt r = loans.returnItem("B003");
        check(r.lateFee() == 0 && !r.isOverdue(), "연체료 0");
        check(catalog.findById("B003").orElseThrow().isAvailable(), "상태 AVAILABLE");
        check(r.loan().getReturnDate().equals(T.plusDays(10)), "반납일 기록");
    }

    static void returnBookLate() throws Exception {
        setUp();
        loans.borrow("G001", "B003");
        clock.setToday(T.plusDays(19)); // 예정일 T+14, 5일 연체
        ReturnReceipt r = loans.returnItem("B003");
        check(r.overdueDays() == 5, "연체 5일");
        check(r.lateFee() == 500, "연체료 500원");
    }

    static void returnMagazineCap() {
        setUp();
        ReturnReceipt r = loans.returnItem("M001"); // 시연 데이터: 23일 연체
        check(r.overdueDays() == 23, "연체 23일");
        check(r.lateFee() == 1000, "상한 1,000원 (계산상 1,150원)");
    }

    static void returnOnDueDate() throws Exception {
        setUp();
        loans.borrow("G001", "B003");
        clock.setToday(T.plusDays(14));
        check(loans.getOverdueLoans().stream().noneMatch(l -> l.getItem().getId().equals("B003")), "당일은 연체 목록에 없음");
        check(loans.returnItem("B003").lateFee() == 0, "당일 반납 연체료 0");
    }

    static void returnNotOnLoan() {
        setUp();
        expect(IllegalArgumentException.class, () -> loans.returnItem("B003"));
    }

    static void returnUnblocksMember() throws Exception {
        setUp();
        expect(OverdueMemberException.class, () -> loans.borrow("S001", "B003"));
        loans.returnItem("M001");
        Loan loan = loans.borrow("S001", "B003");
        check(loan != null, "반납 후 대출 성공");
    }

    // ------------------------------------------------------------------
    // 이력·연체
    // ------------------------------------------------------------------

    static void historyAndActive() {
        setUp();
        List<Loan> active = loans.getActiveLoans("G001");
        List<Loan> history = loans.getLoanHistory("G001");
        check(active.size() == 1 && active.get(0).getItem().getId().equals("B001"), "현재 대출 B001");
        check(history.size() == 2, "이력 2건");
        check(history.get(0).getItem().getId().equals("B002") && history.get(0).isReturned(), "B002 반납 완료가 먼저(대출일 순)");
        expect(UnsupportedOperationException.class, () -> active.clear());
    }

    static void overdueListSorted() throws Exception {
        setUp();
        loans.borrow("G001", "B003");
        clock.setToday(T.plusDays(15));
        List<Loan> overdue = loans.getOverdueLoans();
        // M001 38일, B001 4일, B003 1일
        check(overdue.size() == 3, "연체 3건");
        check(overdue.get(0).getItem().getId().equals("M001"), "1위 M001");
        check(overdue.get(1).getItem().getId().equals("B001"), "2위 B001");
        check(overdue.get(2).getItem().getId().equals("B003"), "3위 B003");
    }

    // ------------------------------------------------------------------
    // 시연 데이터 / 시나리오
    // ------------------------------------------------------------------

    static void sampleDataState() {
        setUp();
        check(catalog.getAllItems().size() == 6, "자료 6개");
        check(members.getAllMembers().size() == 2, "회원 2명");
        List<Loan> overdue = loans.getOverdueLoans();
        check(overdue.size() == 1, "연체 1건");
        Loan m001 = overdue.get(0);
        check(m001.getItem().getId().equals("M001"), "연체 자료 M001");
        check(m001.getOverdueDays(T) == 23, "23일 연체");
        check(loans.getExpectedFee(m001) == 1000, "예상 연체료 1,000원");
        check(!catalog.findById("B001").orElseThrow().isAvailable(), "B001 대출 중");
        check(catalog.findById("B002").orElseThrow().isAvailable(), "B002 비치 중(반납 완료)");
    }

    static void sampleDataRestoresClock() {
        setUp();
        check(clock.today().equals(T), "적재 후 날짜 = T");
    }

    static void demoScenario() throws Exception {
        setUp();
        // 1. 자료 검색
        check(catalog.searchByTitle("자바").get(0).getId().equals("B001"), "1. '자바' 검색 → B001");
        // 2. 정상 대출
        Loan b003 = loans.borrow("G001", "B003");
        check(b003.getDueDate().equals(T.plusDays(14)), "2. B003 반납 예정일 T+14");
        // 3. 대출 불가 자료
        expect(ItemNotAvailableException.class, () -> loans.borrow("G001", "R001"));
        // 4. 연체 차단 (자료는 아무거나 — 회원 자격 검사가 먼저)
        expect(OverdueMemberException.class, () -> loans.borrow("S001", "M002"));
        // 5. 연체 목록
        List<Loan> overdue = loans.getOverdueLoans();
        check(overdue.size() == 1 && loans.getExpectedFee(overdue.get(0)) == 1000, "5. M001 예상 연체료 1,000원");
        // 6. 반납과 연체료 → 이후 대출 가능
        check(loans.returnItem("M001").lateFee() == 1000, "6. M001 반납 연체료 1,000원");
        check(loans.borrow("S001", "M002") != null, "6. S001 대출 가능");
        // 7. 이력 조회
        check(loans.getActiveLoans("G001").size() == 2, "7. G001 현재 대출 2건(B001, B003)");
        check(loans.getLoanHistory("G001").size() == 3, "7. G001 이력 3건");
        // 8. 날짜 15일 뒤 → B001 4일, B003 1일 연체
        clock.setToday(T.plusDays(15));
        List<Loan> after = loans.getOverdueLoans();
        check(after.stream().anyMatch(l -> l.getItem().getId().equals("B001") && l.getOverdueDays(clock.today()) == 4), "8. B001 4일 연체");
        check(after.stream().anyMatch(l -> l.getItem().getId().equals("B003") && l.getOverdueDays(clock.today()) == 1), "8. B003 1일 연체");
        // 8-주의. 14일 뒤면 B003은 아직 연체 아님
        clock.setToday(T.plusDays(14));
        check(loans.getOverdueLoans().stream().noneMatch(l -> l.getItem().getId().equals("B003")), "8. 14일 뒤엔 B003 연체 아님");
        // 9. 날짜 복구
        clock.resetToday();
        check(!clock.isAdjusted(), "9. 오늘로 되돌리기");
    }

    // ------------------------------------------------------------------
    // 테스트 도우미
    // ------------------------------------------------------------------

    /** 고정 날짜 T 기준으로 시연 데이터를 적재한 새 시스템을 만든다. */
    private static void setUp() {
        clock = new LibraryClock(T);
        catalog = new CatalogService();
        members = new MemberService();
        loans = new LoanService(catalog, members, clock);
        SampleDataFactory.load(catalog, members, loans, clock);
    }

    private static void addBooks(int count) {
        for (int i = 1; i <= count; i++) {
            catalog.addItem(new Book("X" + i, "테스트 도서 " + i, "테스터"));
        }
    }

    @FunctionalInterface
    interface TestBody {
        void run() throws Exception;
    }

    private static void section(String name) {
        System.out.println("[" + name + "]");
    }

    private static void run(String name, TestBody body) {
        try {
            body.run();
            passed++;
            System.out.println("  PASS  " + name);
        } catch (Throwable t) {
            failed++;
            System.out.println("  FAIL  " + name + "  →  " + t.getMessage());
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("검사 실패: " + description);
        }
    }

    private static <E extends Throwable> E expect(Class<E> type, TestBody body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) {
                return type.cast(t);
            }
            throw new AssertionError("예상 " + type.getSimpleName() + ", 실제 " + t.getClass().getSimpleName()
                    + ": " + Objects.toString(t.getMessage()));
        }
        throw new AssertionError(type.getSimpleName() + " 예외가 발생하지 않음");
    }
}
