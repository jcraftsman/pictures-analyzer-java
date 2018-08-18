package org.evolutionary;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

public class S3SafeBox implements SafeBox {
    private final String keyPrefix;
    private final String bucketName;
    private final AmazonS3 amazonS3Client;

    public S3SafeBox(String bucketName, String keyPrefix, AmazonS3 amazonS3Client) {
        this.bucketName = bucketName;
        this.keyPrefix = keyPrefix;
        this.amazonS3Client = amazonS3Client;
    }

    @Override
    public String upload(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String keyName = keyPrefix + "/" + fileName;
        File fileToUpload = new File(filePath);
        try {
            amazonS3Client.putObject(bucketName, keyName, fileToUpload);
            URL urlToUploadedFile = amazonS3Client.getUrl(bucketName, keyName);
            return urlToUploadedFile.toString();
        } catch (AmazonServiceException awsException) {
            throw new UnsupportedOperationException(awsException);
        }
    }
}