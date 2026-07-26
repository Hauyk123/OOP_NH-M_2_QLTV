package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.example.UI.design.DesignSystem;
import org.example.controller.LibraryManagement;
import org.example.controller.LoanManagement;
import org.example.model.Book;
import org.example.model.Loan;
import org.example.model.Member;
import org.example.utils.DateUtils;

public class LoanAndMemberPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final transient LibraryManagement library;
    private final JTable memberTable;
    private final DefaultTableModel tableModel;

    public LoanAndMemberPanel() {
        this.library = LibraryManagement.getInstance();

        // Table model setup
        String[] columns = {"ID", "Tên Thành viên", "Tên sách mượn", "Từ ngày", "Đến ngày"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.memberTable = new JTable(tableModel);

        // Initialize panel after all fields are set
        initializeComponents();

        // Load data asynchronously
        loadDataAsync();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ImageIcon originalIcon = new ImageIcon("src/main/java/org/example/icons/library.png");
        Image resizedImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JLabel title = DesignSystem.createHeaderLabel("QUẢN LÝ MƯỢN/TRẢ & THÀNH VIÊN", new ImageIcon(resizedImage));
        add(title, BorderLayout.NORTH);

        // Table styling and scroll
        DesignSystem.styleTable(memberTable);
        // Cấu hình tỷ lệ phần trăm cho các cột (tổng = 100%)
        DesignSystem.fixTableColumnWidth(memberTable, 15, 25, 30, 15, 15);
        JScrollPane scrollPane = DesignSystem.createTableScrollPane(memberTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        add(createButtonPanel(), BorderLayout.SOUTH);

        setupMouseListener();
    }

    private void loadDataAsync() {
        LoadingDialog loadingDialog = new LoadingDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Đang tải dữ liệu..."
        );

        // Fix the cancel listener to match ActionListener interface
        loadingDialog.addCancelListener(e -> {
            if (library != null) {
                library.cancelLoading();
            }
            loadingDialog.dispose();
        });

        CompletableFuture.runAsync(() -> {
            try {
                CompletableFuture<Void> membersLoad = library.loadMembersAsync();
                CompletableFuture<Void> booksLoad = library.loadBooksAsync();

                CompletableFuture.allOf(membersLoad, booksLoad)
                        .thenRunAsync(() -> {
                            SwingUtilities.invokeLater(() -> {
                                showOnlyLoans();
                                loadingDialog.dispose();
                            });
                        })
                        .exceptionally(e -> {
                            SwingUtilities.invokeLater(() -> {
                                showError("Lỗi tải dữ liệu: " + e.getMessage());
                                loadingDialog.dispose();
                            });
                            return null;
                        });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showError("Lỗi tải dữ liệu: " + e.getMessage());
                    loadingDialog.dispose();
                });
            }
        });

        loadingDialog.setVisible(true);
    }

    private void setupMouseListener() {
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = memberTable.getSelectedRow();
                    if (row >= 0) {
                        String id = memberTable.getValueAt(row, 0).toString();
                        if (tableModel.getColumnCount() == 2) {
                            Member member = library.findMemberById(id);
                            if (member != null) {
                                ProFile profile = new ProFile(member);
                                profile.setLocationRelativeTo(LoanAndMemberPanel.this);
                                profile.setVisible(true);
                            }
                        } else {
                            // When viewing loans, get book ID from the title
                            String bookTitle = memberTable.getValueAt(row, 2).toString();
                            Book book = library.getBooks().stream()
                                    .filter(b -> b.getTitle().equals(bookTitle))
                                    .findFirst()
                                    .orElse(null);

                            if (book != null) {
                                BookInfor infor = new BookInfor(
                                        (JFrame) SwingUtilities.getWindowAncestor(LoanAndMemberPanel.this),
                                        book
                                );
                                infor.setVisible(true);
                            }
                        }
                    }
                }
            }
        });
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        DesignSystem.styleButtonPanel(btnPanel);

        String[] labels = {"Thêm Thành viên", "Xóa Thành viên", "Mượn Sách",
            "Trả Sách", "Xem Thành viên", "Xem Trả/Mượn"};
        Runnable[] actions = {
            this::showAddMemberDialog,
            this::showRemoveMemberDialog,
            this::showBorrowDialog,
            this::showReturnDialog,
            this::showOnlyMembers,
            this::showOnlyLoans
        };

        for (int i = 0; i < labels.length; i++) {
            JButton btn = new JButton(labels[i]);
            DesignSystem.styleButton(btn);
            int idx = i;
            btn.addActionListener(e -> actions[idx].run());
            btnPanel.add(btn);
        }

        return btnPanel;
    }

    private void showAddMemberDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ID Thành viên:"));
        panel.add(idField);
        panel.add(new JLabel("Tên Thành viên:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm Thành viên", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                showError("Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            library.addMember(new Member(id, name));
            library.saveMembersAsync();
            showOnlyMembers();
        }
    }

    private void showRemoveMemberDialog() {
        String id = JOptionPane.showInputDialog(this, "Nhập ID thành viên cần xóa:");
        if (id != null && !id.trim().isEmpty()) {
            boolean removed = library.getMembers().removeIf(m -> m.getId().equals(id.trim()));
            if (!removed) {
                showError("Không tìm thấy thành viên với ID này.");
            } else {
                library.saveMembersAsync();
                showOnlyMembers();
            }
        }
        if (id == null || id.trim().isEmpty()) {
            showError("Vui lòng nhập ID thành viên cần xóa!");
            return;
        }
    }

    private void showBorrowDialog() {
        JTextField userIdField = new JTextField();
        JTextField bookIdField = new JTextField();
        JTextField startField = new JTextField("dd/MM/yyyy");
        JTextField endField = new JTextField("dd/MM/yyyy");

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ID Thành viên:"));
        panel.add(userIdField);
        panel.add(new JLabel("ID Sách:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Ngày mượn:"));
        panel.add(startField);
        panel.add(new JLabel("Ngày trả:"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "📥 Mượn Sách", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                if(userIdField == null || bookIdField == null || startField == null || endField == null){
            showError("Vui lòng nhập đủ thông tin!");
            return;
        }
        if(userIdField.getText().isEmpty() || bookIdField.getText().isEmpty() || startField.getText().isEmpty() || endField.getText().isEmpty()){
            showError("Vui lòng nhập đúng thông tin");
            return;
        }
                Date startDate = DateUtils.parse(startField.getText().trim());
                Date endDate = DateUtils.parse(endField.getText().trim());

                if (startDate == null || endDate == null) {
                    showError("Vui lòng nhập đúng định dạng ngày!");
                    return;
                }
                LoanManagement.borrowBook(library, userIdField.getText().trim(), bookIdField.getText().trim(), startDate, endDate);
                showOnlyLoans();
                JOptionPane.showMessageDialog(this,
                        "📚 Mượn sách thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showError("❌ Lỗi mượn sách: " + e.getMessage());
            }
        }
        if (userIdField.getText().isEmpty() || bookIdField.getText().isEmpty()
                || startField.getText().isEmpty() || endField.getText().isEmpty()){
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
    }

    private void showReturnDialog() {
        JTextField userIdField = new JTextField();
        JTextField bookIdField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("ID Thành viên:"));
        panel.add(userIdField);
        panel.add(new JLabel("ID Sách:"));
        panel.add(bookIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "📤 Trả Sách", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String userId = userIdField.getText().trim();
                String bookId = bookIdField.getText().trim();
                LoanManagement.returnBook(library, userId, bookId);
                showOnlyLoans();

                Book returnedBook = library.findBookById(bookId);
                String bookTitle = returnedBook != null ? returnedBook.getTitle() : "Không rõ";
                if(bookTitle == "Không rõ") {
                    showError("Vui lòng nhập đầy đủ thông tin!");
                    return;
                }
                int choice = JOptionPane.showConfirmDialog(this,
                        "📚 Bạn có muốn đánh giá sách \"" + bookTitle + "\" vừa mượn không?",
                        "⭐ Gợi ý đánh giá", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    ReviewDialog dialog = new ReviewDialog((JFrame) SwingUtilities.getWindowAncestor(this), userId, bookId, bookTitle);
                    dialog.setVisible(true);
                }
            } catch (IllegalArgumentException e) {
                showError("❌ Lỗi dữ liệu: " + e.getMessage());
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public void showOnlyMembers() {
        tableModel.setColumnIdentifiers(new String[]{"ID", "Tên Thành viên"});
        tableModel.setRowCount(0);

        for (Member m : library.getMembers()) {
            tableModel.addRow(new Object[]{m.getId(), m.getName()});
        }
        memberTable.revalidate();
        memberTable.repaint();
        memberTable.setCursor(Cursor.getDefaultCursor());
    }

    public void showOnlyLoans() {
        tableModel.setColumnIdentifiers(new String[]{"ID", "Tên Thành viên", "Tên sách mượn", "Từ ngày", "Đến ngày"});
        tableModel.setRowCount(0);

        for (Member m : library.getMembers()) {
            for (Loan l : m.getLoans()) {
                Book b = library.findBookById(l.getBookId());
                String title = (b != null) ? b.getTitle() : "Không rõ";
                tableModel.addRow(new Object[]{
                    m.getId(), m.getName(), title,
                    DateUtils.format(l.getStartDate()),
                    DateUtils.format(l.getEndDate())
                });
            }
        }
    }

    public void reloadData() {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        CompletableFuture.runAsync(() -> {
            try {
                CompletableFuture<Void> booksLoad = library.loadBooksAsync();
                CompletableFuture<Void> membersLoad = library.loadMembersAsync();

                CompletableFuture.allOf(booksLoad, membersLoad).thenRunAsync(() -> {
                    SwingUtilities.invokeLater(() -> {
                        showOnlyLoans();
                        setCursor(Cursor.getDefaultCursor());
                    });
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showError("Error reloading data: " + e.getMessage());
                    setCursor(Cursor.getDefaultCursor());
                });
            }
        });
    }
}
