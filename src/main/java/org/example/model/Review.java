package org.example.model;

import java.nio.charset.StandardCharsets;

/**
 * Đại diện cho một đánh giá sách trong hệ thống thư viện. Lớp này xử lý các
 * đánh giá và bình luận của người dùng về sách.
 */
public class Review {

    private String bookId;
    private String userId;
    private int rating;
    private String comment;

    /**
     * Khởi tạo một đánh giá mới với các thông tin chi tiết.
     *
     * @param bookId Mã số của sách được đánh giá
     * @param userId Mã số của người dùng viết đánh giá
     * @param rating Số sao đánh giá (thường từ 1-5)
     * @param comment Nội dung bình luận kèm theo đánh giá
     */
    public Review(String bookId, String userId, int rating, String comment) {
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    /**
     * Lấy mã số của sách được đánh giá.
     *
     * @return Mã số sách
     */
    public String getBookId() {
        return bookId;
    }

    /**
     * Thiết lập mã số của sách được đánh giá.
     *
     * @param bookId Mã số sách cần thiết lập
     */
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    /**
     * Lấy mã số của người dùng viết đánh giá.
     *
     * @return Mã số người dùng
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Thiết lập mã số của người dùng viết đánh giá.
     *
     * @param userId Mã số người dùng cần thiết lập
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Lấy số sao đánh giá.
     *
     * @return Số sao đánh giá
     */
    public int getRating() {
        return rating;
    }

    /**
     * Thiết lập số sao đánh giá.
     *
     * @param rating Số sao đánh giá cần thiết lập
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Lấy nội dung bình luận của đánh giá.
     *
     * @return Nội dung bình luận
     */
    public String getComment() {
        return comment;
    }

    /**
     * Thiết lập nội dung bình luận cho đánh giá.
     *
     * @param comment Nội dung bình luận cần thiết lập
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Chuyển đổi đánh giá thành định dạng chuỗi UTF-8 để lưu vào file.
     *
     * @return Chuỗi biểu diễn đánh giá theo định dạng
     * "bookId:X,userId:Y,rating:Z,comment:ABC"
     */
    public String toFileString() {
        return String.format("bookId:%s,userId:%s,rating:%d,comment:%s",
                bookId, // Remove UTF-8 conversion since we'll handle encoding in ReviewManagement
                userId,
                rating,
                comment
        );
    }

    /**
     * Tạo đối tượng Review từ một chuỗi UTF-8.
     *
     * @param line Chuỗi chứa dữ liệu đánh giá theo định dạng
     * "bookId:X,userId:Y,rating:Z,comment:ABC"
     * @return Đối tượng Review mới, hoặc null nếu định dạng chuỗi không hợp lệ
     */
    public static Review fromFileString(String line) {
        try {
            String[] parts = line.split(",");  // Remove UTF-8 conversion
            String bookId = parts[0].split(":")[1];
            String userId = parts[1].split(":")[1];
            int rating = Integer.parseInt(parts[2].split(":")[1]);
            String comment = parts[3].split(":")[1];
            return new Review(bookId, userId, rating, comment);
        } catch (Exception e) {
            System.err.println("Lỗi đọc review: " + e.getMessage());
            return null;
        }
    }
}
