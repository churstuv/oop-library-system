// CatalogService - 자료 등록과 검색 (F1, F2)

package com.library.service;

import com.library.model.LibraryItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CatalogService {

    private final Map<String, LibraryItem> items = new LinkedHashMap<>();   // 키: 자료 id

    public void addItem(LibraryItem item) {
        if (item == null) throw new IllegalArgumentException("자료가 null입니다.");
        if (items.containsKey(item.getId())) {
            throw new IllegalArgumentException("이미 등록된 자료 ID입니다: " + item.getId());
        }
        items.put(item.getId(), item);
    }

    public Optional<LibraryItem> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<LibraryItem> searchByTitle(String keyword) {
        return search(keyword, true);
    }

    public List<LibraryItem> searchByAuthor(String keyword) {
        return search(keyword, false);
    }

    public List<LibraryItem> getAllItems() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    // 대소문자 무시, 부분 일치
    private List<LibraryItem> search(String keyword, boolean byTitle) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        List<LibraryItem> result = new ArrayList<>();
        for (LibraryItem item : items.values()) {
            String target = byTitle ? item.getTitle() : item.getAuthor();
            if (target != null && target.toLowerCase().contains(k)) {
                result.add(item);
            }
        }
        return result;
    }
}
