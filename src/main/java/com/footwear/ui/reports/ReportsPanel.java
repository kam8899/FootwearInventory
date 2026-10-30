
package com.footwear.ui.reports;

import com.footwear.service.ReportsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final ReportsService reportsService;

    private final JTabbedPane tabbedPane;
    private final JTable stockTable;
    private final JTable salesTable;
    private final JTable lowStockTable;

    private final DefaultTableModel stockModel;
    private final DefaultTableModel salesModel;
    private final DefaultTableModel lowStockModel;

    private final JTextField startDateField;
    private final JTextField endDateField;

    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color HEADER_COLOR = new Color(29, 63, 129);
    private static final Color HEADER_TEXT_COLOR = Color.WHITE;
    private static final Color GRID_COLOR = new Color(226, 232, 240);
    private static final Color ALTERNATE_ROW_COLOR = new Color(247, 249, 252);
    private static final Color SELECTION_COLOR = new Color(219, 234, 254);
    private static final Color SELECTION_TEXT_COLOR = new Color(30, 58, 95);
    private static final Color ACCENT_BLUE = new Color(37, 99, 235);
    private static final Color WATERMARK_COLOR = new Color(150, 160, 175);

    public ReportsPanel() {
        reportsService = new ReportsService();

        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        stockModel = createModel(new String[]{
                "Product", "Brand", "Category",
                "Size", "Available Stock", "Minimum Stock"
        });

        salesModel = createModel(new String[]{
                "Sale ID", "Sale Date", "Product", "Brand",
                "Category", "Size", "Quantity Sold", "Notes"
        });

        lowStockModel = createModel(new String[]{
                "Product", "Brand", "Category",
                "Size", "Available Stock", "Minimum Stock"
        });

        stockTable = createTable(stockModel);
        salesTable = createTable(salesModel);
        lowStockTable = createTable(lowStockModel);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setForeground(HEADER_COLOR);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(5, 5, 0, 5);
                contentBorderInsets = new Insets(1, 1, 1, 1);
            }

            @Override
            protected void paintTabBackground(
                    Graphics graphics,
                    int tabPlacement,
                    int tabIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    boolean selected) {

                Graphics2D g2 = (Graphics2D) graphics.create();

                if (selected) {
                    g2.setColor(ACCENT_BLUE);
                } else {
                    g2.setColor(new Color(235, 239, 245));
                }

                g2.fillRoundRect(x + 2, y + 2, width - 4, height - 2, 10, 10);
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(
                    Graphics graphics,
                    int tabPlacement,
                    int tabIndex,
                    int x,
                    int y,
                    int width,
                    int height,
                    boolean selected) {
                // Intentionally no tab border.
            }

            @Override
            protected void paintText(
                    Graphics graphics,
                    int tabPlacement,
                    Font font,
                    FontMetrics metrics,
                    int tabIndex,
                    String title,
                    Rectangle textRect,
                    boolean selected) {

                graphics.setFont(font);
                graphics.setColor(selected ? Color.WHITE : HEADER_COLOR);

                FontMetrics fm = graphics.getFontMetrics(font);

                graphics.drawString(
                        title,
                        textRect.x,
                        textRect.y + fm.getAscent()
                );
            }
        });

        tabbedPane.addTab("Stock Report", new JScrollPane(stockTable));
        tabbedPane.addTab("Sales Report", new JScrollPane(salesTable));
        tabbedPane.addTab("Low-Stock Report", new JScrollPane(lowStockTable));

        startDateField = createDateField();
        endDateField = createDateField();

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadReports();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("\uD83D\uDCC8 Inventory Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(35, 45, 65));

        JLabel subtitle = new JLabel(
                "View stock levels, sales history and low-stock items"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(105, 115, 130));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 231, 239)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JLabel fromLabel = new JLabel("Sales From:");
        fromLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        fromLabel.setForeground(HEADER_COLOR);

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        toLabel.setForeground(HEADER_COLOR);

        toolbar.add(fromLabel);
        toolbar.add(startDateField);
        toolbar.add(toLabel);
        toolbar.add(endDateField);

        JButton refreshButton = createActionButton(
                "Refresh Reports", ACCENT_BLUE
        );

        JButton exportButton = createActionButton(
                "Export CSV", new Color(22, 163, 74)
        );

        refreshButton.addActionListener(e -> loadReports());
        exportButton.addActionListener(e -> exportCurrentReport());

        toolbar.add(refreshButton);
        toolbar.add(exportButton);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createActionButton(String text, Color background) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        button.setOpaque(true);

        return button;
    }

    private JTextField createDateField() {
        JTextField field = new JTextField(10);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setForeground(HEADER_COLOR);
        field.setBackground(Color.WHITE);
        field.setCaretColor(ACCENT_BLUE);
        field.setToolTipText(
                "Enter date in YYYY-MM-DD format, for example 2026-10-20"
        );

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));

        field.setPreferredSize(new Dimension(125, 34));

        // Display a date-format watermark without putting it into the field.
        field.putClientProperty("dateWatermark", "YYYY-MM-DD");

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.repaint();
            }
        });

        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintSafely(Graphics graphics) {
                super.paintSafely(graphics);

                if (field.getText().isEmpty() && !field.hasFocus()) {
                    Graphics2D g2 = (Graphics2D) graphics.create();

                    g2.setColor(WATERMARK_COLOR);
                    g2.setFont(field.getFont());

                    Insets insets = field.getInsets();
                    FontMetrics metrics = g2.getFontMetrics();

                    int x = insets.left + 2;
                    int y = (field.getHeight() - metrics.getHeight()) / 2
                            + metrics.getAscent();

                    g2.drawString("YYYY-MM-DD", x, y);
                    g2.dispose();
                }
            }
        });

        return field;
    }

    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);

        table.setRowHeight(32);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(45, 55, 72));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionForeground(SELECTION_TEXT_COLOR);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(HEADER_COLOR);
        header.setForeground(HEADER_TEXT_COLOR);
        header.setOpaque(true);

        // Use custom painting to keep the table header consistently styled.
        header.setDefaultRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(
                    value == null ? "" : value.toString()
            );

            label.setOpaque(true);
            label.setBackground(HEADER_COLOR);
            label.setForeground(HEADER_TEXT_COLOR);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            0, 0, 1, 1, new Color(70, 85, 110)
                    ),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));

            return label;
        });

        // Alternate row colors improve readability.
        table.setDefaultRenderer(Object.class, (table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(
                    value == null ? "" : value.toString()
            );

            label.setOpaque(true);
            label.setFont(table1.getFont());
            label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            if (isSelected) {
                label.setBackground(SELECTION_COLOR);
                label.setForeground(SELECTION_TEXT_COLOR);
            } else {
                label.setBackground(
                        row % 2 == 0 ? Color.WHITE : ALTERNATE_ROW_COLOR
                );
                label.setForeground(new Color(45, 55, 72));
            }

            return label;
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRID_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        return table;
    }

    private void loadReports() {
        try {
            loadStockReport();
            loadSalesReport();
            loadLowStockReport();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load reports:\n" + ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadStockReport() throws Exception {
        stockModel.setRowCount(0);

        List<Object[]> rows = reportsService.getStockReport();

        for (Object[] row : rows) {
            stockModel.addRow(row);
        }
    }

    private void loadLowStockReport() throws Exception {
        lowStockModel.setRowCount(0);

        List<Object[]> rows = reportsService.getLowStockReport();

        for (Object[] row : rows) {
            lowStockModel.addRow(row);
        }
    }

    private void loadSalesReport() throws Exception {
        salesModel.setRowCount(0);

        Date startDate = parseDate(startDateField.getText());
        Date endDate = parseDate(endDateField.getText());

        List<Object[]> rows =
                reportsService.getSalesReport(startDate, endDate);

        for (Object[] row : rows) {
            salesModel.addRow(row);
        }
    }

    private Date parseDate(String value) {
        String text = value.trim();

        if (text.isEmpty()) {
            return null;
        }

        return Date.valueOf(text);
    }

    private void exportCurrentReport() {
        JTable table = getSelectedTable();

        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "There is no data to export.",
                    "Export CSV",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Report as CSV");
        chooser.setSelectedFile(
                new File(getSelectedReportName() + ".csv")
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8"))) {

            // Write column headings.
            for (int col = 0; col < table.getColumnCount(); col++) {
                if (col > 0) {
                    writer.write(",");
                }

                writer.write(csvValue(table.getColumnName(col)));
            }
            writer.newLine();

            // Write visible table rows, including the current sorting.
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col > 0) {
                        writer.write(",");
                    }

                    Object value = table.getValueAt(row, col);
                    writer.write(csvValue(
                            value == null ? "" : value.toString()
                    ));
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Report exported successfully:\n"
                            + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to export report:\n" + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String csvValue(String value) {
        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    private JTable getSelectedTable() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 1) {
            return salesTable;
        }

        if (selectedIndex == 2) {
            return lowStockTable;
        }

        return stockTable;
    }

    private String getSelectedReportName() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 1) {
            return "sales-report";
        }

        if (selectedIndex == 2) {
            return "low-stock-report";
        }

        return "stock-report";
    }
}
