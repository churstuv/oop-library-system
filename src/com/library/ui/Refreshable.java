/*
 * Refreshable - "다시 그릴 수 있는 화면" 규약
 * MainFrame은 탭이 선택되거나 시스템 날짜가 바뀌면, 그 탭이 Refreshable인지 확인하고 refresh()를 호출한다.
 *
 * [설계 이유]
 *  - 다형성: MainFrame은 각 패널의 구체 타입(BookPanel, OverduePanel...)을 몰라도 된다.
 *    "if (탭 == 연체탭) overduePanel.refresh()" 같은 분기 대신 refresh() 한 번으로 처리한다.
 *  - 새 탭을 추가할 때 Refreshable만 구현하면 MainFrame 코드는 바꾸지 않아도 된다.
 */
package com.library.ui;

public interface Refreshable {
    void refresh();
}
