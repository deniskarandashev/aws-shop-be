#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { CdkProjectStack } from '../lib/cdk-project-stack';

const db = 'arn:aws:dynamodb:eu-north-1:058264442488:table'; // add your aws db ARN to check
const app = new cdk.App();
new CdkProjectStack(app, 'CdkProjectStack', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION,
    },
    productsTableArn: `${db}/products`,
    stocksTableArn: `${db}/stocks`

  /* For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html */
});