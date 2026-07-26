package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.example.UI.design.DesignSystem;
import org.example.controller.ReviewManagement;
import org.example.model.Review;

/**
 * Cửa sổ dialog cho phép người dùng đánh giá và viết bình luận cho một cuốn
 * sách. Dialog này hiển thị giao diện với thanh đánh giá sao và ô nhập bình
 * luận.
 */
public class ReviewDialog extends JDialog {

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

    /**
     * Khởi tạo dialog đánh giá sách mới.
     *
     * @param parent Frame cha chứa dialog này
     * @param userId Mã người dùng đang thực hiện đánh giá
     * @param bookId Mã sách được đánh giá
     * @param bookTitle Tên sách được đánh giá
     */
    public ReviewDialog(JFrame parent, String userId, String bookId, String bookTitle) {
        super(parent, "Đánh giá sách: " + bookTitle, true);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(DesignSystem.BACKGROUND_COLOR);

        // Header Panel với style mới
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(DesignSystem.PRIMARY_COLOR);
        JLabel titleLabel = new JLabel("Đánh giá: " + bookTitle);
        titleLabel.setFont(DesignSystem.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Rating Panel
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.setBackground(BACKGROUND_COLOR);
        JLabel ratingLabel = new JLabel("Số sao:");
        ratingLabel.setFont(LABEL_FONT);
        JTextField ratingField = new JTextField(5);
        ratingField.setFont(LABEL_FONT);

        // Star preview panel
        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        starsPanel.setBackground(BACKGROUND_COLOR);
        JLabel[] stars = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            stars[i] = new JLabel("☆");
            stars[i].setFont(new Font("Arial", Font.BOLD, 20));
            final int rating = i + 1;
            stars[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ratingField.setText(String.valueOf(rating));
                    updateStars(stars, rating);
                }
            });
            starsPanel.add(stars[i]);
        }

        // Comment Panel
        JPanel commentPanel = new JPanel(new BorderLayout(5, 5));
        commentPanel.setBackground(BACKGROUND_COLOR);
        JLabel commentLabel = new JLabel("Bình luận:");
        commentLabel.setFont(LABEL_FONT);
        JTextArea commentArea = new JTextArea(5, 30);
        commentArea.setFont(DesignSystem.BODY_FONT);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));

        // Submit Button
        JButton submitButton = new JButton("Gửi đánh giá");
        submitButton.setFont(LABEL_FONT);
        submitButton.setBackground(PRIMARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> {
            try {
                int rating = Integer.parseInt(ratingField.getText());
                String comment = commentArea.getText().trim();

                if (rating < 1 || rating > 5 || comment.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nhập đúng số sao (1-5) và bình luận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Sửa lại thứ tự tham số cho đúng với constructor của Review
                Review review = new Review(bookId, userId, rating, comment);
                ReviewManagement.addReview(review);
                JOptionPane.showMessageDialog(this, "✅ Đã gửi đánh giá!");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "❌ Số sao phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Style text areas và buttons
        DesignSystem.styleTextField(ratingField);
        DesignSystem.styleButton(submitButton);

        // Style star labels
        for (JLabel star : stars) {
            star.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
            star.setForeground(DesignSystem.PRIMARY_COLOR);
        }

        // Layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        mainPanel.add(ratingLabel, gbc);
        gbc.gridy++;
        mainPanel.add(starsPanel, gbc);
        gbc.gridy++;
        mainPanel.add(commentLabel, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollPane, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    /**
     * Cập nhật hiển thị các ngôi sao đánh giá dựa trên số sao được chọn.
     *
     * @param stars Mảng các label hiển thị sao
     * @param rating Số sao được chọn (1-5)
     */
    private void updateStars(JLabel[] stars, int rating) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText(i < rating ? "★" : "☆");
            stars[i].setForeground(i < rating ? PRIMARY_COLOR : Color.GRAY);
        }
    }
}
