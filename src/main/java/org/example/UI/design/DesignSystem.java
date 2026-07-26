package org.example.UI.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class DesignSystem {

    // Colors
    public static final Color PRIMARY_COLOR = new Color(0, 119, 182);    // Blue
    public static final Color SECONDARY_COLOR = new Color(66, 165, 245);  // Light Blue
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);     // Green
    public static final Color ERROR_COLOR = new Color(244, 67, 54);       // Red
    public static final Color WARNING_COLOR = new Color(255, 152, 0);     // Orange
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    public static final Color TEXT_COLOR = new Color(33, 33, 33);         // Dark Gray
    public static final Color BORDER_COLOR = new Color(224, 224, 224);    // Gray

    // Fonts
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Spacing
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    public static final int BORDER_RADIUS = 8;

    // Borders
    public static final Border DEFAULT_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM)
    );

    public static final Border FOCUS_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_MEDIUM - 1, PADDING_MEDIUM - 1, PADDING_MEDIUM - 1, PADDING_MEDIUM - 1)
    );

    // Table constants
    public static final int ROW_HEIGHT = 40;        // Tăng từ 35 lên 40
    public static final int HEADER_HEIGHT = 45;     // Tăng từ 40 lên 45
    public static final int[] COLUMN_WIDTHS = {100, 250, 200, 150, 150, 150}; // Default widths

    // Table column widths - adjust these values to match content
    public static final class ColumnWidths {

        // Book table
        public static final int BOOK_ID = 100;      // Tăng từ 80 lên 100
        public static final int BOOK_TITLE = 350;   // Tăng từ 250 lên 350
        public static final int BOOK_AUTHOR = 200;  // Tăng từ 180 lên 200
        public static final int BOOK_ISBN = 150;    // Tăng từ 120 lên 150
        public static final int BOOK_QUANTITY = 100; // Tăng từ 80 lên 100
        public static final int BOOK_RATING = 100;   // Tăng từ 80 lên 100

        // Member/Loan table
        public static final int MEMBER_ID = 100;    // Tăng từ 80 lên 100
        public static final int MEMBER_NAME = 200;  // Tăng từ 180 lên 200
        public static final int LOAN_BOOK = 350;    // Tăng từ 250 lên 350
        public static final int LOAN_DATE = 150;    // Tăng từ 100 lên 150

        // Review table
        public static final int REVIEW_BOOK_ID = 100;  // Tăng từ 80 lên 100
        public static final int REVIEW_USER_ID = 120;  // Tăng từ 100 lên 120
        public static final int REVIEW_RATING = 100;   // Tăng từ 80 lên 100
        public static final int REVIEW_COMMENT = 400;  // Tăng từ 300 lên 400
    }

    // Panel dimensions
    public static final Dimension MAIN_PANEL_SIZE = new Dimension(1000, 600);
    public static final Dimension DIALOG_SIZE = new Dimension(800, 500);

    // Component styling methods
    public static void styleButton(JButton button) {
        button.setFont(BODY_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(BODY_FONT);
        textField.setBackground(Color.WHITE);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setGridColor(BORDER_COLOR);
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_COLOR);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(30);
        table.setIntercellSpacing(new Dimension(10, 10));
        table.setShowGrid(true);
        centerTableContent(table);
    }

    public static void styleDialog(javax.swing.JDialog dialog) {
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM));
    }

    public static void styleMainPanel(JPanel panel) {
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);
    }

    public static void styleTable(JTable table, int... columnWidths) {
        // Basic styling
        table.setFont(BODY_FONT);
        table.setRowHeight(ROW_HEIGHT);
        table.getTableHeader().setFont(BUTTON_FONT);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        // Disable reordering and manual resizing
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        // Enable auto resize
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR));
        return scrollPane;
    }

    public static void styleSearchPanel(JPanel panel) {
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    public static void styleButtonPanel(JPanel panel) {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    }

    public static JLabel createHeaderLabel(String text, ImageIcon icon) {
        JLabel label = new JLabel(text, icon, SwingConstants.CENTER);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        return label;
    }

    public static void setupTableColumns(JTable table, int... widths) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < Math.min(widths.length, table.getColumnCount()); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setMinWidth(widths[i]);
            column.setPreferredWidth(widths[i]);
        }
    }

    public static void fixTableColumnWidth(JTable table, double... percentages) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Thêm listener để tự động điều chỉnh kích thước khi cửa sổ thay đổi
        table.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int tW = table.getWidth();
                TableColumnModel columnModel = table.getColumnModel();

                for (int i = 0; i < percentages.length && i < columnModel.getColumnCount(); i++) {
                    TableColumn column = columnModel.getColumn(i);
                    int pWidth = (int) (tW * (percentages[i] / 100.0));
                    column.setPreferredWidth(pWidth);
                    column.setWidth(pWidth);
                }
            }
        });

        // Trigger initial resize
        table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
            public void columnAdded(TableColumnModelEvent e) {
            }

            public void columnRemoved(TableColumnModelEvent e) {
            }

            public void columnMoved(TableColumnModelEvent e) {
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
            }

            public void columnMarginChanged(ChangeEvent e) {
                // Ensure columns maintain their relative sizes
                int tW = table.getWidth();
                TableColumnModel columnModel = table.getColumnModel();

                for (int i = 0; i < percentages.length && i < columnModel.getColumnCount(); i++) {
                    TableColumn column = columnModel.getColumn(i);
                    int pWidth = (int) (tW * (percentages[i] / 100.0));
                    column.setPreferredWidth(pWidth);
                    column.setWidth(pWidth);
                }
            }
        });

        // Disable manual column resizing
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static void centerTableContent(JTable table) {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Áp dụng cho tất cả các cột
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Căn giữa header
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);
    }
}
