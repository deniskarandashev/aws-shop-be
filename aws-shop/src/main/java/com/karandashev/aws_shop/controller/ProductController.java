package com.karandashev.aws_shop.controller;

import com.karandashev.aws_shop.model.Product;
import com.karandashev.aws_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<Product> getProductsList() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/{productId}")
    public Optional<Product> getProductsById(@PathVariable String productId) {
        return productService.getProductById(productId);
    }
}