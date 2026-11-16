/*
 * CatalogService - 자료(도서·잡지·참고도서) 등록과 검색
 * 자료 등록(F1)과 제목·저자 키워드 검색(F2)을 담당한다.
 * 검색은 대소문자를 무시하고 부분 일치로 찾는다.
 * 주요 메소드: addItem(), findById(), searchByTitle(), searchByAuthor(), getAllItems()
 *
 * [설계 이유]
 *  - 단일 책임: "자료 목록 관리"만 맡는다. 대출 규칙은 LoanService, 자료별 정책은 각 자료 클래스가 맡는다.
 *  - 다형성: 목록을 LibraryItem 타입으로만 다룬다. Book/Magazine/ReferenceBook을 구분하는 if문이 없어서
 *    새 자료 종류(예: Dvd)가 생겨도 이 클래스는 바뀌지 않는다(개방-폐쇄 원칙).
 *  - 은닉성: 내부 Map은 외부에 그대로 내주지 않고, 읽기 전용 복사본만 반환한다.
 *  - ID는 대소문자 구분 없이 찾는다("b001"로 입력해도 B001을 찾음). 저장 키를 대문자로 통일한다.
 */
package com.library.service;

import com.library.model.LibraryItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CatalogService {
    // LinkedHashMap: ID로 빠르게 찾으면서(O(1)) 등록 순서도 유지한다.
    private final Map<String, LibraryItem> items = new LinkedHashMap<>();

    /** 자료 등록 (F1). ID가 비었거나 중복이면 IllegalArgumentException */
    public void addItem(LibraryItem item) {
        Objects.requireNonNull(item, "등록할 자료가 없습니다.");
        requireText(item.getId(), "자료 ID");
        requireText(item.getTitle(), "제목");
        String key = normalize(item.getId());
        if (items.containsKey(key)) {
            throw new IllegalArgumentException("이미 등록된 자료 ID입니다: " + item.getId());
        }
        items.put(key, item);
    }

    /** ID로 자료 조회. 없을 수 있으므로 null 대신 Optional을 돌려준다. */
    public Optional<LibraryItem> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(normalize(id)));
    }

    /** 제목 키워드 검색 (F2) */
    public List<LibraryItem> searchByTitle(String keyword) {
        return search(keyword, LibraryItem::getTitle);
    }

    /** 저자 키워드 검색 (F2) */
    public List<LibraryItem> searchByAuthor(String keyword) {
        return search(keyword, LibraryItem::getAuthor);
    }

    /** 전체 자료 (등록 순서) */
    public List<LibraryItem> getAllItems() {
        return List.copyOf(items.values());
    }

    /**
     * 제목 검색과 저자 검색의 공통 로직.
     * "무엇을 비교할지"만 함수(Function)로 받아서 같은 코드를 두 번 쓰지 않는다.
     * 키워드가 비어 있으면 전체 목록을 돌려준다.
     */
    private List<LibraryItem> search(String keyword, Function<LibraryItem, String> field) {
        if (keyword == null || keyword.isBlank()) {
            return getAllItems();
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        List<LibraryItem> result = new ArrayList<>();
        for (LibraryItem item : items.values()) {
            String value = field.apply(item);
            if (value != null && value.toLowerCase(Locale.ROOT).contains(needle)) {
                result.add(item);
            }
        }
        return result;
    }

    private static String normalize(String id) {
        return id.trim().toUpperCase(Locale.ROOT);
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "을(를) 입력해 주세요.");
        }
    }
}
