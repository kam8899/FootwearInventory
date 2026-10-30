
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
    private final Color HEADER_COLOR = new Color(35, 55, 90);
    private final Color SUCCESS_COLOR = new Color(22, 163, 74);
    private final Color DANGER_COLOR = new Color(239, 68, 68);

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

        JLabel titleLabel = new JLabel("\uD83D\uDC5F Brands Management");
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

        searchButton = createButton("⌕", PRIMARY_COLOR);
        searchButton.setToolTipText("search");

        searchButton.setPreferredSize(new Dimension(60, 39));
        searchButton.addActionListener(e -> searchBrands());

        refreshButton = createButton("↻",  new Color(99,102,241));
        refreshButton.setToolTipText("Refresh");

        refreshButton.setPreferredSize(new Dimension(60, 39));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadBrands();
        });

        searchField.addActionListener(e -> searchBrands());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

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

        JPanel leftToolbar = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftToolbar.setOpaque(false);

        addButton = createButton("＋ Add Category", new Color(16,185,129));
        editButton = createButton("✎ Edit Category", new Color(59,130,246));
        deactivateButton = createButton("✖ Deactivate", DANGER_COLOR);

        addButton.addActionListener(e -> addBrand());
        editButton.addActionListener(e -> editBrand());
        deactivateButton.addActionListener(e -> deactivateBrand());

        leftToolbar.add(addButton);
        leftToolbar.add(editButton);
        leftToolbar.add(deactivateButton);

        brandCountLabel = new JLabel("0 brands");
        brandCountLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        brandCountLabel.setForeground(MUTED_COLOR);

        toolbar.add(leftToolbar, BorderLayout.WEST);
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
    String[] columns = {
            "Brand ID",
            "Brand Name",
            "Status"
    };

    private void createTable() {
        tableModel = new DefaultTableModel(columns, 0){
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
        brandTable.setFocusable(false);
        brandTable.setIntercellSpacing(new Dimension(0, 1));
        brandTable.setFillsViewportHeight(true);
        brandTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        brandTable.setSelectionBackground(new Color(219, 234, 254));
        brandTable.setSelectionForeground(TEXT_COLOR);
        brandTable.setRowMargin(0);

        JTableHeader tableHeader = brandTable.getTableHeader();
        tableHeader.setFont(new Font("SansSerif", Font.BOLD, 13));
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

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(135, 39));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));

        Color originalColor = backgroundColor;
        Color hoverColor = backgroundColor.darker();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

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