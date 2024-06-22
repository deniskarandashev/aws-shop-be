package com.karandashev.aws_shop.model;

import jakarta.validation.constraints.NotNull;
import nonapi.io.github.classgraph.json.Id;

import java.util.UUID;

public class Product {
    @Id
    @NotNull
    private String id;
    @NotNull
    private String title;
    private String description;
    private int price;

    public Product(String id, String title, String description, int price) {
        this.id = UUID.fromString(id).toString();
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
}
