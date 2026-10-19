
package com.footwear.ui.categories;

import com.footwear.model.Category;
import com.footwear.service.CategoryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoriesManagementPanel extends JPanel {

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 250);

    private static final Color PRIMARY_COLOR =
            new Color(45, 91, 170);

    private final CategoryService categoryService;

    private final DefaultTableModel tableModel;
    private final JTable categoryTable;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JTextField searchField;

    public CategoriesManagementPanel() {
        categoryService = new CategoryService();

        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 28, 25, 28));

        tableModel = new DefaultTableModel(
                new Object[]{"Category ID", "Category Name", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(28);
        categoryTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setAutoCreateRowSorter(true);
        categoryTable.getTableHeader().setReorderingAllowed(false);

        sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        categoryTable.setRowSorter(sorter);

        searchField = new JTextField(20);
        searchField.setToolTipText("Search by category name");

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadCategories();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Category Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(new Color(45, 52, 65));

        JLabel subtitle = new JLabel(
                "Add, edit and manage footwear categories");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(115, 123, 137));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel topPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        topPanel.add(new JLabel("Category Name:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        searchButton.addActionListener(e -> searchCategories());

        searchField.addActionListener(e -> searchCategories());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            sorter.setRowFilter(null);
            loadCategories();
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 229, 236)),
                new EmptyBorder(8, 8, 8, 8)));

        tablePanel.add(
                new JScrollPane(categoryTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        JButton addButton = createActionButton("Add Category");
        JButton editButton = createActionButton("Edit Category");
        JButton deactivateButton =
                createActionButton("Deactivate Category");

        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deactivateButton.addActionListener(e -> deactivateCategory());

        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deactivateButton);

        tablePanel.add(actionPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(165, 36));
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
                        category.isActive() ? "Active" : "Inactive"
                });
            }

        } catch (SQLException ex) {
            showError("Unable to load categories.", ex);
        }
    }

    private void searchCategories() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }

        sorter.setRowFilter(
                RowFilter.regexFilter(
                        "(?i)" + java.util.regex.Pattern.quote(searchText),
                        1));
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

        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);

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

        try {
            categoryService.updateCategory(categoryId, newName);
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

        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);

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

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                message + "\n" + ex.getMessage(),
                "Category Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
