package com.karandashev.aws_shop.config;

import com.karandashev.aws_shop.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.karandashev.aws_shop.service")
public class AppConfig {

    @Bean
    public ProductService productService() {
        return new ProductService();
    }
}