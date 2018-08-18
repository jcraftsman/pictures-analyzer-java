package integration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import integration.configuration.S3ServerMock;
import org.evolutionary.S3SafeBox;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class S3SafeBoxTest {
    private static final ClassLoader CLASS_LOADER = S3SafeBox.class.getClassLoader();
    private static final String PICTURES_DIRECTORY_PATH = CLASS_LOADER.getResource("pictures").getPath();
    private static final String PICTURE_FILE_NAME = "top_secret.png";
    private static final String PATH_TO_A_PICTURE_FILE = Paths.get(PICTURES_DIRECTORY_PATH, PICTURE_FILE_NAME).toString();
    private static final String BUCKET_NAME = "test-bucket";
    private static final String KEY_PREFIX = "agent-007";

    private S3SafeBox s3SafeBox;

    private AmazonS3 s3Client;
    private static S3ServerMock s3Server;

    @BeforeAll
    static void initAll() throws Exception {
        s3Server = S3ServerMock.getInstance();
        s3Server.start();
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        s3Server.stop();
    }

    @BeforeEach
    void setUp() {
        s3Server.createContainer(BUCKET_NAME);
        s3Client = s3Server.buildS3Client();
        s3SafeBox = new S3SafeBox(BUCKET_NAME, KEY_PREFIX, s3Client);
    }

    @AfterEach
    void tearDown() {
        s3Server.deleteContainer(BUCKET_NAME);
    }

    @Test
    void bucket_should_be_empty_on_startup() {
        // Then
        List<S3ObjectSummary> objectsInBucket = s3Client.listObjects(BUCKET_NAME).getObjectSummaries();
        assertThat(objectsInBucket).isEmpty();
    }

    @Test
    void should_upload_the_file_given_by_its_path_to_s3_bucket() {
        // When
        s3SafeBox.upload(PATH_TO_A_PICTURE_FILE);

        // Then
        List<S3ObjectSummary> objectsInBucket = s3Client.listObjects(BUCKET_NAME).getObjectSummaries();
        assertThat(objectsInBucket).hasSize(1);
        assertThat(objectsInBucket).extracting("key").containsExactly(KEY_PREFIX + "/" + PICTURE_FILE_NAME);
    }
}