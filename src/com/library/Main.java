// Main - 프로그램 시작점
// 서비스 객체 생성 → 시연 데이터 적재 → 메인 창 실행

package com.library;

import com.library.service.CatalogService;
import com.library.service.LibraryClock;
import com.library.service.LoanService;
import com.library.service.MemberService;
import com.library.ui.AdminPanel;
import com.library.ui.BookPanel;
import com.library.ui.MainFrame;
import com.library.ui.MemberPanel;
import com.library.ui.OverduePanel;
import com.library.util.SampleDataFactory;

import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        LibraryClock clock = new LibraryClock(LocalDate.now());
        CatalogService catalog = new CatalogService();
        MemberService members = new MemberService();
        LoanService loanService = new LoanService(catalog, members, clock);

        SampleDataFactory.load(catalog, members, loanService, clock);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.addTab("자료·대출", new BookPanel(catalog, loanService));
            frame.addTab("회원", new MemberPanel(members, loanService, clock));
            frame.addTab("연체", new OverduePanel(loanService, clock));
            frame.addTab("관리", new AdminPanel(catalog, members, clock));
            frame.setVisible(true);
        });
    }
}
