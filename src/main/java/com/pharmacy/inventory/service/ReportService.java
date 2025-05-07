package com.pharmacy.inventory.service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Service for generating various reports for the pharmacy management system
 */
public interface ReportService {
    
    /**
     * Generate a sales report for a specific date range
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the PDF report to
     */
    void generateSalesReport(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate a sales report for a specific date range in Excel format
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the Excel report to
     */
    void generateSalesReportExcel(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate an inventory status report
     * 
     * @param includeZeroStock Whether to include items with zero stock
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the PDF report to
     */
    void generateInventoryReport(boolean includeZeroStock, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate an inventory status report in Excel format
     * 
     * @param includeZeroStock Whether to include items with zero stock
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the Excel report to
     */
    void generateInventoryReportExcel(boolean includeZeroStock, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate an expiry report showing medicines that will expire within a certain period
     * 
     * @param days Number of days to look ahead for expiration
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the PDF report to
     */
    void generateExpiryReport(int days, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate an expiry report in Excel format
     * 
     * @param days Number of days to look ahead for expiration
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the Excel report to
     */
    void generateExpiryReportExcel(int days, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate a supplier performance report
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param outputStream The output stream to write the PDF report to
     */
    void generateSupplierPerformanceReport(LocalDate startDate, LocalDate endDate, OutputStream outputStream);
    
    /**
     * Generate a supplier performance report in Excel format
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param outputStream The output stream to write the Excel report to
     */
    void generateSupplierPerformanceReportExcel(LocalDate startDate, LocalDate endDate, OutputStream outputStream);
    
    /**
     * Generate a purchase order report for a specific date range
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the PDF report to
     */
    void generatePurchaseOrderReport(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate a purchase order report in Excel format
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the Excel report to
     */
    void generatePurchaseOrderReportExcel(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate a profit and loss report for a specific date range
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the PDF report to
     */
    void generateProfitLossReport(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream);
    
    /**
     * Generate a profit and loss report in Excel format
     * 
     * @param startDate The start date of the report period
     * @param endDate The end date of the report period
     * @param branchId Optional branch ID to filter by (null for all branches)
     * @param outputStream The output stream to write the Excel report to
     */
    void generateProfitLossReportExcel(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream);
}
