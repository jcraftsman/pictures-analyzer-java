package feature;

import net.sourceforge.tess4j.Tesseract;
import org.evolutionary.domain.*;
import org.evolutionary.infra.TesseractOCR;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class IndexPictureContentFeature {

    private static final ClassLoader CLASS_LOADER = IndexPictureContentFeature.class.getClassLoader();
    private static final String PICTURES_DIRECTORY_PATH = CLASS_LOADER.getResource("pictures").getPath();
    private static final String PICTURE_FILE_NAME = "top_secret.png";
    private static final String PATH_TO_A_PICTURE_FILE = Paths.get(PICTURES_DIRECTORY_PATH, PICTURE_FILE_NAME).toString();
    private static final String PUBLISHED_PICTURE_URL = "https://s3.eu-west-3.amazonaws.com/evolutionary-confidential/agent-phillip/top_secret.png";
    private static final String TESSERACT_DATA_DIRECTORY_PATH = CLASS_LOADER.getResource("tesseract").getPath();
    private static final String TEXT_IN_PICTURE = "Rezidentura";

    private Analyzer analyzer;

    private SafeBox safeBox;
    private SearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        searchEngine = mock(SearchEngine.class);
        safeBox = mock(SafeBox.class);
        Finder localFilesFinder = new Finder();
        Tesseract tesseractInstance = new Tesseract();
        tesseractInstance.setDatapath(TESSERACT_DATA_DIRECTORY_PATH);
        OpticalCharacterRecognition opticalCharacterRecognition = new TesseractOCR(tesseractInstance);
        analyzer = new Analyzer(localFilesFinder, opticalCharacterRecognition, searchEngine, safeBox);
    }

    @Test
    void should_use_search_engine_to_index_analyzed_picture_content() {
        // Given
        given(safeBox.upload(PATH_TO_A_PICTURE_FILE)).willReturn(PUBLISHED_PICTURE_URL);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        PictureContent analyzedPictureContent = new PictureContent(PICTURE_FILE_NAME, PUBLISHED_PICTURE_URL, TEXT_IN_PICTURE);
        then(searchEngine).should().index(analyzedPictureContent);
    }
}