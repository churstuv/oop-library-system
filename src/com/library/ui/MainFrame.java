/*
 * MainFrame - 메인 창과 탭 구성
 * 자료(BookPanel), 회원(MemberPanel), 연체(OverduePanel), 관리(AdminPanel) 탭 4개로 창을 구성한다.
 * 연체 탭을 선택할 때마다 연체 목록을 새로고침한다.
 *
 * [설계 이유]
 *  - 탭이 선택되면 그 탭이 Refreshable인지 확인하고 refresh()를 호출한다(다형성).
 *    연체 탭뿐 아니라 모든 탭이 항상 최신 데이터를 보여주고, 탭을 추가해도 이 코드는 바뀌지 않는다.
 *  - LibraryClock에 리스너를 등록해 날짜가 바뀌면 상태 표시줄과 현재 탭을 갱신한다.
 *    LibraryClock은 MainFrame을 모르고 "날짜가 바뀌었다"는 알림만 보낸다(service → ui 의존 없음).
 *  - 서비스 객체는 직접 만들지 않고 생성자로 받는다. 객체 생성은 Main 한 곳에서만 한다.
 */
package com.library.ui;

import com.library.service.CatalogService;
import com.library.service.LibraryClock;
import com.library.service.LoanService;
import com.library.service.MemberService;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.time.LocalDate;

public class MainFrame extends JFrame {
    private final LibraryClock clock;
    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel statusLabel = new JLabel();

    public MainFrame(CatalogService catalogService, MemberService memberService,
                     LoanService loanService, LibraryClock clock) {
        super("도서관 대출·반납 관리 시스템");
        this.clock = clock;

        tabs.addTab("자료 검색·대출·반납", new BookPanel(catalogService, loanService));
        tabs.addTab("회원 대출 현황", new MemberPanel(loanService));
        tabs.addTab("연체 목록", new OverduePanel(loanService));
        tabs.addTab("관리", new AdminPanel(catalogService, memberService, clock));
        tabs.addChangeListener(e -> refreshSelectedTab());

        clock.addChangeListener(this::onDateChanged);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        add(tabs, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        updateStatus(clock.today());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
    }

    private void onDateChanged(LocalDate newDate) {
        updateStatus(newDate);
        refreshSelectedTab();
    }

    private void refreshSelectedTab() {
        Component selected = tabs.getSelectedComponent();
        if (selected instanceof Refreshable refreshable) {
            refreshable.refresh();
        }
    }

    private void updateStatus(LocalDate date) {
        if (clock.isAdjusted()) {
            statusLabel.setText("시스템 날짜: " + date + "  ·  시연 날짜 적용 중 (관리 탭에서 되돌릴 수 있음)");
            statusLabel.setForeground(new Color(0xB45309));
        } else {
            statusLabel.setText("시스템 날짜: " + date + "  ·  실제 오늘");
            statusLabel.setForeground(Color.DARK_GRAY);
        }
    }
}
