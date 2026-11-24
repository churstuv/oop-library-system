// ItemNotAvailableException - 대출 중 업무 규칙 위반 시 발생하는 예외

package com.library.exception;

public class ItemNotAvailableException extends RuntimeException {

    public ItemNotAvailableException(String message) {
        super(message);
    }
}
