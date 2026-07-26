package org.example.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeUtils {

    public static void generateQRCode(String data, String filePath) {
        QRCodeWriter writer = new QRCodeWriter();
        int width = 300, height = 300;
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            System.out.println("✅ Đã tạo mã QR: " + filePath);
        } catch (WriterException | IOException e) {
            System.out.println("⚠️ Lỗi tạo mã QR: " + e.getMessage());
        }
    }

    public static BufferedImage generateQRCodeImage(String data, int width, int height)
            throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
