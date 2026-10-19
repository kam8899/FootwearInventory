
        package com.footwear.ui;

import com.footwear.model.DashboardSummary;
import com.footwear.service.DashboardSummaryService;
import com.footwear.ui.product.ProductManagementPanel;
import com.footwear.ui.stock.StockManagementPanel;
import com.footwear.ui.stock.LowStockPanel;
import com.footwear.ui.brands.BrandsManagementPanel;
import com.footwear.ui.sales.SalesManagementPanel;
import com.footwear.ui.reports.ReportsPanel;
import com.footwear.ui.categories.CategoriesManagementPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DashboardFrame extends JFrame {

    private static final Color SIDEBAR_COLOR =
            new Color(35, 42, 60);

    private static final Color SELECTED_COLOR =
            new Color(52, 85, 145);

    private static final Color HOVER_COLOR =
            new Color(48, 57, 80);

    private static final Color BACKGROUND_COLOR =
            new Color(245, 247, 250);

    private static final Color PRIMARY_COLOR =
            new Color(45, 91, 170);

    private static final Color CARD_COLOR = Color.WHITE;

    private static final Color TEXT_COLOR =
            new Color(45, 52, 65);

    private final DashboardSummaryService dashboardSummaryService =
            new DashboardSummaryService();

    private JPanel contentContainer;

    private JLabel totalProductsValue;
    private JLabel totalBrandsValue;
    private JLabel totalStockValue;
    private JLabel lowStockValue;

    private DashboardChartPanel stockByCategoryChart;
    private DashboardChartPanel salesLastSevenDaysChart;

    // Tracks the currently selected sidebar button.
    private JButton selectedNavigationButton;

    public DashboardFrame() {
        setTitle("Footwear Inventory Management");
        setSize(1200, 750);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createSidebar();
        createContentContainer();
        showDashboard();
    }

    private void createSidebar() {
        JPanel sidebar = new JPanel();

        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                new EmptyBorder(20, 12, 20, 12));

        JLabel appTitle = new JLabel("FOOTWEAR");
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(
                new Font("SansSerif", Font.BOLD, 23));
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle =
                new JLabel("Inventory Management");
        subtitle.setForeground(new Color(190, 198, 215));
        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(appTitle);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(35));

        addNavigationButton(
                sidebar, "Dashboard", this::showDashboard);

        addNavigationButton(
                sidebar, "Products", this::showProducts);

        addNavigationButton(
                sidebar, "Brands", this::showBrands);

        // Opens the Category Management screen.
        addNavigationButton(
                sidebar, "Categories", this::showCategories);

        addNavigationButton(
                sidebar, "Stock Management", this::showStock);

        // Opens the Sales Management screen.
        addNavigationButton(
                sidebar, "Sales", this::showSales);

        // Opens the screen containing all three reports.
        addNavigationButton(
                sidebar, "Reports", this::showReports);

        sidebar.add(Box.createVerticalGlue());

        addNavigationButton(
                sidebar, "Low-Stock Alerts", this::showLowStock);

        JLabel footer =
                new JLabel("Footwear Inventory v1.0");
        footer.setForeground(new Color(180, 188, 205));
        footer.setFont(
                new Font("SansSerif", Font.PLAIN, 10));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(footer);

        add(sidebar, BorderLayout.WEST);
    }

    private void addNavigationButton(
            JPanel sidebar, String text, Runnable action) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(SIDEBAR_COLOR);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(
                new EmptyBorder(12, 14, 12, 8));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> {
            // Reset the previously selected button.
            if (selectedNavigationButton != null) {
                selectedNavigationButton.setBackground(
                        SIDEBAR_COLOR);
            }

            // Highlight the newly selected button.
            selectedNavigationButton = button;
            selectedNavigationButton.setBackground(
                    SELECTED_COLOR);

            // Open the selected screen.
            action.run();
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedNavigationButton) {
                    button.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedNavigationButton) {
                    button.setBackground(SIDEBAR_COLOR);
                }
            }
        });

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));

        // Select Dashboard by default when the sidebar is created.
        if ("Dashboard".equals(text)) {
            selectedNavigationButton = button;
            button.setBackground(SELECTED_COLOR);
        }
    }

    private void createContentContainer() {
        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BACKGROUND_COLOR);

        add(contentContainer, BorderLayout.CENTER);
    }

    private void showDashboard() {
        setContent(createDashboardContent());

        // Refresh after the dashboard components are created.
        refreshDashboardSummary();
        refreshDashboardCharts();
    }

    private JPanel createDashboardContent() {
        JPanel mainPanel =
                new JPanel(new BorderLayout(0, 18));

        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(
                new EmptyBorder(22, 24, 22, 24));

        // Dashboard header
        JPanel headerPanel =
                new JPanel(new BorderLayout(15, 0));

        headerPanel.setOpaque(false);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Dashboard");
        heading.setFont(
                new Font("SansSerif", Font.BOLD, 26));
        heading.setForeground(TEXT_COLOR);

        JLabel description =
                new JLabel("Welcome to Footwear Inventory");

        description.setFont(
                new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(
                new Color(115, 123, 137));

        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(description);

        JButton refreshButton =
                new JButton("Refresh Dashboard");

        refreshButton.setFont(
                new Font("SansSerif", Font.BOLD, 12));
        refreshButton.setPreferredSize(
                new Dimension(185, 38));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);

        refreshButton.addActionListener(e -> {
            refreshButton.setEnabled(false);

            try {
                refreshDashboardSummary();
                refreshDashboardCharts();
            } finally {
                refreshButton.setEnabled(true);
            }
        });

        headerPanel.add(
                headingPanel, BorderLayout.CENTER);
        headerPanel.add(
                refreshButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Dashboard body
        JPanel bodyPanel = new JPanel();

        bodyPanel.setOpaque(false);
        bodyPanel.setLayout(
                new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));

        // Summary cards
        JPanel summaryCards =
                new JPanel(new GridLayout(1, 4, 14, 0));

        summaryCards.setOpaque(false);
        summaryCards.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        summaryCards.setPreferredSize(
                new Dimension(800, 135));
        summaryCards.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 135));
        summaryCards.setMinimumSize(
                new Dimension(600, 125));

        summaryCards.add(createSummaryCard(
                "Total Products",
                "0",
                "Products registered"));

        summaryCards.add(createSummaryCard(
                "Total Brands",
                "0",
                "Brands registered"));

        summaryCards.add(createSummaryCard(
                "Stock Overview",
                "0",
                "Total pairs in stock"));

        summaryCards.add(createSummaryCard(
                "Low-Stock Items",
                "0",
                "Size entries at or below minimum"));

        bodyPanel.add(summaryCards);
        bodyPanel.add(Box.createVerticalStrut(18));

        // Graph panels
        JPanel chartsPanel =
                new JPanel(new GridLayout(1, 2, 15, 0));

        chartsPanel.setOpaque(false);
        chartsPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        chartsPanel.setPreferredSize(
                new Dimension(800, 245));
        chartsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 245));
        chartsPanel.setMinimumSize(
                new Dimension(600, 210));

        stockByCategoryChart =
                new DashboardChartPanel(
                        "Stock by Category",
                        new Color(124, 92, 252));  // Violet

        salesLastSevenDaysChart =
                new DashboardChartPanel(
                        "Sales - Last 7 Days",
                        new Color(20, 184, 166));  // Teal

        chartsPanel.add(stockByCategoryChart);
        chartsPanel.add(salesLastSevenDaysChart);

        bodyPanel.add(chartsPanel);
        bodyPanel.add(Box.createVerticalStrut(15));

        // Quick Actions card
        JPanel quickActionsPanel =
                new JPanel(new BorderLayout(0, 12));

        quickActionsPanel.setBackground(CARD_COLOR);
        quickActionsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 229, 236)),
                        new EmptyBorder(14, 18, 14, 18)));

        quickActionsPanel.setAlignmentX(
                Component.LEFT_ALIGNMENT);
        quickActionsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 110));

        JLabel quickActionsTitle =
                new JLabel("Quick Actions");

        quickActionsTitle.setFont(
                new Font("SansSerif", Font.BOLD, 16));
        quickActionsTitle.setForeground(TEXT_COLOR);

        JPanel actionsRow =
                new JPanel(new FlowLayout(
                        FlowLayout.LEFT, 12, 0));

        actionsRow.setOpaque(false);

        JButton manageProductsButton =
                createActionButton("Manage Products", 175);

        manageProductsButton.addActionListener(
                e -> showProducts());

        JButton manageStockButton =
                createActionButton("Manage Stock", 175);

        manageStockButton.addActionListener(
                e -> showStock());

        actionsRow.add(manageProductsButton);
        actionsRow.add(manageStockButton);

        quickActionsPanel.add(
                quickActionsTitle, BorderLayout.NORTH);
        quickActionsPanel.add(
                actionsRow, BorderLayout.CENTER);

        bodyPanel.add(quickActionsPanel);

        // Allow scrolling on smaller screens.
        JScrollPane scrollPane = new JScrollPane(bodyPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createSummaryCard(
            String title, String initialValue, String description) {

        JPanel card = new JPanel();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 229, 236)),
                        new EmptyBorder(15, 15, 14, 15)));

        card.setMinimumSize(new Dimension(130, 120));

        JLabel titleLabel;

        if ("Low-Stock Items".equals(title)) {
            titleLabel = new JLabel("\u26A0  " + title);
            titleLabel.setForeground(new Color(245, 158, 11));
        } else {
            titleLabel = new JLabel(title);
            titleLabel.setForeground(new Color(90, 99, 115));
        }

        titleLabel.setFont(
                new Font("SansSerif", Font.BOLD, 13));

        titleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 29));
        valueLabel.setForeground(PRIMARY_COLOR);
        valueLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 10));
        descriptionLabel.setForeground(
                new Color(125, 132, 145));
        descriptionLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT);

        if ("Total Products".equals(title)) {
            totalProductsValue = valueLabel;
        } else if ("Total Brands".equals(title)) {
            totalBrandsValue = valueLabel;
        } else if ("Stock Overview".equals(title)) {
            totalStockValue = valueLabel;
        } else if ("Low-Stock Items".equals(title)) {
            lowStockValue = valueLabel;

            // Use amber color to highlight low-stock items.
            valueLabel.setForeground(new Color(245, 158, 11));
        }

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(descriptionLabel);

        return card;
    }

    private JButton createActionButton(
            String text, int width) {

        JButton button = new JButton(text);
        button.setFont(
                new Font("SansSerif", Font.BOLD, 13));

        Dimension buttonSize =
                new Dimension(width, 38);

        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.setHorizontalAlignment(
                SwingConstants.CENTER);
        button.setMargin(
                new Insets(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void refreshDashboardSummary() {
        try {
            DashboardSummary summary =
                    dashboardSummaryService.getSummary();

            if (totalProductsValue != null) {
                totalProductsValue.setText(
                        String.valueOf(
                                summary.getTotalProducts()));
            }

            if (totalBrandsValue != null) {
                totalBrandsValue.setText(
                        String.valueOf(
                                summary.getTotalBrands()));
            }

            if (totalStockValue != null) {
                totalStockValue.setText(
                        String.valueOf(
                                summary.getTotalStock()));
            }

            if (lowStockValue != null) {

                int lowStockCount = summary.getLowStockItems();

                // Display the current low-stock count.
                lowStockValue.setText(
                        String.valueOf(lowStockCount));

                // Change the number's color based on stock status.
                if (lowStockCount > 0) {
                    lowStockValue.setForeground(
                            new Color(245, 158, 11)); // Amber warning
                } else {
                    lowStockValue.setForeground(
                            new Color(45, 52, 65)); // Normal dark color
                }
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to refresh dashboard summary.\n"
                            + ex.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDashboardCharts() {
        if (stockByCategoryChart == null
                || salesLastSevenDaysChart == null) {
            return;
        }

        try {
            List<Object[]> stockData =
                    dashboardSummaryService.getStockByCategory();

            List<Object[]> salesData =
                    dashboardSummaryService.getSalesLastSevenDays();

            stockByCategoryChart.setChartData(stockData);
            salesLastSevenDaysChart.setChartData(salesData);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to refresh dashboard graphs.\n"
                            + ex.getMessage(),
                    "Dashboard Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProducts() {
        setContent(new ProductManagementPanel());
    }

    private void showBrands() {
        setContent(new BrandsManagementPanel());
    }

    private void showCategories() {
        setContent(new CategoriesManagementPanel());
    }

    private void showLowStock() {
        setContent(new LowStockPanel());
    }

    private void showStock() {
        setContent(new StockManagementPanel());
    }

    // Opens the Sales Management panel.
    private void showSales() {
        setContent(new SalesManagementPanel());
    }

    // Opens the Reports screen with Stock, Sales and Low-Stock tabs.
    private void showReports() {
        setContent(new ReportsPanel());
    }

    private void showPlaceholder(String sectionName) {
        JOptionPane.showMessageDialog(
                this,
                sectionName + " screen will be implemented later.",
                sectionName,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void setContent(JPanel panel) {
        contentContainer.removeAll();
        contentContainer.add(panel, BorderLayout.CENTER);
        contentContainer.revalidate();
        contentContainer.repaint();
    }
}
