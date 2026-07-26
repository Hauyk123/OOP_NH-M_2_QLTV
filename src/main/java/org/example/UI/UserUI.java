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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.example.UI.design.DesignSystem;
import org.example.model.Member;

public class UserUI {

    private final JFrame frame;
    private JTabbedPane tabbedPane;
    private JPanel backgroundPanel;
    private final Member currentUser; // Lưu thông tin người dùng đang đăng nhập

    public UserUI(Member member) {
        this.currentUser = member;
        frame = new JFrame("Hệ Thống Thư Viện - Xin chào: " + currentUser.getName());
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Tạo nền gradient (Sử dụng màu đỏ PTIT để đồng bộ)
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

        // Header Title
        ImageIcon originalIcon = new ImageIcon("src/main/java/org/example/icons/library1.png");
        Image resizedImage = originalIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(resizedImage);
        JLabel titleLabel = new JLabel("THƯ VIỆN PTIT", scaledIcon, SwingConstants.CENTER);
        titleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        titleLabel.setIconTextGap(10);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Cấu hình các Tab trên thanh điều hướng
        setupTabbedPane();

        // Footer buttons (Chỉ giữ lại nút Đăng xuất)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton logoutBtn = new JButton("Đăng xuất");
        DesignSystem.styleButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new LoginUI(); // Quay lại trang đăng nhập
        });

        buttonPanel.add(logoutBtn);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set Icon cửa sổ
        ImageIcon icon = new ImageIcon("src/main/java/org/example/icons/Libarymacdinh.png");
        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        frame.setIconImage(image);

        frame.setVisible(true);
    }

    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);

        // Tab 1: Danh mục sách (Xem và mượn sách)
        UserBookPanel bookPanel = new UserBookPanel(currentUser);
        tabbedPane.addTab("Danh mục sách", bookPanel);

        // Tab 2: Sách đã mượn (Kèm trạng thái Đang mượn / Đã trả)
        UserLoansPanel loansPanel = new UserLoansPanel(currentUser);
        tabbedPane.addTab("Sách đã mượn", loansPanel);

        // Tab 3: Thông tin cá nhân
        UserProfilePanel profilePanel = new UserProfilePanel(currentUser);
        tabbedPane.addTab("Thông tin cá nhân", profilePanel);

        // Lắng nghe sự kiện chuyển tab để tự động cập nhật lại dữ liệu mới nhất
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() instanceof UserLoansPanel) {
                loansPanel.reloadLoans();
            } else if (tabbedPane.getSelectedComponent() instanceof UserBookPanel) {
                bookPanel.reloadBooks();
            }
        });

        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    // Panel con hiển thị thông tin cá nhân ngay trên thanh điều hướng
    private static class UserProfilePanel extends JPanel {
        public UserProfilePanel(Member currentUser) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

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
            JLabel titleLabel = new JLabel("THÔNG TIN CHI TIẾT TÀI KHOẢN", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            headerPanel.add(titleLabel, BorderLayout.CENTER);
            add(headerPanel, BorderLayout.NORTH);

            JPanel infoPanel = new JPanel(new GridLayout(4, 1, 15, 15));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));
            infoPanel.setOpaque(false);

            JLabel idLbl = new JLabel("📌 Mã thành viên: " + currentUser.getId());
            JLabel nameLbl = new JLabel("👤 Họ và tên: " + currentUser.getName());
            JLabel userLbl = new JLabel("🔑 Tên đăng nhập: " + currentUser.getUsername());
            JLabel countLbl = new JLabel("📚 Số lượng sách đang mượn: " + currentUser.getLoans().size());

            Font font = new Font("Arial", Font.PLAIN, 16);
            idLbl.setFont(font);
            nameLbl.setFont(font);
            userLbl.setFont(font);
            countLbl.setFont(font);

            infoPanel.add(idLbl);
            infoPanel.add(nameLbl);
            infoPanel.add(userLbl);
            infoPanel.add(countLbl);

            JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            centerWrapper.setOpaque(false);
            centerWrapper.add(infoPanel);
            add(centerWrapper, BorderLayout.CENTER);
        }
    }
}