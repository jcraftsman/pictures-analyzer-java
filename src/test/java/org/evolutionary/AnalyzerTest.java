package org.evolutionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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

    @Test
    void should_upload_to_the_safeBox_all_the_files_in_the_pictures_directory() {
        // Given
        String pathToFirstPicture = "/users/me/pictures/top-secret.jpeg";
        String pathToSecondPicture = "/users/me/pictures/confidential.jpeg";
        List<String> allPathsInPicturesDirectory = asList(pathToFirstPicture, pathToSecondPicture);
        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(safeBox).should().upload(pathToFirstPicture);
        then(safeBox).should().upload(pathToSecondPicture);
    }

    @Test
    void should_send_the_url_of_the_uploaded_picture_to_the_search_engine() {
        // Given
        String pathToPicture = "/users/me/pictures/top-secret.jpeg";
        List<String> allPathsInPicturesDirectory = singletonList(pathToPicture);
        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        String url = "https://foo.bar/my-uploaded-picture.png";
        given(safeBox.upload(pathToPicture))
                .willReturn(url);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(searchEngine).should().index(argThat(pictureContent -> pictureContent.getUrl().equals(url)));
    }

    @Test
    void should_index_in_the_search_engine_the_urls_of_all_the_uploaded_pictures() {
        // Given
        List<String> allPathsInPicturesDirectory = asList("/pic1", "/pic2", "/pic3");
        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        String url1 = "https://foo.bar/my-uploaded-picture1.png";
        String url2 = "https://foo.bar/my-uploaded-picture2.png";
        String url3 = "https://foo.bar/my-uploaded-picture3.png";
        given(safeBox.upload(any()))
                .willReturn(url1)
                .willReturn(url2)
                .willReturn(url3);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(searchEngine).should().index(argThat(pictureContent -> pictureContent.getUrl().equals(url1)));
        then(searchEngine).should().index(argThat(pictureContent -> pictureContent.getUrl().equals(url2)));
        then(searchEngine).should().index(argThat(pictureContent -> pictureContent.getUrl().equals(url3)));
    }
}