# Backend for AWS Developer course (RS School)

## [Task 4] Add backend using AWS Lambdas and API Gateway - CURRENT STEP

### Links

* Task: https://github.com/rolling-scopes-school/aws/blob/main/aws-developer/04_integration_with_nosql_database/task.md
* cloudfront: https://d3czter152p0zl.cloudfront.net

### Estimation (self-estimation: 0/100)

* #### Main [0/70]
##### Task 4.1
- [x] 1 - Use AWS Console to create two database tables in DynamoDB.
- [x] 2 - Write a script to fill tables with test examples. Store it in your Github repository. Execute it for your DB to fill data.

##### Task 4.2
- [x] 3 - Extend your AWS CDK Stack with data about your database table and pass it to lambda’s environment variables section.
- [ ] 4 - Integrate the getProductsList lambda to return via GET /products request a list of products from the database (joined stocks and products tables).
- [ ] 5 - Implement a Product model on FE side as a joined model of product and stock by productId.
- [ ] 6 - Integrate the getProductsById lambda to return via GET /products/{productId} request a single product from the database.

##### Task 4.3
- [ ] 7 - Create a lambda function called createProduct under the Product Service which will be triggered by the HTTP POST method.
- [ ] 8 - The requested URL should be /products.
- [ ] 9 - Implement its logic so it will be creating a new item in a Products table.
- [ ] 10 - Save the URL (API Gateway URL) to execute the implemented lambda functions for later - you'll need to provide it in the PR (e.g in PR's description) when submitting the task.

##### Task 4.4
- [ ] 11 - Commit all your work to separate branch (e.g. task-4 from the latest master) in BE (backend) and if needed in FE (frontend) repositories.
- [x] 12 - Create a pull request to the master branch.
- [x] 13 - Submit link to the pull request to Crosscheck page in RS App.


* ### Additional [0/30]
- [ ] +7.5 (All languages) - POST /products lambda functions returns error 400 status code if product data is invalid
- [ ]  +7.5 (All languages) - All lambdas return error 500 status code on any error (DB connection, any unhandled error in code)
- [ ]  +7.5 (All languages) - All lambdas do console.log for each incoming requests and their arguments
- [ ]  +7.5 (All languages) - Transaction based creation of product (in case stock creation is failed then related to this stock product is not created and not ready to be used by the end user and vice versa)

## [Task 3] Add backend using AWS Lambdas and API Gateway - ARCHIVED STEP

### Links

* Task: https://github.com/rolling-scopes-school/aws/blob/main/aws-developer/03_serverless_api/task.md
* cloudfront: https://d3czter152p0zl.cloudfront.net

* GitHub repos (`task-3` branch):
    * back-end: https://github.com/deniskarandashev/aws-shop-be
    * front-end: https://github.com/deniskarandashev/nodejs-aws-shop-react
* Requests:
    * GET /products: https://sfn070sct9.execute-api.eu-north-1.amazonaws.com/prod/products
    * GET /products/{productId}: https://sfn070sct9.execute-api.eu-north-1.amazonaws.com/prod/products/10
* Pull requests:
    * back-end: https://github.com/deniskarandashev/aws-shop-be/pull/1
    * front-end: https://github.com/deniskarandashev/nodejs-aws-shop-react/pull/2
* Swagger:
    * local:
        * swagger ui: http://localhost:8080/swagger-ui/index.html
        * api-docs: http://localhost:8080/v3/api-docs
    * `openapi.json` in the root of back-end project (`task-3` branch) contains code that can be rendered by https://editor.swagger.io/

### Estimation (self-estimation: 100/100)

* #### Main [70/70]
[+] Product Service contains configuration for 2 lambda functions, API is not working at all, but configuration is correct

[+] The getProductsList OR getProductsById lambda function returns a correct response (POINT1)

[+] The getProductsById AND getProductsList lambda functions return a correct response code (POINT2)

[+] Your own Frontend application is integrated with Product Service (/products API) and products from Product Service are represented on Frontend. AND POINT1 and POINT2 are done.

* #### Additional [30/30]
[+] +7.5 (All languages) - Swagger documentation is created for Product Service. This can be, for example, openapi.(json|yaml) added to the repository, that can be rendered by https://editor.swagger.io/

[+] +7.5 (All languages) - Lambda handlers are covered by basic UNIT tests (NO infrastructure logic is needed to be covered)

[+] +7.5 (All languages) - Lambda handlers (getProductsList, getProductsById) code is written not in 1 single module (file) and separated in codebase.

[+] +7.5 (All languages) - Main error scenarios are handled by API ("Product not found" error).

## [Task 2] React-shop-cloudfront - ARCHIVED STEP

_NOTE: info below might be outdated because of next steps._

### Links

* cloudfront: https://dike63j25qf7o.cloudfront.net
* s3: http://awsstack-deploymentfrontendawsbucketc2ffaf4a-qy7lnix22sny.s3-website.eu-north-1.amazonaws.com


### Estimation (self-estimation: 100/100)

[+] - **30** - S3 bucket has been created and configured properly. The app has been uploaded to the bucket and is available though the Internet. Nothing else has been done.
_(Link to S3 bucket/website is provided. There is no Pull Request in the YOUR OWN frontend repository.)_

[+] - **40** - In addition to the previous work a CloudFront distribution is created and configured properly and the site is served now with CloudFront and is available through the Internet over CloudFront URL, not S3-website link (due to changes in bucket’s policy...).
_(Link to CloudFront website is provided. S3-website shows 403 Access Denied error. There is no Pull Request in the YOUR OWN frontend repository.)_

[+] - **30** - S3 bucket creation, website deployment, CloudFront Distribution and Invalidation added and configured by using AWS CDK. The app can be built and deployed by running npm script command.
_(Link to CloudFront website is provided. PR with all changes is submitted in the YOUR OWN frontend repository and its link is provided for review.)_