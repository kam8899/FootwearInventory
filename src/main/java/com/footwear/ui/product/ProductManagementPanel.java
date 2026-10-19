
package com.footwear.ui.product;

import com.footwear.model.Product;
import com.footwear.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductManagementPanel extends JPanel {

    private final ProductService productService;
    private final DefaultTableModel tableModel;
    private final JTable productTable;
    private final JTextField searchField;

    public ProductManagementPanel() {
        productService = new ProductService();

        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        JLabel title = new JLabel("Product Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(35, 45, 65));

        JButton addButton = createActionButton(
                "+ Add Product",
                new Color(35, 139, 85)
        );

        JButton refreshButton = createActionButton(
                "Refresh",
                new Color(65, 95, 145)
        );

        JButton editButton = createActionButton(
                "Edit Product",
                new Color(75, 95, 125)
        );

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        actions.add(refreshButton);
        actions.add(editButton);
        actions.add(addButton);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        // Product table
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
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setRowHeight(30);
        productTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        productTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoCreateRowSorter(true);
        productTable.setFillsViewportHeight(true);

        productTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        productTable.getTableHeader().setBackground(
                new Color(225, 232, 242));

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 237)));

        // Search controls
        searchField = new JTextField(22);
        searchField.setToolTipText(
                "Search product code or name");

        JButton searchButton = createActionButton(
                "Search",
                new Color(65, 95, 145)
        );

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 13));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Top section
        JPanel top = new JPanel(new BorderLayout(0, 15));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(searchPanel, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        refreshButton.addActionListener(e -> loadProducts());

        searchButton.addActionListener(e -> searchProducts());

        searchField.addActionListener(e -> searchProducts());

        addButton.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);

            AddProductDialog dialog =
                    new AddProductDialog(owner, productService);

            dialog.setVisible(true);

            loadProducts();
        });

        editButton.addActionListener(e -> editSelectedProduct());

        // Initial table load
        loadProducts();
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

                // Draw button background.
                g2.setColor(buttonColor);
                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        10,
                        10
                );

                // Draw button text explicitly.
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

        button.setText(text);
        button.setFont(
                new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(background);

        button.setPreferredSize(new Dimension(125, 38));
        button.setMinimumSize(new Dimension(125, 38));

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
                    "Could not load products:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Searches products by product code or product name.
     */
    private void searchProducts() {
        String searchText =
                searchField.getText().trim().toLowerCase();

        try {
            List<Product> products =
                    productService.getAllProducts();

            tableModel.setRowCount(0);

            for (Product product : products) {
                String productCode = product.getProductCode();
                String productName = product.getProductName();

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
                    "Could not search products:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the Edit Product dialog for the selected product.
     */
    private void editSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product to edit.",
                    "No Product Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow =
                productTable.convertRowIndexToModel(selectedRow);

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

            EditProductDialog dialog = new EditProductDialog(
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
