
        package com.footwear.ui.categories;

import com.footwear.model.Category;
import com.footwear.service.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriesManagementPanel extends JPanel {

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 251);

    private static final Color PRIMARY_COLOR =
            new Color(37, 99, 235);

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

    private final CategoryService categoryService;

    private final DefaultTableModel tableModel;
    private final JTable categoryTable;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;

    private JLabel categoryCountLabel;

    public CategoriesManagementPanel() {
        categoryService = new CategoryService();

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(24, 26, 24, 26));

        tableModel = new DefaultTableModel(
                new Object[]{"Category ID", "Category Name", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);

        sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        categoryTable.setRowSorter(sorter);

        searchField = new JTextField(19);

        configureTable();

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadCategories();
    }

    private void configureTable() {
        categoryTable.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        categoryTable.setForeground(TEXT_COLOR);
        categoryTable.setBackground(Color.WHITE);
        categoryTable.setRowHeight(43);

        categoryTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        categoryTable.setShowGrid(false);
        categoryTable.setShowHorizontalLines(true);
        categoryTable.setShowVerticalLines(false);
        categoryTable.setGridColor(new Color(238, 242, 247));
        categoryTable.setIntercellSpacing(new Dimension(0, 0));
        categoryTable.setFillsViewportHeight(true);
        categoryTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS);
        categoryTable.setSelectionBackground(
                new Color(219, 234, 254));
        categoryTable.setSelectionForeground(TEXT_COLOR);
        categoryTable.setRowMargin(0);

        JTableHeader header = categoryTable.getTableHeader();

        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 47));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);

        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer();

        headerRenderer.setBackground(HEADER_COLOR);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(
                new Font("SansSerif", Font.BOLD, 13));
        headerRenderer.setHorizontalAlignment(
                SwingConstants.LEFT);
        headerRenderer.setBorder(
                new EmptyBorder(0, 14, 0, 10));

        for (int i = 0;
             i < categoryTable.getColumnModel().getColumnCount();
             i++) {

            categoryTable.getColumnModel()
                    .getColumn(i)
                    .setHeaderRenderer(headerRenderer);
        }

        categoryTable.getColumnModel()
                .getColumn(0).setPreferredWidth(110);

        categoryTable.getColumnModel()
                .getColumn(1).setPreferredWidth(350);

        categoryTable.getColumnModel()
                .getColumn(2).setPreferredWidth(130);

        categoryTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(new CategoryCellRenderer(0));

        categoryTable.getColumnModel()
                .getColumn(1)
                .setCellRenderer(new CategoryCellRenderer(1));

        categoryTable.getColumnModel()
                .getColumn(2)
                .setCellRenderer(new StatusCellRenderer());
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JLabel title = new JLabel("Categories Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(TEXT_COLOR);

        JLabel subtitle = new JLabel(
                "Add, edit and organize your footwear categories");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED_COLOR);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 9, 5));
        searchPanel.setOpaque(false);

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
                "Search by category name");

        JButton searchButton = createButton(
                "Search", PRIMARY_COLOR, 100);

        JButton refreshButton = createButton(
                "Refresh", new Color(71, 85, 105), 105);

        searchButton.addActionListener(
                e -> searchCategories());

        searchField.addActionListener(
                e -> searchCategories());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadCategories();
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(searchPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(
                new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_COLOR, 1),
                        new EmptyBorder(12, 14, 12, 14)));

        JPanel actionPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setOpaque(false);

        JButton addButton = createButton(
                "+  Add Category", SUCCESS_COLOR, 150);

        JButton editButton = createButton(
                "Edit Category", PRIMARY_COLOR, 145);

        JButton deactivateButton = createButton(
                "Deactivate", DANGER_COLOR, 135);

        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deactivateButton.addActionListener(
                e -> deactivateCategory());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deactivateButton);

        categoryCountLabel = new JLabel("0 categories");
        categoryCountLabel.setFont(
                new Font("SansSerif", Font.BOLD, 12));
        categoryCountLabel.setForeground(MUTED_COLOR);

        toolbar.add(actionPanel, BorderLayout.WEST);
        toolbar.add(categoryCountLabel, BorderLayout.EAST);

        JScrollPane scrollPane =
                new JScrollPane(categoryTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER_COLOR, 1));

        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(
                BorderFactory.createLineBorder(
                        BORDER_COLOR, 1));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createButton(
            String text, Color backgroundColor, int width) {

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

    private void loadCategories() {
        try {
            tableModel.setRowCount(0);

            List<Category> categories =
                    categoryService.getAllActiveCategories();

            for (Category category : categories) {
                tableModel.addRow(new Object[]{
                        category.getCategoryId(),
                        category.getCategoryName(),
                        category.isActive()
                                ? "Active"
                                : "Inactive"
                });
            }

            sorter.setRowFilter(null);
            categoryTable.clearSelection();

            updateCategoryCount();

        } catch (SQLException ex) {
            showError("Unable to load categories.", ex);
        }
    }

    private void updateCategoryCount() {
        if (categoryCountLabel == null) {
            return;
        }

        int visibleCount = categoryTable.getRowCount();

        categoryCountLabel.setText(
                visibleCount == 1
                        ? "1 category"
                        : visibleCount + " categories");
    }

    private void searchCategories() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(searchText),
                            1));
        }

        updateCategoryCount();
    }

    private void addCategory() {
        String categoryName = JOptionPane.showInputDialog(
                this,
                "Enter category name:",
                "Add Category",
                JOptionPane.PLAIN_MESSAGE);

        if (categoryName == null) {
            return;
        }

        categoryName = categoryName.trim();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Category name cannot be empty.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            categoryService.addCategory(categoryName);
            loadCategories();

            JOptionPane.showMessageDialog(
                    this,
                    "Category added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError("Unable to add category.", ex);
        }
    }

    private void editCategory() {
        int selectedRow = categoryTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a category to edit.",
                    "Select Category",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow =
                categoryTable.convertRowIndexToModel(
                        selectedRow);

        int categoryId = Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString());

        String oldName =
                tableModel.getValueAt(modelRow, 1).toString();

        String newName = JOptionPane.showInputDialog(
                this,
                "Update category name:",
                oldName);

        if (newName == null) {
            return;
        }

        newName = newName.trim();

        if (newName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Category name cannot be empty.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            categoryService.updateCategory(
                    categoryId, newName);

            loadCategories();

            JOptionPane.showMessageDialog(
                    this,
                    "Category updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError("Unable to update category.", ex);
        }
    }

    private void deactivateCategory() {
        int selectedRow = categoryTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a category to deactivate.",
                    "Select Category",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow =
                categoryTable.convertRowIndexToModel(
                        selectedRow);

        int categoryId = Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString());

        String categoryName =
                tableModel.getValueAt(modelRow, 1).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Deactivate category \"" + categoryName + "\"?",
                "Confirm Deactivation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            categoryService.deactivateCategory(categoryId);
            loadCategories();

            JOptionPane.showMessageDialog(
                    this,
                    "Category deactivated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError("Unable to deactivate category.", ex);
        }
    }

    private void showError(
            String message, Exception ex) {

        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Category Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private class CategoryCellRenderer
            extends DefaultTableCellRenderer {

        private final int column;

        public CategoryCellRenderer(int column) {
            this.column = column;
            setOpaque(true);
            setBorder(
                    new EmptyBorder(0, 14, 0, 10));
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
                    column == 1 ? Font.BOLD : Font.PLAIN,
                    13));

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

            setHorizontalAlignment(
                    column == 0
                            ? SwingConstants.CENTER
                            : SwingConstants.LEFT);

            return this;
        }
    }

    private class StatusCellRenderer
            extends DefaultTableCellRenderer {

        public StatusCellRenderer() {
            setOpaque(true);
            setBorder(
                    new EmptyBorder(0, 14, 0, 10));
            setHorizontalAlignment(
                    SwingConstants.LEFT);
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
                    "SansSerif", Font.BOLD, 12));

            if (isSelected) {
                setBackground(new Color(219, 234, 254));
                setForeground(TEXT_COLOR);
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
