
package com.footwear.ui.brands;

import com.footwear.model.Brand;
import com.footwear.service.BrandService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.util.List;

public class BrandsManagementPanel extends JPanel {

    private final BrandService brandService;

    private JTable brandTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;

    private JButton addButton;
    private JButton editButton;
    private JButton deactivateButton;
    private JButton searchButton;
    private JButton refreshButton;

    private final Color PRIMARY_COLOR = new Color(45, 91, 170);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color TEXT_COLOR = new Color(35, 42, 60);

    public BrandsManagementPanel() {
        brandService = new BrandService();

        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        createHeader();
        createToolbar();
        createTable();

        loadBrands();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(0, 12));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Brands Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 25));
        titleLabel.setForeground(TEXT_COLOR);

        JLabel subtitleLabel = new JLabel(
                "Add, edit, search and manage your footwear brands");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search Brand:");
        searchLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        searchField = new JTextField(22);
        searchField.setPreferredSize(new Dimension(220, 34));
        searchField.setToolTipText("Enter a brand name");

        searchButton = createButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> searchBrands());

        refreshButton = createButton("Refresh", new Color(90, 100, 115));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadBrands();
        });

        searchField.addActionListener(e -> searchBrands());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(5, 5, 5, 5)
        ));

        addButton = createButton(" + Add Brand", new Color(40, 140, 85));
        editButton = createButton("Edit Brand", PRIMARY_COLOR);
        deactivateButton = createButton("Deactivate", new Color(190, 65, 65));

        addButton.addActionListener(e -> addBrand());
        editButton.addActionListener(e -> editBrand());
        deactivateButton.addActionListener(e -> deactivateBrand());

        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deactivateButton);

        JPanel toolbarWrapper = new JPanel(new BorderLayout());
        toolbarWrapper.setOpaque(false);
        toolbarWrapper.add(toolbar, BorderLayout.CENTER);

        add(toolbarWrapper, BorderLayout.CENTER);
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
        brandTable.setRowHeight(32);
        brandTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        brandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        brandTable.setShowGrid(true);
        brandTable.setGridColor(new Color(230, 233, 238));
        brandTable.setFillsViewportHeight(true);
        brandTable.setAutoCreateRowSorter(true);

        brandTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        brandTable.getTableHeader().setBackground(
                new Color(230, 235, 243));
        brandTable.getTableHeader().setForeground(TEXT_COLOR);
        brandTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36));

        brandTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        brandTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        brandTable.getColumnModel().getColumn(2).setPreferredWidth(120);

        sorter = new TableRowSorter<DefaultTableModel>(tableModel);
        brandTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(brandTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(
                new Color(220, 225, 232)));

        /*
         * The buttons are in the CENTER section of the main panel.
         * Put the table and toolbar together in one content panel.
         */
        JPanel contentPanel = new JPanel(new BorderLayout(0, 12));
        contentPanel.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                new EmptyBorder(5, 5, 5, 5)
        ));

        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deactivateButton);

        contentPanel.add(toolbar, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        /*
         * Replace the toolbar-only CENTER component with the complete
         * toolbar and table content.
         */
        remove(getComponent(1));
        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(135, 38));
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

        } catch (Exception ex) {
            showError("Unable to load brands.", ex);
        }
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
                "Are you sure you want to deactivate \"" + brandName + "\"?",
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
            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)" + java.util.regex.Pattern.quote(searchText),
                            1
                    )
            );
        }
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
