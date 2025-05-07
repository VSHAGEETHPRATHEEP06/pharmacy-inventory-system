package com.pharmacy.inventory.service;

import java.io.OutputStream;

public interface BarcodeService {
    /**
     * Generate a barcode image for a given content
     * 
     * @param content The content to encode in the barcode
     * @param width The width of the barcode image
     * @param height The height of the barcode image
     * @param outputStream The output stream to write the barcode image to
     */
    void generateBarcode(String content, int width, int height, OutputStream outputStream);
    
    /**
     * Generate a QR code image for a given content
     * 
     * @param content The content to encode in the QR code
     * @param width The width of the QR code image
     * @param height The height of the QR code image
     * @param outputStream The output stream to write the QR code image to
     */
    void generateQRCode(String content, int width, int height, OutputStream outputStream);
    
    /**
     * Generate a barcode image for a medicine
     * 
     * @param medicineId The ID of the medicine
     * @param outputStream The output stream to write the barcode image to
     */
    void generateMedicineBarcode(String medicineId, OutputStream outputStream);
    
    /**
     * Generate a barcode image for a batch
     * 
     * @param batchId The ID of the batch
     * @param outputStream The output stream to write the barcode image to
     */
    void generateBatchBarcode(String batchId, OutputStream outputStream);
}
