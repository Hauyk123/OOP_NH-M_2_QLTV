package org.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book("B001", "Test Book", "Test Author", 5, "9780062316097");
    }

    @Test
    void testNewBookInitialization() {
        Book newBook = new Book("B002", "Test Title", "Test Author", 3, "9780062316097");
        assertEquals("B002", newBook.getId());
        assertEquals("Test Title", newBook.getTitle());
        assertEquals("Test Author", newBook.getAuthor());
        assertEquals(3, newBook.getQuantity());
        assertEquals(3, newBook.getAvailableQuantity());
        assertEquals("9780062316097", newBook.getIsbn());
    }

    @Test
    void testCanBeBorrowedWhenAvailable() {
        assertTrue(book.canBeBorrowed());
        assertEquals(5, book.getAvailableQuantity());
    }

    @Test
    void testBorrowIncrementsCount() {
        int initialCount = book.getBorrowCount();
        book.borrow();
        assertEquals(initialCount + 1, book.getBorrowCount());
    }

    @Test
    void testGetDetails() {
        String expected = "Book[id=B001, title=Test Book, author=Test Author, isbn=9780062316097, quantity=5]";
        assertEquals(expected, book.getDetails());
    }

    @Test
    void testGetSummary() {
        String expected = "Test Book by Test Author (ISBN: 9780062316097)";
        assertEquals(expected, book.getSummary());
    }

    @Test
    void testReturnWhenMaxQuantity() {
        int initial = book.getAvailableQuantity();
        book.returnItem();
        assertEquals(initial, book.getAvailableQuantity(), "Available quantity should not exceed initial quantity");
    }

    @Test
    void testBorrowAll() {
        int qty = book.getQuantity();
        for (int i = 0; i < qty; i++) {
            assertTrue(book.canBeBorrowed(), "Should be able to borrow book");
            book.borrow();
        }
        assertFalse(book.canBeBorrowed(), "Should not be able to borrow when no copies available");
        assertEquals(0, book.getAvailableQuantity(), "Available quantity should be 0");
    }

    @Test
    void testToString() {
        String expected = "B001 | Test Book | Test Author | 5 copies | ISBN: 9780062316097";
        assertEquals(expected, book.toString(), "toString format is incorrect");
    }

    @Test
    void testBorrowWhenNotAvailable() {
        // Borrow all copies first
        for (int i = 0; i < book.getQuantity(); i++) {
            book.borrow();
        }

        assertThrows(IllegalStateException.class, () -> book.borrow());
    }

    @Test
    void testGetType() {
        assertEquals("Book", book.getType());
    }

    @Test
    void testIsAvailable() {
        assertTrue(book.isAvailable());
        // Borrow all copies
        for (int i = 0; i < book.getQuantity(); i++) {
            book.borrow();
        }
        assertFalse(book.isAvailable());
    }
}
