package com.pharmacy.inventory.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.pharmacy.inventory.service.BarcodeService;
import org.springframework.stereotype.Service;

import java.io.OutputStream;

@Service
public class BarcodeServiceImpl implements BarcodeService {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 100;
    private static final int DEFAULT_QR_SIZE = 250;

    @Override
    public void generateBarcode(String content, int width, int height, OutputStream outputStream) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.CODE_128, width, height);
            
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }

    @Override
    public void generateQRCode(String content, int width, int height, OutputStream outputStream) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, width, height);
            
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    @Override
    public void generateMedicineBarcode(String medicineId, OutputStream outputStream) {
        // Prefix to identify this as a medicine barcode
        String content = "MED-" + medicineId;
        generateBarcode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT, outputStream);
    }

    @Override
    public void generateBatchBarcode(String batchId, OutputStream outputStream) {
        // Prefix to identify this as a batch barcode
        String content = "BATCH-" + batchId;
        generateBarcode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT, outputStream);
    }
}
