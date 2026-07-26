package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.example.UI.design.DesignSystem;
import org.example.model.Librarian;

public class LibraryUI {

    private final JFrame frame;
    private JTabbedPane tabbedPane;
    private JPanel backgroundPanel;
    private final Librarian currentLibrarian; // Lưu thông tin thủ thư đang đăng nhập

    public LibraryUI(Librarian librarian) {
        this.currentLibrarian = librarian;
        frame = new JFrame("QUẢN LÝ THƯ VIỆN PTIT - Thủ thư: " + currentLibrarian.getName());
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Create a panel with gradient background
        backgroundPanel = new JPanel() {
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
        backgroundPanel.setLayout(new BorderLayout());
        frame.setContentPane(backgroundPanel);

        // Title panel
        ImageIcon originalIcon = new ImageIcon("src/main/java/org/example/icons/library1.png");
        Image resizedImage = originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(resizedImage);

        JLabel titleLabel = new JLabel("QUẢN LÝ THƯ VIỆN PTIT", scaledIcon, SwingConstants.CENTER);
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        titleLabel.setIconTextGap(10);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Setup tabbed pane
        setupTabbedPane();

        // Footer button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton exitBtn = new JButton("Thoát");
        exitBtn.addActionListener(e -> System.exit(0));

        // Nút đổi mật khẩu cho thủ thư
        JButton changePassBtn = new JButton("Đổi mật khẩu");
        DesignSystem.styleButton(changePassBtn);
        changePassBtn.addActionListener(e -> showChangePasswordDialog());

        // Add refresh button
        JButton refreshBtn = new JButton("Làm mới");
        DesignSystem.styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> {
            int selectedTab = tabbedPane.getSelectedIndex();
            switch (selectedTab) {
                case 0 ->
                        ((BookPanel) tabbedPane.getComponentAt(0)).reloadBooks();
                case 1 ->
                        ((LoanAndMemberPanel) tabbedPane.getComponentAt(1)).reloadData();
                case 2 ->
                        ((ReviewPanel) tabbedPane.getComponentAt(2)).loadReviewsToTable();
            }
        });

        buttonPanel.add(changePassBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exitBtn);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set frame icon
        ImageIcon icon = new ImageIcon("src/main/java/org/example/icons/Libarymacdinh.png");
        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        frame.setIconImage(image);

        frame.setVisible(true);
    }

    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);

        // Khởi tạo BookPanel ngay từ đầu
        BookPanel bookPanel = new BookPanel();
        bookPanel.reloadBooks(); // Load data ngay lập tức

        // Add các panel
        tabbedPane.addTab("📚 Quản lý Sách", bookPanel);
        tabbedPane.addTab("👥 Thành viên & Mượn Trả", new JPanel());
        tabbedPane.addTab("⭐ Xem đánh giá sách", new JPanel());

        // Add change listener chỉ cho tab 2 và 3
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            Component currentComponent = tabbedPane.getComponentAt(selectedIndex);

            // Skip nếu là BookPanel hoặc đã được khởi tạo
            if (selectedIndex == 0 || currentComponent instanceof LoanAndMemberPanel
                    || currentComponent instanceof ReviewPanel) {
                return;
            }

            LoadingDialog loadingDialog = new LoadingDialog(frame, "Đang tải...");

            SwingWorker<Component, Void> worker = new SwingWorker<>() {
                @Override
                protected Component doInBackground() {
                    return switch (selectedIndex) {
                        case 1 ->
                                new LoanAndMemberPanel();
                        case 2 ->
                                new ReviewPanel();
                        default ->
                                new JPanel();
                    };
                }

                @Override
                protected void done() {
                    try {
                        Component newComponent = get();
                        tabbedPane.setComponentAt(selectedIndex, newComponent);
                        loadingDialog.dispose();
                    } catch (Exception ex) {
                        loadingDialog.dispose();
                        JOptionPane.showMessageDialog(frame,
                                "Lỗi khi tải tab: " + ex.getMessage());
                    }
                }
            };

            worker.execute();
            loadingDialog.setVisible(true);
        });

        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    // Hàm hiển thị hộp thoại đổi mật khẩu cho thủ thư
    private void showChangePasswordDialog() {
        JPasswordField oldPassField = new JPasswordField(15);
        JPasswordField newPassField = new JPasswordField(15);
        JPasswordField confirmPassField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Mật khẩu cũ:"));
        panel.add(oldPassField);
        panel.add(new JLabel("Mật khẩu mới:"));
        panel.add(newPassField);
        panel.add(new JLabel("Xác nhận mật khẩu mới:"));
        panel.add(confirmPassField);

        int result = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Đổi mật khẩu thủ thư: " + currentLibrarian.getUsername(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String oldPass = new String(oldPassField.getPassword()).trim();
            String newPass = new String(newPassField.getPassword()).trim();
            String confirmPass = new String(confirmPassField.getPassword()).trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!currentLibrarian.getPassword().equals(oldPass)) {
                JOptionPane.showMessageDialog(frame, "Mật khẩu cũ không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(frame, "Mật khẩu mới và xác nhận không khớp nhau!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = Librarian.changePassword(currentLibrarian.getUsername(), oldPass, newPass);
            if (success) {
                currentLibrarian.setPassword(newPass);
                JOptionPane.showMessageDialog(frame, "Đổi mật khẩu thành công!");
            } else {
                JOptionPane.showMessageDialog(frame, "Đổi mật khẩu thất bại, vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}