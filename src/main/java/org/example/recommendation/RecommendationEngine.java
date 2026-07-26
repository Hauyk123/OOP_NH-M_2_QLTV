package org.example.recommendation;

import org.example.model.Book;
import org.example.controller.LibraryManagement;
import org.example.controller.ReviewManagement;
import java.util.*;

public class RecommendationEngine {

    private final LibraryManagement library;

    public RecommendationEngine(LibraryManagement library) {
        this.library = library;
    }

    public List<Book> getRecommendedBooks(int limit) {
        List<Book> allBooks = new ArrayList<>(library.getBooks());

        // Calculate scores for each book
        Map<Book, Double> bookScores = new HashMap<>();
        for (Book book : allBooks) {
            double borrowScore = calculateBorrowScore(book);
            double ratingScore = calculateRatingScore(book);
            bookScores.put(book, borrowScore * 0.6 + ratingScore * 0.4); // Weight factors
        }

        // Sort books by score
        return allBooks.stream()
                .sorted((b1, b2) -> Double.compare(bookScores.get(b2), bookScores.get(b1)))
                .limit(limit)
                .toList();
    }

    private double calculateBorrowScore(Book book) {
        int maxBorrows = library.getBooks().stream()
                .mapToInt(Book::getBorrowCount)
                .max()
                .orElse(1);
        return (double) book.getBorrowCount() / maxBorrows;
    }

    private double calculateRatingScore(Book book) {
        double avgRating = ReviewManagement.getAverageRating(book.getId());
        return avgRating / 5.0; // Normalize to 0-1 range
    }
}
