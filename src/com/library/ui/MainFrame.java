// MainFrame - 메인 창과 탭 구성
// 탭 패널은 Main에서 addTab()으로 추가한다.

package com.library.ui;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

    private final JTabbedPane tabs = new JTabbedPane();

    public MainFrame() {
        super("도서관 도서 대출·반납 관리 시스템");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(tabs);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    public void addTab(String title, JComponent panel) {
        tabs.addTab(title, panel);
    }

    // 탭 선택 변경 리스너 등록용 (예: 연체 탭 선택 시 새로고침)
    public JTabbedPane getTabs() {
        return tabs;
    }
}
