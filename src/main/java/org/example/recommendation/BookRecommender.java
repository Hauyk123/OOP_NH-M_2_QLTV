package org.example.recommendation;

import java.util.List;

import org.example.model.Book;

/**
 * Interface for book recommendation strategies
 */
public interface BookRecommender {

    /**
     * Get book recommendations for a user
     *
     * @param userId ID of the user
     * @param limit Maximum number of recommendations
     * @return List of recommended books
     */
    List<Book> getRecommendations(String userId, int limit);
}
