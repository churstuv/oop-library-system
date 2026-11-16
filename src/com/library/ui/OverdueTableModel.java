/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * OverdueTableModel - 연체 목록 표 데이터
 */
package com.library.ui;

import com.library.model.Loan;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OverdueTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"회원", "자료 ID", "제목", "반납 예정일", "연체 일수", "예상 연체료"};

    private List<Loan> loans = new ArrayList<>();
    private LocalDate today = LocalDate.now();

    public void setLoans(List<Loan> loans, LocalDate today) {
        this.loans = new ArrayList<>(loans);
        this.today = today;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return loans.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int column) { return COLUMNS[column]; }

    @Override
    public Object getValueAt(int row, int column) {
        Loan loan = loans.get(row);
        return switch (column) {
            case 0 -> loan.getMember().toString();
            case 1 -> loan.getItem().getId();
            case 2 -> loan.getItem().getTitle();
            case 3 -> loan.getDueDate();
            case 4 -> loan.getOverdueDays(today) + "일";
            case 5 -> String.format("%,d원", loan.calculateFee(today));
            default -> "";
        };
    }
}
