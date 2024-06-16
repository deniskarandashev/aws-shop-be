package com.karandashev.aws_shop.aws_lambda_handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karandashev.aws_shop.model.Product;
import com.karandashev.aws_shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class GetProductByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ProductService productService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public GetProductByIdHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            String productId = request.getPathParameters().get("id");
            Optional<Product> product = productService.getProductById(productId);
            if (!product.isPresent()) {
                responseEvent.setStatusCode(404);
                responseEvent.setBody("Product not found");
            } else {
                String responseMessage = objectMapper.writeValueAsString(product);
                responseEvent.setStatusCode(200);
                responseEvent.setBody(responseMessage);
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
