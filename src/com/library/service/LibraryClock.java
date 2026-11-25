// LibraryClock - 시스템 날짜 관리 (시연용 날짜 변경 포함)
// 프로그램 전체가 같은 "오늘"을 보도록 한 곳에서 관리한다.

package com.library.service;

import java.time.LocalDate;

public class LibraryClock {

    private final LocalDate initial;    // 시작 날짜 (resetToday에서 복구할 값)
    private LocalDate current;          // 현재 시스템 날짜

    public LibraryClock(LocalDate initial) {
        if (initial == null) throw new IllegalArgumentException("초기 날짜는 null일 수 없습니다.");
        this.initial = initial;
        this.current = initial;
    }

    public LocalDate today() {
        return current;
    }

    public void setToday(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
        this.current = date;
    }

    public void resetToday() {
        this.current = initial;
    }
}
