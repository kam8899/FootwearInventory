
package com.footwear.ui.reports;

import com.footwear.service.ReportsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.util.List;

public class ReportsPanel extends JPanel {

    private final ReportsService reportsService;

    private final JTabbedPane tabbedPane;
    private final JTable stockTable;
    private final JTable salesTable;
    private final JTable lowStockTable;

    private final DefaultTableModel stockModel;
    private final DefaultTableModel salesModel;
    private final DefaultTableModel lowStockModel;

    private final JTextField startDateField;
    private final JTextField endDateField;

    public ReportsPanel() {
        reportsService = new ReportsService();

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        stockModel = createModel(new String[] {
                "Product", "Brand", "Category",
                "Size", "Available Stock", "Minimum Stock"
        });

        salesModel = createModel(new String[] {
                "Sale ID", "Sale Date", "Product", "Brand",
                "Category", "Size", "Quantity Sold", "Notes"
        });

        lowStockModel = createModel(new String[] {
                "Product", "Brand", "Category",
                "Size", "Available Stock", "Minimum Stock"
        });

        stockTable = createTable(stockModel);
        salesTable = createTable(salesModel);
        lowStockTable = createTable(lowStockModel);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Stock Report", new JScrollPane(stockTable));
        tabbedPane.addTab("Sales Report", new JScrollPane(salesTable));
        tabbedPane.addTab("Low-Stock Report", new JScrollPane(lowStockTable));

        startDateField = new JTextField(10);
        endDateField = new JTextField(10);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadReports();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("Inventory Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(35, 48, 70));

        JLabel subtitle = new JLabel(
                "View stock levels, sales history and low-stock items");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(105, 115, 130));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        return panel;
    }

    private JPanel createMainContent() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        toolbar.add(new JLabel("Sales From (yyyy-mm-dd):"));
        startDateField.setToolTipText("Example: 2026-10-01");
        toolbar.add(startDateField);

        toolbar.add(new JLabel("To:"));
        endDateField.setToolTipText("Example: 2026-10-31");
        toolbar.add(endDateField);

        JButton refreshButton = new JButton("Refresh Reports");
        JButton exportButton = new JButton("Export CSV");

        refreshButton.addActionListener(e -> loadReports());
        exportButton.addActionListener(e -> exportCurrentReport());

        toolbar.add(refreshButton);
        toolbar.add(exportButton);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private DefaultTableModel createModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        return table;
    }

    private void loadReports() {
        try {
            loadStockReport();
            loadSalesReport();
            loadLowStockReport();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load reports:\n" + ex.getMessage(),
                    "Report Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStockReport() throws Exception {
        stockModel.setRowCount(0);

        List<Object[]> rows = reportsService.getStockReport();

        for (Object[] row : rows) {
            stockModel.addRow(row);
        }
    }

    private void loadLowStockReport() throws Exception {
        lowStockModel.setRowCount(0);

        List<Object[]> rows = reportsService.getLowStockReport();

        for (Object[] row : rows) {
            lowStockModel.addRow(row);
        }
    }

    private void loadSalesReport() throws Exception {
        salesModel.setRowCount(0);

        Date startDate = parseDate(startDateField.getText());
        Date endDate = parseDate(endDateField.getText());

        List<Object[]> rows =
                reportsService.getSalesReport(startDate, endDate);

        for (Object[] row : rows) {
            salesModel.addRow(row);
        }
    }

    private Date parseDate(String value) {
        String text = value.trim();

        if (text.isEmpty()) {
            return null;
        }

        return Date.valueOf(text);
    }

    private void exportCurrentReport() {
        JTable table = getSelectedTable();

        if (table.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "There is no data to export.",
                    "Export CSV",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Report as CSV");
        chooser.setSelectedFile(
                new File(getSelectedReportName() + ".csv"));

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8"))) {

            // Write column headings.
            for (int col = 0; col < table.getColumnCount(); col++) {
                if (col > 0) {
                    writer.write(",");
                }

                writer.write(csvValue(
                        table.getColumnName(col)));
            }
            writer.newLine();

            // Write visible table rows, including the current sorting.
            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    if (col > 0) {
                        writer.write(",");
                    }

                    Object value = table.getValueAt(row, col);
                    writer.write(csvValue(
                            value == null ? "" : value.toString()));
                }
                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Report exported successfully:\n"
                            + file.getAbsolutePath(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to export report:\n" + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String csvValue(String value) {
        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",")
                || escaped.contains("\"")
                || escaped.contains("\n")
                || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }

    private JTable getSelectedTable() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 1) {
            return salesTable;
        }

        if (selectedIndex == 2) {
            return lowStockTable;
        }

        return stockTable;
    }

    private String getSelectedReportName() {
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 1) {
            return "sales-report";
        }

        if (selectedIndex == 2) {
            return "low-stock-report";
        }

        return "stock-report";
    }
}
