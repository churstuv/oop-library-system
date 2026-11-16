/*
 * ItemTableModel - 자료 목록 표 데이터
 * AbstractTableModel을 상속해 BookPanel의 JTable에 자료 목록(ID, 종류, 제목, 저자, 상태)을 제공한다.
 * 주요 메소드: setItems(), getItemAt(), getRowCount(), getColumnCount(), getValueAt()
 *
 * [설계 이유]
 *  - 상속·다형성: JTable은 TableModel 인터페이스 타입으로만 데이터를 요청한다(getValueAt 등).
 *    이 클래스가 그 메소드들을 재정의(@Override)하므로 JTable은 자료 표인지 대출 표인지 몰라도 된다.
 *  - 표가 보여줄 데이터와 화면(JTable)을 분리했다. 패널은 setItems()로 목록만 바꾸면 된다.
 */
package com.library.ui;

import com.library.model.LibraryItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"ID", "종류", "제목", "저자", "대출 기간", "상태"};

    private List<LibraryItem> items = new ArrayList<>();

    /** 표에 보여줄 목록을 바꾸고 다시 그린다. */
    public void setItems(List<LibraryItem> items) {
        this.items = new ArrayList<>(items);
        fireTableDataChanged();
    }

    /** 선택된 행의 자료 */
    public LibraryItem getItemAt(int row) {
        return items.get(row);
    }

    @Override
    public int getRowCount() {
        return items.size();
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
        LibraryItem item = items.get(row);
        return switch (column) {
            case 0 -> item.getId();
            case 1 -> item.getTypeName();
            case 2 -> item.getTitle();
            case 3 -> item.getAuthor();
            case 4 -> item.canBeLoaned() ? item.getLoanPeriodDays() + "일" : "열람 전용";
            case 5 -> item.getStatus().getLabel();
            default -> "";
        };
    }
}
