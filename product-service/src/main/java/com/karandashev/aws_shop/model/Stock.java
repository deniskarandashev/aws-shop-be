package com.karandashev.aws_shop.model;

import jakarta.validation.constraints.NotNull;
import nonapi.io.github.classgraph.json.Id;

public class Stock {
    /**
     * Foreign key from products.id
     */
    @Id
    @NotNull
    private String product_id;
    /**
     * Total number of products in stock, can't be exceeded
     */
    private int count;

    public Stock(String product_id, int count) {
        this.product_id = product_id;
        this.count = count;
    }

    @Override
    public String toString() {
        return "{" +
                "product_id='" + product_id + '\'' +
                ", count=" + count +
                '}';
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
