package integration.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.gaul.s3proxy.S3Proxy;
import org.gaul.shaded.org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.Callable;

import static com.amazonaws.regions.Regions.US_EAST_1;
import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public class S3ServerMock {

    private static final String ACCESS_KEY = "identity";
    private static final String SECRET_KEY = "credential";
    private static final URI ENDPOINT = URI.create("http://127.0.0.1:8080");
    private static final String FILESYSTEM_STORE_CONTEXT_PROVIDER = "filesystem";

    private S3Proxy s3Proxy;
    private BlobStoreContext storeContext;

    private static S3ServerMock instance;

    private S3ServerMock(BlobStoreContext storeContext, S3Proxy s3Proxy) {
        this.storeContext = storeContext;
        this.s3Proxy = s3Proxy;
    }

    public static S3ServerMock getInstance() {
        if (instance == null) {
            S3ServerMock.instance = buildInstance();
        }
        return instance;
    }

    public void start() throws Exception {
        s3Proxy.start();
        await().atMost(3, SECONDS).until(serverIsStarted());
    }

    public AmazonS3 buildS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(ENDPOINT.toString(), US_EAST_1.getName()))
                .build();
    }

    public void createContainer(String bucketName) {
        storeContext.getBlobStore().createContainerInLocation(null, bucketName);
    }

    public void deleteContainer(String bucketName) {
        storeContext.getBlobStore().deleteContainer(bucketName);
    }

    public void stop() throws Exception {
        s3Proxy.stop();
        storeContext.close();
    }

    private static S3ServerMock buildInstance() {
        Properties properties = new Properties();
        properties.setProperty("jclouds.filesystem.basedir", "/tmp/blobstore");
        BlobStoreContext storeContext = ContextBuilder
                .newBuilder(FILESYSTEM_STORE_CONTEXT_PROVIDER)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .overrides(properties)
                .build(BlobStoreContext.class);
        BlobStore blobStore = storeContext.getBlobStore();
        S3Proxy s3Proxy = S3Proxy.builder()
                .blobStore(blobStore)
                .endpoint(ENDPOINT)
                .build();
        return new S3ServerMock(storeContext, s3Proxy);
    }

    private Callable<Boolean> serverIsStarted() {
        return () -> s3Proxy.getState().equals(AbstractLifeCycle.STARTED);
    }
}