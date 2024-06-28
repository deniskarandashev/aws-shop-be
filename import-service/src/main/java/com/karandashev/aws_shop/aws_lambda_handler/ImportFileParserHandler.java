package com.karandashev.aws_shop.aws_lambda_handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.opencsv.CSVReader;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStreamReader;

public class ImportFileParserHandler implements RequestHandler<S3Event, Void> {

    private static final S3Client s3Client = S3Client.builder().region(Region.EU_NORTH_1).build();

    @Override
    public Void handleRequest(S3Event event, Context context) {
        try {
            for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
                String bucketName = record.getS3().getBucket().getName();
                String objectKey = record.getS3().getObject().getKey();
                ResponseInputStream<GetObjectResponse> responseStream = downloadObject(bucketName, objectKey);
                parseCsvFile(responseStream, bucketName, objectKey, context);
            }
        } catch (Exception e) {
            context.getLogger().log("Error processing S3 event: " + e.getMessage());
        }
        return null;
    }

    private ResponseInputStream<GetObjectResponse> downloadObject(String bucketName, String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

        // Download the object
        return s3Client.getObject(getObjectRequest);
    }

    private void parseCsvFile(
            ResponseInputStream<GetObjectResponse> responseStream,
            String bucketName,
            String objectKey,
            Context context
    ) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(responseStream))) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                context.getLogger().log("CSV Line: " + String.join(",", line));
            }

            removeFileAfterParsing(bucketName, objectKey, context);

        } catch (Exception e) {
            throw new RuntimeException("Error parsing CSV file and moving it to 'parsed/' folder", e);
        }
    }

    private void removeFileAfterParsing(String bucketName, String objectKey, Context context) {
        String parsedObjectKey = "parsed/" + objectKey.substring(objectKey.lastIndexOf('/') + 1);
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(objectKey)
                .destinationBucket(bucketName)
                .destinationKey(parsedObjectKey)
                .build();

        s3Client.copyObject(copyRequest);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteRequest);

        context.getLogger().log("File moved from 'uploaded/' to 'parsed/': " + objectKey);
    }
}
