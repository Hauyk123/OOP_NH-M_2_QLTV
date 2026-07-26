package org.example.UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import org.example.UI.design.DesignSystem;
import org.example.model.Member;
import org.example.model.Loan;
import org.example.controller.LibraryManagement;
import org.example.utils.DateUtils;

public class ProFile extends JDialog {

    private static final long serialVersionUID = 1L;
    private final transient LibraryManagement library;
    private final transient Member member;

    public ProFile(Member member) {
        super((Frame) null, "Thông tin thành viên", true);
        this.member = member;
        this.library = LibraryManagement.getInstance();

        // Bắt buộc để dùng setShape()
        setUndecorated(true);

        // Kích thước và bo góc
        int width = 800, height = 600;
        setSize(width, height);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Bottom panel với nút đóng
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        DesignSystem.styleButton(closeButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));

        // Avatar panel
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.setOpaque(false);
        ImageIcon originalIcon = new ImageIcon("src/main/java/org/example/icons/user.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel avatarLabel = new JLabel(new ImageIcon(scaledImage));
        avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        avatarPanel.add(avatarLabel);

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);

        JLabel idLabel = new JLabel("Mã thành viên: " + member.getId());
        JLabel nameLabel = new JLabel("Tên thành viên: " + member.getName());
        JLabel loanCountLabel = new JLabel("Số sách đang mượn: " + member.getLoans().size());

        Font font = new Font("Arial", Font.BOLD, 14);
        idLabel.setFont(font);
        nameLabel.setFont(font);
        loanCountLabel.setFont(font);

        infoPanel.add(idLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(loanCountLabel);

        // Add panels to header
        headerPanel.add(avatarPanel, BorderLayout.WEST);
        headerPanel.add(infoPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Mã sách", "Tên sách", "Ngày mượn", "Ngày trả"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable loanTable = new JTable(model);
        DesignSystem.styleTable(loanTable);

        for (Loan loan : member.getLoans()) {
            String bookId = loan.getBookId();
            String bookTitle = "Không rõ";
            if (library.findBookById(bookId) != null) {
                bookTitle = library.findBookById(bookId).getTitle();
            }

            model.addRow(new Object[]{
                bookId,
                bookTitle,
                loan.getStartDate(),
                loan.getEndDate()
            });
        }

        JScrollPane scrollPane = new JScrollPane(loanTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    // Optional: để test riêng
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Member testMember = new Member("M001", "Nguyễn Văn A");
            testMember.borrowBook(new Loan("B000001",
                    DateUtils.parse("2023-10-01"),
                    DateUtils.parse("2023-10-15")));
            testMember.borrowBook(new Loan("B000002",
                    DateUtils.parse("2023-10-05"),
                    DateUtils.parse("2023-10-20")));

            ProFile profile = new ProFile(testMember);
            profile.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            profile.setVisible(true);
        });
    }
}
