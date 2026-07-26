package org.example;

import org.example.model.Librarian;

public class TestLibrarian {
    public static void main(String[] args) {
        System.out.println("Testing Librarian registration...");
        
        // Test tạo tài khoản thứ nhất
        Librarian lib1 = new Librarian("Test User 1", "testuser1", "password1", "0123456789");
        boolean result1 = lib1.saveLibrarianSafely();
        System.out.println("Tài khoản 1 - Kết quả: " + (result1 ? "Thành công" : "Thất bại"));
        
        // Test tạo tài khoản thứ hai với username khác
        Librarian lib2 = new Librarian("Test User 2", "testuser2", "password2", "0987654321");
        boolean result2 = lib2.saveLibrarianSafely();
        System.out.println("Tài khoản 2 - Kết quả: " + (result2 ? "Thành công" : "Thất bại"));
        
        // Test tạo tài khoản trùng username
        Librarian lib3 = new Librarian("Test User 3", "testuser1", "password3", "0111111111");
        boolean result3 = lib3.saveLibrarianSafely();
        System.out.println("Tài khoản trùng - Kết quả: " + (result3 ? "Thành công" : "Thất bại (mong đợi)"));
        
        // Kiểm tra danh sách tài khoản
        System.out.println("\nDanh sách tài khoản hiện tại:");
        for (Librarian lib : Librarian.loadLibrarians()) {
            System.out.println("- " + lib.getName() + " (" + lib.getUsername() + ")");
        }
    }
}
