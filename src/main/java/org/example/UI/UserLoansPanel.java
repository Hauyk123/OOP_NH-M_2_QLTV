package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.example.UI.design.DesignSystem;
import org.example.controller.LibraryManagement;
import org.example.model.Book;
import org.example.model.Loan;
import org.example.model.Member;
import org.example.utils.DateUtils;

public class UserLoansPanel extends JPanel {
    private final LibraryManagement library;
    private final Member currentUser;
    private final JTable loanTable;
    private final DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    public UserLoansPanel(Member currentUser) {
        this.currentUser = currentUser;
        this.library = LibraryManagement.getInstance();

        // Cột hiển thị thông tin kèm trạng thái
        String[] columns = {"Mã sách", "Tên sách", "Ngày mượn", "Ngày hẹn trả", "Trạng thái"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.loanTable = new JTable(tableModel);
        DesignSystem.styleTable(loanTable);
        DesignSystem.fixTableColumnWidth(loanTable, 15, 35, 15, 15, 20);

        sorter = new TableRowSorter<>(tableModel);
        loanTable.setRowSorter(sorter);

        initializeComponents();
        reloadLoans();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        // Header Panel với màu nền đỏ Gradient đồng bộ
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(204, 0, 0), 0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JLabel titleLabel = new JLabel("LỊCH SỬ MƯỢN SÁCH CỦA BẠN", new ImageIcon(new ImageIcon("src/main/java/org/example/icons/reading.png")
                .getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Bảng danh sách
        JScrollPane scrollPane = DesignSystem.createTableScrollPane(loanTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void reloadLoans() {
        tableModel.setRowCount(0);

        // Lấy lại thông tin mới nhất của user từ danh sách hệ thống
        Member freshUser = library.findMemberById(currentUser.getId());
        if (freshUser == null) return;

        // Lưu ý: Hệ thống hiện tại khi trả sách sẽ xóa khỏi danh sách loans trực tiếp.
        // Do đó các cuốn nằm trong mảng loans của member sẽ mang trạng thái "Đang mượn".
        List<Loan> activeLoans = freshUser.getLoans();
        for (Loan l : activeLoans) {
            Book b = library.findBookById(l.getBookId());
            String title = (b != null) ? b.getTitle() : "Không rõ";

            tableModel.addRow(new Object[]{
                    l.getBookId(),
                    title,
                    DateUtils.format(l.getStartDate()),
                    DateUtils.format(l.getEndDate()),
                    "Đang mượn" // Trạng thái hiển thị
            });
        }
    }
}