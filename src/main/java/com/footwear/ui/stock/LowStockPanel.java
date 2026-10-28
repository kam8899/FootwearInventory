
        package com.footwear.ui.stock;

import com.footwear.model.ProductSize;
import com.footwear.service.LowStockService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class LowStockPanel extends JPanel {

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 250);

    private static final Color PRIMARY_COLOR =
            new Color(45, 91, 170);

    private static final Color HEADER_COLOR =
            new Color(29, 63, 129);

    private static final Color HEADER_TEXT_COLOR =
            Color.WHITE;

    private static final Color GRID_COLOR =
            new Color(226, 232, 240);

    private static final Color ALTERNATE_ROW_COLOR =
            new Color(247, 249, 252);

    private static final Color SELECTION_COLOR =
            new Color(219, 234, 254);

    private static final Color SELECTION_TEXT_COLOR =
            new Color(30, 58, 95);

    private final LowStockService lowStockService;

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JLabel countLabel;

    public LowStockPanel() {

        lowStockService = new LowStockService();

        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 28, 25, 28));

        createHeader();
        createTable();

        loadLowStockItems();
    }

    private void createHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Low-Stock Alerts");
        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(45, 52, 65));

        JLabel descriptionLabel = new JLabel(
                "Products that need restocking");
        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(115, 123, 137));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(descriptionLabel);

        countLabel = new JLabel("Low-stock items: 0");
        countLabel.setFont(
                new Font("SansSerif", Font.BOLD, 14));
        countLabel.setForeground(PRIMARY_COLOR);

        JButton refreshButton = new JButton("↻ Refresh");

        refreshButton.setFont(
                new Font("SansSerif", Font.BOLD, 12));
        refreshButton.setPreferredSize(
                new Dimension(100, 50));
        refreshButton.setBackground(new Color(99,102,241));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        refreshButton.addActionListener(
                e -> loadLowStockItems());

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(
                new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        rightPanel.add(refreshButton);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(countLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createTable() {

        String[] columns = {
                "ID",
                "Product Code",
                "Product Name",
                "Brand",
                "Category",
                "Size",
                "Available Qty",
                "Minimum Stock",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stockTable = new JTable(tableModel);

        stockTable.setRowHeight(34);
        stockTable.setFont(
                new Font("SansSerif", Font.PLAIN, 12));
        stockTable.setForeground(new Color(45, 55, 72));
        stockTable.setBackground(Color.WHITE);

        stockTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        stockTable.setAutoCreateRowSorter(true);
        stockTable.setFillsViewportHeight(true);

        stockTable.setShowGrid(true);
        stockTable.setGridColor(GRID_COLOR);
        stockTable.setIntercellSpacing(new Dimension(1, 1));

        stockTable.setSelectionBackground(SELECTION_COLOR);
        stockTable.setSelectionForeground(SELECTION_TEXT_COLOR);

        JTableHeader header = stockTable.getTableHeader();

        header.setFont(
                new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(HEADER_COLOR);
        header.setForeground(HEADER_TEXT_COLOR);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setOpaque(true);

        // Modern table header styling.
        header.setDefaultRenderer(
                (table, value, isSelected, hasFocus, row, column) -> {

                    JLabel label = new JLabel(
                            value == null ? "" : value.toString());

                    label.setOpaque(true);
                    label.setBackground(HEADER_COLOR);
                    label.setForeground(HEADER_TEXT_COLOR);
                    label.setFont(
                            new Font("SansSerif", Font.BOLD, 12));
                    label.setHorizontalAlignment(
                            SwingConstants.CENTER);

                    label.setBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createMatteBorder(
                                            0, 0, 1, 1,
                                            new Color(70, 85, 110)),
                                    BorderFactory.createEmptyBorder(
                                            8, 8, 8, 8)));

                    return label;
                });

        // Alternating row colors and consistent cell padding.
        stockTable.setDefaultRenderer(
                Object.class,
                (table, value, isSelected, hasFocus, row, column) -> {

                    JLabel label = new JLabel(
                            value == null ? "" : value.toString());

                    label.setOpaque(true);
                    label.setFont(table.getFont());
                    label.setBorder(
                            BorderFactory.createEmptyBorder(0, 10, 0, 10));

                    if (isSelected) {
                        label.setBackground(SELECTION_COLOR);
                        label.setForeground(SELECTION_TEXT_COLOR);
                    } else {
                        label.setBackground(
                                row % 2 == 0
                                        ? Color.WHITE
                                        : ALTERNATE_ROW_COLOR);

                        label.setForeground(new Color(45, 55, 72));
                    }

                    return label;
                });

        JScrollPane scrollPane = new JScrollPane(stockTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(GRID_COLOR));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLowStockItems() {

        try {
            tableModel.setRowCount(0);

            List<ProductSize> lowStockItems =
                    lowStockService.getLowStockItems();

            for (ProductSize item : lowStockItems) {

                Object[] row = {
                        item.getProductSizeId(),
                        item.getProductCode(),
                        item.getProductName(),
                        item.getBrandName(),
                        item.getCategoryName(),
                        item.getShoeSize(),
                        item.getQuantity(),
                        item.getMinimumStock(),
                        "LOW STOCK"
                };

                tableModel.addRow(row);
            }

            countLabel.setText(
                    "Low-stock items: " + lowStockItems.size());

            if (lowStockItems.isEmpty()) {
                countLabel.setForeground(new Color(30, 130, 75));
            } else {
                countLabel.setForeground(new Color(190, 55, 55));
            }

        } catch (RuntimeException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load low-stock items.\n"
                            + ex.getMessage(),
                    "Low-Stock Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}