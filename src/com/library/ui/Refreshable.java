// Refreshable - 탭이 선택될 때 화면을 다시 그려야 하는 패널의 규약
// MainFrame은 패널의 구체 타입을 모르고 이 인터페이스로만 새로고침을 요청한다 (다형성).

package com.library.ui;

public interface Refreshable {

    void refresh();     // 최신 데이터(현재 시스템 날짜 기준)로 화면 갱신
}
