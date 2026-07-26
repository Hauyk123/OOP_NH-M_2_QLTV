package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Đại diện cho một thành viên thư viện có thể mượn sách. Lớp này kế thừa từ lớp
 * User cơ bản và bổ sung thêm chức năng mượn sách.
 */
public class Member extends User {

    private List<Loan> loans = new ArrayList<>();

    /**
     * Tạo một thành viên mới với đầy đủ thông tin bao gồm thông tin đăng nhập.
     *
     * @param id Mã định danh duy nhất của thành viên
     * @param name Họ tên đầy đủ của thành viên
     * @param username Tên đăng nhập của thành viên
     * @param password Mật khẩu của thành viên
     */
    public Member(String id, String name, String username, String password) {
        super(id, name, username, password);
    }

    /**
     * Tạo một thành viên mới với thông tin cơ bản.
     *
     * @param id Mã định danh duy nhất của thành viên
     * @param name Họ tên đầy đủ của thành viên
     */
    public Member(String id, String name) {
        super(id, name);
    }

    /**
     * Lấy danh sách các sách đang được mượn bởi thành viên này.
     *
     * @return Danh sách các phiếu mượn đang hoạt động
     */
    public List<Loan> getLoans() {
        return loans;
    }

    /**
     * Ghi nhận một lần mượn sách mới cho thành viên này.
     *
     * @param loan Thông tin phiếu mượn cần thêm
     */
    public void borrowBook(Loan loan) {
        loans.add(loan);
    }

    /**
     * Xóa bỏ thông tin mượn sách khi sách được trả.
     *
     * @param bookId Mã số của sách được trả
     */
    public void returnBook(String bookId) {
        loans.removeIf(l -> l.getBookId().equals(bookId));
    }

    /**
     * Tạo chuỗi biểu diễn thông tin của thành viên bao gồm các sách đã mượn.
     *
     * @return Chuỗi đã được định dạng chứa thông tin thành viên và trạng thái
     * mượn sách
     */
    @Override
    public String toString() {
        if (loans.isEmpty()) {
            return "👤 Thành viên: " + getName() + " | ID: " + getId()
                    + " | Username: " + getUsername() + " | Chưa mượn sách nào!";
        } else {
            return "👤 Thành viên: " + getName() + " | ID: " + getId()
                    + " | Username: " + getUsername() + " | Sách đã mượn: " + loans;
        }
    }
}
