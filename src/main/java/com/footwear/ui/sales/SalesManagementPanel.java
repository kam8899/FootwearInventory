
package com.footwear.ui.sales;

import com.footwear.dao.ProductSizeDAO;
import com.footwear.model.ProductSize;
import com.footwear.service.SalesService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    private final Color BACKGROUND_COLOR = new Color(245, 247, 251);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color PRIMARY_COLOR = new Color(37, 76, 145);
    private final Color HEADER_COLOR = new Color(31, 55, 95);
    private final Color TEXT_COLOR = new Color(40, 49, 66);
    private final Color MUTED_COLOR = new Color(112, 122, 140);
    private final Color BORDER_COLOR = new Color(225, 230, 238);
    private final Color ALTERNATE_ROW_COLOR = new Color(246, 249, 253);
    private final Color SUCCESS_COLOR = new Color(35, 139, 91);

    public SalesManagementPanel() {
        productSizeDAO = new ProductSizeDAO();
        salesService = new SalesService();

        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(22, 24, 22, 24));

        createHeader();
        createSaleForm();
        createSalesHistory();

        loadProducts();
        loadSalesHistory();
    }

    private void createHeader() {
        JLabel title = new JLabel("Sales Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(TEXT_COLOR);

        JLabel subtitle = new JLabel(
                "Record footwear sales and monitor your sales history"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED_COLOR);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);

        add(panel, BorderLayout.NORTH);
    }

    private void createSaleForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 18, 16, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel formTitle = new JLabel("Record New Sale");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        formTitle.setForeground(TEXT_COLOR);

        productComboBox = new JComboBox<ProductSize>();
        productComboBox.setPreferredSize(new Dimension(340, 36));
        productComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        productComboBox.setBackground(Color.WHITE);
        productComboBox.setFocusable(false);
        productComboBox.addActionListener(e -> updateAvailableStock());

        quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, 100000, 1)
        );
        quantitySpinner.setPreferredSize(new Dimension(130, 36));
        quantitySpinner.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JComponent spinnerEditor = quantitySpinner.getEditor();
        if (spinnerEditor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) spinnerEditor)
                    .getTextField()
                    .setHorizontalAlignment(JTextField.CENTER);
        }

        notesField = new JTextField(20);
        notesField.setPreferredSize(new Dimension(240, 36));
        notesField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        notesField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(5, 9, 5, 9)
        ));

        availableStockLabel = new JLabel("Available stock: -");
        availableStockLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        availableStockLabel.setForeground(PRIMARY_COLOR);
        availableStockLabel.setOpaque(true);
        availableStockLabel.setBackground(new Color(237, 243, 253));
        availableStockLabel.setBorder(new EmptyBorder(9, 12, 9, 12));

        JButton recordButton = createButton(
                "Record Sale", SUCCESS_COLOR
        );

        JButton refreshButton = createButton(
                "Refresh", new Color(89, 101, 120)
        );

        recordButton.addActionListener(e -> recordSale());
        refreshButton.addActionListener(e -> refreshData());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;

        formPanel.add(formTitle, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0;

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createFieldLabel("Product / Size"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(productComboBox, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        formPanel.add(availableStockLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createFieldLabel("Quantity Sold"), gbc);

        gbc.gridx = 1;
        formPanel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createFieldLabel("Notes"), gbc);

        gbc.gridx = 1;
        formPanel.add(notesField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 2)
        );
        buttons.setOpaque(false);
        buttons.add(recordButton);
        buttons.add(refreshButton);

        formPanel.add(buttons, gbc);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(formPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
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
        salesTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        salesTable.setRowHeight(36);
        salesTable.setFillsViewportHeight(true);
        salesTable.setShowVerticalLines(false);
        salesTable.setShowHorizontalLines(true);
        salesTable.setGridColor(new Color(235, 239, 245));
        salesTable.setIntercellSpacing(new Dimension(0, 1));
        salesTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        salesTable.setSelectionBackground(new Color(218, 231, 251));
        salesTable.setSelectionForeground(TEXT_COLOR);
        salesTable.setAutoCreateRowSorter(true);
        salesTable.setRowMargin(0);

        JTableHeader header = salesTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 43));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createEmptyBorder());

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

                        Component component =
                                super.getTableCellRendererComponent(
                                        table, value, isSelected,
                                        hasFocus, row, column
                                );

                        if (isSelected) {
                            component.setBackground(
                                    new Color(218, 231, 251)
                            );
                            component.setForeground(TEXT_COLOR);
                        } else {
                            component.setBackground(
                                    row % 2 == 0
                                            ? Color.WHITE
                                            : ALTERNATE_ROW_COLOR
                            );
                            component.setForeground(TEXT_COLOR);
                        }

                        setBorder(new EmptyBorder(0, 10, 0, 10));

                        if (column == 0
                                || column == 4
                                || column == 5) {
                            setHorizontalAlignment(
                                    SwingConstants.CENTER
                            );
                        } else {
                            setHorizontalAlignment(
                                    SwingConstants.LEFT
                            );
                        }

                        return component;
                    }
                };

        for (int i = 0; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(cellRenderer);
        }

        salesTable.getColumnModel().getColumn(0)
                .setPreferredWidth(95);
        salesTable.getColumnModel().getColumn(1)
                .setPreferredWidth(180);
        salesTable.getColumnModel().getColumn(2)
                .setPreferredWidth(120);
        salesTable.getColumnModel().getColumn(3)
                .setPreferredWidth(120);
        salesTable.getColumnModel().getColumn(4)
                .setPreferredWidth(65);
        salesTable.getColumnModel().getColumn(5)
                .setPreferredWidth(115);
        salesTable.getColumnModel().getColumn(6)
                .setPreferredWidth(145);
        salesTable.getColumnModel().getColumn(7)
                .setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel historyPanel = new JPanel(new BorderLayout(0, 12));
        historyPanel.setBackground(CARD_COLOR);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel historyTitle = new JLabel("Sales History");
        historyTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        historyTitle.setForeground(TEXT_COLOR);

        JLabel historySubtitle = new JLabel(
                "Review recorded sales and quantities sold"
        );
        historySubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        historySubtitle.setForeground(MUTED_COLOR);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(historyTitle);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(historySubtitle);

        historyPanel.add(titlePanel, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        /*
         * The form and sales history are displayed vertically.
         */
        JPanel contentPanel = new JPanel(new BorderLayout(0, 16));
        contentPanel.setOpaque(false);

        /*
         * The sale form was added first to the main panel.
         * Retrieve it and place it above the history table.
         */
        Component form = getComponent(1);
        remove(form);

        contentPanel.add(form, BorderLayout.NORTH);
        contentPanel.add(historyPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(75, 85, 102));
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(135, 38));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(color);
            }
        });

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
                "Available stock: " + product.getQuantity()
        );
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
