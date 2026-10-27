
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
import java.time.LocalTime;
import java.util.List;

public class DashboardFrame extends JFrame {

    // =========================================================
    // DASHBOARD COLOR PALETTE
    // =========================================================

    private static final Color SIDEBAR_COLOR =
            new Color(22, 35, 63);

    private static final Color SIDEBAR_HOVER =
            new Color(35, 53, 91);

    private static final Color SELECTED_COLOR =
            new Color(43, 99, 220);

    private static final Color BACKGROUND_COLOR =
            new Color(244, 247, 252);

    private static final Color PRIMARY_COLOR =
            new Color(37, 99, 235);

    private static final Color TEXT_COLOR =
            new Color(26, 39, 69);

    private static final Color MUTED_COLOR =
            new Color(105, 119, 145);

    private static final Color BORDER_COLOR =
            new Color(224, 232, 244);

    private static final Font FONT =
            new Font("SansSerif", Font.PLAIN, 13);

    private static final Font FONT_BOLD =
            new Font("SansSerif", Font.BOLD, 13);

    // =========================================================
    // DASHBOARD VARIABLES
    // =========================================================

    private final DashboardSummaryService dashboardSummaryService =
            new DashboardSummaryService();

    private JPanel contentContainer;

    private JLabel totalProductsValue;
    private JLabel totalBrandsValue;
    private JLabel totalStockValue;
    private JLabel lowStockValue;

    private DashboardChartPanel stockByCategoryChart;
    private DashboardChartPanel salesLastSevenDaysChart;

    private JButton selectedNavigationButton;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public DashboardFrame() {

        setTitle("Footwear Inventory Management");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND_COLOR);

