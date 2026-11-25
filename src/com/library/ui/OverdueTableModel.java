// OverdueTableModel - 연체 목록 표 데이터 (OverduePanel의 JTable에 사용)
// 예상 연체료는 Loan.calculateFee()를 호출한다. 자료 종류별 계산(잡지 상한 등)은 다형성으로 처리된다.

package com.library.ui;

import com.library.model.Loan;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OverdueTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"회원 ID", "이름", "자료 ID", "종류", "제목", "반납 예정일", "연체 일수", "예상 연체료(원)"};
    private List<Loan> loans = new ArrayList<>();
    private LocalDate today = LocalDate.now();      // 연체 일수·연체료 계산 기준일

    public void setLoans(List<Loan> newLoans, LocalDate today) {
        this.loans = new ArrayList<>(newLoans);
        this.today = today;
        fireTableDataChanged();
    }

    public int getTotalFee() {      // 표에 있는 연체료 합계
        int total = 0;
        for (Loan loan : loans) {
            total += loan.calculateFee(today);
        }
        return total;
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
            case 0: return loan.getMember().getMemberId();
            case 1: return loan.getMember().getName();
            case 2: return loan.getItem().getId();
            case 3: return loan.getItem().getTypeName();
            case 4: return loan.getItem().getTitle();
            case 5: return loan.getDueDate();
            case 6: return loan.getOverdueDays(today);
            case 7: return loan.calculateFee(today);
            default: return null;
        }
    }
}
