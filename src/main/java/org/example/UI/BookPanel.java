package org.example.UI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.example.UI.design.DesignSystem;
import org.example.controller.LibraryManagement;
import org.example.controller.ReviewManagement;
import org.example.model.Book;
import org.example.utils.GoogleBooksUtils;

import javax.swing.*;
import java.awt.*;

public class BookPanel extends JPanel {

    private final LibraryManagement library;
    private final JTable bookTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JPopupMenu popupMenu;
    private TableRowSorter<DefaultTableModel> sorter;

    public BookPanel() {
        // Initialize all final fields first
        this.library = LibraryManagement.getInstance();

        // Initialize search components
        this.searchField = new JTextField(20);

        // Initialize table components
        String[] columns = {"Mã sách", "Tên sách", "Tác giả", "Số lượng", "ISBN", "Rating"};
        this.tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.bookTable = new JTable(tableModel);
        DesignSystem.styleTable(bookTable);
        // Cấu hình tỷ lệ phần trăm cho các cột (tổng = 100%)
        DesignSystem.fixTableColumnWidth(bookTable, 10, 35, 25, 10, 10, 10);
        JScrollPane scrollPane = DesignSystem.createTableScrollPane(bookTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        this.popupMenu = new JPopupMenu();
        sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        // Now call initialization method
        initializeComponents();

        // Load initial data
        loadInitialData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));

        // Create header panel with gradient background
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

