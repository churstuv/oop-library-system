/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * OverduePanel - 연체 목록(F7)과 예상 연체료
 */
package com.library.ui;

import com.library.model.Loan;
import com.library.service.LoanService;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.util.List;

public class OverduePanel extends JPanel implements Refreshable {
    private final LoanService loanService;
    private final OverdueTableModel tableModel = new OverdueTableModel();
    private final JLabel summaryLabel = new JLabel();

    public OverduePanel(LoanService loanService) {
        super(new BorderLayout(8, 8));
        this.loanService = loanService;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(summaryLabel, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        refresh();
    }

    @Override
    public void refresh() {
        List<Loan> overdue = loanService.getOverdueLoans();
        tableModel.setLoans(overdue, loanService.today());
        summaryLabel.setText(loanService.today() + " 기준 연체 " + overdue.size() + "건");
    }
}
