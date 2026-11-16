/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * MemberPanel - 회원 대출 현황(F6): 현재 대출 표 + 전체 이력 표
 */
package com.library.ui;

import com.library.service.LoanService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class MemberPanel extends JPanel implements Refreshable {
    private final LoanService loanService;
    private final JTextField memberIdField = new JTextField(8);
    private final LoanTableModel activeModel = new LoanTableModel();
    private final LoanTableModel historyModel = new LoanTableModel();
    private String lastMemberId;

    public MemberPanel(LoanService loanService) {
        super(new BorderLayout(8, 8));
        this.loanService = loanService;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("조회");
        searchButton.addActionListener(e -> onSearch());
        memberIdField.addActionListener(e -> onSearch());
        top.add(new JLabel("회원 ID"));
        top.add(memberIdField);
        top.add(searchButton);

        JPanel tables = new JPanel(new GridLayout(2, 1, 0, 8));
        tables.add(wrap("현재 대출", new JTable(activeModel)));
        tables.add(wrap("전체 이력 (반납 완료 포함)", new JTable(historyModel)));

        add(top, BorderLayout.NORTH);
        add(tables, BorderLayout.CENTER);
    }

    private JPanel wrap(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table));
        return panel;
    }

    void onSearch() {
        try {
            String id = memberIdField.getText();
            activeModel.setLoans(loanService.getActiveLoans(id));
            historyModel.setLoans(loanService.getLoanHistory(id));
            lastMemberId = id;
        } catch (IllegalArgumentException e) {
            Dialogs.error(this, e);
        }
    }

    @Override
    public void refresh() {
        if (lastMemberId != null) {
            activeModel.setLoans(loanService.getActiveLoans(lastMemberId));
            historyModel.setLoans(loanService.getLoanHistory(lastMemberId));
        }
    }
}
