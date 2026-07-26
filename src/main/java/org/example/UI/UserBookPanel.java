package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.example.UI.design.DesignSystem;
import org.example.controller.LibraryManagement;
import org.example.controller.LoanManagement;
import org.example.controller.ReviewManagement;
import org.example.model.Book;
import org.example.model.Member;
import org.example.utils.DateUtils;

public class UserBookPanel extends JPanel {
    private final LibraryManagement library;
    private final Member currentUser;
    private final JTable bookTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public UserBookPanel(Member currentUser) {
        this.currentUser = currentUser;
        this.library = LibraryManagement.getInstance();
        this.searchField = new JTextField(20);

        String[] columns = {"Mã sách", "Tên sách", "Tác giả", "Số lượng", "ISBN", "Rating"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.bookTable = new JTable(tableModel);
        DesignSystem.styleTable(bookTable);
        DesignSystem.fixTableColumnWidth(bookTable, 10, 35, 25, 10, 10, 10);

        sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        initializeComponents();
        reloadBooks();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        // Header Panel với màu nền đỏ Gradient
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

        JLabel titleLabel = new JLabel("DANH MỤC SÁCH", new ImageIcon(new ImageIcon("src/main/java/org/example/icons/stack-of-books.png")
                .getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        DesignSystem.styleTextField(searchField);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Tìm kiếm");
        DesignSystem.styleButton(searchBtn);
        searchBtn.addActionListener(e -> searchBooks());
        searchPanel.add(searchBtn);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Bảng dữ liệu
        JScrollPane scrollPane = DesignSystem.createTableScrollPane(bookTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Nút chức năng phía dưới
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton detailsBtn = new JButton("Chi tiết");
        JButton borrowBtn = new JButton("Mượn sách");

        DesignSystem.styleButton(detailsBtn);
        DesignSystem.styleButton(borrowBtn);

        detailsBtn.addActionListener(e -> showBookDetails());
        borrowBtn.addActionListener(e -> showBorrowDialog());

        buttonPanel.add(detailsBtn);
        buttonPanel.add(borrowBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Double click vào sách để xem chi tiết
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showBookDetails();
                }
            }
        });
    }

    private void searchBooks() {
        String keyword = searchField.getText().toLowerCase().trim();
        List<Book> results = library.getBooks().stream()
                .filter(book -> book.getId().toLowerCase().contains(keyword)
                        || book.getTitle().toLowerCase().contains(keyword)
                        || book.getIsbn().contains(keyword))
                .collect(Collectors.toList());
        updateTable(results);
    }

    private void updateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            double rating = ReviewManagement.getAverageRating(book.getId());
            tableModel.addRow(new Object[]{
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getQuantity(),
                    book.getIsbn(),
                    rating > 0 ? String.format("%.1f", rating) : "Chưa có"
            });
        }
    }

    public void reloadBooks() {
        updateTable(library.getBooks());
    }

    private void showBookDetails() {
        int row = bookTable.getSelectedRow();
        if (row >= 0) {
            String bookId = (String) bookTable.getValueAt(row, 0);
            Book book = library.findBookById(bookId);
            if (book != null) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                BookInfor dialog = new BookInfor(parentFrame, book);
                dialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách để xem chi tiết!");
        }
    }

    private void showBorrowDialog() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách để mượn!");
            return;
        }

        String bookId = (String) bookTable.getValueAt(row, 0);
        Book book = library.findBookById(bookId);

        if (book == null || book.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Sách này hiện đã hết số lượng khả dụng!");
            return;
        }

        JTextField startField = new JTextField("dd/MM/yyyy");
        JTextField endField = new JTextField("dd/MM/yyyy");
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Tên sách:"));
        panel.add(new JLabel(book.getTitle()));
        panel.add(new JLabel("Ngày mượn:"));
        panel.add(startField);
        panel.add(new JLabel("Ngày hẹn trả:"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Xác nhận mượn sách", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Date startDate = DateUtils.parse(startField.getText().trim());
                Date endDate = DateUtils.parse(endField.getText().trim());

                if (startDate == null || endDate == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng ngày (dd/MM/yyyy)!");
                    return;
                }

                LoanManagement.borrowBook(library, currentUser.getId(), bookId, startDate, endDate);
                reloadBooks(); // Cập nhật lại số lượng sách hiển thị
                JOptionPane.showMessageDialog(this, "Mượn sách thành công!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}