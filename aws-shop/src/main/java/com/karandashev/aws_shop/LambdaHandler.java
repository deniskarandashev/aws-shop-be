package com.karandashev.aws_shop;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

//public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
//    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
//
//    static {
//        try {
//            System.out.println("[====>] LambdaHandler");
//            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(AwsShopApplication.class);
//            System.out.println("[====>] LambdaHandler initialised");
//        } catch (ContainerInitializationException e) {
//            System.out.println("[====>] LambdaHandler ERROR");
//            System.out.println(e);
//            throw new RuntimeException("[====>] Error initializing Spring Boot Lambda container", e);
//        }
//    }
//
//    @Override
//    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
//        return handler.proxy(awsProxyRequest, context);
//    }
//}

//public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
//    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
//
//    static {
//        try {
//            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
//                    .defaultProxy()
//                    .asyncInit()
//                    .springBootApplication(AwsShopApplication.class)
//                    .buildAndInitialize();
//        } catch (Exception e) {
//            throw new RuntimeException("Could not initialize Spring Boot application", e);
//        }
//    }
//
//    @Override
//    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
//        return handler.proxy(awsProxyRequest, context);
//    }
//}

public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static {
        try {
            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
                    .defaultProxy()
                    .asyncInit()
                    .springBootApplication(AwsShopApplication.class)
                    .buildAndInitialize();
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        // Логирование запроса для отладки
        context.getLogger().log("Received request: " + awsProxyRequest.toString());
        long startTime = System.currentTimeMillis();

        AwsProxyResponse response = handler.proxy(awsProxyRequest, context);

        // Логирование времени выполнения
        long endTime = System.currentTimeMillis();
        context.getLogger().log("Request processed in " + (endTime - startTime) + "ms");

        return response;
    }
}