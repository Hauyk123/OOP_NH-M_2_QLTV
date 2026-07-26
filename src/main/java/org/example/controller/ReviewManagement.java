package org.example.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.model.Review;

public class ReviewManagement {

    private static final String FILE_PATH = System.getProperty("user.dir")
            + "/src/main/java/org/example/data/reviews.txt";

    public static List<Review> loadReviews() {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(FILE_PATH),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                Review review = Review.fromFileString(line);
                if (review != null) {
                    reviews.add(review);
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi đọc file reviews: " + e.getMessage());
        }
        return reviews;
    }

    public static void addReview(Review review) {
        // Xóa review cũ và thêm review mới
        List<Review> reviews = loadReviews();
        reviews.removeIf(r -> r.getUserId().equals(review.getUserId())
                && r.getBookId().equals(review.getBookId()));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(FILE_PATH, false), // false to overwrite file
                        StandardCharsets.UTF_8))) {
            // Write all existing reviews
            for (Review r : reviews) {
                bw.write(r.toFileString());
                bw.newLine();
            }
            // Write new review
            bw.write(review.toFileString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Lỗi ghi file reviews: " + e.getMessage());
        }
    }

    // Tính điểm trung bình cho một cuốn sách
    public static double getAverageRating(String bookId) {
        List<Review> reviews = loadReviews();
        List<Review> bookReviews = reviews.stream()
                .filter(r -> r.getBookId().equals(bookId))
                .toList();

        if (bookReviews.isEmpty()) {
            return 0;
        }

        double sum = bookReviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        return sum / bookReviews.size();
    }

    // Tìm review của một user cho một cuốn sách
    public static Review findReview(String userId, String bookId) {
        return loadReviews().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getBookId().equals(bookId))
                .findFirst()
                .orElse(null);
    }

    // Lấy tất cả review cho một cuốn sách
    public static List<Review> getBookReviews(String bookId) {
        return loadReviews().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .toList();
    }
    /*
     * Lấy tất cả review của một Book
     */
    public static List<Review> getReviewsByBookId(String bookId) {
        return loadReviews().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }
}
