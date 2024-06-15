package com.karandashev.aws_shop;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.karandashev.aws_shop.config.AppConfig;
import com.karandashev.aws_shop.model.Product;
import com.karandashev.aws_shop.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static ApplicationContext applicationContext;

    static {
        applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        String responseMessage = getResponseMessage(request);

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(200);
        responseEvent.setBody(responseMessage);

        return responseEvent;
    }

    private String getResponseMessage(APIGatewayProxyRequestEvent request) {
        String responseMessage = null;
        ProductService productService = applicationContext.getBean(ProductService.class);
        Map<String, String> pathParams = request.getPathParameters();
        if (pathParams != null) {
            String productId = pathParams.get("productId");
            Optional<Product> product = productService.getProductById(productId);
            if (product.isPresent()) {
                responseMessage = product.get().toString();
            }
        } else {
            List<Product> products = productService.getAllProducts();
            responseMessage = String.valueOf(products);
        }
        return responseMessage;
    }
}