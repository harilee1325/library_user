package com.harilee.libraryuser.Models;

public class IssueModel {

    private String bookId;
    private String dayLeft;
    private String studentNumber;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getDayLeft() {
        return dayLeft;
    }

    public void setDayLeft(String dayLeft) {
        this.dayLeft = dayLeft;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
}