        createSidebar();
        createContentContainer();
        showDashboard();
    }

    // =========================================================
    // PERSONALIZED GREETING
    // =========================================================

    private String getGreeting() {

        int hour = java.time.LocalTime.now().getHour();

        if (hour >= 5 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private void createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(232, 0));
        sidebar.setLayout(
                new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(
                new EmptyBorder(25, 14, 18, 14));

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(
                new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel brandIcon = new JLabel("\u25C8");
        brandIcon.setForeground(new Color(125, 170, 255));
        brandIcon.setFont(
                new Font("SansSerif", Font.BOLD, 27));
        brandIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appTitle = new JLabel("FOOTWEAR");
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(
                new Font("SansSerif", Font.BOLD, 23));
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Inventory Management");
        subtitle.setForeground(new Color(180, 196, 222));
        subtitle.setFont(
                new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        brandPanel.add(brandIcon);
        brandPanel.add(Box.createVerticalStrut(7));
        brandPanel.add(appTitle);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(subtitle);

        sidebar.add(brandPanel);
        sidebar.add(Box.createVerticalStrut(35));

        addNavigationButton(
                sidebar, "\u2302", "Dashboard",
                this::showDashboard);

        addNavigationButton(
                sidebar, "\u25A3", "Products",
                this::showProducts);

        addNavigationButton(
                sidebar, "\u25C6", "Brands",
                this::showBrands);

        addNavigationButton(
                sidebar, "\u25A6", "Categories",
                this::showCategories);

        addNavigationButton(
                sidebar, "\u25CE", "Stock Management",
                this::showStock);

        addNavigationButton(
                sidebar, "\u25A5", "Sales",
                this::showSales);

        addNavigationButton(
                sidebar, "\u26A0", "Low-Stock Alerts",
                this::showLowStock);

        addNavigationButton(
                sidebar, "\u2637", "Reports",
                this::showReports);

        sidebar.add(Box.createVerticalGlue());

        JPanel footerLine = new JPanel();
        footerLine.setBackground(new Color(57, 74, 108));
        footerLine.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 1));
        footerLine.setPreferredSize(new Dimension(190, 1));
        footerLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(footerLine);
        sidebar.add(Box.createVerticalStrut(14));

        JLabel footer =
                new JLabel("Footwear Inventory  \u2022  v1.0");

        footer.setForeground(new Color(174, 190, 216));
        footer.setFont(
                new Font("SansSerif", Font.PLAIN, 10));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(footer);

        add(sidebar, BorderLayout.WEST);
    }

    private void addNavigationButton(
            JPanel sidebar,
            String icon,
            String text,
            Runnable action) {

        JButton button = new JButton(icon + "   " + text);

        button.setFont(FONT);
        button.setForeground(new Color(229, 237, 250));
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
                new Dimension(Integer.MAX_VALUE, 47));
        button.setPreferredSize(new Dimension(204, 47));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> {

            if (selectedNavigationButton != null) {

                selectedNavigationButton.setBackground(
                        SIDEBAR_COLOR);

                selectedNavigationButton.setForeground(
                        new Color(229, 237, 250));
            }

            selectedNavigationButton = button;

            selectedNavigationButton.setBackground(
                    SELECTED_COLOR);

            selectedNavigationButton.setForeground(
                    Color.WHITE);

            action.run();
        });

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                if (button != selectedNavigationButton) {
                    button.setBackground(SIDEBAR_HOVER);
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

        if ("Dashboard".equals(text)) {

            selectedNavigationButton = button;
            button.setBackground(SELECTED_COLOR);
            button.setForeground(Color.WHITE);
        }
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private void createContentContainer() {

        contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BACKGROUND_COLOR);

        add(contentContainer, BorderLayout.CENTER);
    }

    private void showDashboard() {

        setContent(createDashboardContent());

        refreshDashboardSummary();
        refreshDashboardCharts();
    }

    // =========================================================
    // DASHBOARD UI
    // =========================================================

    private JPanel createDashboardContent() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(0, 22));

        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(
                new EmptyBorder(27, 28, 26, 28));

        // ---------------- HEADER ----------------

        JPanel headerPanel = new JPanel(
                new BorderLayout(16, 0));

        headerPanel.setOpaque(false);

        JPanel headingPanel = new JPanel();

        headingPanel.setOpaque(false);
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS));

        // Personalized greeting
        JLabel greetingLabel =
                new JLabel(getGreeting() + ", Kamlesh!");

        greetingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 17));

        greetingLabel.setForeground(PRIMARY_COLOR);
        greetingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dashboard heading
        JLabel heading = new JLabel("Dashboard");

        heading.setFont(
                new Font("SansSerif", Font.BOLD, 31));

        heading.setForeground(TEXT_COLOR);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Dashboard description
        JLabel description = new JLabel(
                "Welcome back to your footwear inventory overview");

        description.setFont(
                new Font("SansSerif", Font.PLAIN, 14));

        description.setForeground(MUTED_COLOR);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add greeting above dashboard title
        headingPanel.add(greetingLabel);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(description);

        // Header actions
        JPanel headerActions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 4));

        headerActions.setOpaque(false);

        JLabel updatedLabel =
                new JLabel("Inventory overview");

        updatedLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 12));

        updatedLabel.setForeground(MUTED_COLOR);

        JButton refreshButton =
                new JButton("\u21BB  Refresh");

        refreshButton.setFont(FONT_BOLD);
        refreshButton.setPreferredSize(
                new Dimension(112, 40));

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

        headerActions.add(updatedLabel);
        headerActions.add(refreshButton);

        headerPanel.add(
                headingPanel, BorderLayout.CENTER);

        headerPanel.add(
                headerActions, BorderLayout.EAST);

        mainPanel.add(
                headerPanel, BorderLayout.NORTH);

        // ---------------- DASHBOARD BODY ----------------

        JPanel bodyPanel = new JPanel();

        bodyPanel.setOpaque(false);
        bodyPanel.setLayout(
                new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));

        // ---------------- SUMMARY CARDS ----------------

        JPanel summaryCards = new JPanel(
                new GridLayout(1, 4, 16, 0));

        summaryCards.setOpaque(false);
        summaryCards.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryCards.setPreferredSize(
                new Dimension(850, 148));

        summaryCards.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 148));

        summaryCards.setMinimumSize(
                new Dimension(650, 140));

        summaryCards.add(
                createSummaryCard(
                        "Total Products",
                        "0",
                        "Products registered",
                        new Color(37, 99, 235),
                        "\u25A3"));

        summaryCards.add(
                createSummaryCard(
                        "Total Brands",
                        "0",
                        "Brands registered",
                        new Color(111, 76, 235),
                        "\u25C6"));

        summaryCards.add(
                createSummaryCard(
                        "Stock Overview",
                        "0",
                        "Total pairs in stock",
                        new Color(8, 163, 181),
                        "\u25CE"));

        summaryCards.add(
                createSummaryCard(
                        "Low-Stock Items",
                        "0",
                        "Size entries at or below minimum",
                        new Color(239, 159, 24),
                        "\u26A0"));

        bodyPanel.add(summaryCards);

        bodyPanel.add(Box.createVerticalStrut(20));

        // ---------------- CHARTS ----------------

        JPanel chartsPanel = new JPanel(
                new GridLayout(1, 2, 16, 0));

        chartsPanel.setOpaque(false);
        chartsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        chartsPanel.setPreferredSize(
                new Dimension(850, 300));

        chartsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 300));

        chartsPanel.setMinimumSize(
                new Dimension(650, 260));

        stockByCategoryChart =
                new DashboardChartPanel(
                        "Stock by Category",
                        new Color(105, 83, 245));

        salesLastSevenDaysChart =
                new DashboardChartPanel(
                        "Sales - Last 7 Days",
                        new Color(37, 132, 232));

        styleChartPanel(stockByCategoryChart);
        styleChartPanel(salesLastSevenDaysChart);

        chartsPanel.add(stockByCategoryChart);
        chartsPanel.add(salesLastSevenDaysChart);

        bodyPanel.add(chartsPanel);

        bodyPanel.add(Box.createVerticalStrut(20));

        // ---------------- QUICK ACTIONS ----------------

        RoundedPanel quickActionsPanel =
                new RoundedPanel(18, Color.WHITE);

        quickActionsPanel.setLayout(
                new BorderLayout(0, 15));

        quickActionsPanel.setBorder(
                new EmptyBorder(18, 20, 20, 20));

        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        quickActionsPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 140));

        quickActionsPanel.setPreferredSize(
                new Dimension(850, 140));

        JLabel quickActionsTitle =
                new JLabel("\u26A1  Quick Actions");

        quickActionsTitle.setFont(
                new Font("SansSerif", Font.BOLD, 18));

        quickActionsTitle.setForeground(TEXT_COLOR);

        JPanel actionsRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 0));

        actionsRow.setOpaque(false);

        JButton manageProductsButton =
                createActionButton(
                        "\u25A3   Manage Products",
                        205,
                        new Color(37, 99, 235));

        manageProductsButton.addActionListener(
                e -> showProducts());

        JButton manageStockButton =
                createActionButton(
                        "\u25CE   Manage Stock",
                        205,
                        new Color(105, 76, 235));

        manageStockButton.addActionListener(
                e -> showStock());

        actionsRow.add(manageProductsButton);
        actionsRow.add(manageStockButton);

        quickActionsPanel.add(
                quickActionsTitle, BorderLayout.NORTH);

        quickActionsPanel.add(
                actionsRow, BorderLayout.CENTER);

        bodyPanel.add(quickActionsPanel);

        // ---------------- SCROLL SUPPORT ----------------

        JScrollPane scrollPane =
                new JScrollPane(bodyPanel);

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(
                scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================================================
    // CHART STYLING
    // =========================================================

    private void styleChartPanel(JPanel chartPanel) {

        chartPanel.setBackground(Color.WHITE);

        chartPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER_COLOR),
                        new EmptyBorder(8, 8, 8, 8)));
    }

    // =========================================================
    // SUMMARY CARD CREATION
    // =========================================================

    private JPanel createSummaryCard(
            String title,
            String initialValue,
            String description,
            Color accent,
            String icon) {

        RoundedPanel card =
                new RoundedPanel(17, Color.WHITE);

        card.setLayout(new BorderLayout(0, 0));

        // Colored accent strip
        JPanel accentBar = new JPanel();

        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(1, 4));

        card.add(accentBar, BorderLayout.NORTH);

        JPanel inner = new JPanel();

        inner.setOpaque(false);
        inner.setBorder(
                new EmptyBorder(14, 16, 14, 15));

        inner.setLayout(
                new BoxLayout(inner, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(
                new BorderLayout(8, 0));

        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);

        iconLabel.setFont(
                new Font("SansSerif", Font.BOLD, 18));

        iconLabel.setForeground(accent);
        iconLabel.setPreferredSize(new Dimension(25, 25));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(FONT_BOLD);

        titleLabel.setForeground(
                "Low-Stock Items".equals(title)
                        ? accent : MUTED_COLOR);

        titleRow.add(iconLabel, BorderLayout.WEST);
        titleRow.add(titleLabel, BorderLayout.CENTER);

        JLabel valueLabel = new JLabel(initialValue);

        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 31));

        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                new Font("SansSerif", Font.PLAIN, 10));

        descriptionLabel.setForeground(MUTED_COLOR);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Store references for updating the values
        if ("Total Products".equals(title)) {

            totalProductsValue = valueLabel;

        } else if ("Total Brands".equals(title)) {

            totalBrandsValue = valueLabel;

        } else if ("Stock Overview".equals(title)) {

            totalStockValue = valueLabel;

        } else if ("Low-Stock Items".equals(title)) {

            lowStockValue = valueLabel;
        }

        inner.add(titleRow);
        inner.add(Box.createVerticalStrut(13));
        inner.add(valueLabel);
        inner.add(Box.createVerticalStrut(4));
        inner.add(descriptionLabel);

        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    // =========================================================
    // QUICK ACTION BUTTON
    // =========================================================

    private JButton createActionButton(
            String text,
            int width,
            Color background) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 13));

        Dimension buttonSize =
                new Dimension(width, 48);

        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.setHorizontalAlignment(
                SwingConstants.CENTER);

        button.setMargin(
                new Insets(8, 14, 8, 14));

        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(background.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });

        return button;
    }

    // =========================================================
    // REFRESH DASHBOARD SUMMARY
    // =========================================================

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

                int lowStockCount =
                        summary.getLowStockItems();

                lowStockValue.setText(
                        String.valueOf(lowStockCount));

                lowStockValue.setForeground(
                        lowStockCount > 0
                                ? new Color(239, 159, 24)
                                : new Color(16, 145, 102));
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

    // =========================================================
    // REFRESH DASHBOARD CHARTS
    // =========================================================

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

    // =========================================================
    // NAVIGATION METHODS
    // =========================================================

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

    private void showSales() {
        setContent(new SalesManagementPanel());
    }

    private void showReports() {
        setContent(new ReportsPanel());
    }

    // =========================================================
    // SET MAIN CONTENT
    // =========================================================

    private void setContent(JPanel panel) {

        contentContainer.removeAll();

        contentContainer.add(
                panel, BorderLayout.CENTER);

        contentContainer.revalidate();
        contentContainer.repaint();
    }

    // =========================================================
    // ROUNDED PANEL
    // =========================================================

    private static class RoundedPanel extends JPanel {

        private final int radius;
        private final Color fillColor;

        RoundedPanel(int radius, Color fillColor) {

            this.radius = radius;
            this.fillColor = fillColor;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {

            Graphics2D g2 =
                    (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(fillColor);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius);

            g2.dispose();

            super.paintComponent(graphics);
        }

        @Override
        protected void paintBorder(Graphics graphics) {

            Graphics2D g2 =
                    (Graphics2D) graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(BORDER_COLOR);

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius);

            g2.dispose();
        }
    }
}
