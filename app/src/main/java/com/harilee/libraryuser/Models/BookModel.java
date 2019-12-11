package com.harilee.libraryuser.Models;

public class BookModel {

    private String name;
    private String subTitile;
    private String description;
    private String author;
    private String genre;
    private String available;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public String getSubTitile() {
        return subTitile;
    }

    public void setSubTitile(String subTitile) {
        this.subTitile = subTitile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setName(String name) {
        this.name = name;
    }
}
