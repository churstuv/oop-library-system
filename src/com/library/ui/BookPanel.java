// BookPanel - 자료 검색, 대출, 반납 화면
// 화면은 서비스를 호출만 하고, 결과와 예외는 Dialogs로 안내한다.

package com.library.ui;

import com.library.model.Loan;
import com.library.model.LibraryItem;
import com.library.service.CatalogService;
import com.library.service.LoanService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class BookPanel extends JPanel {

    private final CatalogService catalog;
    private final LoanService loanService;
    private final ItemTableModel tableModel = new ItemTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField keywordField = new JTextField(15);
    private final JTextField memberField = new JTextField(8);

    public BookPanel(CatalogService catalog, LoanService loanService) {
        this.catalog = catalog;
        this.loanService = loanService;

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("검색어(제목·저자):"));
        top.add(keywordField);
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> onSearch());
        keywordField.addActionListener(e -> onSearch());
        top.add(searchButton);
        add(top, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("회원 ID:"));
        bottom.add(memberField);
        JButton borrowButton = new JButton("대출");
        borrowButton.addActionListener(e -> onBorrow());
        JButton returnButton = new JButton("반납");
        returnButton.addActionListener(e -> onReturn());
        bottom.add(borrowButton);
        bottom.add(returnButton);
        add(bottom, BorderLayout.SOUTH);

        onSearch();     // 처음에는 전체 자료 표시
    }

    public void onSearch() {
        String keyword = keywordField.getText().trim();
        Set<LibraryItem> result = new LinkedHashSet<>(catalog.searchByTitle(keyword));
        result.addAll(catalog.searchByAuthor(keyword));
        tableModel.setItems(new ArrayList<>(result));
    }

    public void onBorrow() {
        LibraryItem item = selectedItem();
        if (item == null) return;
        String memberId = memberField.getText().trim();
        if (memberId.isEmpty()) {
            Dialogs.error(this, "회원 ID를 입력하세요.");
            return;
        }
        try {
            Loan loan = loanService.borrow(memberId, item.getId());
            Dialogs.info(this, item.getTitle() + " 대출 완료. 반납 예정일: " + loan.getDueDate());
            onSearch();
        } catch (RuntimeException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    public void onReturn() {
        LibraryItem item = selectedItem();
        if (item == null) return;
        try {
            int fee = loanService.returnItem(item.getId());
            Dialogs.info(this, item.getTitle() + " 반납 완료. 연체료: " + fee + "원");
            onSearch();
        } catch (RuntimeException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    private LibraryItem selectedItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Dialogs.error(this, "목록에서 자료를 선택하세요.");
            return null;
        }
        return tableModel.getItemAt(table.convertRowIndexToModel(row));
    }
}
