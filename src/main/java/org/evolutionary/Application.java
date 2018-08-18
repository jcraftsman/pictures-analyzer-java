package org.evolutionary;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import net.sourceforge.tess4j.Tesseract;

public class Application {

    private static final ClassLoader CLASS_LOADER = Application.class.getClassLoader();
    private static final String TESSERACT_DATA_DIRECTORY_PATH = CLASS_LOADER.getResource("tesseract").getPath();
    private static final String S3_SAFE_BOX_BUCKET_NAME_KEY = "S3_SAFE_BOX_BUCKET_NAME";
    private static final String SEARCH_ENGINE_URL_KEY = "SEARCH_ENGINE_URL";


    public static void main(String[] args) {

        checkArguments(args);
        String safeBoxKeyPrefix = args[0];
        String pictureDirectoryPath = args[1];

        String bucketName = System.getenv(S3_SAFE_BOX_BUCKET_NAME_KEY);
        SafeBox safeBox = new S3SafeBox(bucketName, safeBoxKeyPrefix, AmazonS3ClientBuilder.defaultClient());

        String searchEngineUrl = System.getenv(SEARCH_ENGINE_URL_KEY);
        SearchEngine searchEngine = SearchEngineHttpClient.createInstance(searchEngineUrl);

        Tesseract tesseractInstance = new Tesseract();
        tesseractInstance.setDatapath(TESSERACT_DATA_DIRECTORY_PATH);
        OpticalCharacterRecognition opticalCharacterRecognition = new OpticalCharacterRecognition(tesseractInstance);

        Finder localFilesFinder = new Finder();

        Analyzer analyzer = new Analyzer(localFilesFinder, opticalCharacterRecognition, searchEngine, safeBox);

        analyzer.index(pictureDirectoryPath);
    }

    private static void checkArguments(String[] args) {
        final String USAGE = "\n" +
                "To run pictures analysis and indexing, supply the agent name and a path to pictures directory.\n" +
                "pictures-analyzer <agentName> <picturesDirectoryPath>\n" +
                "\n" +
                "Ex:\n" +
                "java -jar pictures-analyzer \"agent-007\" \"/Users/james/Documents/confidential/pictures\" \n";
        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }
    }
}
