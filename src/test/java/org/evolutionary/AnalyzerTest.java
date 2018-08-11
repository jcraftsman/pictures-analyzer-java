package org.evolutionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class AnalyzerTest {

    private static final String PICTURES_DIRECTORY_PATH = "/users/me/pictures";

    private Analyzer analyzer;

    private Finder finder;
    private SafeBox safeBox;
    private SearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        searchEngine = mock(SearchEngine.class);
        safeBox = mock(SafeBox.class);
        finder = mock(Finder.class);
        analyzer = new Analyzer(finder, searchEngine, safeBox);
    }

    @Test
    void should_upload_one_file_to_the_safeBox_when_the_pictures_directory_contains_only_one_picture_file() {
        // Given
        String pathToPicture = "/users/me/pictures/top-secret.jpeg";
        List<String> allPathsInPicturesDirectory = singletonList(pathToPicture);
        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(safeBox).should().upload(pathToPicture);
    }
}