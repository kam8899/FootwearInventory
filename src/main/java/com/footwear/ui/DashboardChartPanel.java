
package com.footwear.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DashboardChartPanel extends JPanel {

    private final String chartTitle;
    private final Color barColor;

    private List<String> labels = new ArrayList<String>();
    private List<Long> values = new ArrayList<Long>();

    private static final Color TEXT_COLOR =
            new Color(45, 52, 65);

    private static final Color MUTED_COLOR =
            new Color(115, 123, 137);

    private static final Color GRID_COLOR =
            new Color(232, 237, 244);

    public DashboardChartPanel(String chartTitle, Color barColor) {
        this.chartTitle = chartTitle;
        this.barColor = barColor;

        setBackground(Color.WHITE);

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 231, 239)),
                new EmptyBorder(12, 15, 12, 15)
        ));

        setPreferredSize(new Dimension(400, 260));
        setMinimumSize(new Dimension(250, 220));
        setOpaque(true);
    }

    public void setChartData(List<Object[]> records) {
        labels = new ArrayList<String>();
        values = new ArrayList<Long>();

        if (records != null) {
            for (Object[] record : records) {
                if (record == null || record.length < 2
                        || record[0] == null
                        || !(record[1] instanceof Number)) {
                    continue;
                }

                labels.add(String.valueOf(record[0]));
                values.add(((Number) record[1]).longValue());
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics.create();

        try {
            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Chart heading
            g.setColor(TEXT_COLOR);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString(chartTitle, 15, 24);

            // Small blue accent line under the heading
            g.setColor(barColor);
            g.fillRoundRect(15, 34, 38, 3, 3, 3);

            if (labels.isEmpty()) {
                g.setColor(MUTED_COLOR);
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));

                String message = "No data available";
                FontMetrics fm = g.getFontMetrics();

                g.drawString(
                        message,
                        Math.max(15, (width - fm.stringWidth(message)) / 2),
                        height / 2);

                return;
            }

            int left = 43;
            int right = 15;
            int top = 55;
            int bottom = 42;

            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            if (chartWidth <= 0 || chartHeight <= 0) {
                return;
            }

            long maxValue = 0;

            for (Long value : values) {
                if (value != null && value > maxValue) {
                    maxValue = value;
                }
            }

            if (maxValue <= 0) {
                maxValue = 1;
            }

            // Draw horizontal grid lines and scale values.
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));

            for (int i = 0; i <= 4; i++) {
                int y = top + chartHeight
                        - (i * chartHeight / 4);

                long scaleValue = maxValue * i / 4;

                g.setColor(MUTED_COLOR);
                g.drawString(String.valueOf(scaleValue), 2, y + 4);

                g.setColor(GRID_COLOR);
                g.drawLine(left, y, width - right, y);
            }

            int count = labels.size();
            double slotWidth = (double) chartWidth / count;

            int barWidth = Math.max(
                    8,
                    Math.min(38, (int) (slotWidth * 0.55)));

            for (int i = 0; i < count; i++) {
                long value = Math.max(0, values.get(i));

                int barHeight = (int) Math.round(
                        (double) value / maxValue * chartHeight);

                int x = left + (int) (i * slotWidth)
                        + ((int) slotWidth - barWidth) / 2;

                int y = top + chartHeight - barHeight;

                // Rounded bar
                if (barHeight > 0) {
                    g.setColor(barColor);
                    g.fill(new RoundRectangle2D.Double(
                            x, y, barWidth, barHeight, 9, 9));
                }

                // Value above each bar
                String valueText = String.valueOf(value);

                g.setFont(new Font("SansSerif", Font.BOLD, 10));
                g.setColor(TEXT_COLOR);

                FontMetrics valueMetrics = g.getFontMetrics();

                int valueX = x + (barWidth
                        - valueMetrics.stringWidth(valueText)) / 2;

                int valueY = Math.max(
                        top + 12,
                        y - 5);

                g.drawString(valueText, valueX, valueY);

                // Category or date label
                String label = labels.get(i);

                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                FontMetrics labelMetrics = g.getFontMetrics();

                int availableWidth = Math.max(
                        15, (int) slotWidth - 2);

                while (label.length() > 3
                        && labelMetrics.stringWidth(label) > availableWidth) {
                    label = label.substring(0, label.length() - 1);
                }

                if (!label.equals(labels.get(i))) {
                    label = label + "...";
                }

                int labelX = x + (barWidth
                        - labelMetrics.stringWidth(label)) / 2;

                // Keep the label within its chart slot.
                labelX = Math.max(
                        left + (int) (i * slotWidth),
                        labelX);

                g.setColor(MUTED_COLOR);
                g.drawString(
                        label,
                        labelX,
                        top + chartHeight + 18);
            }

        } finally {
            g.dispose();
        }
    }
}
