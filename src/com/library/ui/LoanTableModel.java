/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * LoanTableModel - 대출 목록/이력 표 데이터
 */
package com.library.ui;

import com.library.model.Loan;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LoanTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"자료 ID", "제목", "종류", "대출일", "반납 예정일", "반납일", "연체료"};

    private List<Loan> loans = new ArrayList<>();

    public void setLoans(List<Loan> loans) {
        this.loans = new ArrayList<>(loans);
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return loans.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int column) { return COLUMNS[column]; }

    @Override
    public Object getValueAt(int row, int column) {
        Loan loan = loans.get(row);
        return switch (column) {
            case 0 -> loan.getItem().getId();
            case 1 -> loan.getItem().getTitle();
            case 2 -> loan.getItem().getTypeName();
            case 3 -> loan.getLoanDate();
            case 4 -> loan.getDueDate();
            case 5 -> loan.isReturned() ? loan.getReturnDate().toString() : "대출 중";
            case 6 -> loan.isReturned() ? String.format("%,d원", loan.calculateFee(loan.getReturnDate())) : "-";
            default -> "";
        };
    }
}
