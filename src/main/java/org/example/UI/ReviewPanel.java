package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.example.UI.design.DesignSystem;
import org.example.controller.ReviewManagement;
import org.example.model.Review;
import org.example.controller.LibraryManagement;
import org.example.model.Book;

public class ReviewPanel extends JPanel {

    private static final Font UNICODE_FONT = new Font("Arial Unicode MS", Font.PLAIN, 14);
    private final LibraryManagement library;
    private final JTable reviewTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public ReviewPanel() {
        this.library = LibraryManagement.getInstance();

        // Apply main panel styling
        DesignSystem.styleMainPanel(this);

        // Header
        ImageIcon originalIcon = new ImageIcon("src/main/java/org/example/icons/reading.png");
        Image resizedImage = originalIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(resizedImage);
        add(DesignSystem.createHeaderLabel("DANH SÁCH ĐÁNH GIÁ SÁCH", scaledIcon), BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Mã sách", "Tên sách", "Mã người dùng", "Số sao", "Bình luận"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reviewTable = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        reviewTable.setRowSorter(sorter);
        DesignSystem.styleTable(reviewTable, 100, 120, 80, 500);
        add(DesignSystem.createTableScrollPane(reviewTable), BorderLayout.CENTER);

        DesignSystem.styleTable(reviewTable);
        // Cấu hình tỷ lệ phần trăm cho các cột (tổng = 100%)
        DesignSystem.fixTableColumnWidth(reviewTable, 10, 25, 15, 10, 40);
        JScrollPane scrollPane = DesignSystem.createTableScrollPane(reviewTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = new JPanel();
        DesignSystem.styleSearchPanel(searchPanel);
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Tìm theo mã sách");

        DesignSystem.styleTextField(searchField);
        DesignSystem.styleButton(searchBtn);

        searchPanel.add(new JLabel("Nhập mã sách:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        searchBtn.addActionListener(e -> filterReviewsByBookId(searchField.getText().trim()));

        add(searchPanel, BorderLayout.SOUTH);

        loadReviewsToTable();
    }

    public void loadReviewsToTable() {
        List<Review> reviews = ReviewManagement.loadReviews();
        updateTable(reviews);
    }

    private void filterReviewsByBookId(String bookId) {
        List<Review> all = ReviewManagement.loadReviews();
        List<Review> filtered = all.stream()
                .filter(r -> r.getBookId().equalsIgnoreCase(bookId))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            filtered = all; // If no match, show all reviews
        }
        updateTable(filtered);
    }

    private void updateTable(List<Review> reviews) {
        tableModel.setRowCount(0);
        for (Review r : reviews) {
            // Lookup book title from bookId
            String bookTitle = "Không rõ";
            Book book = library.findBookById(r.getBookId());
            if (book != null) {
                bookTitle = book.getTitle();
            }

            tableModel.addRow(new Object[]{
                r.getBookId(),
                bookTitle,
                r.getUserId(),
                r.getRating(),
                r.getComment() // No need for UTF-8 conversion here
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
