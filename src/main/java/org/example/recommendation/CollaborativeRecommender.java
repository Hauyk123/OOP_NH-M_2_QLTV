package org.example.recommendation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.controller.LibraryManagement;
import org.example.model.Book;
import org.example.model.Review;

/**
 * Recommends books based on user similarity and ratings
 */
public class CollaborativeRecommender implements BookRecommender {

    private final Map<String, List<Review>> userReviews = new HashMap<>();
    private final LibraryManagement library;

    public CollaborativeRecommender(LibraryManagement library) {
        this.library = library;
    }

    @Override
    public List<Book> getRecommendations(String userId, int limit) {
        List<Review> userRatings = userReviews.get(userId);
        if (userRatings == null || userRatings.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Double> similarUsers = findSimilarUsers(userId);

        return similarUsers.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> library.findBookById(entry.getKey()))
                .filter(book -> book != null)
                .collect(Collectors.toList());
    }

    private Map<String, Double> findSimilarUsers(String userId) {
        Map<String, Double> similarities = new HashMap<>();
        List<Review> userRatings = userReviews.get(userId);

        for (Map.Entry<String, List<Review>> entry : userReviews.entrySet()) {
            if (!entry.getKey().equals(userId)) {
                double similarity = calculateSimilarity(userRatings, entry.getValue());
                similarities.put(entry.getKey(), similarity);
            }
        }

        return similarities;
    }

    private double calculateSimilarity(List<Review> ratings1, List<Review> ratings2) {
        double avg1 = ratings1.stream().mapToInt(Review::getRating).average().orElse(0);
        double avg2 = ratings2.stream().mapToInt(Review::getRating).average().orElse(0);
        return 1.0 / (1.0 + Math.abs(avg1 - avg2));
    }

    public void addReview(Review review) {
        userReviews.computeIfAbsent(review.getUserId(), k -> new ArrayList<>())
                .add(review);
    }
}
