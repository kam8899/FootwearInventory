
package com.footwear.service;

import com.footwear.dao.DashboardSummaryDAO;
import com.footwear.model.DashboardSummary;

import java.util.List;

public class DashboardSummaryService {

    private final DashboardSummaryDAO dashboardSummaryDAO;

    public DashboardSummaryService() {
        dashboardSummaryDAO = new DashboardSummaryDAO();
    }

    public DashboardSummary getSummary() {
        return dashboardSummaryDAO.getSummary();
    }

    public List<Object[]> getStockByCategory() {
        return dashboardSummaryDAO.getStockByCategory();
    }

    public List<Object[]> getSalesLastSevenDays() {
        return dashboardSummaryDAO.getSalesLastSevenDays();
    }
}