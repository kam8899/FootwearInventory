
package com.footwear.ui.product;

import com.footwear.model.Product;
import com.footwear.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class EditProductDialog extends JDialog {

    private final ProductService productService;
    private final Product product;

    private JTextField productCodeField;
    private JTextField productNameField;
    private JTextField imagePathField;
    private JTextArea descriptionArea;
    private JComboBox<AddProductDialog.LookupItem> brandComboBox;
    private JComboBox<AddProductDialog.LookupItem> categoryComboBox;
    private JCheckBox activeCheckBox;

    public EditProductDialog(
            Window owner,
            ProductService productService,
            Product product) {

        super(owner, "Edit Product", ModalityType.APPLICATION_MODAL);

        this.productService = productService;
        this.product = product;

        initializeUI();
        loadDropdowns();
        populateProductDetails();

        setSize(520, 530);
        setMinimumSize(new Dimension(480, 500));
        setLocationRelativeTo(owner);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(22, 25, 22, 25));

        JLabel title = new JLabel("Edit Product");
        title.setFont(new Font("SansSerif", Font.BOLD, 23));

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);
        heading.add(title);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 0, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

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
        JButton updateButton = new JButton("Update Product");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(cancelButton);
        buttons.add(updateButton);

        mainPanel.add(heading, BorderLayout.NORTH);
        mainPanel.add(fields, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        cancelButton.addActionListener(e -> dispose());
        updateButton.addActionListener(e -> updateProduct());
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
            for (Map.Entry<Integer, String> entry
                    : productService.getActiveBrands().entrySet()) {
                brandComboBox.addItem(
                        new AddProductDialog.LookupItem(
                                entry.getKey(), entry.getValue()));
            }

            for (Map.Entry<Integer, String> entry
                    : productService.getActiveCategories().entrySet()) {
                categoryComboBox.addItem(
                        new AddProductDialog.LookupItem(
                                entry.getKey(), entry.getValue()));
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load brands or categories:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateProductDetails() {
        productCodeField.setText(product.getProductCode());
        productNameField.setText(product.getProductName());
        imagePathField.setText(product.getImagePath() == null
                ? "" : product.getImagePath());
        descriptionArea.setText(product.getDescription() == null
                ? "" : product.getDescription());
        activeCheckBox.setSelected(product.isActive());

        selectLookupItem(brandComboBox, product.getBrandId());
        selectLookupItem(categoryComboBox, product.getCategoryId());
    }

    private void selectLookupItem(
            JComboBox<AddProductDialog.LookupItem> comboBox,
            int id) {

        for (int i = 0; i < comboBox.getItemCount(); i++) {
            AddProductDialog.LookupItem item = comboBox.getItemAt(i);

            if (item.getId() == id) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void updateProduct() {
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

        AddProductDialog.LookupItem brand =
                (AddProductDialog.LookupItem) brandComboBox.getSelectedItem();

        AddProductDialog.LookupItem category =
                (AddProductDialog.LookupItem) categoryComboBox.getSelectedItem();

        if (brand == null || category == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a brand and category.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        product.setProductCode(code);
        product.setProductName(name);
        product.setBrandId(brand.getId());
        product.setCategoryId(category.getId());
        product.setImagePath(emptyToNull(imagePathField.getText()));
        product.setDescription(emptyToNull(descriptionArea.getText()));
        product.setActive(activeCheckBox.isSelected());

        try {
            boolean updated = productService.updateProduct(product);

            if (updated) {
                JOptionPane.showMessageDialog(
                        this,
                        "Product updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No product was updated.",
                        "Update Failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not update product:\n" + e.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String emptyToNull(String value) {
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
