package com.karandashev.aws_shop.aws_lambda_handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karandashev.aws_shop.model.Product;
import com.karandashev.aws_shop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig
@ComponentScan(basePackages = "com.karandashev.aws_shop.aws_lambda_handler")
public class GetProductsListHandlerTest {

    @Autowired
    private GetProductsListHandler handler;

    @MockBean
    private ProductService productService;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    public void testHandleRequest() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        List<Product> mockProducts = Arrays.asList(
                new Product("1", "Product 1", "Description 1", 100),
                new Product("2", "Product 2", "Description 2", 150)
        );
        String expectedResponse = objectMapper.writeValueAsString(mockProducts);

        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals(expectedResponse, responseEvent.getBody());
        verify(productService).getAllProducts();
    }

    @Test
    public void testHandleRequestError() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();

        when(productService.getAllProducts()).thenThrow(new RuntimeException("Mocked exception"));

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(500, responseEvent.getStatusCode());
        assertEquals("Internal Server Error", responseEvent.getBody());
        verify(productService).getAllProducts();
    }

    @Test
    public void testHandleRequestEmptyList() throws Exception {
        // Тестирует случай, когда метод getAllProducts() возвращает пустой список
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        List<Product> mockProducts = Collections.emptyList();
        String expectedResponse = objectMapper.writeValueAsString(mockProducts);

        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals(expectedResponse, responseEvent.getBody());
        verify(productService).getAllProducts();
    }

    @Test
    public void testHandleRequestWithLargeDataset() throws Exception {
        // Тестирует случай, когда метод getAllProducts() возвращает большой набор данных
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        List<Product> mockProducts = Arrays.asList(
                new Product("1", "Product 1", "Description 1", 100),
                new Product("2", "Product 2", "Description 2", 150),
                // добавьте больше продуктов для создания большого набора данных
                new Product("3", "Product 3", "Description 3", 200),
                new Product("4", "Product 4", "Description 4", 250)
        );
        String expectedResponse = objectMapper.writeValueAsString(mockProducts);

        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals(expectedResponse, responseEvent.getBody());
        verify(productService).getAllProducts();
    }

    @Test
    public void testHandleRequestWithNullProducts() throws Exception {
        // Тестирует случай, когда метод getAllProducts() возвращает null
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();

        when(productService.getAllProducts()).thenReturn(null);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(500, responseEvent.getStatusCode());
        assertEquals("Internal Server Error", responseEvent.getBody());
        verify(productService).getAllProducts();
    }

    @Test
    public void testHandleRequestWithValidHeaders() throws Exception {
        // Тестирует наличие корректных заголовков в ответе
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        List<Product> mockProducts = Arrays.asList(
                new Product("1", "Product 1", "Description 1", 100),
                new Product("2", "Product 2", "Description 2", 150)
        );

        when(productService.getAllProducts()).thenReturn(mockProducts);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals("*", responseEvent.getHeaders().get("Access-Control-Allow-Origin"));
        assertEquals("Content-Type", responseEvent.getHeaders().get("Access-Control-Allow-Headers"));
        assertEquals("GET, OPTIONS", responseEvent.getHeaders().get("Access-Control-Allow-Methods"));
        verify(productService).getAllProducts();
    }
}
