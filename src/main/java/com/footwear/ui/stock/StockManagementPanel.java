
        package com.footwear.ui.stock;

import com.footwear.model.ProductSize;
import com.footwear.service.ProductSizeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class StockManagementPanel extends JPanel {

    private final ProductSizeService productSizeService;

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JLabel stockCountLabel;

    private static final Color PRIMARY_COLOR =
            new Color(37, 99, 235);

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 251);

    private static final Color TEXT_COLOR =
            new Color(31, 41, 55);

    private static final Color MUTED_COLOR =
            new Color(107, 114, 128);

    private static final Color BORDER_COLOR =
            new Color(226, 232, 240);

    private static final Color HEADER_COLOR =
            new Color(35, 55, 90);

    private static final Color SUCCESS_COLOR =
            new Color(22, 163, 74);

    private static final Color DANGER_COLOR =
            new Color(220, 53, 69);

    private static final Color WARNING_COLOR =
            new Color(217, 119, 6);

    public StockManagementPanel() {
        productSizeService = new ProductSizeService();

        initializeUI();
        loadStock();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(24, 26, 24, 26));
        setBackground(BACKGROUND_COLOR);

        createHeader();
        createStockTable();
        createFooter();
    }

    // ================= HEADER =================

    private void createHeader() {
        JPanel headerPanel = new JPanel(
                new BorderLayout(0, 18));

        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel =
                new JLabel("Stock Management");

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 27));
        titleLabel.setForeground(TEXT_COLOR);

        JLabel subtitleLabel = new JLabel(
                "Monitor inventory by product, brand and shoe size");

        subtitleLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED_COLOR);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitleLabel);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 9, 5));

        searchPanel.setOpaque(false);

        searchField = new JTextField(19);
        searchField.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        searchField.setForeground(TEXT_COLOR);
        searchField.setBackground(Color.WHITE);
        searchField.setCaretColor(PRIMARY_COLOR);
        searchField.setPreferredSize(
                new Dimension(245, 39));

        searchField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_COLOR, 1),
                        new EmptyBorder(0, 12, 0, 12)));

        searchField.setToolTipText(
                "Search by code, product, brand, category or size");

        JButton searchButton = createButton(
                "Search", PRIMARY_COLOR, 100);

        JButton refreshButton = createButton(
                "Refresh", new Color(71, 85, 105), 105);

        searchButton.addActionListener(e -> searchStock());

        searchField.addActionListener(e -> searchStock());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStock();
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    // ================= STOCK TABLE =================

    private void createStockTable() {
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
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        stockTable = new JTable(tableModel);

        stockTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        stockTable.setForeground(TEXT_COLOR);
        stockTable.setBackground(Color.WHITE);
        stockTable.setRowHeight(43);

        stockTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        stockTable.setShowGrid(false);
        stockTable.setShowHorizontalLines(true);
        stockTable.setShowVerticalLines(false);
        stockTable.setGridColor(
                new Color(238, 242, 247));

        stockTable.setIntercellSpacing(
                new Dimension(0, 0));

        stockTable.setFillsViewportHeight(true);
        stockTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS);

        stockTable.setSelectionBackground(
                new Color(219, 234, 254));
        stockTable.setSelectionForeground(TEXT_COLOR);
        stockTable.setRowMargin(0);

        sorter = new TableRowSorter<DefaultTableModel>(
                tableModel);

        stockTable.setRowSorter(sorter);

        configureTableHeader();
        configureTableColumns();

        JScrollPane scrollPane =
                new JScrollPane(stockTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER_COLOR, 1));

        scrollPane.setBackground(Color.WHITE);

        scrollPane.getViewport().setBackground(
                Color.WHITE);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        JPanel tablePanel = new JPanel(
                new BorderLayout());

        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(
                BorderFactory.createLineBorder(
                        BORDER_COLOR, 1));

        tablePanel.add(
                scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    private void configureTableHeader() {
        JTableHeader header =
                stockTable.getTableHeader();

        header.setFont(
                new Font("SansSerif", Font.BOLD, 12));

        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);

        header.setPreferredSize(
                new Dimension(0, 47));

        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer();

        headerRenderer.setBackground(HEADER_COLOR);
        headerRenderer.setForeground(Color.WHITE);

        headerRenderer.setFont(
                new Font("SansSerif", Font.BOLD, 12));

        headerRenderer.setHorizontalAlignment(
                SwingConstants.LEFT);

        headerRenderer.setBorder(
                new EmptyBorder(0, 10, 0, 8));

        for (int i = 0;
             i < stockTable.getColumnModel().getColumnCount();
             i++) {

            stockTable.getColumnModel()
                    .getColumn(i)
                    .setHeaderRenderer(headerRenderer);
        }
    }

    private void configureTableColumns() {
        int[] widths = {
                55, 115, 180, 105, 105,
                65, 105, 110, 110
        };

        for (int i = 0; i < widths.length; i++) {
            stockTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        stockTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(new StockCellRenderer(0));

        stockTable.getColumnModel()
                .getColumn(1)
                .setCellRenderer(new StockCellRenderer(1));

        stockTable.getColumnModel()
                .getColumn(2)
                .setCellRenderer(new StockCellRenderer(2));

        stockTable.getColumnModel()
                .getColumn(3)
                .setCellRenderer(new StockCellRenderer(3));

        stockTable.getColumnModel()
                .getColumn(4)
                .setCellRenderer(new StockCellRenderer(4));

        stockTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(new StockCellRenderer(5));

        stockTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new QuantityCellRenderer(false));

        stockTable.getColumnModel()
                .getColumn(7)
                .setCellRenderer(new QuantityCellRenderer(true));

        stockTable.getColumnModel()
                .getColumn(8)
                .setCellRenderer(new StatusCellRenderer());
    }

    // ================= FOOTER =================

    private void createFooter() {
        JPanel footerPanel = new JPanel(
                new BorderLayout(12, 0));

        footerPanel.setOpaque(false);
        footerPanel.setBorder(
                new EmptyBorder(4, 0, 0, 0));

        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(
                new BoxLayout(informationPanel, BoxLayout.Y_AXIS));
        informationPanel.setOpaque(false);

        stockCountLabel = new JLabel("0 stock records");

        stockCountLabel.setFont(
                new Font("SansSerif", Font.BOLD, 12));
        stockCountLabel.setForeground(TEXT_COLOR);

        JLabel footerLabel = new JLabel(
                "Low Stock: available quantity is at or below minimum stock.");

        footerLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 11));
        footerLabel.setForeground(MUTED_COLOR);

        informationPanel.add(stockCountLabel);
        informationPanel.add(Box.createVerticalStrut(5));
        informationPanel.add(footerLabel);

        JPanel movementPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 4));

        movementPanel.setOpaque(false);

        JButton stockInButton = createButton(
                "Stock In", SUCCESS_COLOR, 125);

        JButton stockOutButton = createButton(
                "Stock Out", DANGER_COLOR, 125);

        stockInButton.addActionListener(
                e -> openMovementDialog("IN"));

        stockOutButton.addActionListener(
                e -> openMovementDialog("OUT"));

        movementPanel.add(stockInButton);
        movementPanel.add(stockOutButton);

        footerPanel.add(
                informationPanel, BorderLayout.CENTER);

        footerPanel.add(
                movementPanel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    // ================= BUTTON DESIGN =================

    private JButton createButton(
            String text,
            Color backgroundColor,
            int width) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 12));

        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);

        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        button.setPreferredSize(
                new Dimension(width, 39));

        button.setBorder(
                new EmptyBorder(8, 12, 8, 12));

        Color originalColor = backgroundColor;
        Color hoverColor = backgroundColor.darker();

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {
                        button.setBackground(hoverColor);
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {
                        button.setBackground(originalColor);
                    }
                });

        return button;
    }

    // ================= LOAD STOCK =================

    private void loadStock() {
        try {
            List<ProductSize> productSizes =
                    productSizeService.getAllProductSizes();

            tableModel.setRowCount(0);

            for (ProductSize productSize : productSizes) {
                addStockRow(productSize);
            }

            updateStockCount();
            stockTable.clearSelection();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load stock records:\n"
                            + e.getMessage(),
                    "Stock Loading Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SEARCH STOCK =================

    private void searchStock() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            RowFilter<DefaultTableModel, Integer> filter =
                    new RowFilter<DefaultTableModel, Integer>() {

                        @Override
                        public boolean include(
                                Entry<? extends DefaultTableModel,
                                        ? extends Integer> entry) {

                            for (int i = 0; i < entry.getValueCount(); i++) {
                                Object value = entry.getValue(i);

                                if (value != null
                                        && Pattern.compile(
                                                Pattern.quote(searchText),
                                                Pattern.CASE_INSENSITIVE)
                                        .matcher(value.toString())
                                        .find()) {
                                    return true;
                                }
                            }

                            return false;
                        }
                    };

            sorter.setRowFilter(filter);
        }

        updateStockCount();
    }

    // ================= ADD TABLE ROW =================

    private void addStockRow(ProductSize productSize) {
        int quantity = productSize.getQuantity();
        int minimumStock = productSize.getMinimumStock();

        String status = quantity <= minimumStock
                ? "Low Stock"
                : "In Stock";

        tableModel.addRow(new Object[]{
                productSize.getProductSizeId(),
                productSize.getProductCode(),
                productSize.getProductName(),
                productSize.getBrandName(),
                productSize.getCategoryName(),
                productSize.getShoeSize(),
                quantity,
                minimumStock,
                status
        });
    }

    private void updateStockCount() {
        if (stockCountLabel == null) {
            return;
        }

        int visibleCount = stockTable.getRowCount();

        stockCountLabel.setText(
                visibleCount == 1
                        ? "1 stock record"
                        : visibleCount + " stock records");
    }

    // ================= STOCK IN / STOCK OUT =================

    private void openMovementDialog(String movementType) {
        int selectedRow = stockTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product-size row from the table first.",
                    "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow =
                stockTable.convertRowIndexToModel(selectedRow);

        int productSizeId = ((Number) tableModel
                .getValueAt(modelRow, 0)).intValue();

        String productCode =
                safe(tableModel.getValueAt(modelRow, 1));

        String productName =
                safe(tableModel.getValueAt(modelRow, 2));

        String shoeSize =
                safe(tableModel.getValueAt(modelRow, 5));

        int availableQuantity = ((Number) tableModel
                .getValueAt(modelRow, 6)).intValue();

        String title = "IN".equals(movementType)
                ? "Stock In"
                : "Stock Out";

        String[] reasons;

        if ("IN".equals(movementType)) {
            reasons = new String[]{"PURCHASE", "OTHER"};
        } else {
            reasons = new String[]{
                    "SOLD",
                    "DAMAGED",
                    "RETURN_TO_SUPPLIER",
                    "OTHER"
            };
        }

        JComboBox<String> reasonComboBox =
                new JComboBox<String>(reasons);

        JSpinner quantitySpinner = new JSpinner(
                new SpinnerNumberModel(
                        1, 1, Integer.MAX_VALUE, 1));

        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(
                new Font("SansSerif", Font.PLAIN, 13));

        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addDialogRow(
                formPanel, gbc, 0,
                "Product:", productCode + " - " + productName);

        addDialogRow(
                formPanel, gbc, 1,
                "Shoe Size:", shoeSize);

        addDialogRow(
                formPanel, gbc, 2,
                "Available Qty:",
                String.valueOf(availableQuantity));

        addDialogRow(
                formPanel, gbc, 3,
                "Movement:", title);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;

        formPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;

        formPanel.add(new JLabel("Reason:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        formPanel.add(reasonComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;

        formPanel.add(new JLabel("Notes:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;

        formPanel.add(new JScrollPane(notesArea), gbc);

        int result = JOptionPane.showConfirmDialog(
                this,
                formPanel,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        int quantity =
                ((Number) quantitySpinner.getValue()).intValue();

        String reason =
                (String) reasonComboBox.getSelectedItem();

        String notes = notesArea.getText().trim();

        if ("OUT".equals(movementType)
                && quantity > availableQuantity) {

            JOptionPane.showMessageDialog(
                    this,
                    "Insufficient stock. Available quantity: "
                            + availableQuantity,
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Confirm " + title + " of " + quantity
                        + " pair(s) for " + productName
                        + ", size " + shoeSize + "?",
                "Confirm Stock Movement",
                JOptionPane.YES_NO_OPTION);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            productSizeService.recordMovement(
                    productSizeId,
                    movementType,
                    quantity,
                    reason,
                    notes);

            JOptionPane.showMessageDialog(
                    this,
                    title + " recorded successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            if (searchField.getText().trim().isEmpty()) {
                loadStock();
            } else {
                searchStock();
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not record " + title + ":\n"
                            + e.getMessage(),
                    "Stock Movement Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= DIALOG ROW HELPER =================

    private void addDialogRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            String value) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(new JLabel(value), gbc);
    }

    // ================= NULL-SAFE TEXT =================

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }

    // ================= STANDARD TABLE CELL RENDERER =================

    private class StockCellRenderer
            extends DefaultTableCellRenderer {

        private final int column;

        public StockCellRenderer(int column) {
            this.column = column;
            setOpaque(true);
            setBorder(
                    new EmptyBorder(0, 10, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int columnIndex) {

            super.getTableCellRendererComponent(
                    table, value, isSelected,
                    hasFocus, row, columnIndex);

            setFont(new Font(
                    "SansSerif",
                    column == 2 ? Font.BOLD : Font.PLAIN,
                    12));

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                setForeground(TEXT_COLOR);
            } else {
                setBackground(row % 2 == 0
                        ? Color.WHITE
                        : new Color(248, 250, 253));

                setForeground(column == 0
                        ? MUTED_COLOR
                        : TEXT_COLOR);
            }

            if (column == 0 || column == 5) {
                setHorizontalAlignment(
                        SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(
                        SwingConstants.LEFT);
            }

            return this;
        }
    }

    // ================= QUANTITY CELL RENDERER =================

    private class QuantityCellRenderer
            extends DefaultTableCellRenderer {

        private final boolean minimumStockColumn;

        public QuantityCellRenderer(
                boolean minimumStockColumn) {

            this.minimumStockColumn = minimumStockColumn;
            setOpaque(true);
            setBorder(
                    new EmptyBorder(0, 10, 0, 8));
            setHorizontalAlignment(
                    SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected,
                    hasFocus, row, column);

            setFont(new Font(
                    "SansSerif", Font.BOLD, 12));

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                setForeground(TEXT_COLOR);
                return this;
            }

            setBackground(row % 2 == 0
                    ? Color.WHITE
                    : new Color(248, 250, 253));

            int quantity = 0;

            if (value instanceof Number) {
                quantity = ((Number) value).intValue();
            }

            if (minimumStockColumn) {
                setForeground(WARNING_COLOR);
            } else {
                Object minimumValue =
                        table.getValueAt(row, 7);

                int minimumStock = minimumValue instanceof Number
                        ? ((Number) minimumValue).intValue()
                        : 0;

                if (quantity <= minimumStock) {
                    setForeground(DANGER_COLOR);
                } else {
                    setForeground(SUCCESS_COLOR);
                }
            }

            return this;
        }
    }

    // ================= STOCK STATUS RENDERER =================

    private class StatusCellRenderer
            extends DefaultTableCellRenderer {

        public StatusCellRenderer() {
            setOpaque(true);
            setBorder(
                    new EmptyBorder(0, 10, 0, 8));
            setHorizontalAlignment(
                    SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected,
                    hasFocus, row, column);

            String status =
                    value == null ? "" : value.toString();

            setFont(new Font(
                    "SansSerif", Font.BOLD, 11));

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                setForeground(TEXT_COLOR);
            } else {
                setBackground(row % 2 == 0
                        ? Color.WHITE
                        : new Color(248, 250, 253));

                if ("Low Stock".equalsIgnoreCase(status)) {
                    setForeground(DANGER_COLOR);
                } else {
                    setForeground(SUCCESS_COLOR);
                }
            }

            return this;
        }
    }
}
