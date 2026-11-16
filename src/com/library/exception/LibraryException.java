/*
 * LibraryException - 도서관 업무 규칙 위반 예외의 공통 부모(추상 클래스)
 *
 * [설계 이유]
 *  - 추상성: "업무 규칙 위반"이라는 공통 개념만 정의하고, 구체적인 위반 내용은 하위 예외가 책임진다.
 *    abstract로 선언해 이 예외를 직접 생성(throw new LibraryException)하지 못하게 막는다.
 *  - 상속성·다형성: 화면(UI)은 catch (LibraryException e) 한 번으로 모든 업무 예외를 받아
 *    e.getMessage()를 보여주면 된다. 새 업무 예외가 추가돼도 화면 코드는 바뀌지 않는다.
 *  - checked 예외(Exception 상속)로 만든 이유: 대출 실패는 정상적인 업무 상황이므로
 *    호출하는 쪽이 반드시 처리하도록 컴파일러가 강제하게 한다.
 *    반대로 "존재하지 않는 ID" 같은 입력 오류는 IllegalArgumentException(unchecked)으로 구분한다.
 */
package com.library.exception;

public abstract class LibraryException extends Exception {

    protected LibraryException(String message) {
        super(message);
    }
}
