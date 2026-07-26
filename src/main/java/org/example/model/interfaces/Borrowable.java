package org.example.model.interfaces;

public interface Borrowable {

    boolean canBeBorrowed();

    void borrow();

    void returnItem();

    int getAvailableQuantity();
}
