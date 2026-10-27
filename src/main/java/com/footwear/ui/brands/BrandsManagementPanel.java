
package com.footwear.ui.brands;

import com.footwear.model.Brand;
import com.footwear.service.BrandService;

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

public class BrandsManagementPanel extends JPanel {

    private final BrandService brandService;

    private JTable brandTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JLabel brandCountLabel;

    private JButton addButton;
    private JButton editButton;
    private JButton deactivateButton;
    private JButton searchButton;
    private JButton refreshButton;

    private final Color PRIMARY_COLOR = new Color(63, 116, 236);
    private final Color PRIMARY_DARK = new Color(30, 64, 175);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 251);
    private final Color TEXT_COLOR = new Color(31, 41, 55);
    private final Color MUTED_COLOR = new Color(107, 114, 128);
    private final Color BORDER_COLOR = new Color(226, 232, 240);
    private final Color HEADER_COLOR = new Color(29, 63, 129);
    private final Color SUCCESS_COLOR = new Color(38, 165, 85);
    private final Color DANGER_COLOR = new Color(220, 53, 69);

    public BrandsManagementPanel() {
        brandService = new BrandService();

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 26, 24, 26));

        createHeader();
        createContent();

        loadBrands();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 18));
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Brands Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
                "Manage and organize your footwear brands in one place");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED_COLOR);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitleLabel);

        // Action Buttons
        JPanel actionsPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);

        JButton refreshButton =
                createActionButton("↻", new Color(71, 85, 105));

        JButton editButton =
                createActionButton("✎", new Color(245, 158, 11));

        JButton addButton =
                createActionButton("+", new Color(22, 163, 74));

        JButton deactivateButton =
                createActionButton("🚫", DANGER_COLOR);

        refreshButton.setToolTipText("Refresh");
        editButton.setToolTipText("Edit Brand");
        addButton.setToolTipText("Add Brand");
        deactivateButton.setToolTipText("Deactivate");

        actionsPanel.add(refreshButton);
        actionsPanel.add(editButton);
        actionsPanel.add(addButton);
        actionsPanel.add(deactivateButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(actionsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> addBrand());
        editButton.addActionListener(e -> editBrand());
        deactivateButton.addActionListener(e -> deactivateBrand());
        refreshButton.addActionListener(e -> {
            searchField.setText("");



            loadBrands();

        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 5));
        searchPanel.setOpaque(false);

        searchField = new JTextField(19);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setForeground(TEXT_COLOR);
        searchField.setBackground(Color.WHITE);
        searchField.setCaretColor(PRIMARY_COLOR);
        searchField.setPreferredSize(new Dimension(245, 39));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(0, 12, 0, 12)
        ));
        searchField.setToolTipText("Search by brand name");

        searchButton = createActionButton("🔍", PRIMARY_COLOR);

        // searchButton = createButton("Search", PRIMARY_COLOR);
        searchButton.setPreferredSize(new Dimension(100, 39));
        searchButton.addActionListener(e -> searchBrands());



        searchField.addActionListener(e -> searchBrands());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        //  searchPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 14));
        contentPanel.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        brandCountLabel = new JLabel("0 brands");
        brandCountLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        brandCountLabel.setForeground(MUTED_COLOR);

        // toolbar.add(leftToolbar, BorderLayout.WEST);
        toolbar.add(brandCountLabel, BorderLayout.EAST);

        createTable();

        JScrollPane scrollPane = new JScrollPane(brandTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(toolbar, BorderLayout.NORTH);
        contentPanel.add(tableCard, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void createTable() {
        tableModel = new DefaultTableModel(
                new Object[]{"Brand ID", "Brand Name", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        brandTable = new JTable(tableModel);

        brandTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        brandTable.setForeground(TEXT_COLOR);
        brandTable.setBackground(Color.WHITE);
        brandTable.setRowHeight(43);
        brandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        brandTable.setShowGrid(false);
        brandTable.setIntercellSpacing(new Dimension(0, 0));
        brandTable.setFillsViewportHeight(true);
        brandTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        brandTable.setSelectionBackground(new Color(219, 234, 254));
        brandTable.setSelectionForeground(TEXT_COLOR);
        brandTable.setRowMargin(0);
        brandTable.setFocusable(true);

        JTableHeader tableHeader = brandTable.getTableHeader();
        tableHeader.setFont(new Font("SansSerif",Font.BOLD, 15));
        tableHeader.setBackground(HEADER_COLOR);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(0, 47));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setResizingAllowed(true);

        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer();

        headerRenderer.setBackground(HEADER_COLOR);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("SansSerif", Font.BOLD, 13));
        headerRenderer.setBorder(new EmptyBorder(0, 14, 0, 10));

        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < brandTable.getColumnModel().getColumnCount(); i++) {
            brandTable.getColumnModel().getColumn(i)
                    .setHeaderRenderer(headerRenderer);
        }

        brandTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        brandTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        brandTable.getColumnModel().getColumn(2).setPreferredWidth(130);

        brandTable.getColumnModel().getColumn(0)
                .setCellRenderer(new ZebraCellRenderer(this, 0));

        brandTable.getColumnModel().getColumn(1)
                .setCellRenderer(new ZebraCellRenderer(this, 1));

        brandTable.getColumnModel().getColumn(2)
                .setCellRenderer(new StatusCellRenderer(this));

        sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        brandTable.setRowSorter(sorter);

        brandTable.setShowHorizontalLines(true);
        brandTable.setShowVerticalLines(false);
        brandTable.setGridColor(new Color(238, 242, 247));
    }

   /* private JButton createActionButton(
            String text,
            Color backgroundColor) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Segoe UI Symbol",
                        Font.BOLD,
                        18));

        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR));

        button.setPreferredSize(
                new Dimension(55, 42));

        return button;
    }

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

    private void loadBrands() {
        try {
            List<Brand> brands = brandService.getAllActiveBrands();

            tableModel.setRowCount(0);

            for (Brand brand : brands) {
                tableModel.addRow(new Object[]{
                        brand.getBrandId(),
                        brand.getBrandName(),
                        brand.isActive() ? "Active" : "Inactive"
                });
            }

            if (sorter != null) {
                sorter.setRowFilter(null);
            }

            updateBrandCount();

            brandTable.clearSelection();

        } catch (Exception ex) {
            showError("Unable to load brands.", ex);
        }
    }

    private void updateBrandCount() {
        if (brandCountLabel == null || sorter == null) {
            return;
        }

        int visibleCount = brandTable.getRowCount();

        brandCountLabel.setText(
                visibleCount == 1 ? "1 brand" : visibleCount + " brands");
    }

    private void addBrand() {
        String brandName = JOptionPane.showInputDialog(
                this,
                "Enter the brand name:",
                "Add Brand",
                JOptionPane.PLAIN_MESSAGE
        );

        if (brandName == null) {
            return;
        }

        brandName = brandName.trim();

        if (brandName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Brand name cannot be empty.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            brandService.addBrand(brandName);

            JOptionPane.showMessageDialog(
                    this,
                    "Brand added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadBrands();

        } catch (Exception ex) {
            showError("Unable to add brand.", ex);
        }
    }

    private void editBrand() {
        int selectedRow = brandTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a brand from the table first.",
                    "Select Brand",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = brandTable.convertRowIndexToModel(selectedRow);

        int brandId = Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString());

        String oldName = tableModel.getValueAt(modelRow, 1).toString();

        String newName = JOptionPane.showInputDialog(
                this,
                "Edit brand name:",
                oldName
        );

        if (newName == null) {
            return;
        }

        newName = newName.trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Brand name cannot be empty.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Brand brand = new Brand(brandId, newName, true);
            brandService.updateBrand(brand);

            JOptionPane.showMessageDialog(
                    this,
                    "Brand updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadBrands();

        } catch (Exception ex) {
            showError("Unable to update brand.", ex);
        }
    }

    private void deactivateBrand() {
        int selectedRow = brandTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a brand from the table first.",
                    "Select Brand",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = brandTable.convertRowIndexToModel(selectedRow);

        int brandId = Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString());

        String brandName = tableModel.getValueAt(modelRow, 1).toString();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to deactivate \""
                        + brandName + "\"?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            brandService.deactivateBrand(brandId);

            JOptionPane.showMessageDialog(
                    this,
                    "Brand deactivated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadBrands();

        } catch (Exception ex) {
            showError("Unable to deactivate brand.", ex);
        }
    }

    private void searchBrands() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(
                    "(?i)" + Pattern.quote(searchText), 1));
        }

        updateBrandCount();
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class ZebraCellRenderer
            extends DefaultTableCellRenderer {

        private final BrandsManagementPanel panel;
        private final int column;

        public ZebraCellRenderer(BrandsManagementPanel panel, int column) {
            this.panel = panel;
            this.column = column;
            setOpaque(true);
            setBorder(new EmptyBorder(0, 14, 0, 10));
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
                    table, value, isSelected, hasFocus, row, columnIndex);

            setFont(new Font("SansSerif",
                    column == 1 ? Font.BOLD : Font.PLAIN, 13));

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                setForeground(panel.TEXT_COLOR);
            } else {
                setBackground(row % 2 == 0
                        ? Color.WHITE
                        : new Color(248, 250, 253));
                setForeground(column == 0
                        ? panel.MUTED_COLOR
                        : panel.TEXT_COLOR);
            }

            setHorizontalAlignment(
                    column == 0
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT);

            return this;
        }
    }

    private static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        private final BrandsManagementPanel panel;

        public StatusCellRenderer(BrandsManagementPanel panel) {
            this.panel = panel;
            setOpaque(true);
            setBorder(new EmptyBorder(0, 14, 0, 10));
            setHorizontalAlignment(SwingConstants.LEFT);
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
                    table, value, isSelected, hasFocus, row, column);

            String status = value == null ? "" : value.toString();

            setFont(new Font("SansSerif", Font.BOLD, 12));

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                setForeground(panel.TEXT_COLOR);
            } else {
                setBackground(row % 2 == 0
                        ? Color.WHITE
                        : new Color(248, 250, 253));

                if ("Active".equalsIgnoreCase(status)) {
                    setForeground(new Color(21, 128, 61));
                } else {
                    setForeground(new Color(185, 28, 28));
                }
            }

            return this;
        }
    }
}