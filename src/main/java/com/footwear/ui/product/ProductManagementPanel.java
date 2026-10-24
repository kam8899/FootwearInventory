
package com.footwear.ui.product;

import com.footwear.model.Product;
import com.footwear.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 250);

    private static final Color HEADER_COLOR =
            new Color(29, 63, 129);

    private static final Color HEADER_BORDER_COLOR =
            new Color(52, 79, 125);

    private static final Color ROW_COLOR =
            Color.WHITE;

    private static final Color ALTERNATE_ROW_COLOR =
            new Color(247, 249, 252);

    private static final Color SELECTED_ROW_COLOR =
            new Color(226, 237, 255);

    private static final Color TEXT_COLOR =
            new Color(45, 52, 65);

    private final ProductService productService;
    private final DefaultTableModel tableModel;
    private final JTable productTable;
    private final JTextField searchField;

    public ProductManagementPanel() {

        productService = new ProductService();

        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel header = new JPanel(
                new BorderLayout(10, 10));
        header.setOpaque(false);

        JLabel title = new JLabel("Product Management");
        title.setFont(
                new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(35, 45, 65));

        JButton addButton = createActionButton(
                "➕",
                new Color(35, 139, 85));
        addButton.setToolTipText("Add Product");

        JButton refreshButton = createActionButton(
                "🔄",
                new Color(71, 85, 105));
        refreshButton.setToolTipText("Refresh");

        JButton editButton = createActionButton(
                "✎",
                new Color(63, 116, 236));
        editButton.setToolTipText("Edit");

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        actions.add(refreshButton);
        actions.add(editButton);
        actions.add(addButton);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        // Product table columns
        String[] columns = {
                "ID",
                "Product Code",
                "Product Name",
                "Brand",
                "Category",
                "Active"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);

        // Basic table settings
        productTable.setRowHeight(36);
        productTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        productTable.setForeground(TEXT_COLOR);
        productTable.setBackground(ROW_COLOR);
        productTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoCreateRowSorter(true);
        productTable.setFillsViewportHeight(true);

        productTable.setShowVerticalLines(false);
        productTable.setShowHorizontalLines(true);
        productTable.setGridColor(
                new Color(232, 236, 242));
        productTable.setIntercellSpacing(
                new Dimension(0, 1));

        productTable.setSelectionBackground(
                SELECTED_ROW_COLOR);
        productTable.setSelectionForeground(
                new Color(30, 64, 120));

        productTable.setRowMargin(1);

        // Improve column alignment and widths.
        productTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Apply custom header and row styling.
        styleProductTable();

        // Scroll pane
        JScrollPane scrollPane =
                new JScrollPane(productTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 226, 235)));

        scrollPane.getViewport().setBackground(
                Color.WHITE);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        // Search controls
        searchField = new JTextField(22);
        searchField.setToolTipText(
                "Search product code or name");
        searchField.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        searchField.setPreferredSize(
                new Dimension(250, 36));

        JButton searchButton = createActionButton(
                "🔍",
                new Color(63, 116, 236));
        searchButton.setToolTipText("Search Product");


        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel();
        searchLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13));
        searchLabel.setForeground(TEXT_COLOR);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Top section
        JPanel top = new JPanel(
                new BorderLayout(0, 15));
        top.setOpaque(false);

        top.add(header, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        refreshButton.addActionListener(
                e -> loadProducts());

        searchButton.addActionListener(
                e -> searchProducts());

        searchField.addActionListener(
                e -> searchProducts());

        addButton.addActionListener(e -> {
            Window owner =
                    SwingUtilities.getWindowAncestor(this);

            AddProductDialog dialog =
                    new AddProductDialog(
                            owner, productService);

            dialog.setVisible(true);

            loadProducts();
        });

        editButton.addActionListener(
                e -> editSelectedProduct());

        // Initial table load
        loadProducts();
    }

    /**
     * Applies custom styling to the product table.
     */
    private void styleProductTable() {

        JTableHeader header =
                productTable.getTableHeader();

        header.setPreferredSize(
                new Dimension(0, 44));
        header.setFont(
                new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 2, 0,
                        HEADER_BORDER_COLOR));

        // Custom header renderer ensures the colors
        // are applied consistently across look and feels.
        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        super.getTableCellRendererComponent(
                                table,
                                value,
                                false,
                                false,
                                row,
                                column);

                        setFont(new Font(
                                "SansSerif",
                                Font.BOLD,
                                12));

                        setForeground(Color.WHITE);
                        setBackground(HEADER_COLOR);
                        setOpaque(true);

                        setHorizontalAlignment(
                                SwingConstants.LEFT);

                        setVerticalAlignment(
                                SwingConstants.CENTER);

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 12, 0, 8));

                        return this;
                    }
                };

        header.setDefaultRenderer(headerRenderer);

        // Alternating row colors and readable cell padding.
        DefaultTableCellRenderer cellRenderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column);

                        setFont(new Font(
                                "SansSerif",
                                Font.PLAIN,
                                13));

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 12, 0, 8));

                        if (isSelected) {
                            setBackground(SELECTED_ROW_COLOR);
                            setForeground(
                                    new Color(30, 64, 120));
                        } else {
                            if (row % 2 == 0) {
                                setBackground(ROW_COLOR);
                            } else {
                                setBackground(
                                        ALTERNATE_ROW_COLOR);
                            }

                            setForeground(TEXT_COLOR);
                        }

                        return this;
                    }
                };

        productTable.setDefaultRenderer(
                Object.class, cellRenderer);

        // Center the ID and Active columns.
        DefaultTableCellRenderer centeredRenderer =
                new DefaultTableCellRenderer();

        centeredRenderer.setHorizontalAlignment(
                SwingConstants.CENTER);

        productTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(centeredRenderer);

        productTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(centeredRenderer);

        // Preferred column widths.
        productTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        productTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(125);

        productTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(210);

        productTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(130);

        productTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(130);

        productTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(75);
    }

    /**
     * Creates a button with a custom-painted background
     * and explicitly rendered text.
     */
    private JButton createActionButton(
            final String text,
            final Color background) {

        JButton button = new JButton(text) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color buttonColor = background;

                if (getModel().isPressed()) {
                    buttonColor = background.darker();
                } else if (getModel().isRollover()) {
                    buttonColor = background.brighter();
                }

                // Draw rounded button background.
                g2.setColor(buttonColor);
                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        10,
                        10);

                // Draw button text.
                g2.setFont(getFont());
                g2.setColor(getForeground());

                FontMetrics metrics =
                        g2.getFontMetrics();

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

        button.setText(text);
        button.setFont(
                new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(background);

        button.setPreferredSize(
                new Dimension(50, 38));
        button.setMinimumSize(
                new Dimension(50, 38));

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setIcon(null);

        button.setHorizontalAlignment(
                SwingConstants.CENTER);
        button.setHorizontalTextPosition(
                SwingConstants.CENTER);
        button.setVerticalAlignment(
                SwingConstants.CENTER);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * Loads all products into the table.
     */
    private void loadProducts() {

        try {
            List<Product> products =
                    productService.getAllProducts();

            tableModel.setRowCount(0);

            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getProductCode(),
                        product.getProductName(),
                        product.getBrandName(),
                        product.getCategoryName(),
                        product.isActive() ? "Yes" : "No"
                });
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load products:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Searches products by product code or product name.
     */
    private void searchProducts() {

        String searchText =
                searchField.getText()
                        .trim()
                        .toLowerCase();

        try {
            List<Product> products =
                    productService.getAllProducts();

            tableModel.setRowCount(0);

            for (Product product : products) {

                String productCode =
                        product.getProductCode();

                String productName =
                        product.getProductName();

                boolean matches =
                        productCode != null
                                && productCode.toLowerCase()
                                .contains(searchText);

                matches = matches
                        || (productName != null
                        && productName.toLowerCase()
                        .contains(searchText));

                if (matches) {
                    tableModel.addRow(new Object[]{
                            product.getProductId(),
                            product.getProductCode(),
                            product.getProductName(),
                            product.getBrandName(),
                            product.getCategoryName(),
                            product.isActive() ? "Yes" : "No"
                    });
                }
            }

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not search products:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the Edit Product dialog for the selected product.
     */
    private void editSelectedProduct() {

        int selectedRow =
                productTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product to edit.",
                    "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow =
                productTable.convertRowIndexToModel(
                        selectedRow);

        int productId = ((Number) tableModel
                .getValueAt(modelRow, 0)).intValue();

        try {
            Product selectedProduct = null;

            for (Product product :
                    productService.getAllProducts()) {

                if (product.getProductId() == productId) {
                    selectedProduct = product;
                    break;
                }
            }

            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "The selected product could not be found.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Window owner =
                    SwingUtilities.getWindowAncestor(this);

            EditProductDialog dialog =
                    new EditProductDialog(
                            owner,
                            productService,
                            selectedProduct);

            dialog.setVisible(true);

            // Refresh the table after the dialog closes.
            loadProducts();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not open product for editing:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
