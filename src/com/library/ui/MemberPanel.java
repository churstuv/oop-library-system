// MemberPanel - 회원 대출 현황과 전체 이력 조회 화면 (F6)
// 화면은 서비스를 호출만 하고, 결과와 예외는 Dialogs로 안내한다.

package com.library.ui;

import com.library.model.Loan;
import com.library.model.Member;
import com.library.service.LibraryClock;
import com.library.service.LoanService;
import com.library.service.MemberService;

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
import java.time.LocalDate;
import java.util.List;

public class MemberPanel extends JPanel implements Refreshable {

    private final MemberService members;
    private final LoanService loanService;
    private final LibraryClock clock;

    private final JTextField memberField = new JTextField(8);
    private final JLabel infoLabel = new JLabel("회원 ID를 입력하고 조회하세요.");
    private final LoanTableModel activeModel = new LoanTableModel();     // 현재 대출 (미반납)
    private final LoanTableModel historyModel = new LoanTableModel();    // 전체 이력 (반납 포함)

    private String currentMemberId;     // 마지막으로 조회한 회원 (탭 재선택 시 새로고침용)

    public MemberPanel(MemberService members, LoanService loanService, LibraryClock clock) {
        this.members = members;
        this.loanService = loanService;
        this.clock = clock;

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("회원 ID:"));
        top.add(memberField);
        JButton searchButton = new JButton("조회");
        searchButton.addActionListener(e -> onSearch());
        memberField.addActionListener(e -> onSearch());
        top.add(searchButton);
        top.add(infoLabel);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 8));
        JScrollPane activePane = new JScrollPane(new JTable(activeModel));
        activePane.setBorder(BorderFactory.createTitledBorder("현재 대출"));
        JScrollPane historyPane = new JScrollPane(new JTable(historyModel));
        historyPane.setBorder(BorderFactory.createTitledBorder("전체 대출 이력 (최근 대출 순)"));
        center.add(activePane);
        center.add(historyPane);
        add(center, BorderLayout.CENTER);
    }

    public void onSearch() {
        String memberId = memberField.getText().trim();
        if (memberId.isEmpty()) {
            Dialogs.error(this, "회원 ID를 입력하세요.");
            return;
        }
        if (members.findMember(memberId).isEmpty()) {
            Dialogs.error(this, "회원을 찾을 수 없습니다: " + memberId);
            return;
        }
        currentMemberId = memberId;
        refresh();
    }

    @Override
    public void refresh() {
        if (currentMemberId == null) return;    // 아직 조회한 회원이 없음
        Member member = members.findMember(currentMemberId).orElse(null);
        if (member == null) return;

        LocalDate today = clock.today();
        List<Loan> active = loanService.getActiveLoans(currentMemberId);
        activeModel.setLoans(active, today);
        historyModel.setLoans(loanService.getLoanHistory(currentMemberId), today);

        infoLabel.setText(member.getName() + " (" + member.getMemberType() + " 회원) · 대출 "
                + active.size() + " / " + member.getMaxLoanCount() + "권"
                + (member.hasOverdue(today) ? " · 연체 중 (대출 불가)" : ""));
    }
}
