package org.example.controller;

import java.util.Date;

import org.example.model.Book;
import org.example.model.Loan;
import org.example.model.Member;

public class LoanManagement {

    // Mượn sách với ngày bắt đầu và ngày kết thúc cụ thể
    public static void borrowBook(LibraryManagement library, String userId, String bookId, Date startDate, Date endDate) {
        Member member = library.findMemberById(userId);
        Book book = library.findBookById(bookId);

        if (book == null || book.getQuantity() <= 0) {
            System.out.println("❌ Xin lỗi quý khách đã hết sách.");
            return;
        }

        if (member == null) {
            System.out.println("❌ Mượn thất bại. Không tìm thấy thành viên.");
            return;
        }

        // Cập nhật số lượng sách
        book.setQuantity(book.getQuantity() - 1);
        Loan loan = new Loan(bookId, startDate, endDate);
        member.borrowBook(loan);

        library.saveBooksAsync();
        library.saveMembersAsync();

        System.out.println("✅ Mượn thành công: " + loan);
    }

    // Trả sách
    public static void returnBook(LibraryManagement library, String userId, String bookId) {
        Member member = library.findMemberById(userId);
        Book book = library.findBookById(bookId);

        if (member == null || book == null) {
            System.out.println("❌ Trả thất bại. Kiểm tra lại thông tin.");
            return;
        }

        // Cập nhật số lượng sách
        book.setQuantity(book.getQuantity() + 1);
        member.returnBook(bookId);

        library.saveBooksAsync();
        library.saveMembersAsync();

        System.out.println("✅ Trả thành công.");
    }
}
