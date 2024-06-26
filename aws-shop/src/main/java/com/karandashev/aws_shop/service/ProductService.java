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
            new Product("Product 1", "Product 1 description", 10),
            new Product("Product 2", "Product 2 description", 20),
            new Product("Product 3","Product 3 description", 30),
            new Product("Product 4", "Product 4 description", 30),
            new Product("Product 5", "Product 5 description", 30),
            new Product("Product 6", "Product 6 description", 50),
            new Product("Product 7","Product 7 description", 30),
            new Product("Product 8","Product 8 description", 60),
            new Product("Product 9", "Product 9 description", 80),
            new Product("Product 10", "Product 10 description", 20),
            new Product("Product 11","Product 11 description", 30),
            new Product("Product 12","Product 12 description", 30)
    );

    public List<Product> getAllProducts() {
        return products;
    }

    public Optional<Product> getProductById(String id) {
        return products.stream().filter(product -> product.getId().equals(id)).findFirst();
    }
}
