// ItemTableModel - 자료 목록 표 데이터 (BookPanel의 JTable에 사용)

package com.library.ui;

import com.library.model.Book;
import com.library.model.LibraryItem;
import com.library.model.Magazine;
import com.library.model.ReferenceBook;

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
            case 1: return typeName(item);
            case 2: return item.getTitle();
            case 3: return item.getAuthor();
            case 4: return item.isAvailable() ? "비치 중" : "대출 중";
            default: return null;
        }
    }

    private String typeName(LibraryItem item) {
        if (item instanceof Book) return "도서";
        if (item instanceof Magazine) return "잡지";
        if (item instanceof ReferenceBook) return "참고도서";
        return "기타";
    }
}
