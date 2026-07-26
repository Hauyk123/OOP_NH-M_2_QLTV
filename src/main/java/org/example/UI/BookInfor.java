package org.example.UI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import com.google.zxing.WriterException;
import org.json.JSONObject;
import org.example.controller.ReviewManagement;
import org.example.model.Book;
import org.example.utils.QRCodeUtils;
import org.example.utils.GoogleBooksUtils;

public class BookInfor extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Color PRIMARY_COLOR = new Color(204, 0, 0);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 14);

    private final Book book;
    private String description = "Loading description...";
    private static final int COVER_WIDTH = 300;
    private static final int COVER_HEIGHT = 450;

    public BookInfor(JFrame parent, Book book) {
        super(parent, "Chi tiết sách: " + book.getTitle(), true);
        this.book = book;

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(204, 0, 0), 0, getHeight(), Color.WHITE);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left panel for book cover
        JPanel leftPanel = createCoverPanel();

        // Right panel for QR and details
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setOpaque(false);

        // Top right panel containing QR and details side by side
        JPanel topRightPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topRightPanel.setOpaque(false);

        // Book details panel
        JPanel detailsPanel = createDetailsPanel();

        // QR code panel
        JPanel qrPanel = createQRPanel();

        topRightPanel.add(detailsPanel);
        topRightPanel.add(qrPanel);

        rightPanel.add(topRightPanel, BorderLayout.NORTH);

        // Description panel at bottom
        JPanel descPanel = createDescriptionPanel();
        rightPanel.add(descPanel, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setSize(1000, 600);
        setLocationRelativeTo(parent);

        // Load description asynchronously
        loadBookDescription();
    }

    private JPanel createCoverPanel() {
        JPanel coverPanel = new JPanel();
        coverPanel.setPreferredSize(new Dimension(COVER_WIDTH + 20, COVER_HEIGHT + 20));
        coverPanel.setOpaque(false);
        coverPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Try to get cover from Google Books first
        String coverUrl = GoogleBooksUtils.getBookCoverUrl(book.getIsbn());
        ImageIcon coverIcon = null;

        if (coverUrl != null) {
            try {
                // Load image from URL
                URL url = new URL(coverUrl);
                Image image = ImageIO.read(url);
                if (image != null) {
                    coverIcon = new ImageIcon(image);
                }
            } catch (Exception e) {
                System.out.println("Error loading cover from URL: " + e.getMessage());
            }
        }

        // If Google Books cover failed, try local file
        if (coverIcon == null) {
            String coverPath = "src/main/java/org/example/data/covers/" + book.getId() + ".jpg";
            if (new File(coverPath).exists()) {
                coverIcon = new ImageIcon(coverPath);
            } else {
                // Create default gray cover
                BufferedImage defaultCover = new BufferedImage(COVER_WIDTH, COVER_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = defaultCover.createGraphics();
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 0, COVER_WIDTH, COVER_HEIGHT);
                // Add book title to default cover
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                drawWrappedText(g, book.getTitle(), 10, 50, COVER_WIDTH - 20);
                g.dispose();
                coverIcon = new ImageIcon(defaultCover);
            }
        }

        // Scale the image
        Image scaledImage = coverIcon.getImage().getScaledInstance(COVER_WIDTH, COVER_HEIGHT, Image.SCALE_SMOOTH);
        JLabel coverLabel = new JLabel(new ImageIcon(scaledImage));
        coverLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        coverPanel.add(coverLabel);

        return coverPanel;
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split("\\s+");
        String line = "";
        int lineHeight = fm.getHeight();

        for (String word : words) {
            String testLine = line + (line.isEmpty() ? "" : " ") + word;
            if (fm.stringWidth(testLine) <= maxWidth) {
                line = testLine;
            } else {
                g.drawString(line, x + (maxWidth - fm.stringWidth(line)) / 2, y);
                y += lineHeight;
                line = word;
            }
        }
        if (!line.isEmpty()) {
            g.drawString(line, x + (maxWidth - fm.stringWidth(line)) / 2, y);
        }
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Thông tin sách"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        addStyledLabel(detailsPanel, "ID: " + book.getId());
        addStyledLabel(detailsPanel, "Tựa sách: " + book.getTitle());
        addStyledLabel(detailsPanel, "Tác giả: " + book.getAuthor());
        addStyledLabel(detailsPanel, "ISBN: " + book.getIsbn());
        addStyledLabel(detailsPanel, "Số lượng còn lại: " + book.getQuantity());
        addStyledLabel(detailsPanel, "Đánh giá: " + String.format("%.1f", book.getRating()) + "/5.0");

        return detailsPanel;
    }

    private JPanel createQRPanel() {
        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        qrPanel.setOpaque(false);
        qrPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "QR Code"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        try {
            String googleBooksUrl = "https://www.google.com/search?tbm=bks&q=isbn:" + book.getIsbn();
            Image qrImage = QRCodeUtils.generateQRCodeImage(googleBooksUrl, 150, 150);
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
            qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            qrPanel.add(qrLabel);

            JButton linkButton = new JButton("Mở Google Books");
            linkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            linkButton.addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(googleBooksUrl));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            qrPanel.add(Box.createVerticalStrut(10));
            qrPanel.add(linkButton);
        } catch (WriterException e) {
            qrPanel.add(new JLabel("Không thể tạo mã QR"));
        }

        return qrPanel;
    }

    private JPanel createDescriptionPanel() {
        JPanel descPanel = new JPanel(new BorderLayout(10, 10));
        descPanel.setOpaque(false);
        descPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Mô tả"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JTextArea descArea = new JTextArea(description);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(255, 255, 255, 200));

        JScrollPane scrollPane = new JScrollPane(descArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        descPanel.add(scrollPane);

        return descPanel;
    }

    private void addStyledLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panel.add(label);
    }

    private void loadBookDescription() {
        CompletableFuture.runAsync(() -> {
            try {
                JSONObject bookInfo = GoogleBooksUtils.getBookInfo(book.getIsbn());
                if (bookInfo != null) {
                    description = bookInfo.optString("description", "Không có mô tả.");
                    SwingUtilities.invokeLater(this::updateDescription);
                }
            } catch (Exception e) {
                description = "Không thể tải mô tả sách.";
                SwingUtilities.invokeLater(this::updateDescription);
            }
        });
    }

    private void updateDescription() {
        Component[] components = ((JPanel) getContentPane()).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                updateDescriptionInPanel((JPanel) comp);
            }
        }
    }

    private void updateDescriptionInPanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JTextArea) {
                    ((JTextArea) scrollPane.getViewport().getView()).setText(description);
                    return;
                }
            } else if (comp instanceof JPanel) {
                updateDescriptionInPanel((JPanel) comp);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Book book = new Book("1", "Test Book", "Test Author", 5, "978-0-7535-2243-1");
        BookInfor dialog = new BookInfor(frame, book);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}