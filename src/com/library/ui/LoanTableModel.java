// LoanTableModel - 대출 기록 표 데이터 (MemberPanel의 현재 대출 표, 전체 이력 표에 공통 사용)

package com.library.ui;

import com.library.model.Loan;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"자료 ID", "종류", "제목", "대출일", "반납 예정일", "반납일", "상태"};
    private List<Loan> loans = new ArrayList<>();
    private LocalDate today = LocalDate.now();      // 상태(연체 여부) 판단 기준일

    public void setLoans(List<Loan> newLoans, LocalDate today) {
        this.loans = new ArrayList<>(newLoans);
        this.today = today;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return loans.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Loan loan = loans.get(row);
        switch (column) {
            case 0: return loan.getItem().getId();
            case 1: return loan.getItem().getTypeName();
            case 2: return loan.getItem().getTitle();
            case 3: return loan.getLoanDate();
            case 4: return loan.getDueDate();
            case 5: return loan.isReturned() ? loan.getReturnDate() : "-";
            case 6: return statusOf(loan);
            default: return null;
        }
    }

    private String statusOf(Loan loan) {
        if (loan.isReturned()) return "반납 완료";
        if (loan.isOverdue(today)) return "연체 중 (" + loan.getOverdueDays(today) + "일)";
        return "대출 중";
    }
}
