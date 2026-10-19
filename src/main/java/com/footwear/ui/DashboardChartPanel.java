
        package com.footwear.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class DashboardChartPanel extends JPanel {

    private final String chartTitle;
    private final Color barColor;

    private List<String> labels = new ArrayList<String>();
    private List<Long> values = new ArrayList<Long>();

    public DashboardChartPanel(String chartTitle, Color barColor) {
        this.chartTitle = chartTitle;
        this.barColor = barColor;

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 229, 236)),
                BorderFactory.createEmptyBorder(12, 15, 10, 15)
        ));

        setPreferredSize(new Dimension(400, 245));
        setMinimumSize(new Dimension(250, 200));
    }

    public void setChartData(List<Object[]> records) {
        labels = new ArrayList<String>();
        values = new ArrayList<Long>();

        for (Object[] record : records) {
            labels.add(String.valueOf(record[0]));
            values.add(((Number) record[1]).longValue());
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

            g.setColor(new Color(45, 52, 65));
            g.setFont(new Font("SansSerif", Font.BOLD, 15));
            g.drawString(chartTitle, 15, 25);

            if (labels.isEmpty()) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g.setColor(Color.GRAY);
                g.drawString(
                        "No data available",
                        20,
                        Math.max(60, height / 2));
                return;
            }

            int left = 38;
            int right = 15;
            int top = 48;
            int bottom = 43;

            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            if (chartWidth <= 0 || chartHeight <= 0) {
                return;
            }

            long maxValue = 0;

            for (Long value : values) {
                if (value > maxValue) {
                    maxValue = value;
                }
            }

            if (maxValue == 0) {
                maxValue = 1;
            }

            // Draw horizontal guide lines and scale labels.
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.setColor(new Color(125, 132, 145));

            for (int i = 0; i <= 4; i++) {
                int y = top + chartHeight
                        - (i * chartHeight / 4);

                long scaleValue = maxValue * i / 4;

                g.drawString(
                        String.valueOf(scaleValue),
                        2,
                        y + 4);

                g.setColor(new Color(235, 238, 243));
                g.drawLine(left, y, width - right, y);
                g.setColor(new Color(125, 132, 145));
            }

            int count = labels.size();
            double slotWidth = (double) chartWidth / count;
            int barWidth = Math.max(
                    5,
                    (int) (slotWidth * 0.55));

            for (int i = 0; i < count; i++) {
                long value = values.get(i);

                int barHeight = (int) Math.round(
                        (double) value / maxValue * chartHeight);

                int x = left
                        + (int) (i * slotWidth)
                        + ((int) slotWidth - barWidth) / 2;

                int y = top + chartHeight - barHeight;

                g.setColor(barColor);

                if (barHeight > 0) {
                    g.fillRoundRect(
                            x,
                            y,
                            barWidth,
                            barHeight,
                            12,
                            12);
                }

                // Value above each bar.
                g.setColor(new Color(45, 52, 65));
                g.setFont(new Font("SansSerif", Font.BOLD, 10));

                String valueText = String.valueOf(value);
                FontMetrics fm = g.getFontMetrics();

                int valueX = x + (barWidth
                        - fm.stringWidth(valueText)) / 2;

                int valueY = Math.max(top + 11, y - 5);

                g.drawString(valueText, valueX, valueY);

                // Category or weekday label.
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));

                String label = labels.get(i);

                int maxLabelWidth = Math.max(
                        20, (int) slotWidth - 2);

                if (fm.stringWidth(label) > maxLabelWidth) {
                    while (label.length() > 3
                            && fm.stringWidth(label + "...") > maxLabelWidth) {
                        label = label.substring(0, label.length() - 1);
                    }

                    if (!label.equals(labels.get(i))) {
                        label = label + "...";
                    }
                }

                fm = g.getFontMetrics();

                int labelX = x + (barWidth
                        - fm.stringWidth(label)) / 2;

                g.setColor(new Color(90, 99, 115));
                g.drawString(
                        label,
                        labelX,
                        top + chartHeight + 17);
            }

        } finally {
            g.dispose();
        }
    }
}
