package org.example.model;

import java.util.List;

import org.example.controller.ReviewManagement;
import org.example.model.interfaces.Borrowable;

/**
 * Represents a book in the library system. Extends AbstractItem and implements
 * Borrowable interface.
 */
public class Book extends AbstractItem implements Borrowable {

    private String author;
    private String isbn;
    private int availableQuantity;
    private int borrowCount = 0;  // Track number of times borrowed

    public Book(String id, String title, String author, int quantity, String isbn) {
        super(id, title, quantity);
        this.author = author;
        this.isbn = isbn;
        this.availableQuantity = quantity;
        this.borrowCount = 0;
    }

    @Override
    public String getType() {
        return "Book";
    }

    @Override
    public String getDetails() {
        return String.format("Book[id=%s, title=%s, author=%s, isbn=%s, quantity=%d]",
                id, title, author, isbn, quantity);
    }

    @Override
    public boolean isAvailable() {
        return availableQuantity > 0;
    }

    @Override
    public String getSummary() {
        return String.format("%s by %s (ISBN: %s)", title, author, isbn);
    }

    @Override
    public void printInfo() {
        System.out.println("📚 " + getSummary());
        System.out.println("ID: " + id);
        System.out.println("Quantity: " + availableQuantity + "/" + quantity);
    }

    @Override
    public boolean canBeBorrowed() {
        return availableQuantity > 0;
    }

    @Override
    public void borrow() {
        if (!canBeBorrowed()) {
            throw new IllegalStateException("Không có sách nào có sẵn để mượn");
        }
        availableQuantity--;
        borrowCount++;
    }

    @Override
    public void returnItem() {
        if (availableQuantity < quantity) {
            availableQuantity++;
        }
    }

    @Override
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getBorrowCount() {
        return borrowCount;
    }

    public void incrementBorrowCount() {
        this.borrowCount++;
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    // Setters bổ sung để phục vụ tính năng sửa thông tin sách
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // This method is needed for Google Books integration
    public void setId(String newId) {
        this.id = newId;
    }

    @Override
    public void setQuantity(int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Số lượng không thể âm");
        }
        // Tính số sách đang được mượn
        int borrowedBooks = this.quantity - this.availableQuantity;
        // Cập nhật tổng số sách
        this.quantity = newQuantity;
        // Cập nhật số sách có sẵn = tổng số - số đang mượn
        this.availableQuantity = Math.max(0, newQuantity - borrowedBooks);
        // Đảm bảo availableQuantity không vượt quá quantity
        if (this.availableQuantity > this.quantity) {
            this.availableQuantity = this.quantity;
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %d copies | ISBN: %s",
                getId(), getTitle(), getAuthor(), getQuantity(), isbn);
    }

    public double getRating() {
        List<Review> bookReviews = ReviewManagement.getReviewsByBookId(getId());
        if (bookReviews.isEmpty()) {
            return 0.0;
        }
        return bookReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}