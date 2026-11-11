/*
 * LibraryClock - 시스템 날짜 관리
 * 프로그램 전체가 같은 "오늘" 날짜를 보도록 한 곳에서 관리한다.
 * 업무 로직은 LocalDate.now()를 직접 쓰지 않고 이 클래스를 통해 날짜를 얻는다.
 * 시연용 날짜 변경(F8)도 이 클래스로 처리한다.
 * 주요 메소드: today(), setToday(LocalDate), resetToday()
 */
package com.library.service;

public class LibraryClock {
}
