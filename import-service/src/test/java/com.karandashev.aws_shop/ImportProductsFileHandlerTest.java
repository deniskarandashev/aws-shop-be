package com.karandashev.aws_shop;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.karandashev.aws_shop.aws_lambda_handler.ImportProductsFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ImportProductsFileHandlerTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private Context context;

    private ImportProductsFileHandler handler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        handler = new ImportProductsFileHandler(s3Client);
    }

    @Test
    public void testHandleRequestWithMissingName() {
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, String> queryStringParams = new HashMap<>();
        requestEvent.setQueryStringParameters(queryStringParams);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(400, responseEvent.getStatusCode());
        assertEquals("Missing 'name' parameter in query string", responseEvent.getBody());
    }

    @Test
    public void testHandleRequestWithNullNameParameter() {
        // Arrange
        APIGatewayProxyRequestEvent requestEvent = new APIGatewayProxyRequestEvent();
        Map<String, String> queryStringParams = new HashMap<>();
        queryStringParams.put("name", null);
        requestEvent.setQueryStringParameters(queryStringParams);

        // Act
        APIGatewayProxyResponseEvent responseEvent = handler.handleRequest(requestEvent, context);

        // Assert
        assertEquals(400, responseEvent.getStatusCode());
        assertEquals("Missing 'name' parameter in query string", responseEvent.getBody());
    }

    @Test
    public void testGenerateSignedUrl() {
        // Arrange
        String fileName = "testfile.csv";

        // Act
        String signedUrl = handler.generateSignedUrl(fileName);

        // Assert
        assertNotNull(signedUrl);
        assertTrue(signedUrl.startsWith("https://"));
        assertTrue(signedUrl.contains(fileName));
    }
}
