/*
 * Main - 프로그램 시작점
 * 서비스 객체를 생성하고, SampleDataFactory로 시연 데이터를 적재한 뒤 MainFrame을 실행한다.
 *
 * [설계 이유]
 *  - 객체 생성과 연결(조립)을 이 한 곳에서만 한다. 각 클래스는 필요한 객체를 생성자로 받기만 한다.
 *    LibraryClock은 하나만 만들어 모든 서비스가 공유하므로, 날짜를 바꾸면 전체가 같은 날짜를 본다.
 *  - Swing 화면은 이벤트 디스패치 스레드(EDT)에서 만들어야 안전하므로 invokeLater를 사용한다.
 */
package com.library;

import com.library.service.CatalogService;
import com.library.service.LibraryClock;
import com.library.service.LoanService;
import com.library.service.MemberService;
import com.library.ui.MainFrame;
import com.library.util.SampleDataFactory;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        LibraryClock clock = new LibraryClock();
        CatalogService catalogService = new CatalogService();
        MemberService memberService = new MemberService();
        LoanService loanService = new LoanService(catalogService, memberService, clock);

        SampleDataFactory.load(catalogService, memberService, loanService, clock);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // 기본 모양으로 실행해도 기능에는 문제가 없다.
            }
            new MainFrame(catalogService, memberService, loanService, clock).setVisible(true);
        });
    }
}
