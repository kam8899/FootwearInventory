
package com.footwear.ui.product;

import com.footwear.model.Product;
import com.footwear.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddProductDialog extends JDialog {

    private final ProductService productService;

    private JTextField productCodeField;
    private JTextField productNameField;
    private JTextField imagePathField;
    private JTextArea descriptionArea;
    private JComboBox<LookupItem> brandComboBox;
    private JComboBox<LookupItem> categoryComboBox;
    private JCheckBox activeCheckBox;

    public AddProductDialog(
            Window owner,
            ProductService productService) {

        super(owner, "Add Product", ModalityType.APPLICATION_MODAL);

        this.productService = productService;

        initializeUI();
        loadDropdowns();

        setSize(520, 530);
        setMinimumSize(new Dimension(480, 500));
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel title = new JLabel("Add New Product");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel subtitle = new JLabel(
                "Enter the footwear product details below.");
        subtitle.setForeground(new Color(105, 115, 130));

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        productCodeField = new JTextField(20);
        productNameField = new JTextField(20);
        imagePathField = new JTextField(20);

        brandComboBox = new JComboBox<>();
        categoryComboBox = new JComboBox<>();

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        activeCheckBox = new JCheckBox("Product is active", true);
        activeCheckBox.setOpaque(false);

        addField(fields, gbc, 0, "Product Code *", productCodeField);
        addField(fields, gbc, 1, "Product Name *", productNameField);
        addField(fields, gbc, 2, "Brand *", brandComboBox);
        addField(fields, gbc, 3, "Category *", categoryComboBox);
        addField(fields, gbc, 4, "Image Path", imagePathField);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        fields.add(new JLabel("Description"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        fields.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        fields.add(activeCheckBox, gbc);

        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save Product");

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(cancelButton);
        buttons.add(saveButton);

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(fields, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveProduct());
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent field) {

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }


    private void loadDropdowns() {
        try {
            brandComboBox.removeAllItems();
            categoryComboBox.removeAllItems();

            for (java.util.Map.Entry<Integer, String> entry
                    : productService.getActiveBrands().entrySet()) {
                brandComboBox.addItem(
                        new LookupItem(entry.getKey(), entry.getValue())
                );
            }

            for (java.util.Map.Entry<Integer, String> entry
                    : productService.getActiveCategories().entrySet()) {
                categoryComboBox.addItem(
                        new LookupItem(entry.getKey(), entry.getValue())
                );
            }

            System.out.println("Brands loaded: "
                    + brandComboBox.getItemCount());

            System.out.println("Categories loaded: "
                    + categoryComboBox.getItemCount());

            if (brandComboBox.getItemCount() == 0
                    || categoryComboBox.getItemCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No active brands or categories found.",
                        "Missing Data",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (RuntimeException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load brands or categories:\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    private void saveProduct() {
        String code = productCodeField.getText().trim();
        String name = productNameField.getText().trim();

        if (code.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Product Code and Product Name are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LookupItem brand =
                (LookupItem) brandComboBox.getSelectedItem();

        LookupItem category =
                (LookupItem) categoryComboBox.getSelectedItem();

        if (brand == null || category == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a brand and category.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = new Product();
        product.setProductCode(code);
        product.setProductName(name);
        product.setBrandId(brand.getId());
        product.setCategoryId(category.getId());
        product.setImagePath(emptyToNull(imagePathField.getText()));
        product.setDescription(
                emptyToNull(descriptionArea.getText()));
        product.setActive(activeCheckBox.isSelected());

        try {
            boolean saved = productService.addProduct(product);

            if (saved) {
                JOptionPane.showMessageDialog(
                        this,
                        "Product saved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Product could not be saved.",
                        "Save Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not save product:\n" + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String emptyToNull(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static class LookupItem {
        private final int id;
        private final String name;

        public LookupItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
