// AdminPanel - 관리 화면 (자료·회원 등록, 시연용 날짜 설정)

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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AdminPanel extends JPanel {

    private final CatalogService catalog;
    private final MemberService members;
    private final LibraryClock clock;

    private final JComboBox<String> itemType = new JComboBox<>(new String[]{"도서", "잡지", "참고도서"});
    private final JTextField itemIdField = new JTextField(6);
    private final JTextField itemTitleField = new JTextField(12);
    private final JTextField itemAuthorField = new JTextField(8);

    private final JComboBox<String> memberType = new JComboBox<>(new String[]{"일반", "학생"});
    private final JTextField memberIdField = new JTextField(6);
    private final JTextField memberNameField = new JTextField(8);

    private final JTextField dateField = new JTextField(10);    // yyyy-mm-dd
    private final JLabel currentDateLabel = new JLabel();

    public AdminPanel(CatalogService catalog, MemberService members, LibraryClock clock) {
        this.catalog = catalog;
        this.members = members;
        this.clock = clock;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel itemRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemRow.setBorder(javax.swing.BorderFactory.createTitledBorder("자료 등록"));
        itemRow.add(new JLabel("종류:"));
        itemRow.add(itemType);
        itemRow.add(new JLabel("ID:"));
        itemRow.add(itemIdField);
        itemRow.add(new JLabel("제목:"));
        itemRow.add(itemTitleField);
        itemRow.add(new JLabel("저자:"));
        itemRow.add(itemAuthorField);
        JButton addItemButton = new JButton("자료 등록");
        addItemButton.addActionListener(e -> onAddItem());
        itemRow.add(addItemButton);
        add(itemRow);

        JPanel memberRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        memberRow.setBorder(javax.swing.BorderFactory.createTitledBorder("회원 등록"));
        memberRow.add(new JLabel("유형:"));
        memberRow.add(memberType);
        memberRow.add(new JLabel("회원 ID:"));
        memberRow.add(memberIdField);
        memberRow.add(new JLabel("이름:"));
        memberRow.add(memberNameField);
        JButton addMemberButton = new JButton("회원 등록");
        addMemberButton.addActionListener(e -> onAddMember());
        memberRow.add(addMemberButton);
        add(memberRow);

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateRow.setBorder(javax.swing.BorderFactory.createTitledBorder("날짜 설정 (시연용)"));
        dateRow.add(currentDateLabel);
        dateRow.add(new JLabel("날짜(yyyy-mm-dd):"));
        dateRow.add(dateField);
        JButton changeDateButton = new JButton("날짜 변경");
        changeDateButton.addActionListener(e -> onChangeDate());
        JButton resetDateButton = new JButton("오늘로 되돌리기");
        resetDateButton.addActionListener(e -> onResetDate());
        dateRow.add(changeDateButton);
        dateRow.add(resetDateButton);
        add(dateRow);

        refreshDateLabel();
    }

    public void onAddItem() {
        try {
            String id = itemIdField.getText().trim();
            String title = itemTitleField.getText().trim();
            String author = itemAuthorField.getText().trim();
            if (id.isEmpty() || title.isEmpty()) {
                Dialogs.error(this, "ID와 제목을 입력하세요.");
                return;
            }
            LibraryItem item;
            switch ((String) itemType.getSelectedItem()) {
                case "잡지": item = new Magazine(id, title, author); break;
                case "참고도서": item = new ReferenceBook(id, title, author); break;
                default: item = new Book(id, title, author); break;
            }
            catalog.addItem(item);
            Dialogs.info(this, title + " 자료를 등록했습니다.");
            itemIdField.setText("");
            itemTitleField.setText("");
            itemAuthorField.setText("");
        } catch (RuntimeException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    public void onAddMember() {
        try {
            String id = memberIdField.getText().trim();
            String name = memberNameField.getText().trim();
            if (id.isEmpty() || name.isEmpty()) {
                Dialogs.error(this, "회원 ID와 이름을 입력하세요.");
                return;
            }
            Member member = "학생".equals(memberType.getSelectedItem())
                    ? new StudentMember(id, name)
                    : new GeneralMember(id, name);
            members.registerMember(member);
            Dialogs.info(this, name + " 회원을 등록했습니다.");
            memberIdField.setText("");
            memberNameField.setText("");
        } catch (RuntimeException ex) {
            Dialogs.error(this, ex.getMessage());
        }
    }

    public void onChangeDate() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            clock.setToday(date);
            refreshDateLabel();
        } catch (DateTimeParseException ex) {
            Dialogs.error(this, "날짜 형식은 yyyy-mm-dd 입니다.");
        }
    }

    public void onResetDate() {
        clock.resetToday();
        refreshDateLabel();
    }

    private void refreshDateLabel() {
        currentDateLabel.setText("현재 날짜: " + clock.today());
        dateField.setText(clock.today().toString());
    }
}
