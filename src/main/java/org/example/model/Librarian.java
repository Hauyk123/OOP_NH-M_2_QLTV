package org.example.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Librarian extends User {

    private final String phone;
    private static final String FILE_PATH = "src/main/java/org/example/data/admin.txt";

    public Librarian(String name, String username, String password, String phone) {
        super(generateId(), name, username, password); // Gọi constructor của User với đầy đủ 4 tham số
        this.phone = phone;
    }

    private static String generateId() {
        // Logic để tạo ID duy nhất cho thủ thư
        return "LIB" + System.currentTimeMillis();
    }

    public String getPhone() {
        return phone;
    }

    // Load tất cả thủ thư từ file
    public static List<Librarian> loadLibrarians() {
        List<Librarian> librarians = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String username = parts[1];
                    String password = parts[2];
                    String phone = parts[3];
                    librarians.add(new Librarian(name, username, password, phone));
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Lỗi đọc danh sách thủ thư");
        }
        return librarians;
    }

    // Tìm thủ thư theo username
    public static Librarian findLibrarian(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[1].equals(username)) {
                    return new Librarian(parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi tìm thủ thư: " + e.getMessage());
        }
        return null;
    }    // Lưu thủ thư mới vào file

    public void saveLibrarian() {
        try {
            // Kiểm tra xem file có tồn tại và có nội dung không
            java.io.File file = new java.io.File(FILE_PATH);
            boolean needNewLine = false;

            if (file.exists() && file.length() > 0) {
                // Đọc ký tự cuối cùng để kiểm tra xem có phải là newline không
                try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r")) {
                    if (raf.length() > 0) {
                        raf.seek(raf.length() - 1);
                        char lastChar = (char) raf.readByte();
                        needNewLine = (lastChar != '\n' && lastChar != '\r');
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                if (needNewLine) {
                    writer.newLine();
                }
                writer.write(String.format("%s,%s,%s,%s",
                        getName(), getUsername(), getPassword(), phone));
                writer.newLine(); // Thêm newline sau dữ liệu mới
                writer.flush(); // Đảm bảo dữ liệu được ghi ngay lập tức
            }
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi lưu thủ thư mới: " + e.getMessage());
        }
    }    // Đổi mật khẩu thủ thư

    public static boolean changePassword(String username, String oldPassword, String newPassword) {
        List<Librarian> librarians = loadLibrarians();
        boolean updated = false;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Librarian lib : librarians) {
                if (lib.getUsername().equals(username) && lib.getPassword().equals(oldPassword)) {
                    lib.setPassword(newPassword);
                    updated = true;
                }
                bw.write(lib.getName() + "," + lib.getUsername() + "," + lib.getPassword() + "," + lib.getPhone());
                bw.newLine();
            }
            bw.flush(); // Đảm bảo dữ liệu được ghi ngay lập tức
        } catch (IOException e) {
            System.out.println("⚠️ Lỗi cập nhật mật khẩu: " + e.getMessage());
        }
        return updated;
    }

    // Kiểm tra xem username đã tồn tại chưa
    public static boolean isUsernameExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi kiểm tra username: " + e.getMessage());
        }
        return false;
    }

    // Lưu thủ thư mới vào file (với kiểm tra trùng lặp)
    public boolean saveLibrarianSafely() {
        // Kiểm tra username đã tồn tại chưa
        if (isUsernameExists(this.getUsername())) {
            System.err.println("⚠️ Username đã tồn tại: " + this.getUsername());
            return false;
        }

        saveLibrarian();
        return true;
    }
}