        // Create title with icon
        JLabel titleLabel = new JLabel("QUẢN LÝ SÁCH", new ImageIcon(new ImageIcon("src/main/java/org/example/icons/stack-of-books.png")
                .getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Create search panel with transparent background
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        searchField.setPreferredSize(new Dimension(300, 30));
        DesignSystem.styleTextField(searchField);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Tìm kiếm");
        searchBtn.setPreferredSize(new Dimension(100, 30));
        DesignSystem.styleButton(searchBtn);
        searchBtn.addActionListener(e -> searchBooks());
        searchPanel.add(searchBtn);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        // Add header panel
        add(headerPanel, BorderLayout.NORTH);

        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Create button panel with transparent background
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);

        // Create and style buttons (Đã thêm nút Sửa sách)
        JButton[] buttons = {
                new JButton("Thêm sách"),
                new JButton("Sửa sách"),
                new JButton("Sắp xếp theo ID"),
                new JButton("Gợi ý sách"),
                new JButton("Xóa sách"),
                new JButton("Cập nhật số lượng")
        };

        for (JButton button : buttons) {
            button.setPreferredSize(new Dimension(150, 35));
            DesignSystem.styleButton(button);
            buttonPanel.add(button);
        }

        // Add action listeners
        buttons[0].addActionListener(e -> showAddBookDialog());
        buttons[1].addActionListener(e -> showEditBookDialog()); // Sự kiện cho nút Sửa sách
        buttons[2].addActionListener(e -> sortBooksByID());
        buttons[3].addActionListener(e -> showRecommendations());
        buttons[4].addActionListener(e -> deleteSelectedBook());
        buttons[5].addActionListener(e -> showQuantityDialog());

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        // Setup popup menu
        JMenuItem editItem = new JMenuItem("Sửa thông tin sách");
        editItem.addActionListener(e -> showEditBookDialog());
        popupMenu.add(editItem);

        JMenuItem adjustQuantityItem = new JMenuItem("Điều chỉnh số lượng");
        adjustQuantityItem.addActionListener(e -> showQuantityDialog());
        popupMenu.add(adjustQuantityItem);

        // Add mouse listeners for table
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showBookDetails();
                } else if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
        });
    }

    private void loadInitialData() {
        List<Book> books = library.getBooks();
        if (books != null && !books.isEmpty()) {
            updateTable(books);
        }
    }

    private void showPopupMenu(MouseEvent e) {
        int row = bookTable.rowAtPoint(e.getPoint());
        if (row >= 0) {
            bookTable.setRowSelectionInterval(row, row);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void showAddBookDialog() {
        JTextField isbnField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField quantityField = new JTextField("1", 5);
        JButton fetchButton = new JButton("Tìm sách");

        DesignSystem.styleTextField(isbnField);
        DesignSystem.styleTextField(titleField);
        DesignSystem.styleTextField(authorField);
        DesignSystem.styleTextField(quantityField);
        DesignSystem.styleButton(fetchButton);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);
        gbc.gridx = 2;
        panel.add(fetchButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Tác giả:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        fetchButton.addActionListener(e -> {
            String isbn = isbnField.getText().trim();
            if (!isbn.isEmpty()) {
                Book book = GoogleBooksUtils.fetchByISBN(isbn);
                if (book != null) {
                    titleField.setText(book.getTitle());
                    authorField.setText(book.getAuthor());
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Không tìm thấy sách với ISBN này!");
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm sách mới",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = library.generateNextBookId();
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                String isbn = isbnField.getText().trim();

                if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "❌ Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                Book book = new Book(id, title, author, quantity, isbn);
                library.addBook(book);
                library.saveBooksAsync();
                reloadBooks();
                JOptionPane.showMessageDialog(this, "✅ Đã thêm sách thành công!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Số lượng không hợp lệ!");
            }
        }
    }

    // Hàm mở Dialog Sửa thông tin sách
    private void showEditBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách cần sửa trên bảng!");
            return;
        }

        String bookId = (String) bookTable.getValueAt(selectedRow, 0);
        Book book = library.findBookById(bookId);
        if (book == null) return;

        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JTextField isbnField = new JTextField(book.getIsbn(), 20);
        JTextField quantityField = new JTextField(String.valueOf(book.getQuantity()), 5);

        DesignSystem.styleTextField(titleField);
        DesignSystem.styleTextField(authorField);
        DesignSystem.styleTextField(isbnField);
        DesignSystem.styleTextField(quantityField);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Mã sách (ID):"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(book.getId()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tên sách:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Tác giả:"), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        panel.add(isbnField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa thông tin sách",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String isbn = isbnField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "❌ Vui lòng không để trống thông tin!");
                    return;
                }

                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this, "❌ Số lượng không được nhỏ hơn 0!");
                    return;
                }

                book.setTitle(title);
                book.setAuthor(author);
                book.setIsbn(isbn);
                book.setQuantity(quantity);

                library.saveBooksAsync();
                reloadBooks();
                JOptionPane.showMessageDialog(this, "✅ Cập nhật thông tin sách thành công!");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Số lượng phải là một số nguyên hợp lệ!");
            }
        }
    }

    private void searchBooks() {
        String keyword = searchField.getText().toLowerCase().trim();
        List<Book> results = library.getBooks().stream()
                .filter(book
                        -> book.getId().toLowerCase().contains(keyword)
                        || book.getTitle().toLowerCase().contains(keyword)
                        || book.getIsbn().contains(keyword))
                .collect(Collectors.toList());
        updateTable(results);
    }

    private void showRecommendations() {
        List<Book> books = library.getBooks();
        Map<Book, Double> scores = new HashMap<>();

        for (Book book : books) {
            double rating = ReviewManagement.getAverageRating(book.getId());
            int borrowCount = book.getBorrowCount();

            double normalizedRating = rating / 5.0;
            double normalizedBorrowCount = borrowCount / (double) getMaxBorrowCount(books);
            double score = (normalizedRating * 0.6) + (normalizedBorrowCount * 0.4);

            scores.put(book, score);
        }

        List<Book> recommendations = scores.entrySet().stream()
                .sorted(Map.Entry.<Book, Double>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!recommendations.isEmpty()) {
            StringBuilder message = new StringBuilder("📚 Top 5 sách được đề xuất:\n\n");
            for (Book book : recommendations) {
                double rating = ReviewManagement.getAverageRating(book.getId());
                message.append(String.format("- %s (%.1f)\n",
                        book.getTitle(), rating, book.getBorrowCount()));
            }
            JOptionPane.showMessageDialog(this, message.toString());
        }
    }

    private int getMaxBorrowCount(List<Book> books) {
        return books.stream()
                .mapToInt(Book::getBorrowCount)
                .max()
                .orElse(1);
    }

    private void showQuantityDialog() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách!");
            return;
        }

        String bookId = (String) bookTable.getValueAt(selectedRow, 0);
        Book book = library.findBookById(bookId);

        String input = JOptionPane.showInputDialog(this,
                "Nhập số lượng mới cho sách: " + book.getTitle(),
                book.getQuantity());

        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQuantity = Integer.parseInt(input.trim());
                if (newQuantity >= 0) {
                    book.setQuantity(newQuantity);
                    library.saveBooksAsync();
                    reloadBooks();
                } else {
                    JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn hoặc bằng 0!");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách!");
            return;
        }

        String bookId = (String) bookTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sách này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            library.deleteBook(bookId);
            library.saveBooksAsync();
            reloadBooks();
        }
    }

    private void sortBooksByID() {
        List<Book> books = library.getBooks();
        books.sort((b1, b2) -> b1.getId().compareTo(b2.getId()));
        updateTable(books);
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
                    rating > 0 ? String.format("%.1f", rating) : "Chưa có",});
        }
    }

    public void reloadBooks() {
        tableModel.setRowCount(0);

        List<Book> books = library.getBooks();
        if (books != null && !books.isEmpty()) {
            for (Book book : books) {
                Object[] row = {
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getQuantity(),
                        book.getIsbn(),
                        String.format("%.1f", book.getRating())
                };
                tableModel.addRow(row);
            }
        }

        bookTable.revalidate();
        bookTable.repaint();
    }

    private void showBookDetails() {
        int row = bookTable.getSelectedRow();
        if (row >= 0) {
            String bookId = (String) bookTable.getValueAt(row, 0);
            Book book = library.findBookById(bookId);
            if (book != null) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                BookInfor dialog = new BookInfor(parentFrame, book);
                dialog.setLocationRelativeTo(parentFrame);
                dialog.setVisible(true);
            }
        }
    }
}