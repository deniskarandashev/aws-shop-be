package com.karandashev.aws_shop.controller;

import com.karandashev.aws_shop.model.Product;
import com.karandashev.aws_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Product> getProductsById(@PathVariable String productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty()) {
            return ResponseEntity.notFound().build(); // Возвращает HTTP статус 404 Not Found
        } else {
            return ResponseEntity.ok(product.get()); // Возвращает продукт и HTTP статус 200 OK
        }
    }
}