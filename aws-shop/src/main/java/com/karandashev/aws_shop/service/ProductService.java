package com.karandashev.aws_shop.service;

import com.karandashev.aws_shop.model.Product;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductService {

    /**
     * Note: id will be converted in UUID format by Product's constructor
     */
    private List<Product> products = Arrays.asList(
            new Product("1", "Product 1", "Product 1 description", 10),
            new Product("2", "Product 2", "Product 2 description", 20),
            new Product("3", "Product 3","Product 3 description", 30),
            new Product("4", "Product 4", "Product 4 description", 30),
            new Product("5", "Product 5", "Product 5 description", 30),
            new Product("6", "Product 6", "Product 6 description", 50),
            new Product("7", "Product 7","Product 7 description", 30),
            new Product("8", "Product 8","Product 8 description", 60),
            new Product("9", "Product 9", "Product 9 description", 80),
            new Product("10", "Product 10", "Product 10 description", 20),
            new Product("11", "Product 11","Product 11 description", 30),
            new Product("12", "Product 12","Product 12 description", 30)
    );

    public List<Product> getAllProducts() {
        return products;
    }

    public Optional<Product> getProductById(String id) {
        return products.stream().filter(product -> product.getId().equals(id)).findFirst();
    }
}
