// Dialogs - 안내·오류 창 공통 처리

package com.library.ui;

import javax.swing.JOptionPane;
import java.awt.Component;

public class Dialogs {

    private Dialogs() {
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "안내", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "오류", JOptionPane.ERROR_MESSAGE);
    }
}
