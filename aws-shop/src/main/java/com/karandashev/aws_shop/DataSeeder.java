package com.karandashev.aws_shop;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to fill tables with test examples.
 * @implNote To run configure your connection to AWS (e.g., run in console script like
 * {@code aws configure --profile karandashev.denis}) and afther that run java class DataSeeder
 */
public class DataSeeder {

    private static final String PRODUCTS_TABLE = "products";
    private static final String STOCKS_TABLE = "stocks";

    private final DynamoDbClient dynamoDbClient;

    public DataSeeder() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public void seedData() {
        seedProducts();
        seedStocks();
    }

    private void seedProducts() {
        addProduct("1e3af1b2-e799-4b18-9102-ecfd0c3e4b62", "Product 1", "Description for product 1", 100);
        addProduct("2d2b1f3c-0dbf-4f1b-abc6-129f5c4d512d", "Product 2", "Description for product 2", 200);
        addProduct("3c0a6d5e-7b47-44b9-bb75-8ddf0d9256f8", "Product 3", "Description for product 3", 300);
    }

    private void seedStocks() {
        addStock("1e3af1b2-e799-4b18-9102-ecfd0c3e4b62", 50);
        addStock("2d2b1f3c-0dbf-4f1b-abc6-129f5c4d512d", 20);
        addStock("3c0a6d5e-7b47-44b9-bb75-8ddf0d9256f8", 30);
    }

    private void addProduct(String id, String title, String description, int price) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id).build());
        item.put("title", AttributeValue.builder().s(title).build());
        item.put("description", AttributeValue.builder().s(description).build());
        item.put("price", AttributeValue.builder().n(String.valueOf(price)).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(PRODUCTS_TABLE)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    private void addStock(String productId, int count) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("product_id", AttributeValue.builder().s(productId).build());
        item.put("count", AttributeValue.builder().n(String.valueOf(count)).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(STOCKS_TABLE)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public static void main(String[] args) {
        DataSeeder seeder = new DataSeeder();
        seeder.seedData();
    }
}
