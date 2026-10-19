
package com.footwear;

import com.footwear.ui.DashboardFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                DashboardFrame dashboard = new DashboardFrame();
                dashboard.setVisible(true);
            }
        });
    }
}
