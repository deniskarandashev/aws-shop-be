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
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.EU_NORTH_1)
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String PRODUCTS_TABLE_NAME = System.getenv("PRODUCTS_TABLE_NAME");

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        context.getLogger().log("Received request: " + request.toString());

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            // Deserialize JSON payload into Product object
            ProductDto product = objectMapper.readValue(request.getBody(), ProductDto.class);

            // Validate input
            if (product == null || product.getTitle() == null || product.getDescription() == null || product.getPrice() <= 0) {
                responseEvent.setStatusCode(400);
                responseEvent.setBody("Invalid input: Product title, description, and price are required.");
                return responseEvent;
            }

            // Prepare DynamoDB item
            String productId = UUID.randomUUID().toString();
            Map<String, AttributeValue> itemValues = new HashMap<>();
            itemValues.put("id", AttributeValue.builder().s(productId).build());
            itemValues.put("title", AttributeValue.builder().s(product.getTitle()).build());
            itemValues.put("description", AttributeValue.builder().s(product.getDescription()).build());
            itemValues.put("price", AttributeValue.builder().n(String.valueOf(product.getPrice())).build());
            itemValues.put("count", AttributeValue.builder().n(String.valueOf(product.getCount())).build());

            // Insert item into DynamoDB
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(PRODUCTS_TABLE_NAME)
                    .item(itemValues)
                    .build());

            // Successful response
            responseEvent.setStatusCode(201);
            responseEvent.setBody("Product created successfully");

            // CORS headers
            responseEvent.setHeaders(Map.of(
                    "Access-Control-Allow-Origin", "*",
                    "Access-Control-Allow-Headers", "Content-Type",
                    "Access-Control-Allow-Methods", "PUT, OPTIONS"
            ));
        } catch (IOException e) {
            // JSON parsing or deserialization error
            responseEvent.setStatusCode(400);
            responseEvent.setBody("Invalid JSON format: " + e.getMessage());
        } catch (DynamoDbException e) {
            // DynamoDB operation error
            responseEvent.setStatusCode(500);
            responseEvent.setBody("DynamoDB Error: " + e.getMessage());
        } catch (Exception e) {
            // Other unexpected errors
            responseEvent.setStatusCode(500);
            responseEvent.setBody("Internal Server Error: " + e.getMessage());
        }

        return responseEvent;
    }
}