package org.example.model;

import java.util.Date;

import org.example.utils.DateUtils;

public class Loan {
    private String bookId;
    private Date startDate;
    private Date endDate;

    public String getBookId() {
        return bookId;
    }


    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public Date getStartDate() {
        return startDate;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    public Date getEndDate() {
        return endDate;
    }


    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public Loan(String bookId, Date startDate, Date endDate) {
        this.bookId = bookId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    
    public String getFormattedStartDate() {
        return DateUtils.format(startDate);
    }
    public String getFormattedEndDate() {
        return DateUtils.format(endDate);
    }
    

    @Override
    public String toString() {
        return bookId + " (" + org.example.utils.DateUtils.format(startDate) + " -> " + org.example.utils.DateUtils.format(endDate) + ")";
    }
}
