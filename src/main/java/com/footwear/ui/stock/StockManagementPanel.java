
package com.footwear.ui.stock;

import com.footwear.model.ProductSize;
import com.footwear.service.ProductSizeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockManagementPanel extends JPanel {

    private final ProductSizeService productSizeService;

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public StockManagementPanel() {
        productSizeService = new ProductSizeService();

        initializeUI();
        loadStock();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Stock Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(35, 45, 65));

        JLabel subtitleLabel =
                new JLabel("Manage available stock by product and size");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // ================= SEARCH AND REFRESH =================
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setOpaque(false);

        searchField = new JTextField(14);
        searchField.setPreferredSize(new Dimension(170, 34));
        searchField.setToolTipText(
                "Search by code, product, brand, category or size");

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchStock());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadStock();
        });

        searchField.addActionListener(e -> searchStock());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        headerPanel.add(searchPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ================= STOCK TABLE =================
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
        stockTable.setRowHeight(30);
        stockTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        stockTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        stockTable.setAutoCreateRowSorter(true);
        stockTable.setFillsViewportHeight(true);

        stockTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        stockTable.getTableHeader().setBackground(
                new Color(225, 232, 242));

        stockTable.getColumnModel().getColumn(0)
                .setPreferredWidth(50);
        stockTable.getColumnModel().getColumn(1)
                .setPreferredWidth(110);
        stockTable.getColumnModel().getColumn(2)
                .setPreferredWidth(170);
        stockTable.getColumnModel().getColumn(3)
                .setPreferredWidth(100);
        stockTable.getColumnModel().getColumn(4)
                .setPreferredWidth(100);
        stockTable.getColumnModel().getColumn(5)
                .setPreferredWidth(60);
        stockTable.getColumnModel().getColumn(6)
                .setPreferredWidth(100);
        stockTable.getColumnModel().getColumn(7)
                .setPreferredWidth(100);
        stockTable.getColumnModel().getColumn(8)
                .setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(220, 225, 232)));

        add(scrollPane, BorderLayout.CENTER);

        // ================= FOOTER =================
        JPanel footerPanel = new JPanel(new BorderLayout(10, 0));
        footerPanel.setOpaque(false);
        footerPanel.setPreferredSize(new Dimension(1, 60));
        footerPanel.setMinimumSize(new Dimension(1, 60));

        JLabel footerLabel = new JLabel(
                "Low Stock: quantity is less than or equal to minimum stock.");
        footerLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));

        footerPanel.add(footerLabel, BorderLayout.CENTER);

        // ================= STOCK MOVEMENT BUTTONS =================
        JPanel movementPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 8));
        movementPanel.setOpaque(false);

        JButton stockInButton = createActionButton(
                "Stock In", new Color(35, 139, 85));

        JButton stockOutButton = createActionButton(
                "Stock Out", new Color(190, 65, 65));

        stockInButton.addActionListener(
                e -> openMovementDialog("IN"));

        stockOutButton.addActionListener(
                e -> openMovementDialog("OUT"));

        movementPanel.add(stockInButton);
        movementPanel.add(stockOutButton);

        footerPanel.add(movementPanel, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    // ================= CUSTOM BUTTON =================
    private JButton createActionButton(
            final String text,
            final Color background) {

        JButton button = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color buttonColor = background;

                if (getModel().isPressed()) {
                    buttonColor = background.darker();
                } else if (getModel().isRollover()) {
                    buttonColor = background.brighter();
                }

                // Draw the background.
                g2.setColor(buttonColor);
                g2.fillRoundRect(
                        0, 0, getWidth(), getHeight(), 10, 10);

                // Draw the text directly so it does not depend
                // on the Look and Feel's button text painting.
                g2.setFont(getFont());
                g2.setColor(getForeground());

                FontMetrics metrics = g2.getFontMetrics();

                int textX = (getWidth()
                        - metrics.stringWidth(text)) / 2;

                int textY = (getHeight()
                        - metrics.getHeight()) / 2
                        + metrics.getAscent();

                g2.drawString(text, textX, textY);
                g2.dispose();
            }

            @Override
            public boolean isContentAreaFilled() {
                return false;
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(background);

        button.setPreferredSize(new Dimension(145, 42));
        button.setMinimumSize(new Dimension(145, 42));

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setIcon(null);

        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
        String searchText = searchField.getText()
                .trim()
                .toLowerCase();

        try {
            List<ProductSize> productSizes =
                    productSizeService.getAllProductSizes();

            tableModel.setRowCount(0);

            for (ProductSize productSize : productSizes) {
                String productCode =
                        safe(productSize.getProductCode());
                String productName =
                        safe(productSize.getProductName());
                String brandName =
                        safe(productSize.getBrandName());
                String categoryName =
                        safe(productSize.getCategoryName());
                String shoeSize =
                        safe(productSize.getShoeSize());

                if (productCode.toLowerCase().contains(searchText)
                        || productName.toLowerCase().contains(searchText)
                        || brandName.toLowerCase().contains(searchText)
                        || categoryName.toLowerCase().contains(searchText)
                        || shoeSize.toLowerCase().contains(searchText)) {

                    addStockRow(productSize);
                }
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not search stock records:\n"
                            + e.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addDialogRow(formPanel, gbc, 0,
                "Product:", productCode + " - " + productName);

        addDialogRow(formPanel, gbc, 1,
                "Shoe Size:", shoeSize);

        addDialogRow(formPanel, gbc, 2,
                "Available Qty:", String.valueOf(availableQuantity));

        addDialogRow(formPanel, gbc, 3,
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
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(new JLabel(value), gbc);
    }

    // ================= NULL-SAFE TEXT =================
    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }
}
