
package com.footwear.ui.sales;

import com.footwear.dao.ProductSizeDAO;
import com.footwear.model.ProductSize;
import com.footwear.service.SalesService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SalesManagementPanel extends JPanel {

    private final ProductSizeDAO productSizeDAO;
    private final SalesService salesService;

    private JComboBox<ProductSize> productComboBox;
    private JSpinner quantitySpinner;
    private JTextField notesField;
    private JLabel availableStockLabel;

    private DefaultTableModel tableModel;
    private JTable salesTable;

    public SalesManagementPanel() {
        productSizeDAO = new ProductSizeDAO();
        salesService = new SalesService();

        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        createHeader();
        createSaleForm();
        createSalesHistory();

        loadProducts();
        loadSalesHistory();
    }

    private void createHeader() {
        JLabel title = new JLabel("Sales Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(new Color(35, 42, 60));

        JLabel subtitle = new JLabel(
                "Record footwear sales and track sold quantities");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);

        add(panel, BorderLayout.NORTH);
    }

    private void createSaleForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Record New Sale"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productComboBox = new JComboBox<ProductSize>();
        productComboBox.setPreferredSize(new Dimension(340, 34));

        productComboBox.addActionListener(e -> updateAvailableStock());

        quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, 100000, 1));
        quantitySpinner.setPreferredSize(new Dimension(120, 34));

        notesField = new JTextField(20);
        notesField.setPreferredSize(new Dimension(220, 34));

        availableStockLabel = new JLabel("Available stock: -");
        availableStockLabel.setForeground(new Color(45, 91, 170));
        availableStockLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13));

        JButton recordButton = createButton(
                "Record Sale", new Color(40, 140, 85));

        JButton refreshButton = createButton(
                "Refresh", new Color(90, 100, 115));

        recordButton.addActionListener(e -> recordSale());
        refreshButton.addActionListener(e -> refreshData());

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Product / Size:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(productComboBox, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(availableStockLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Quantity Sold:"), gbc);

        gbc.gridx = 1;
        formPanel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Notes:"), gbc);

        gbc.gridx = 1;
        formPanel.add(notesField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(recordButton);
        buttons.add(refreshButton);
        formPanel.add(buttons, gbc);

        /*
         * The form is placed above the history table in the main layout.
         */
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);
        topPanel.add(formPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.CENTER);
    }

    private void createSalesHistory() {
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Movement ID",
                        "Product",
                        "Brand",
                        "Category",
                        "Size",
                        "Quantity Sold",
                        "Date",
                        "Notes"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(29);
        salesTable.setFillsViewportHeight(true);
        salesTable.setFont(new Font("SansSerif", Font.PLAIN, 12));

        salesTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        salesTable.getTableHeader().setBackground(
                new Color(230, 235, 243));

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Sales History"));

        /*
         * Put the form and history table into one vertical content panel.
         */
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);

        /*
         * The first component is the form added by createSaleForm().
         * Retrieve it so it can be combined with the history table.
         */
        Component form = getComponent(1);
        remove(form);

        contentPanel.add(form, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(135, 36));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadProducts() {
        try {
            List<ProductSize> products =
                    productSizeDAO.getAllProductSizes();

            productComboBox.removeAllItems();

            for (ProductSize product : products) {
                productComboBox.addItem(product);
            }

            updateAvailableStock();

        } catch (Exception ex) {
            showError("Unable to load products.", ex);
        }
    }

    private void updateAvailableStock() {
        ProductSize product =
                (ProductSize) productComboBox.getSelectedItem();

        if (product == null) {
            availableStockLabel.setText("Available stock: -");
            return;
        }

        availableStockLabel.setText(
                "Available stock: " + product.getQuantity());
    }

    private void recordSale() {
        ProductSize product =
                (ProductSize) productComboBox.getSelectedItem();

        if (product == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int quantity = (Integer) quantitySpinner.getValue();

        if (quantity > product.getQuantity()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Quantity sold cannot exceed available stock.",
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String notes = notesField.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Confirm sale of " + quantity + " unit(s)?",
                "Confirm Sale",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            salesService.recordSale(
                    product.getProductSizeId(),
                    quantity,
                    notes
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Sale recorded successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            notesField.setText("");
            quantitySpinner.setValue(1);

            loadProducts();
            loadSalesHistory();

        } catch (Exception ex) {
            showError("Unable to record sale.", ex);
        }
    }

    private void loadSalesHistory() {
        try {
            List<Object[]> records = salesService.getSalesHistory();

            tableModel.setRowCount(0);

            for (Object[] record : records) {
                tableModel.addRow(record);
            }

        } catch (Exception ex) {
            showError("Unable to load sales history.", ex);
        }
    }

    private void refreshData() {
        loadProducts();
        loadSalesHistory();
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
