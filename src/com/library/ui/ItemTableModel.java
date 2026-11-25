// ItemTableModel - 자료 목록 표 데이터 (BookPanel의 JTable에 사용)

package com.library.ui;

import com.library.model.LibraryItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = {"ID", "종류", "제목", "저자", "상태"};
    private List<LibraryItem> items = new ArrayList<>();

    public void setItems(List<LibraryItem> newItems) {
        this.items = new ArrayList<>(newItems);
        fireTableDataChanged();
    }

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
        switch (column) {
            case 0: return item.getId();
            case 1: return item.getTypeName();     // instanceof 분기 대신 다형성
            case 2: return item.getTitle();
            case 3: return item.getAuthor();
            case 4: return item.isAvailable() ? "비치 중" : "대출 중";
            default: return null;
        }
    }
}
