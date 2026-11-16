/*
 * Dialogs - 안내·오류 창 공통 처리
 * 대출·반납 결과 안내와 예외 메시지 표시를 한 곳에서 처리해 패널마다 중복되지 않도록 한다.
 * 주요 메소드: info(), error()
 *
 * [설계 이유]
 *  - 예외 종류에 따라 창 모양을 다르게 보여준다.
 *      LibraryException(업무 규칙 위반: 연체·권수·대출불가) → 경고 창 "대출 불가"
 *      IllegalArgumentException 등(입력 오류: 없는 ID, 빈 칸)   → 오류 창 "입력 오류"
 *    이 구분을 한 곳에 모아 두어 모든 패널이 같은 방식으로 안내한다.
 *  - 상태가 없는 도구 클래스라 final + private 생성자로 객체 생성을 막고 static 메소드만 제공한다.
 */
package com.library.ui;

import com.library.exception.LibraryException;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class Dialogs {

    private Dialogs() {
    }

    /** 처리 완료 등 일반 안내 */
    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "안내", JOptionPane.INFORMATION_MESSAGE);
    }

    /** 사용자 입력 누락 등 간단한 경고 */
    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "확인 필요", JOptionPane.WARNING_MESSAGE);
    }

    /** 예외를 종류에 맞는 창으로 보여준다. */
    public static void error(Component parent, Exception e) {
        if (e instanceof LibraryException) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "대출 불가", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
