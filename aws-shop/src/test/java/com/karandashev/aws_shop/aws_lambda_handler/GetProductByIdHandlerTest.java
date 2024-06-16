package com.karandashev.aws_shop.aws_lambda_handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

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
public class GetProductByIdHandlerTest {

    @Autowired
    private GetProductByIdHandler handler;

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
    public void testHandleRequestProductNotFound() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Map.of("id", "1"));

        when(productService.getProductById("1")).thenReturn(Optional.empty());

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(404, responseEvent.getStatusCode());
        assertEquals("Product not found", responseEvent.getBody());
        verify(productService).getProductById("1");
    }


    @Test
    public void testHandleRequestError() throws Exception {
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Map.of("id", "1"));

        when(productService.getProductById("1")).thenThrow(new RuntimeException("Mocked exception"));

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(500, responseEvent.getStatusCode());
        assertEquals("Internal Server Error", responseEvent.getBody());
        verify(productService).getProductById("1");
    }

    @Test
    public void testHandleRequestWithValidHeaders() throws Exception {
        // Тестирует наличие корректных заголовков в ответе
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        requestEvent.setPathParameters(Map.of("id", "1"));
        Product mockProduct = new Product("1", "Product 1", "Description 1", 100);

        when(productService.getProductById("1")).thenReturn(Optional.of(mockProduct));

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(200, responseEvent.getStatusCode());
        assertEquals("*", responseEvent.getHeaders().get("Access-Control-Allow-Origin"));
        assertEquals("Content-Type", responseEvent.getHeaders().get("Access-Control-Allow-Headers"));
        assertEquals("GET, OPTIONS", responseEvent.getHeaders().get("Access-Control-Allow-Methods"));
        verify(productService).getProductById("1");
    }
}
