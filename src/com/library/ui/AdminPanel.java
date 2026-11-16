/*
 * AdminPanel - 관리 화면 (자료·회원 등록, 날짜 설정)
 * 자료 등록(F1), 회원 등록(F3), 시연용 시스템 날짜 변경(F8)을 처리한다.
 * 날짜 변경은 LibraryClock.setToday()로, "오늘로 되돌리기"는 LibraryClock.resetToday()로 처리한다.
 * 주요 메소드: onAddItem(), onAddMember(), onChangeDate(), onResetDate()
 *
 * [설계 이유]
 *  - 개방-폐쇄 원칙: 자료·회원 종류를 if/switch로 나누지 않고 "이름 → 생성 함수" 표(Map)로 관리한다.
 *    새 자료 종류(예: Dvd)를 추가할 때는 ITEM_TYPES에 한 줄만 넣으면 콤보박스와 등록 기능이 함께 동작한다.
 *        ITEM_TYPES.put("DVD", Dvd::new);
 *  - Book::new 같은 생성자 참조가 ItemFactory 인터페이스로 다뤄진다(함수형 인터페이스 + 다형성).
 *  - 날짜 입력 오류(형식 오류)는 이 화면에서 잡아 안내하고, 실제 날짜 변경은 LibraryClock에 맡긴다.
 */
package com.library.ui;

