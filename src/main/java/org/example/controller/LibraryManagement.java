package org.example.controller;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.example.model.Book;
import org.example.model.Loan;
import org.example.model.Member;
import org.example.utils.DateUtils;
import org.example.utils.Logger;

public class LibraryManagement {

    private static volatile LibraryManagement instance;
    private final CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Member> members = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Logger logger = Logger.getInstance();
    private final String BOOKS_FILE = "src/main/java/org/example/data/books.txt";
    private final String MEMBERS_FILE = "src/main/java/org/example/data/members.txt";
    private volatile boolean isCancelled = false;

    private LibraryManagement() {
        // Load books immediately when LibraryManagement is instantiated
        loadBooksAsync().join();
        loadMembersAsync().join();
    }

    public static LibraryManagement getInstance() {
        if (instance == null) {
            synchronized (LibraryManagement.class) {
                if (instance == null) {
                    instance = new LibraryManagement();
                }
            }
        }
        return instance;
    }

    public Book findBookById(String id) {
        return books.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    public Book findBookByISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            System.out.println("⚠️ ISBN trống!");
            return null;
        }
        // Chuẩn hóa ISBN bằng cách xóa dấu gạch ngang
        String normalizedISBN = isbn.replace("-", "").trim();
        return books.stream()
                .filter(b -> b.getIsbn().replace("-", "").equals(normalizedISBN))
                .findFirst()
                .orElse(null);
    }

    public void addBook(Book book) {
        if (book == null) {
            System.out.println("⚠️ Lỗi thêm sách");
            return;
        }

        System.out.println("📚 Đang thêm sách: ID=" + book.getId() + ", ISBN=" + book.getIsbn());

        Book existingById = findBookById(book.getId());
        if (existingById != null) {
            System.out.println("ℹ️ Cập nhật số lượng cho sách có ID=" + book.getId());
            existingById.setQuantity(existingById.getQuantity() + book.getQuantity());
        } else {
            Book existingByIsbn = findBookByISBN(book.getIsbn());
            if (existingByIsbn != null) {
                System.out.println("ℹ️ Cập nhật số lượng cho sách có ISBN=" + book.getIsbn());
                existingByIsbn.setQuantity(existingByIsbn.getQuantity() + book.getQuantity());
            } else {
                System.out.println("✅ Thêm sách mới vào danh sách");
                books.add(book);
            }
        }
        saveBooksAsync();
    }

    public void addMember(Member member) {
        members.add(member);
        saveMembersAsync();
    }

    public Member findMemberById(String id) {
        // duyệt kiểu lambda
        return members.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    public void borrowBook(String memberId, String bookId, Date startDate, Date endDate) {
        Member m = findMemberById(memberId);
        Book b = findBookById(bookId);
        if (m != null && b != null && b.getQuantity() > 0) {
            b.setQuantity(b.getQuantity() - 1);
            m.borrowBook(new Loan(bookId, startDate, endDate));
            saveBooksAsync();
            saveMembersAsync();
        }
    }

    public void returnBook(String memberId, String bookId) {
        Member m = findMemberById(memberId);
        Book b = findBookById(bookId);
        if (m != null && b != null) {
            m.returnBook(bookId);
            b.setQuantity(b.getQuantity() + 1);
            saveBooksAsync();
            saveMembersAsync();
        }
    }

    public void saveBooks() {
        Map<String, Book> uniqueBooks = new HashMap<>();

        // Gộp các sách trùng ID
        for (Book book : books) {
            String bookId = book.getId();
            if (uniqueBooks.containsKey(bookId)) {
                // Cộng dồn số lượng nếu trùng ID
                Book existingBook = uniqueBooks.get(bookId);
                existingBook.setQuantity(existingBook.getQuantity() + book.getQuantity());
            } else {
                uniqueBooks.put(bookId, book);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : uniqueBooks.values()) {
                writer.write(String.format("%s,%s,%s,%d,%s\n",
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getQuantity(),
                        book.getIsbn()
                ));
            }
            logger.log("Books saved successfully");
        } catch (IOException e) {
            logger.error("Error saving books: " + e.getMessage());
        }
    }

    public void saveBooksAsync() {
        CompletableFuture.runAsync(this::saveBooks);
    }

    public CompletableFuture<Void> saveMembersAsync() {
        return CompletableFuture.runAsync(() -> {
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(MEMBERS_FILE), StandardCharsets.UTF_8))) {
                for (Member m : members) {
                    bw.write(m.getId() + "," + m.getName() + "," + m.getUsername() + "," + m.getPassword());
                    for (Loan l : m.getLoans()) {
                        bw.write("," + l.getBookId() + "," + DateUtils.format(l.getStartDate()) + "," + DateUtils.format(l.getEndDate()));
                    }
                    bw.newLine();
                }
                logger.log("Members saved successfully");
            } catch (IOException e) {
                logger.error("Error saving members: " + e.getMessage());
            }
        }, executorService);
    }

    public CompletableFuture<Void> loadBooksAsync() {
        return CompletableFuture.runAsync(() -> {
            // Reset cancel flag
            isCancelled = false;
            books.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(BOOKS_FILE))) {
                String line;
                while ((line = br.readLine()) != null && !isCancelled) {
                    String[] p = line.split(",");
                    if (p.length == 5) {
                        books.add(new Book(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4]));
                    }
                }
                logger.log("Books loaded successfully");
            } catch (IOException e) {
                logger.error("Error loading books: " + e.getMessage());
            }
        }, executorService);
    }

    public CompletableFuture<Void> loadMembersAsync() {
        return CompletableFuture.runAsync(() -> {
            isCancelled = false;
            members.clear();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(MEMBERS_FILE), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null && !isCancelled) {
                    String[] p = line.split(",");
                    if (p.length >= 4) {
                        String id = p[0];
                        String name = p[1];
                        String username = p[2];
                        String password = p[3];
                        Member m = new Member(id, name, username, password);
                        for (int i = 4; i + 2 < p.length; i += 3) {
                            String bookId = p[i];
                            Date start = DateUtils.parse(p[i + 1]);
                            Date end = DateUtils.parse(p[i + 2]);
                            m.borrowBook(new Loan(bookId, start, end));
                        }
                        members.add(m);
                    }
                }
                logger.log("Members loaded successfully");
            } catch (IOException e) {
                logger.error("Error loading members: " + e.getMessage());
            }
        }, executorService);
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void shutdown() {
        executorService.shutdown();
    }

    // Override finalize to ensure executor service is shut down
    @Override
    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }

    /**
     * Generates the next available book ID in format B000XXX
     *
     * @return Next available book ID
     */
    public String generateNextBookId() {
        int maxId = 0;
        for (Book book : books) {
            String id = book.getId();
            if (id.startsWith("B") && id.length() == 7) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("B%06d", maxId + 1);
    }

    public void deleteBook(String bookId) {
        books.removeIf(book -> book.getId().equals(bookId));
    }

    public void cancelLoading() {
        isCancelled = true;
    }
}
