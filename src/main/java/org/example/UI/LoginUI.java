package org.example.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.example.model.Librarian;

public class LoginUI {

    private JFrame frame;
    private JTextField userField;
    private JPasswordField passField;
    private boolean passwordVisible = false;

    public LoginUI() {
        frame = new JFrame("Hệ thống Quản lý Thư viện");
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
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

        JLabel title = new JLabel("Chào mừng đến với Hệ thống Quản lý Thư viện", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(25, 25, 112));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setOpaque(false);

        userField = new JTextField();
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        userField.setBackground(new Color(245, 245, 245));
        userField.setFont(new Font("Arial", Font.PLAIN, 14));

        passField = new JPasswordField();
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        passField.setBackground(new Color(245, 245, 245));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));

        ImageIcon userIcon = new ImageIcon("src/main/java/org/example/icons/user.png");
        ImageIcon lockIcon = new ImageIcon("src/main/java/org/example/icons/lock.png");
        ImageIcon eyeIcon = new ImageIcon("src/main/java/org/example/icons/eye.png");
        ImageIcon eyeOffIcon = new ImageIcon("src/main/java/org/example/icons/eyeoff.png");

        JLabel userLabel = new JLabel(new ImageIcon(userIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        JLabel passLabel = new JLabel(new ImageIcon(lockIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));

        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        passLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JButton togglePassword = new JButton(new ImageIcon(eyeOffIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        togglePassword.setPreferredSize(new Dimension(40, 40));
        togglePassword.setBorderPainted(false);
        togglePassword.setContentAreaFilled(false);
        togglePassword.setFocusPainted(false);

        togglePassword.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            passField.setEchoChar(passwordVisible ? (char) 0 : '\u2022');
            Image iconImg = (passwordVisible ? eyeIcon : eyeOffIcon).getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            togglePassword.setIcon(new ImageIcon(iconImg));
        });

        JLabel usernameTextLabel = new JLabel("Tên đăng nhập");
        usernameTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameTextLabel.setForeground(new Color(25, 25, 112));
        usernameTextLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setOpaque(false);
        userPanel.add(userLabel, BorderLayout.WEST);
        userPanel.add(userField, BorderLayout.CENTER);
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel passwordTextLabel = new JLabel("Mật khẩu");
        passwordTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordTextLabel.setForeground(new Color(25, 25, 112));
        passwordTextLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel passPanel = new JPanel(new BorderLayout(10, 10));
        passPanel.setOpaque(false);
        passPanel.add(passLabel, BorderLayout.WEST);
        passPanel.add(passField, BorderLayout.CENTER);
        passPanel.add(togglePassword, BorderLayout.EAST);
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton loginBtn = new JButton("Đăng nhập");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setBackground(new Color(204, 0, 0));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton registerBtn = new JButton("Đăng ký");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.setBackground(new Color(255, 165, 0));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> showSignupUI());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        centerPanel.add(usernameTextLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(userPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(passwordTextLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonPanel);

        frame.add(title, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void login() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        // 1. Kiểm tra tài khoản Thủ thư (Librarian) trước
        Librarian librarian = Librarian.findLibrarian(username);

        if (librarian != null && librarian.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(frame, "✅ Đăng nhập thành công (Quyền Thủ thư)!");
            frame.dispose();

            LibraryUI ui = new LibraryUI(librarian);
            BookPanel bookPanel = new BookPanel();
            bookPanel.reloadBooks();
            ui.getTabbedPane().setComponentAt(0, bookPanel);
            return; // Đăng nhập thành công thì thoát hàm
        }

        // 2. Nếu không phải thủ thư, kiểm tra tiếp xem có phải Người dùng (Member) không
        org.example.controller.LibraryManagement library = org.example.controller.LibraryManagement.getInstance();
        org.example.model.Member loggedInMember = null;

        // Duyệt qua danh sách thành viên để tìm tài khoản khớp
        for (org.example.model.Member m : library.getMembers()) {
            // Kiểm tra khác null để tránh lỗi do dữ liệu trống trong file txt
            if (m.getUsername() != null && m.getPassword() != null &&
                    m.getUsername().equals(username) && m.getPassword().equals(password)) {
                loggedInMember = m;
                break;
            }
        }

        // 3. Xử lý kết quả đăng nhập cho Member
        if (loggedInMember != null) {
            JOptionPane.showMessageDialog(frame, "✅ Đăng nhập thành công! Xin chào " + loggedInMember.getName());
            frame.dispose();

            // Gọi giao diện UserUI dành riêng cho người dùng
            new UserUI(loggedInMember);

        } else {
            JOptionPane.showMessageDialog(frame, "❌ Sai tài khoản hoặc mật khẩu!");
        }
    }
    private void showSignupUI() {
        frame.setVisible(false);
        new SignupUI(frame);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}

// Class SignupUI
class SignupUI {

    private JFrame frame;
    private JFrame loginFrame;
    private JTextField nameField, phoneField, usernameField;
    private JPasswordField passwordField;
    private boolean passwordVisible = false;

    public SignupUI(JFrame loginFrame) {
        this.loginFrame = loginFrame;

        frame = new JFrame("Library Management - Sign Up");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
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

        JLabel title = new JLabel("Đăng ký tài khoản", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(25, 25, 112));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Giảm khoảng cách trên/dưới

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50)); // Giảm padding trên/dưới
        centerPanel.setOpaque(false);

        // Name field
        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        nameField.setBackground(new Color(245, 245, 245));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Phone field
        phoneField = new JTextField();
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        phoneField.setBackground(new Color(245, 245, 245));
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Username field
        usernameField = new JTextField();
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        usernameField.setBackground(new Color(245, 245, 245));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Password field
        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 0, 0), 2, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        passwordField.setBackground(new Color(245, 245, 245));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));

        ImageIcon userIcon = new ImageIcon("src/main/java/org/example/icons/user.png");
        ImageIcon phoneIcon = new ImageIcon("src/main/java/org/example/icons/phone.png");
        ImageIcon lockIcon = new ImageIcon("src/main/java/org/example/icons/lock.png");
        ImageIcon eyeIcon = new ImageIcon("src/main/java/org/example/icons/eye.png");
        ImageIcon eyeOffIcon = new ImageIcon("src/main/java/org/example/icons/eyeoff.png");

        JLabel nameLabel = new JLabel(new ImageIcon(userIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        JLabel phoneLabel = new JLabel(new ImageIcon(phoneIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        JLabel usernameLabel = new JLabel(new ImageIcon(userIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        JLabel passwordLabel = new JLabel(new ImageIcon(lockIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));

        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        phoneLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JButton togglePassword = new JButton(new ImageIcon(eyeOffIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        togglePassword.setPreferredSize(new Dimension(30, 30));
        togglePassword.setBorderPainted(false);
        togglePassword.setContentAreaFilled(false);
        togglePassword.setFocusPainted(false);

        togglePassword.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            passwordField.setEchoChar(passwordVisible ? (char) 0 : '\u2022');
            Image iconImg = (passwordVisible ? eyeIcon : eyeOffIcon).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            togglePassword.setIcon(new ImageIcon(iconImg));
        });

        JLabel nameTextLabel = new JLabel("Họ và tên");
        nameTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameTextLabel.setForeground(new Color(25, 25, 112));
        nameTextLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel namePanel = new JPanel(new BorderLayout(8, 8));
        namePanel.setOpaque(false);
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(nameField, BorderLayout.CENTER);
        namePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel phoneTextLabel = new JLabel("Số điện thoại");
        phoneTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        phoneTextLabel.setForeground(new Color(25, 25, 112));
        phoneTextLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel phonePanel = new JPanel(new BorderLayout(8, 8));
        phonePanel.setOpaque(false);
        phonePanel.add(phoneLabel, BorderLayout.WEST);
        phonePanel.add(phoneField, BorderLayout.CENTER);
        phonePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel usernameTextLabel = new JLabel("Tên đăng nhập");
        usernameTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameTextLabel.setForeground(new Color(25, 25, 112));
        usernameTextLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel usernamePanel = new JPanel(new BorderLayout(8, 8));
        usernamePanel.setOpaque(false);
        usernamePanel.add(usernameLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        usernamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        centerPanel.add(nameTextLabel);

        JLabel passwordTextLabel = new JLabel("Mật khẩu");
        passwordTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordTextLabel.setForeground(new Color(25, 25, 112));
        passwordTextLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel passwordPanel = new JPanel(new BorderLayout(8, 8));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePassword, BorderLayout.EAST);
        passwordPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton signupBtn = new JButton("Đăng ký");
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.setBackground(new Color(255, 165, 0));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signupBtn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupBtn.setPreferredSize(new Dimension(150, 40));

        signupBtn.addActionListener(e -> signup());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(signupBtn);

        centerPanel.add(nameTextLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(namePanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(phoneTextLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(phonePanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(usernameTextLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(usernamePanel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(passwordTextLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(passwordPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(buttonPanel);

        frame.add(title, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                loginFrame.setVisible(true);
            }
        });
    }

    private void signup() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (name.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "❌ Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra tính hợp lệ của username (không chứa dấu phẩy)
        if (username.contains(",")) {
            JOptionPane.showMessageDialog(frame, "❌ Tên đăng nhập không được chứa dấu phẩy!");
            return;
        }

        // Kiểm tra tính hợp lệ của các trường khác (không chứa dấu phẩy)
        if (name.contains(",") || phone.contains(",") || password.contains(",")) {
            JOptionPane.showMessageDialog(frame, "❌ Thông tin không được chứa dấu phẩy!");
            return;
        }

        // Kiểm tra username đã tồn tại
        if (Librarian.isUsernameExists(username)) {
            JOptionPane.showMessageDialog(frame, "❌ Tên đăng nhập đã tồn tại!");
            return;
        }

        Librarian librarian = new Librarian(name, username, password, phone);
        if (librarian.saveLibrarianSafely()) {
            JOptionPane.showMessageDialog(frame, "✅ Đăng ký thành công! Vui lòng đăng nhập.");
            frame.dispose();
            loginFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Đăng ký thất bại! Vui lòng thử lại.");
        }
    }
}
