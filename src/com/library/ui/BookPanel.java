/*
 * BookPanel - 자료 검색, 대출, 반납 화면
 * 제목·저자 키워드 검색(F2), 대출(F4), 반납(F5)을 처리한다.
 * 화면은 LoanService·CatalogService를 호출만 하고, 결과와 예외는 Dialogs로 안내한다.
 * 주요 메소드: onSearch(), onBorrow(), onReturn()
 *
 * [설계 이유]
 *  - 계층 분리: 이 패널에는 업무 규칙이 없다. "연체면 막는다" 같은 판단은 모두 LoanService가 하고,
 *    패널은 입력을 넘기고 → 결과나 예외를 받아 → 보여주기만 한다.
 *    그래서 화면을 콘솔로 바꿔도 service·model은 그대로 쓸 수 있다.
 *  - 대출·반납 뒤에는 마지막 검색 조건으로 표를 다시 그려 상태(비치 중/대출 중)가 바로 반영되게 했다.
 */
package com.library.ui;

import com.library.exception.LibraryException;
import com.library.model.LibraryItem;
import com.library.model.Loan;
import com.library.service.CatalogService;
import com.library.service.LoanService;
import com.library.service.ReturnReceipt;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class BookPanel extends JPanel implements Refreshable {
    private static final String BY_TITLE = "제목";
    private static final String BY_AUTHOR = "저자";

    private final CatalogService catalogService;
    private final LoanService loanService;

    private final JComboBox<String> searchTypeBox = new JComboBox<>(new String[]{BY_TITLE, BY_AUTHOR});
    private final JTextField keywordField = new JTextField(18);
    private final JTextField memberIdField = new JTextField(8);
    private final ItemTableModel tableModel = new ItemTableModel();
    private final JTable table = new JTable(tableModel);

    public BookPanel(CatalogService catalogService, LoanService loanService) {
        super(new BorderLayout(8, 8));
        this.catalogService = catalogService;
        this.loanService = loanService;
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createSearchBar(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
        add(createLoanBar(), BorderLayout.SOUTH);
        refresh();
    }

    private JPanel createSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("검색");
        JButton showAllButton = new JButton("전체 보기");
        searchButton.addActionListener(e -> onSearch());
        keywordField.addActionListener(e -> onSearch()); // Enter 키로도 검색
        showAllButton.addActionListener(e -> {
            keywordField.setText("");
            onSearch();
        });
        bar.add(new JLabel("검색 기준"));
        bar.add(searchTypeBox);
        bar.add(keywordField);
        bar.add(searchButton);
        bar.add(showAllButton);
        return bar;
    }

    private JScrollPane createTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(24);
        return new JScrollPane(table);
    }

    private JPanel createLoanBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton borrowButton = new JButton("대출");
        JButton returnButton = new JButton("반납");
        borrowButton.addActionListener(e -> onBorrow());
        returnButton.addActionListener(e -> onReturn());
        bar.add(new JLabel("회원 ID"));
        bar.add(memberIdField);
        bar.add(borrowButton);
        bar.add(returnButton);
        bar.add(new JLabel("  ← 표에서 자료를 선택한 뒤 누르세요. (반납은 회원 ID 없이 가능)"));
        return bar;
    }

    /** 검색 (F2): 키워드가 비어 있으면 전체 목록 */
    void onSearch() {
        String keyword = keywordField.getText();
        List<LibraryItem> result = BY_AUTHOR.equals(searchTypeBox.getSelectedItem())
                ? catalogService.searchByAuthor(keyword)
                : catalogService.searchByTitle(keyword);
        tableModel.setItems(result);
    }

    /** 대출 (F4) */
    void onBorrow() {
        LibraryItem item = getSelectedItem();
        if (item == null) {
            return;
        }
        try {
            Loan loan = loanService.borrow(memberIdField.getText(), item.getId());
            Dialogs.info(this, String.format("대출 완료%n%n회원: %s%n자료: %s%n반납 예정일: %s (%d일)",
                    loan.getMember(), item, loan.getDueDate(), item.getLoanPeriodDays()));
        } catch (LibraryException | IllegalArgumentException e) {
            Dialogs.error(this, e);
        }
        onSearch();
    }

    /** 반납 (F5) */
    void onReturn() {
        LibraryItem item = getSelectedItem();
        if (item == null) {
            return;
        }
        try {
            ReturnReceipt receipt = loanService.returnItem(item.getId());
            String feeText = receipt.isOverdue()
                    ? String.format("%d일 연체 · 연체료 %,d원", receipt.overdueDays(), receipt.lateFee())
                    : "연체 없음";
            Dialogs.info(this, String.format("반납 완료%n%n회원: %s%n자료: %s%n%s",
                    receipt.loan().getMember(), item, feeText));
        } catch (IllegalArgumentException e) {
            Dialogs.error(this, e);
        }
        onSearch();
    }

    private LibraryItem getSelectedItem() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            Dialogs.warn(this, "표에서 자료를 먼저 선택해 주세요.");
            return null;
        }
        // 정렬된 상태에서도 올바른 행을 가져오도록 화면 행 번호를 모델 행 번호로 변환한다.
        return tableModel.getItemAt(table.convertRowIndexToModel(viewRow));
    }

    @Override
    public void refresh() {
        onSearch();
    }
}