import com.library.model.Book;
import com.library.model.GeneralMember;
import com.library.model.LibraryItem;
import com.library.model.Magazine;
import com.library.model.Member;
import com.library.model.ReferenceBook;
import com.library.model.StudentMember;
import com.library.service.CatalogService;
import com.library.service.LibraryClock;
import com.library.service.MemberService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminPanel extends JPanel implements Refreshable {

    /** 자료 생성 함수: (ID, 제목, 저자) → 자료 객체 */
    @FunctionalInterface
    interface ItemFactory {
        LibraryItem create(String id, String title, String author);
    }

    /** 회원 생성 함수: (ID, 이름) → 회원 객체 */
    @FunctionalInterface
    interface MemberFactory {
        Member create(String id, String name);
    }

    // 종류 추가 시 여기에 한 줄만 추가한다. (LinkedHashMap: 콤보박스 표시 순서 유지)
    private static final Map<String, ItemFactory> ITEM_TYPES;
    private static final Map<String, MemberFactory> MEMBER_TYPES;

    static {
        Map<String, ItemFactory> items = new LinkedHashMap<>();
        items.put("도서", Book::new);
        items.put("잡지", Magazine::new);
        items.put("참고도서", ReferenceBook::new);
        ITEM_TYPES = Collections.unmodifiableMap(items);

        Map<String, MemberFactory> members = new LinkedHashMap<>();
        members.put("일반", GeneralMember::new);
        members.put("학생", StudentMember::new);
        MEMBER_TYPES = Collections.unmodifiableMap(members);
    }

    private final CatalogService catalogService;
    private final MemberService memberService;
    private final LibraryClock clock;

    private final JComboBox<String> itemTypeBox = new JComboBox<>(ITEM_TYPES.keySet().toArray(new String[0]));
    private final JTextField itemIdField = new JTextField(8);
    private final JTextField titleField = new JTextField(16);
    private final JTextField authorField = new JTextField(10);

    private final JComboBox<String> memberTypeBox = new JComboBox<>(MEMBER_TYPES.keySet().toArray(new String[0]));
    private final JTextField memberIdField = new JTextField(8);
    private final JTextField memberNameField = new JTextField(10);

    private final JTextField dateField = new JTextField(10);
    private final JLabel currentDateLabel = new JLabel();

    public AdminPanel(CatalogService catalogService, MemberService memberService, LibraryClock clock) {
        this.catalogService = catalogService;
        this.memberService = memberService;
        this.clock = clock;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(fixedHeight(createItemForm()));
        add(Box.createVerticalStrut(10));
        add(fixedHeight(createMemberForm()));
        add(Box.createVerticalStrut(10));
        add(fixedHeight(createDateForm()));
        add(Box.createVerticalGlue());
        refresh();
    }

    private JPanel createItemForm() {
        JPanel form = titled("자료 등록 (F1)");
        JButton addButton = new JButton("자료 등록");
        addButton.addActionListener(e -> onAddItem());
        form.add(new JLabel("종류"));
        form.add(itemTypeBox);
        form.add(new JLabel("ID"));
        form.add(itemIdField);
        form.add(new JLabel("제목"));
        form.add(titleField);
        form.add(new JLabel("저자"));
        form.add(authorField);
        form.add(addButton);
        return form;
    }

    private JPanel createMemberForm() {
        JPanel form = titled("회원 등록 (F3)");
        JButton addButton = new JButton("회원 등록");
        addButton.addActionListener(e -> onAddMember());
        form.add(new JLabel("유형"));
        form.add(memberTypeBox);
        form.add(new JLabel("ID"));
        form.add(memberIdField);
        form.add(new JLabel("이름"));
        form.add(memberNameField);
        form.add(addButton);
        return form;
    }

    private JPanel createDateForm() {
        JPanel wrapper = new JPanel(new GridLayout(2, 1));
        wrapper.setBorder(BorderFactory.createTitledBorder("시스템 날짜 설정 (F8 · 시연용)"));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton applyButton = new JButton("날짜 적용");
        JButton plus15Button = new JButton("+15일");
        JButton resetButton = new JButton("오늘로 되돌리기");
        applyButton.addActionListener(e -> onChangeDate());
        dateField.addActionListener(e -> onChangeDate());
        plus15Button.addActionListener(e -> {
            dateField.setText(clock.today().plusDays(15).toString());
            onChangeDate();
        });
        resetButton.addActionListener(e -> onResetDate());
        row1.add(new JLabel("날짜 (yyyy-MM-dd)"));
        row1.add(dateField);
        row1.add(applyButton);
        row1.add(plus15Button);
        row1.add(resetButton);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(currentDateLabel);

        wrapper.add(row1);
        wrapper.add(row2);
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        return wrapper;
    }

    /** BoxLayout에서 폼이 세로로 늘어나지 않도록 높이를 고정한다. */
    private static JPanel fixedHeight(JPanel panel) {
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private JPanel titled(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        return panel;
    }

    /** 자료 등록 (F1) */
    void onAddItem() {
        String type = (String) itemTypeBox.getSelectedItem();
        String id = itemIdField.getText().trim().toUpperCase();
        try {
            LibraryItem item = ITEM_TYPES.get(type).create(id, titleField.getText().trim(), authorField.getText().trim());
            catalogService.addItem(item);
            Dialogs.info(this, "자료가 등록되었습니다.\n" + item);
            itemIdField.setText("");
            titleField.setText("");
            authorField.setText("");
        } catch (IllegalArgumentException e) {
            Dialogs.error(this, e);
        }
    }

    /** 회원 등록 (F3) */
    void onAddMember() {
        String type = (String) memberTypeBox.getSelectedItem();
        String id = memberIdField.getText().trim().toUpperCase();
        try {
            Member member = MEMBER_TYPES.get(type).create(id, memberNameField.getText().trim());
            memberService.registerMember(member);
            Dialogs.info(this, String.format("회원이 등록되었습니다.%n%s · 최대 %d권",
                    member, member.getMaxLoanCount()));
            memberIdField.setText("");
            memberNameField.setText("");
        } catch (IllegalArgumentException e) {
            Dialogs.error(this, e);
        }
    }

    /** 시스템 날짜 변경 (F8) */
    void onChangeDate() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            clock.setToday(date);
            Dialogs.info(this, "시스템 날짜를 " + date + "로 변경했습니다.\n연체 탭에서 결과를 확인하세요.");
        } catch (DateTimeParseException e) {
            Dialogs.warn(this, "날짜 형식이 올바르지 않습니다. 예: 2026-12-01");
        }
    }

    /** 실제 오늘 날짜로 복구 */
    void onResetDate() {
        clock.resetToday();
        Dialogs.info(this, "시스템 날짜를 오늘(" + clock.today() + ")로 되돌렸습니다.");
    }

    @Override
    public void refresh() {
        dateField.setText(clock.today().toString());
        currentDateLabel.setText("현재 시스템 날짜: " + clock.today()
                + (clock.isAdjusted() ? "  (시연 날짜 적용 중)" : "  (실제 오늘)"));
    }
}
