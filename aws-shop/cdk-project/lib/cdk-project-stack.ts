import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as path from "path";

export class CdkProjectStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const jarPath = path.join(__dirname, '../../target/aws-shop-0.0.1-SNAPSHOT-shaded.jar');

    const productLambda = new lambda.Function(this, 'ProductLambda', {
      runtime: lambda.Runtime.JAVA_17,
      code: lambda.Code.fromAsset(jarPath),
      handler: 'com.karandashev.aws_shop.LambdaHandler::handleRequest',
      memorySize: 1024,
      timeout: cdk.Duration.seconds(30),
    });

    const api = new apigateway.RestApi(this, 'productsApi', {
      restApiName: 'Products Service',
      description: 'This service serves products.',
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
        allowHeaders: ['Content-Type', 'X-Amz-Date', 'Authorization', 'X-Api-Key', 'X-Amz-Security-Token'],
        statusCode: 200,
      },
    });

    const products = api.root.addResource('products');
    const getProductsListIntegration = new apigateway.LambdaIntegration(productLambda);
    products.addMethod('GET', getProductsListIntegration);

    const singleProduct = products.addResource('{productId}');
    const getProductsByIdIntegration = new apigateway.LambdaIntegration(productLambda);
    singleProduct.addMethod('GET', getProductsByIdIntegration);
  }
}
