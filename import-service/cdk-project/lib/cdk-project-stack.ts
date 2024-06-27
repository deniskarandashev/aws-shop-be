import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3Notifications from 'aws-cdk-lib/aws-s3-notifications';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as path from 'path';

export class CdkProjectStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create or import S3 bucket
    const bucket = s3.Bucket.fromBucketName(this, 'ImportServiceBucket', 'import-service-aws-shop') ||
        new s3.Bucket(this, 'ImportServiceBucket', {
          bucketName: 'import-service-aws-shop',
          removalPolicy: cdk.RemovalPolicy.DESTROY,
          autoDeleteObjects: true
        });

    // Define the path to the JAR file for the importProductsFileLambda function
    const jarPath = path.join(__dirname, '../../target/import-service-0.0.1-SNAPSHOT-shaded.jar');

    // Define the importProductsFile Lambda function
    const importProductsFileLambda = new lambda.Function(this, 'ImportProductsFileHandler', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'com.karandashev.aws_shop.aws_lambda_handler.ImportProductsFileHandler::handleRequest',
      code: lambda.Code.fromAsset(jarPath),
      environment: {
        BUCKET_NAME: bucket.bucketName,
      },
    });

    bucket.grantReadWrite(importProductsFileLambda);

    // Define the importFileParser Lambda function
    const importFileParserLambda = new lambda.Function(this, 'ImportFileParserHandler', {
      runtime: lambda.Runtime.JAVA_17,
      handler: 'com.karandashev.aws_shop.aws_lambda_handler.ImportFileParserHandler::handleRequest',
      code: lambda.Code.fromAsset(jarPath),
      environment: {
        BUCKET_NAME: bucket.bucketName,
      },
    });

    bucket.grantReadWrite(importFileParserLambda);

    // Create a role for the importFileParser Lambda function
    const lambdaExecutionRole = new iam.Role(this, 'ImportFileParserLambdaExecutionRole', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
    });

    lambdaExecutionRole.addToPolicy(new iam.PolicyStatement({
      actions: ['s3:GetObject'],
      resources: [`${bucket.bucketArn}/uploaded/*`],
    }));

    importFileParserLambda.addToRolePolicy(new iam.PolicyStatement({
      actions: ['logs:*'],
      resources: ['*'],
    }));

    // Configure S3 event notification to trigger importFileParserLambda
    bucket.addEventNotification(s3.EventType.OBJECT_CREATED, new s3Notifications.LambdaDestination(importFileParserLambda), {
      prefix: 'uploaded/',
    });

    // Define the API Gateway and link it with importProductsFile Lambda function
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
