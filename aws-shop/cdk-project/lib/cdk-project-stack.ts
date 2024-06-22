// import * as cdk from 'aws-cdk-lib';
// import { Construct } from 'constructs';
// import * as lambda from 'aws-cdk-lib/aws-lambda';
// import * as apigateway from 'aws-cdk-lib/aws-apigateway';
// import * as path from 'path';
//
// interface CdkProjectStackProps extends cdk.StackProps {
//   productsTableArn: string; // ARN of the products DynamoDB table
//   stocksTableArn: string; // ARN of the stocks DynamoDB table
// }
//
// export class CdkProjectStack extends cdk.Stack {
//   constructor(scope: Construct, id: string, props: CdkProjectStackProps) {
//     super(scope, id, props);
//
//     const jarPath = path.join(__dirname, '../../target/aws-shop-0.0.1-SNAPSHOT-shaded.jar');
//
//     const getProductsListLambda = new lambda.Function(this, 'GetProductsListLambda', {
//       runtime: lambda.Runtime.JAVA_17,
//       code: lambda.Code.fromAsset(jarPath),
//       handler: 'com.karandashev.aws_shop.aws_lambda_handler.GetProductsListHandler::handleRequest',
//       environment: {
//         PRODUCTS_TABLE_ARN: props.productsTableArn, // Environment variable for products table ARN
//         STOCKS_TABLE_ARN: props.stocksTableArn, // Environment variable for stocks table ARN
//       },
//       memorySize: 1024,
//       timeout: cdk.Duration.seconds(30),
//     });
//
//     const api = new apigateway.RestApi(this, 'productsApi', {
//       restApiName: 'Products Service',
//       description: 'This service serves products.',
//     });
//
//     const products = api.root.addResource('products');
//     const getProductsListIntegration = new apigateway.LambdaIntegration(getProductsListLambda);
//     products.addMethod('GET', getProductsListIntegration, {
//       authorizationType: apigateway.AuthorizationType.NONE,
//       methodResponses: [
//         {
//           statusCode: '200',
//           responseParameters: {
//             'method.response.header.Access-Control-Allow-Origin': true,
//           },
//         },
//       ],
//     });
//
//     products.addMethod('OPTIONS', new apigateway.MockIntegration({
//       integrationResponses: [
//         {
//           statusCode: '200',
//           responseParameters: {
//             'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'",
//             'method.response.header.Access-Control-Allow-Origin': "'*'",
//             'method.response.header.Access-Control-Allow-Credentials': "'false'",
//             'method.response.header.Access-Control-Allow-Methods': "'OPTIONS,GET,PUT,POST,DELETE'",
//           },
//         },
//       ],
//       passthroughBehavior: apigateway.PassthroughBehavior.NEVER,
//       requestTemplates: {
//         'application/json': '{"statusCode": 200}',
//       },
//     }), {
//       methodResponses: [
//         {
//           statusCode: '200',
//           responseParameters: {
//             'method.response.header.Access-Control-Allow-Headers': true,
//             'method.response.header.Access-Control-Allow-Origin': true,
//             'method.response.header.Access-Control-Allow-Credentials': true,
//             'method.response.header.Access-Control-Allow-Methods': true,
//           },
//         },
//       ],
//     });
//
//     // TODO: fix in the next steps
//     // const singleProduct = products.addResource('{productId}');
//     // const getProductByIdIntegration = new apigateway.LambdaIntegration(getProductByIdLambda);
//     // singleProduct.addMethod('GET', getProductByIdIntegration, {
//     //   authorizationType: apigateway.AuthorizationType.NONE,
//     //   methodResponses: [
//     //     {
//     //       statusCode: '200',
//     //       responseParameters: {
//     //         'method.response.header.Access-Control-Allow-Origin': true,
//     //       },
//     //     },
//     //   ],
//     // });
//
//     // singleProduct.addMethod('OPTIONS', new apigateway.MockIntegration({
//     //   integrationResponses: [
//     //     {
//     //       statusCode: '200',
//     //       responseParameters: {
//     //         'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'",
//     //         'method.response.header.Access-Control-Allow-Origin': "'*'",
//     //         'method.response.header.Access-Control-Allow-Credentials': "'false'",
//     //         'method.response.header.Access-Control-Allow-Methods': "'OPTIONS,GET,PUT,POST,DELETE'",
//     //       },
//     //     },
//     //   ],
//     //   passthroughBehavior: apigateway.PassthroughBehavior.NEVER,
//     //   requestTemplates: {
//     //     'application/json': '{"statusCode": 200}',
//     //   },
//     // }), {
//     //   methodResponses: [
//     //     {
//     //       statusCode: '200',
//     //       responseParameters: {
//     //         'method.response.header.Access-Control-Allow-Headers': true,
//     //         'method.response.header.Access-Control-Allow-Origin': true,
//     //         'method.response.header.Access-Control-Allow-Credentials': true,
//     //         'method.response.header.Access-Control-Allow-Methods': true,
//     //       },
//     //     },
//     //   ],
//     // });
//   }
// }

import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as path from 'path';

interface CdkProjectStackProps extends cdk.StackProps {
  productsTableArn: string; // ARN of the products DynamoDB table
  stocksTableArn: string; // ARN of the stocks DynamoDB table
}

export class CdkProjectStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: CdkProjectStackProps) {
    super(scope, id, props);

    const jarPath = path.join(__dirname, '../../target/aws-shop-0.0.1-SNAPSHOT-shaded.jar');

    const getProductsListLambda = new lambda.Function(this, 'GetProductsListLambda', {
      runtime: lambda.Runtime.JAVA_17,
      code: lambda.Code.fromAsset(jarPath),
      handler: 'com.karandashev.aws_shop.aws_lambda_handler.GetProductsListHandler::handleRequest',
      environment: {
        PRODUCTS_TABLE_NAME: props.productsTableArn.split('/').pop()!,
        STOCKS_TABLE_NAME: props.stocksTableArn.split('/').pop()!,
      },
      memorySize: 1024,
      timeout: cdk.Duration.seconds(30),
    });

    const api = new apigateway.RestApi(this, 'productsApi', {
      restApiName: 'Products Service',
      description: 'This service serves products.',
    });

    const products = api.root.addResource('products');
    const getProductsListIntegration = new apigateway.LambdaIntegration(getProductsListLambda);
    products.addMethod('GET', getProductsListIntegration, {
      authorizationType: apigateway.AuthorizationType.NONE,
      methodResponses: [
        {
          statusCode: '200',
          responseParameters: {
            'method.response.header.Access-Control-Allow-Origin': true,
          },
        },
      ],
    });

    products.addMethod('OPTIONS', new apigateway.MockIntegration({
      integrationResponses: [
        {
          statusCode: '200',
          responseParameters: {
            'method.response.header.Access-Control-Allow-Headers': "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'",
            'method.response.header.Access-Control-Allow-Origin': "'*'",
            'method.response.header.Access-Control-Allow-Credentials': "'false'",
            'method.response.header.Access-Control-Allow-Methods': "'OPTIONS,GET,PUT,POST,DELETE'",
          },
        },
      ],
      passthroughBehavior: apigateway.PassthroughBehavior.NEVER,
      requestTemplates: {
        'application/json': '{"statusCode": 200}',
      },
    }), {
      methodResponses: [
        {
          statusCode: '200',
          responseParameters: {
            'method.response.header.Access-Control-Allow-Headers': true,
            'method.response.header.Access-Control-Allow-Origin': true,
            'method.response.header.Access-Control-Allow-Credentials': true,
            'method.response.header.Access-Control-Allow-Methods': true,
          },
        },
      ],
    });
  }
}
