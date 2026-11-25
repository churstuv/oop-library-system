// OverduePanel - 연체 목록과 예상 연체료 화면 (F7)
// 탭을 선택할 때마다 MainFrame이 refresh()를 호출해 현재 시스템 날짜 기준으로 다시 계산한다.

package com.library.ui;

import com.library.model.Loan;
import com.library.service.LibraryClock;
import com.library.service.LoanService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

public class OverduePanel extends JPanel implements Refreshable {

    private final LoanService loanService;
    private final LibraryClock clock;

    private final OverdueTableModel tableModel = new OverdueTableModel();
    private final JLabel summaryLabel = new JLabel();

    public OverduePanel(LoanService loanService, LibraryClock clock) {
        this.loanService = loanService;
        this.clock = clock;

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("새로고침");
        refreshButton.addActionListener(e -> refresh());
        top.add(refreshButton);
        top.add(summaryLabel);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        refresh();
    }

    @Override
    public void refresh() {
        LocalDate today = clock.today();
        List<Loan> overdue = loanService.getOverdueLoans();
        tableModel.setLoans(overdue, today);
        summaryLabel.setText("기준일: " + today + " · 연체 " + overdue.size()
                + "건 · 예상 연체료 합계 " + tableModel.getTotalFee() + "원");
    }
}
