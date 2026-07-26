package org.example.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.util.Scanner;
import java.util.UUID;
import java.nio.file.Files;

import org.example.controller.LibraryManagement;
import org.example.model.Book;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class GoogleBooksUtils {

    private static final String API_KEY = "AIzaSyBkKAVhX7UB0N6z1-s1cbLSPVeEsgECmR4";
    private static final boolean DEBUG = true;
    private static final int TIMEOUT_MS = 10000;
    private static final int MAX_RETRIES = 3;
    private static final String[] searchFormats = {
        "https://www.googleapis.com/books/v1/volumes?q=isbn:",
        "https://www.googleapis.com/books/v1/volumes?q=ISBN:"
    };

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

    public static Book fetchByISBN(String isbn) {
        try {
            isbn = isbn.replaceAll("[^0-9X]", "").trim();
            System.out.println("🔍 Tìm sách với ISBN: " + isbn);

            String query = URLEncoder.encode("isbn:" + isbn, "UTF-8");
            String urlString = String.format("https://www.googleapis.com/books/v1/volumes?q=%s", query);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("📡 Phản hồi từ Google Books: " + responseCode);

            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8")) {
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNext()) {
                        response.append(scanner.nextLine());
                    }

                    JSONObject json = new JSONObject(response.toString());
                    if (json.has("items")) {
                        JSONArray items = json.getJSONArray("items");
                        if (items.length() > 0) {
                            JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");

                            String title = volumeInfo.optString("title", "Không rõ tiêu đề");
                            String author = "Không rõ tác giả";

                            if (volumeInfo.has("authors")) {
                                JSONArray authors = volumeInfo.getJSONArray("authors");
                                author = authors.join(", ").replace("\"", "");
                            }

                            System.out.println("📘 Tìm thấy sách: " + title + " - " + author);
                            String newId = LibraryManagement.getInstance().generateNextBookId();
                            return new Book(newId, title, author, 1, isbn);
                        }
                    } else {
                        System.out.println("❌ Không có kết quả phù hợp.");
                    }
                }
            } else {
                System.out.println("❌ Lỗi phản hồi API: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Lỗi khi fetch ISBN: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static boolean validateISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        // Remove hyphens and spaces
        isbn = isbn.replaceAll("[-\\s]", "");

        // Check length (ISBN-10 or ISBN-13)
        if (isbn.length() != 10 && isbn.length() != 13) {
            return false;
        }

        // Check if all characters are digits (except last char of ISBN-10 which can be 'X')
        if (!isbn.matches("^\\d{9}[\\dX]$") && !isbn.matches("^\\d{13}$")) {
            return false;
        }

        return true;
    }

    public static JSONObject getBookInfo(String isbn) {
        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject root = new JSONObject(json.toString());
            JSONArray items = root.optJSONArray("items");
            if (items != null && items.length() > 0) {
                return items.getJSONObject(0).getJSONObject("volumeInfo");
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy thông tin sách: " + e.getMessage());
        }
        return null;
    }

    public static String getBookCoverUrl(String isbn) {
        try {
            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder json = new StringBuilder();
            while (scanner.hasNext()) {
                json.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject root = new JSONObject(json.toString());
            JSONArray items = root.optJSONArray("items");
            if (items != null && items.length() > 0) {
                JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");
                if (volumeInfo.has("imageLinks")) {
                    String thumbnail = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    return thumbnail.replace("http:", "https:"); // Use HTTPS
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching book cover: " + e.getMessage());
        }
        return null;
    }
}
