package com.pharmacy.inventory.controller;

import com.pharmacy.inventory.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reporting", description = "APIs for generating various reports")
public class ReportController {

    private final ReportService reportService;
    
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/sales")
    @Operation(summary = "Generate sales report", description = "Generates a PDF sales report for a specific date range")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generateSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        setReportFilename(response, "sales_report", startDate, endDate);
        
        reportService.generateSalesReport(startDate, endDate, branchId, response.getOutputStream());
    }

    @GetMapping("/sales/excel")
    @Operation(summary = "Generate sales report Excel", description = "Generates an Excel sales report for a specific date range")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generateSalesReportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType("application/vnd.ms-excel");
        setReportFilename(response, "sales_report", startDate, endDate, ".xlsx");
        
        reportService.generateSalesReportExcel(startDate, endDate, branchId, response.getOutputStream());
    }

    @GetMapping("/inventory")
    @Operation(summary = "Generate inventory report", description = "Generates a PDF inventory report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PHARMACIST')")
    public void generateInventoryReport(
            @RequestParam(defaultValue = "false") boolean includeZeroStock,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory_report.pdf");
        
        reportService.generateInventoryReport(includeZeroStock, branchId, response.getOutputStream());
    }

    @GetMapping("/inventory/excel")
    @Operation(summary = "Generate inventory report Excel", description = "Generates an Excel inventory report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PHARMACIST')")
    public void generateInventoryReportExcel(
            @RequestParam(defaultValue = "false") boolean includeZeroStock,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory_report.xlsx");
        
        reportService.generateInventoryReportExcel(includeZeroStock, branchId, response.getOutputStream());
    }

    @GetMapping("/expiry")
    @Operation(summary = "Generate expiry report", description = "Generates a PDF report of medicines that will expire within a certain period")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PHARMACIST')")
    public void generateExpiryReport(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expiry_report.pdf");
        
        reportService.generateExpiryReport(days, branchId, response.getOutputStream());
    }

    @GetMapping("/expiry/excel")
    @Operation(summary = "Generate expiry report Excel", description = "Generates an Excel report of medicines that will expire within a certain period")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PHARMACIST')")
    public void generateExpiryReportExcel(
            @RequestParam(defaultValue = "30") int days,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expiry_report.xlsx");
        
        reportService.generateExpiryReportExcel(days, branchId, response.getOutputStream());
    }

    @GetMapping("/supplier-performance")
    @Operation(summary = "Generate supplier performance report", description = "Generates a PDF supplier performance report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generateSupplierPerformanceReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        setReportFilename(response, "supplier_performance", startDate, endDate);
        
        reportService.generateSupplierPerformanceReport(startDate, endDate, response.getOutputStream());
    }

    @GetMapping("/supplier-performance/excel")
    @Operation(summary = "Generate supplier performance report Excel", description = "Generates an Excel supplier performance report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generateSupplierPerformanceReportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        setReportFilename(response, "supplier_performance", startDate, endDate, ".xlsx");
        
        reportService.generateSupplierPerformanceReportExcel(startDate, endDate, response.getOutputStream());
    }

    @GetMapping("/purchase-orders")
    @Operation(summary = "Generate purchase order report", description = "Generates a PDF purchase order report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generatePurchaseOrderReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        setReportFilename(response, "purchase_orders", startDate, endDate);
        
        reportService.generatePurchaseOrderReport(startDate, endDate, branchId, response.getOutputStream());
    }

    @GetMapping("/purchase-orders/excel")
    @Operation(summary = "Generate purchase order report Excel", description = "Generates an Excel purchase order report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generatePurchaseOrderReportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        setReportFilename(response, "purchase_orders", startDate, endDate, ".xlsx");
        
        reportService.generatePurchaseOrderReportExcel(startDate, endDate, branchId, response.getOutputStream());
    }

    @GetMapping("/profit-loss")
    @Operation(summary = "Generate profit & loss report", description = "Generates a PDF profit & loss report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generateProfitLossReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        setReportFilename(response, "profit_loss", startDate, endDate);
        
        reportService.generateProfitLossReport(startDate, endDate, branchId, response.getOutputStream());
    }

    @GetMapping("/profit-loss/excel")
    @Operation(summary = "Generate profit & loss report Excel", description = "Generates an Excel profit & loss report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void generateProfitLossReportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        setReportFilename(response, "profit_loss", startDate, endDate, ".xlsx");
        
        reportService.generateProfitLossReportExcel(startDate, endDate, branchId, response.getOutputStream());
    }

    // Helper methods for setting filenames with date ranges
    
    private void setReportFilename(HttpServletResponse response, String reportName, LocalDate startDate, LocalDate endDate) {
        setReportFilename(response, reportName, startDate, endDate, ".pdf");
    }
    
    private void setReportFilename(HttpServletResponse response, String reportName, LocalDate startDate, LocalDate endDate, String extension) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String filename = reportName + "_" + startDate.format(formatter) + 
                          "_to_" + endDate.format(formatter) + extension;
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
    }
}
