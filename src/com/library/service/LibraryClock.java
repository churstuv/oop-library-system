/*
 * LibraryClock - 시스템 날짜 관리
 * 프로그램 전체가 같은 "오늘" 날짜를 보도록 한 곳에서 관리한다.
 * 업무 로직은 LocalDate.now()를 직접 쓰지 않고 이 클래스를 통해 날짜를 얻는다.
 * 시연용 날짜 변경(F8)도 이 클래스로 처리한다.
 * 주요 메소드: today(), setToday(LocalDate), resetToday()
 *
 * [설계 이유]
 *  - 은닉성: today 필드는 private이고, 바꾸는 길은 setToday()/resetToday() 두 개뿐이다.
 *    null 같은 잘못된 값이 들어오는 것을 이 두 메소드에서 막는다.
 *  - 객체 하나를 Main에서 만들어 모든 서비스에 생성자로 넘긴다(의존성 주입).
 *    그래서 날짜를 바꾸면 모든 서비스가 같은 날짜를 보고, 테스트에서는 원하는 날짜로 고정할 수 있다.
 *    static 필드로 만들지 않은 이유: 전역 상태가 되면 테스트끼리 서로 영향을 주고, 의존 관계가 코드에 드러나지 않는다.
 *  - 날짜가 바뀌면 등록된 리스너(Consumer)에게 알린다(옵저버 패턴).
 *    LibraryClock은 화면(UI)을 전혀 모르고 "바뀌었다"는 사실만 전달하므로 계층 분리가 유지된다.
 */
package com.library.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class LibraryClock {
    private LocalDate today;
    private final List<Consumer<LocalDate>> listeners = new ArrayList<>();

    /** 실제 오늘 날짜로 시작한다. (프로그램 실행용) */
    public LibraryClock() {
        this(LocalDate.now());
    }

    /** 지정한 날짜로 시작한다. (테스트용) */
    public LibraryClock(LocalDate initialDate) {
        this.today = Objects.requireNonNull(initialDate, "날짜는 null일 수 없습니다.");
    }

    /** 시스템이 기준으로 삼는 "오늘" */
    public LocalDate today() {
        return today;
    }

    /** 시연용으로 시스템 날짜를 바꾼다. */
    public void setToday(LocalDate date) {
        this.today = Objects.requireNonNull(date, "날짜는 null일 수 없습니다.");
        notifyListeners();
    }

    /** 실제 오늘 날짜로 되돌린다. ("오늘로 되돌리기" 버튼) */
    public void resetToday() {
        setToday(LocalDate.now());
    }

    /** 시스템 날짜가 실제 오늘과 다른지 (화면에 "시연 날짜 적용 중" 표시용) */
    public boolean isAdjusted() {
        return !today.equals(LocalDate.now());
    }

    /** 날짜가 바뀔 때 호출될 리스너를 등록한다. */
    public void addChangeListener(Consumer<LocalDate> listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    private void notifyListeners() {
        for (Consumer<LocalDate> listener : listeners) {
            listener.accept(today);
        }
    }
}
