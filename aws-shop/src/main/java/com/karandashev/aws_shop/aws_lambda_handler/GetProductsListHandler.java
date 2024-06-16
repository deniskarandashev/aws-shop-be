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

import java.util.List;
import java.util.Map;

@Component
public class GetProductsListHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final ProductService productService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public GetProductsListHandler(ProductService productService) {
        this.productService = productService;
    }

    public GetProductsListHandler() {
        this.productService = null; // или любая другая логика, если требуется
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            List<Product> products = productService.getAllProducts();
            if (products == null) {
                throw new NullPointerException("Product list is null");
            }
            String responseMessage = objectMapper.writeValueAsString(products);

            responseEvent.setStatusCode(200);
            responseEvent.setBody(responseMessage);
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
