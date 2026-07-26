package org.example.model;

import org.example.utils.ValidationUtils;

/**
 * Abstract base class for all library items Implements common properties and
 * validation
 */
public abstract class AbstractItem {

    protected String id;
    protected String title;
    protected int quantity;
    protected boolean isAvailable;

    protected AbstractItem(String id, String title, int quantity) {
        if (!ValidationUtils.isValidId(id)) {
            throw new IllegalArgumentException("Định dạng ID không hợp lệ Định dạng ID không hợp lệ");
        }
        if (!ValidationUtils.hasText(title)) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }
        if (!ValidationUtils.isPositive(quantity)) {
            throw new IllegalArgumentException("Số lượng phải là số dương");
        }

        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    // Abstract methods that all items must implement
    public abstract String getType();

    public abstract String getDetails();

    public abstract String getSummary();

    public abstract void printInfo();

    public abstract boolean canBeBorrowed();

    // Getters and setters with validation
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (!ValidationUtils.isValidId(id)) {
            throw new IllegalArgumentException("Định dạng ID không hợp lệ Định dạng ID không hợp lệ");
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!ValidationUtils.hasText(title)) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (!ValidationUtils.isPositive(quantity)) {
            throw new IllegalArgumentException("Số lượng phải là số dương");
        }
        this.quantity = quantity;
        this.isAvailable = quantity > 0;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
