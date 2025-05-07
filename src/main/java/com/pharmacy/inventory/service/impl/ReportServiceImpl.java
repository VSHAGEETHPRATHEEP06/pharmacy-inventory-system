package com.pharmacy.inventory.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.pharmacy.inventory.model.*;
import com.pharmacy.inventory.repository.*;
import com.pharmacy.inventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final MedicineRepository medicineRepository;
    private final StockRepository stockRepository;
    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public ReportServiceImpl(SaleRepository saleRepository, MedicineRepository medicineRepository,
                           StockRepository stockRepository, BatchRepository batchRepository,
                           SupplierRepository supplierRepository, PurchaseOrderRepository purchaseOrderRepository,
                           BranchRepository branchRepository) {
        this.saleRepository = saleRepository;
        this.medicineRepository = medicineRepository;
        this.stockRepository = stockRepository;
        this.batchRepository = batchRepository;
        this.supplierRepository = supplierRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.branchRepository = branchRepository;
    }

    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

    @Override
    public void generateSalesReport(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            addReportTitle(document, "Sales Report", startDate, endDate, branchId);
            
            // Get sales data
            List<Sale> sales = getSalesByDateRangeAndBranch(startDate, endDate, branchId);
            
            // Summary section
            addSalesSummarySection(document, sales);
            
            // Sales details table
            addSalesDetailsTable(document, sales);
            
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate sales report", e);
        }
    }

    @Override
    public void generateSalesReportExcel(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create sheet
            Sheet sheet = workbook.createSheet("Sales Report");
            
            // Create header row
            createExcelHeaderRow(sheet, "Sales Report", startDate, endDate, branchId);
            
            // Get sales data
            List<Sale> sales = getSalesByDateRangeAndBranch(startDate, endDate, branchId);
            
            // Create sales summary
            createSalesSummaryExcel(sheet, sales, 3);
            
            // Create sales details table
            createSalesDetailsExcel(sheet, sales, 8);
            
            // Auto size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate sales report Excel", e);
        }
    }

    @Override
    public void generateInventoryReport(boolean includeZeroStock, UUID branchId, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            Paragraph title = new Paragraph("Inventory Status Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph dateInfo = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), NORMAL_FONT);
            dateInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(dateInfo);
            
            if (branchId != null) {
                Branch branch = branchRepository.findById(branchId).orElse(null);
                if (branch != null) {
                    Paragraph branchInfo = new Paragraph("Branch: " + branch.getName(), NORMAL_FONT);
                    branchInfo.setAlignment(Element.ALIGN_CENTER);
                    document.add(branchInfo);
                }
            }
            
            document.add(Chunk.NEWLINE);
            
            // Get inventory data
            List<Stock> stockItems = getStockByBranch(branchId, includeZeroStock);
            
            // Inventory summary section
            addInventorySummarySection(document, stockItems);
            
            // Inventory details table
            addInventoryDetailsTable(document, stockItems);
            
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate inventory report", e);
        }
    }

    @Override
    public void generateInventoryReportExcel(boolean includeZeroStock, UUID branchId, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create sheet
            Sheet sheet = workbook.createSheet("Inventory Report");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            
            // Create header
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Inventory Status Report");
            titleCell.setCellStyle(headerStyle);
            
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dateCell.setCellStyle(normalStyle);
            
            if (branchId != null) {
                Branch branch = branchRepository.findById(branchId).orElse(null);
                if (branch != null) {
                    Row branchRow = sheet.createRow(2);
                    Cell branchCell = branchRow.createCell(0);
                    branchCell.setCellValue("Branch: " + branch.getName());
                    branchCell.setCellStyle(normalStyle);
                }
            }
            
            // Get inventory data
            List<Stock> stockItems = getStockByBranch(branchId, includeZeroStock);
            
            // Create inventory summary
            createInventorySummaryExcel(sheet, stockItems, 4);
            
            // Create inventory details table
            createInventoryDetailsExcel(sheet, stockItems, 9);
            
            // Auto size columns
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate inventory report Excel", e);
        }
    }

    @Override
    public void generateExpiryReport(int days, UUID branchId, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            Paragraph title = new Paragraph("Medicine Expiry Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subtitle = new Paragraph("Medicines expiring within " + days + " days", SUBTITLE_FONT);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);
            
            Paragraph dateInfo = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), NORMAL_FONT);
            dateInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(dateInfo);
            
            if (branchId != null) {
                Branch branch = branchRepository.findById(branchId).orElse(null);
                if (branch != null) {
                    Paragraph branchInfo = new Paragraph("Branch: " + branch.getName(), NORMAL_FONT);
                    branchInfo.setAlignment(Element.ALIGN_CENTER);
                    document.add(branchInfo);
                }
            }
            
            document.add(Chunk.NEWLINE);
            
            // Get expiring batches
            List<Batch> expiringBatches = getExpiringBatches(days, branchId);
            
            // Add expiry details table
            addExpiryDetailsTable(document, expiringBatches);
            
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate expiry report", e);
        }
    }

    @Override
    public void generateExpiryReportExcel(int days, UUID branchId, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expiry Report");
            
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            
            // Create header
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Medicine Expiry Report");
            titleCell.setCellStyle(headerStyle);
            
            Row subtitleRow = sheet.createRow(1);
            Cell subtitleCell = subtitleRow.createCell(0);
            subtitleCell.setCellValue("Medicines expiring within " + days + " days");
            subtitleCell.setCellStyle(normalStyle);
            
            // Get expiring batches
            List<Batch> expiringBatches = getExpiringBatches(days, branchId);
            
            // Create expiry details table
            createExpiryDetailsExcel(sheet, expiringBatches, 3);
            
            // Auto size columns
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate expiry report Excel", e);
        }
    }

    @Override
    public void generateSupplierPerformanceReport(LocalDate startDate, LocalDate endDate, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            addReportTitle(document, "Supplier Performance Report", startDate, endDate, null);
            
            // Get purchase orders for the period
            List<PurchaseOrder> purchaseOrders = getPurchaseOrdersByDateRange(startDate, endDate);
            
            // Add supplier performance table
            addSupplierPerformanceTable(document, purchaseOrders, startDate, endDate);
            
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate supplier performance report", e);
        }
    }

    @Override
    public void generateSupplierPerformanceReportExcel(LocalDate startDate, LocalDate endDate, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Supplier Performance");
            
            // Create header
            createExcelHeaderRow(sheet, "Supplier Performance Report", startDate, endDate, null);
            
            // Get purchase orders for the period
            List<PurchaseOrder> purchaseOrders = getPurchaseOrdersByDateRange(startDate, endDate);
            
            // Create supplier performance table
            createSupplierPerformanceExcel(sheet, purchaseOrders, 3);
            
            // Auto size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate supplier performance report Excel", e);
        }
    }

    @Override
    public void generatePurchaseOrderReport(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            addReportTitle(document, "Purchase Order Report", startDate, endDate, branchId);
            
            // Get purchase orders
            List<PurchaseOrder> purchaseOrders = getPurchaseOrdersByDateRangeAndBranch(startDate, endDate, branchId);
            
            // Add purchase order summary
            addPurchaseOrderSummary(document, purchaseOrders);
            
            // Add purchase order details table
            addPurchaseOrderDetailsTable(document, purchaseOrders);
            
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate purchase order report", e);
        }
    }

    @Override
    public void generatePurchaseOrderReportExcel(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Purchase Orders");
            
            // Create header
            createExcelHeaderRow(sheet, "Purchase Order Report", startDate, endDate, branchId);
            
            // Get purchase orders
            List<PurchaseOrder> purchaseOrders = getPurchaseOrdersByDateRangeAndBranch(startDate, endDate, branchId);
            
            // Create purchase order summary
            createPurchaseOrderSummaryExcel(sheet, purchaseOrders, 3);
            
            // Create purchase order details table
            createPurchaseOrderDetailsExcel(sheet, purchaseOrders, 8);
            
            // Auto size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate purchase order report Excel", e);
        }
    }

    @Override
    public void generateProfitLossReport(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            
            // Add title
            addReportTitle(document, "Profit & Loss Report", startDate, endDate, branchId);
            
            // Get sales and purchase data
            List<Sale> sales = getSalesByDateRangeAndBranch(startDate, endDate, branchId);
            List<PurchaseOrder> purchases = getPurchaseOrdersByDateRangeAndBranch(startDate, endDate, branchId);
            
            // Add profit & loss summary
            addProfitLossSummary(document, sales, purchases);
            
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate profit & loss report", e);
        }
    }

    @Override
    public void generateProfitLossReportExcel(LocalDate startDate, LocalDate endDate, UUID branchId, OutputStream outputStream) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Profit & Loss");
            
            // Create header
            createExcelHeaderRow(sheet, "Profit & Loss Report", startDate, endDate, branchId);
            
            // Get sales and purchase data
            List<Sale> sales = getSalesByDateRangeAndBranch(startDate, endDate, branchId);
            List<PurchaseOrder> purchases = getPurchaseOrdersByDateRangeAndBranch(startDate, endDate, branchId);
            
            // Create profit & loss summary
            createProfitLossSummaryExcel(sheet, sales, purchases, 3);
            
            // Auto size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate profit & loss report Excel", e);
        }
    }

    // Helper methods for data retrieval
    
    private List<Sale> getSalesByDateRangeAndBranch(LocalDate startDate, LocalDate endDate, UUID branchId) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        if (branchId != null) {
            return saleRepository.findByCreatedAtBetweenAndBranchId(startDateTime, endDateTime, branchId);
        } else {
            return saleRepository.findByCreatedAtBetween(startDateTime, endDateTime);
        }
    }
    
    private List<Stock> getStockByBranch(UUID branchId, boolean includeZeroStock) {
        if (branchId != null) {
            if (includeZeroStock) {
                return stockRepository.findByBranchId(branchId);
            } else {
                return stockRepository.findByBranchIdAndCurrentQuantityGreaterThan(branchId, 0);
            }
        } else {
            if (includeZeroStock) {
                return stockRepository.findAll();
            } else {
                return stockRepository.findByCurrentQuantityGreaterThan(0);
            }
        }
    }
    
    private List<Batch> getExpiringBatches(int days, UUID branchId) {
        LocalDate expiryLimit = LocalDate.now().plusDays(days);
        
        if (branchId != null) {
            return batchRepository.findExpiringBatchesByBranchId(expiryLimit, branchId);
        } else {
            return batchRepository.findByExpiryDateLessThanEqual(expiryLimit);
        }
    }
    
    private List<PurchaseOrder> getPurchaseOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        return purchaseOrderRepository.findByOrderDateBetween(startDateTime, endDateTime);
    }
    
    private List<PurchaseOrder> getPurchaseOrdersByDateRangeAndBranch(LocalDate startDate, LocalDate endDate, UUID branchId) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        
        if (branchId != null) {
            return purchaseOrderRepository.findByOrderDateBetweenAndBranchId(startDateTime, endDateTime, branchId);
        } else {
            return purchaseOrderRepository.findByOrderDateBetween(startDateTime, endDateTime);
        }
    }

    // Helper methods for PDF report generation
    
    private void addReportTitle(Document document, String reportTitle, LocalDate startDate, LocalDate endDate, UUID branchId) throws DocumentException {
        Paragraph title = new Paragraph(reportTitle, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Paragraph dateRange = new Paragraph("Period: " + startDate.format(formatter) + " to " + endDate.format(formatter), NORMAL_FONT);
        dateRange.setAlignment(Element.ALIGN_CENTER);
        document.add(dateRange);
        
        Paragraph generatedOn = new Paragraph("Generated on: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), NORMAL_FONT);
        generatedOn.setAlignment(Element.ALIGN_CENTER);
        document.add(generatedOn);
        
        if (branchId != null) {
            Branch branch = branchRepository.findById(branchId).orElse(null);
            if (branch != null) {
                Paragraph branchInfo = new Paragraph("Branch: " + branch.getName(), NORMAL_FONT);
                branchInfo.setAlignment(Element.ALIGN_CENTER);
                document.add(branchInfo);
            }
        }
        
        document.add(Chunk.NEWLINE);
    }
    
    // Additional helper methods that will be implemented later
    
    private void addSalesSummarySection(Document document, List<Sale> sales) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addSalesDetailsTable(Document document, List<Sale> sales) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addInventorySummarySection(Document document, List<Stock> stockItems) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addInventoryDetailsTable(Document document, List<Stock> stockItems) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addExpiryDetailsTable(Document document, List<Batch> expiringBatches) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addSupplierPerformanceTable(Document document, List<PurchaseOrder> purchaseOrders, LocalDate startDate, LocalDate endDate) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addPurchaseOrderSummary(Document document, List<PurchaseOrder> purchaseOrders) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addPurchaseOrderDetailsTable(Document document, List<PurchaseOrder> purchaseOrders) throws DocumentException {
        // Implementation will be added later
    }
    
    private void addProfitLossSummary(Document document, List<Sale> sales, List<PurchaseOrder> purchases) throws DocumentException {
        // Implementation will be added later
    }
    
    private void createSalesSummaryExcel(Sheet sheet, List<Sale> sales, int startRow) {
        // Implementation will be added later
    }
    
    private void createSalesDetailsExcel(Sheet sheet, List<Sale> sales, int startRow) {
        // Implementation will be added later
    }
    
    private void createInventorySummaryExcel(Sheet sheet, List<Stock> stockItems, int startRow) {
        // Implementation will be added later
    }
    
    private void createInventoryDetailsExcel(Sheet sheet, List<Stock> stockItems, int startRow) {
        // Implementation will be added later
    }
    
    private void createExpiryDetailsExcel(Sheet sheet, List<Batch> expiringBatches, int startRow) {
        // Implementation will be added later
    }
    
    private void createSupplierPerformanceExcel(Sheet sheet, List<PurchaseOrder> purchaseOrders, int startRow) {
        // Implementation will be added later
    }
    
    private void createPurchaseOrderSummaryExcel(Sheet sheet, List<PurchaseOrder> purchaseOrders, int startRow) {
        // Implementation will be added later
    }
    
    private void createPurchaseOrderDetailsExcel(Sheet sheet, List<PurchaseOrder> purchaseOrders, int startRow) {
        // Implementation will be added later
    }
    
    private void createProfitLossSummaryExcel(Sheet sheet, List<Sale> sales, List<PurchaseOrder> purchases, int startRow) {
        // Implementation will be added later
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    // This is a placeholder - actual implementation would be added as needed for each report type
    private void createExcelHeaderRow(Sheet sheet, String title, LocalDate startDate, LocalDate endDate, UUID branchId) {
        // Implementation will be added later
    }
}
