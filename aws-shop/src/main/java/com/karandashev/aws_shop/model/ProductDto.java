package com.karandashev.aws_shop.model;

public class ProductDto {
    private String id;
    private String title;
    private String description;
    private int price;
    private int сount;

    public ProductDto() {}

    public ProductDto(String id, String title, String description, int price, int сount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.сount = сount;
    }

    public ProductDto(String id, String title, String description, int price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", сount=" + сount +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return сount;
    }

    public void setCount(int сount) {
        this.сount = сount;
    }
}
