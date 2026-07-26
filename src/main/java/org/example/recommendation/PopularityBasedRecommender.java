package org.example.recommendation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.controller.LibraryManagement;
import org.example.model.Book;

/**
 * Recommends books based on popularity (borrow count)
 */
public class PopularityBasedRecommender implements BookRecommender {

    private final Map<String, Integer> borrowCount = new HashMap<>();
    private final LibraryManagement library;

    public PopularityBasedRecommender(LibraryManagement library) {
        this.library = library;
    }

    @Override
    public List<Book> getRecommendations(String userId, int limit) {
        return borrowCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> library.findBookById(entry.getKey()))
                .filter(book -> book != null)
                .collect(Collectors.toList());
    }

    public void updateBorrowCount(String bookId) {
        borrowCount.merge(bookId, 1, Integer::sum);
    }
}
