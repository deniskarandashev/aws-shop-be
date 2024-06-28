package com.karandashev.aws_shop.aws_lambda_handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ImportProductsFileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final S3Presigner s3Presigner = S3Presigner.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.EU_NORTH_1)
            .build();

    private static final String BUCKET_NAME = "import-service-aws-shop";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            String fileName = request.getQueryStringParameters().getOrDefault("name", null);

            if (fileName == null) {
                responseEvent.setStatusCode(400);
                responseEvent.setBody("Missing 'name' parameter in query string");
                return responseEvent;
            }

            // Generate signed URL
            String signedUrl = generateSignedUrl(fileName);

            responseEvent.setStatusCode(200);
            responseEvent.setBody(signedUrl);
            responseEvent.setHeaders(createCorsHeaders());
        } catch (Exception e) {
            responseEvent.setStatusCode(500);
            responseEvent.setBody("Internal Server Error");
            e.printStackTrace();
        }
        return responseEvent;
    }

    private String generateSignedUrl(String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key("uploaded/" + fileName)
                    .build();

            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

            URL signedUrl = presignedRequest.url();

            return signedUrl.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

    private Map<String, String> createCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        headers.put("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
        return headers;
    }
}

