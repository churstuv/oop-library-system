/*
 * [팀원 A 담당 · 연동 확인용 임시 구현] 팀원 A 코드로 교체 예정
 * ItemStatus - 자료 상태
 */
package com.library.model;

public enum ItemStatus {
    AVAILABLE("비치 중"),
    ON_LOAN("대출 중");

    private final String label;

    ItemStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
