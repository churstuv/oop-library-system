/*
 * ItemNotAvailableException - 대출 불가 자료 예외
 * 대출 불가 자료(참고도서)이거나 이미 대출 중인 자료를 대출하려 할 때
 * LoanService.borrow()에서 발생한다. (대출 검사 3·4순위)
 *
 * 두 경우를 하나의 예외로 묶되, Reason(enum)으로 원인을 구분한다.
 *  - 예외 클래스를 원인마다 따로 만들면 클래스 수만 늘어나고 처리 방식은 같다.
 *  - enum은 정해진 값만 가질 수 있어 문자열로 원인을 표시하는 것보다 안전하다.
 */
package com.library.exception;

public class ItemNotAvailableException extends LibraryException {

    /** 대출할 수 없는 원인 */
    public enum Reason {
        NOT_LOANABLE("열람 전용 자료라 대출할 수 없습니다."),
        ALREADY_ON_LOAN("이미 대출 중인 자료입니다.");

        private final String description;

        Reason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String itemId;
    private final Reason reason;

    public ItemNotAvailableException(String itemId, String itemTitle, Reason reason) {
        super(String.format("「%s」(%s): %s", itemTitle, itemId, reason.getDescription()));
        this.itemId = itemId;
        this.reason = reason;
    }

    public String getItemId() {
        return itemId;
    }

    public Reason getReason() {
        return reason;
    }
}
