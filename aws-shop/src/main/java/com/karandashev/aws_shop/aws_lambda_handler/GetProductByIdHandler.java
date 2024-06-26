package com.karandashev.aws_shop.aws_lambda_handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karandashev.aws_shop.model.ProductDto;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetProductByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.EU_NORTH_1)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PRODUCTS_TABLE_NAME = "products";
    private static final String STOCKS_TABLE_NAME = "stocks";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            Map<String, String> pathParams = request.getPathParameters();
            ProductDto productDto = getProduct(pathParams);

            if (productDto == null) {
                String productId = pathParams.get("productId");
                responseEvent.setStatusCode(404);
                responseEvent.setBody("Product not found: wrong ID " + productId);
                context.getLogger().log("Product not found: wrong ID " + productId);
            } else {
                String responseMessage = objectMapper.writeValueAsString(productDto);
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
            responseEvent.setBody("Internal Server Error" + e.getMessage());
        }

        return responseEvent;
    }

    private ProductDto getProduct(Map<String, String> pathParams) {
        ProductDto productDto = null;
        if (pathParams != null && pathParams.containsKey("productId")) {
            String productId = pathParams.get("productId");
            try {
                ScanResponse scanResponse = dynamoDbClient.scan(ScanRequest.builder()
                        .tableName(PRODUCTS_TABLE_NAME)
                        .build());

                List<Map<String, AttributeValue>> items = scanResponse.items();
                Map<String, AttributeValue> productDtoMap = items.stream()
                        .filter(item -> item.get("id").s().equals(productId))
                        .findAny()
                        .orElse(null);
                if (productDtoMap != null) {
                    productDto = new ProductDto(
                            productDtoMap.get("id").s(),
                            productDtoMap.get("title").s(),
                            productDtoMap.get("description").s(),
                            Integer.parseInt(productDtoMap.get("price").n())
                    );

                    // Retrieve stock count from stocks table
                    Map<String, AttributeValue> key = new HashMap<>();
                    key.put("product_id", AttributeValue.builder().s(productDtoMap.get("id").s()).build());

                    try {
                        Map<String, AttributeValue> stockItem = dynamoDbClient.getItem(r -> r.tableName(STOCKS_TABLE_NAME).key(key)).item();
                        if (stockItem != null && stockItem.containsKey("count")) {
                            productDto.setCount(Integer.parseInt(stockItem.get("count").n()));
                        } else {
                            productDto.setCount(0);
                        }
                    } catch (DynamoDbException e) {
                        System.err.println("Unable to read item: " + key);
                        System.err.println(e.getMessage());
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception, log error, etc.
            }
        }

        return productDto;
    }
}

