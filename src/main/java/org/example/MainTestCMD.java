package org.example;

import org.example.controller.LibraryManagement;
import org.example.model.Book;
import org.example.model.Member;
import org.example.utils.Logger;

import java.io.Console;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MainTestCMD {
    private static final LibraryManagement library = LibraryManagement.getInstance();
    private static final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    private static final Logger logger = Logger.getInstance();

    public static void main(String[] args) {
        // Cấu hình console để hiển thị tiếng Việt
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        
        // Kiểm tra và cấu hình console
        Console console = System.console();
        if (console != null) {
            console.writer().println("Đang khởi động chương trình...");
        }

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Nhập lựa chọn của bạn: ");
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    displayBooks();
                    break;
                case 3:
                    searchBook();
                    break;
                case 4:
                    addMember();
                    break;
                case 5:
                    displayMembers();
                    break;
                case 6:
                    borrowBook();
                    break;
                case 7:
                    returnBook();
                    break;
                case 8:
                    deleteBook();
                    break;
                case 0:
                    running = false;
                    logger.log("Đang thoát chương trình...");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        }
        scanner.close();
        library.shutdown();
    }

    private static void displayMenu() {
        System.out.println("\n=== QUẢN LÝ THƯ VIỆN ===");
        System.out.println("1. Thêm sách");
        System.out.println("2. Hiển thị sách");
        System.out.println("3. Tìm sách (theo ID/ISBN)");
        System.out.println("4. Thêm thành viên");
        System.out.println("5. Hiển thị thành viên");
        System.out.println("6. Mượn sách");
        System.out.println("7. Trả sách");
        System.out.println("8. Xóa sách");
        System.out.println("0. Thoát");
    }

    private static void addBook() {
        System.out.println("\n=== THÊM SÁCH MỚI ===");
        String id = getStringInput("Nhập ID sách: ");
        String title = getStringInput("Nhập tên sách: ");
        String author = getStringInput("Nhập tác giả: ");
        int quantity = getIntInput("Nhập số lượng: ");
        String isbn = getStringInput("Nhập ISBN: ");

        Book book = new Book(id, title, author, quantity, isbn);
        library.addBook(book);
        logger.log("Đã thêm sách mới: " + title);
    }

    private static void displayBooks() {
        System.out.println("\n=== DANH SÁCH SÁCH ===");
        List<Book> books = library.getBooks();
        if (books.isEmpty()) {
            System.out.println("Không có sách nào trong thư viện!");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    private static void searchBook() {
        System.out.println("\n=== TÌM KIẾM SÁCH ===");
        System.out.println("1. Tìm theo ID");
        System.out.println("2. Tìm theo ISBN");
        int choice = getIntInput("Chọn cách tìm kiếm: ");

        Book book = null;
        if (choice == 1) {
            String id = getStringInput("Nhập ID sách: ");
            book = library.findBookById(id);
        } else if (choice == 2) {
            String isbn = getStringInput("Nhập ISBN: ");
            book = library.findBookByISBN(isbn);
        }

        if (book != null) {
            System.out.println("\nThông tin sách:");
            System.out.println(book);
        } else {
            System.out.println("Không tìm thấy sách!");
        }
    }

    private static void addMember() {
        System.out.println("\n=== THÊM THÀNH VIÊN MỚI ===");
        String id = getStringInput("Nhập ID thành viên: ");
        String name = getStringInput("Nhập tên thành viên: ");

        Member member = new Member(id, name);
        library.addMember(member);
        logger.log("Đã thêm thành viên mới: " + name);
    }

    private static void displayMembers() {
        System.out.println("\n=== DANH SÁCH THÀNH VIÊN ===");
        List<Member> members = library.getMembers();
        if (members.isEmpty()) {
            System.out.println("Không có thành viên nào!");
            return;
        }
        for (Member member : members) {
            System.out.println(member);
        }
    }

    private static void borrowBook() {
        System.out.println("\n=== MƯỢN SÁCH ===");
        String memberId = getStringInput("Nhập ID thành viên: ");
        String bookId = getStringInput("Nhập ID sách: ");

        try {
            library.borrowBook(memberId, bookId, new Date(), new Date());
            logger.log("Thành viên " + memberId + " đã mượn sách " + bookId);
        } catch (Exception e) {
            System.out.println("Lỗi khi mượn sách: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.println("\n=== TRẢ SÁCH ===");
        String memberId = getStringInput("Nhập ID thành viên: ");
        String bookId = getStringInput("Nhập ID sách: ");

        try {
            library.returnBook(memberId, bookId);
            logger.log("Thành viên " + memberId + " đã trả sách " + bookId);
        } catch (Exception e) {
            System.out.println("Lỗi khi trả sách: " + e.getMessage());
        }
    }

    private static void deleteBook() {
        System.out.println("\n=== XÓA SÁCH ===");
        String bookId = getStringInput("Nhập ID sách cần xóa: ");
        library.deleteBook(bookId);
        logger.log("Đã xóa sách có ID: " + bookId);
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập một số nguyên!");
            }
        }
    }
}
