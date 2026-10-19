
package com.footwear.service;

import com.footwear.dao.ReportsDAO;

import java.sql.Date;
import java.util.List;

public class ReportsService {

    private final ReportsDAO reportsDAO;

    public ReportsService() {
        reportsDAO = new ReportsDAO();
    }

    public List<Object[]> getStockReport() throws Exception {
        return reportsDAO.getStockReport();
    }

    public List<Object[]> getLowStockReport() throws Exception {
        return reportsDAO.getLowStockReport();
    }

    public List<Object[]> getSalesReport(
            Date startDate, Date endDate) throws Exception {

        if (startDate != null && endDate != null
                && startDate.after(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date.");
        }

        return reportsDAO.getSalesReport(startDate, endDate);
    }
}
