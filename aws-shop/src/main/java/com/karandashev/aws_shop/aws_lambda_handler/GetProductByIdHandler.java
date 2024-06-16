package com.karandashev.aws_shop.aws_lambda_handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karandashev.aws_shop.config.AppConfig;
import com.karandashev.aws_shop.model.Product;
import com.karandashev.aws_shop.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;
import java.util.Optional;

public class GetProductByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static ApplicationContext applicationContext;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            ProductService productService = applicationContext.getBean(ProductService.class);
            Map<String, String> pathParams = request.getPathParameters();

            if (pathParams != null && pathParams.containsKey("productId")) {
                String productId = pathParams.get("productId");
                Optional<Product> product = productService.getProductById(productId);
                String responseMessage = product.map(value -> {
                    try {
                        return objectMapper.writeValueAsString(value);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).orElse(null);

                if (product.isEmpty()) {
                    responseEvent.setStatusCode(404);
                    responseEvent.setBody("Product not found: wrong ID " + productId);
                    context.getLogger().log("Product not found: wrong ID " + productId);
                } else {
                    responseEvent.setStatusCode(200);
                    responseEvent.setBody(responseMessage);
                }
            } else {
                responseEvent.setStatusCode(400);
                responseEvent.setBody("Product ID not provided");
            }

            responseEvent.setHeaders(Map.of(
                    "Access-Control-Allow-Origin", "*",
                    "Access-Control-Allow-Headers", "Content-Type",
                    "Access-Control-Allow-Methods", "GET, OPTIONS"
            ));
        } catch (Exception e) {
            responseEvent.setStatusCode(500);
            responseEvent.setBody("Internal Server Error");
        }

        return responseEvent;
    }
}

