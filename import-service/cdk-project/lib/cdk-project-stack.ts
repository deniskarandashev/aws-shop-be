import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as s3 from 'aws-cdk-lib/aws-s3';
import path from 'path';

export class CdkProjectStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Find an existing S3 bucket if it already exists
    const existingBucket = s3.Bucket.fromBucketName(this, 'ImportServiceBucket', 'import-service-aws-shop');

    // If the bucket does not exist, create a new one
    const bucket = existingBucket || new s3.Bucket(this, 'ImportServiceBucket', {
      bucketName: 'import-service-aws-shop',
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      autoDeleteObjects: true
    });

    const jarPath = path.join(__dirname, '../../target/import-service-0.0.1-SNAPSHOT-shaded.jar');

    const importProductsFileLambda = new lambda.Function(this, 'ImportProductsFileHandler', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'com.karandashev.aws_shop.aws_lambda_handler.ImportProductsFileHandler::handleRequest',
      code: lambda.Code.fromAsset(jarPath),
      environment: {
        BUCKET_NAME: bucket.bucketName,
      },
    });

    bucket.grantPut(importProductsFileLambda);
    bucket.grantRead(importProductsFileLambda);

    const api = new apigateway.RestApi(this, 'ImportProductsFileApi', {
      restApiName: 'Import Products API',
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
        allowHeaders: ['Content-Type', 'Authorization'],
      },
    });

    const importResource = api.root.addResource('import');
    const importIntegration = new apigateway.LambdaIntegration(importProductsFileLambda);
    importResource.addMethod('GET', importIntegration);

    new cdk.CfnOutput(this, 'ImportProductsApiUrl', {
      value: api.url,
      description: 'Endpoint URL for Import Products API',
    });
  }
}
