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
    private OpticalCharacterRecognition opticalCharacterRecognition;

    @BeforeEach
    void setUp() {
        searchEngine = mock(SearchEngine.class);
        safeBox = mock(SafeBox.class);
        finder = mock(Finder.class);
        opticalCharacterRecognition = mock(OpticalCharacterRecognition.class);
        analyzer = new Analyzer(finder, opticalCharacterRecognition, searchEngine, safeBox);
    }

    @Test
    void should_upload_one_file_to_the_safeBox_when_the_pictures_directory_contains_only_one_picture_file() {
        // Given
        String pathToPicture = "/users/me/pictures/top-secret.jpeg";
        File pictureFile = File.builder().path(pathToPicture).build();
        List<File> allPathsInPicturesDirectory = singletonList(pictureFile);

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
        File firstPicture = File.builder().path(pathToFirstPicture).build();
        File secondPicture = File.builder().path(pathToSecondPicture).build();
        List<File> allPathsInPicturesDirectory = asList(firstPicture, secondPicture);
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
        File pictureFile = File.builder().path(pathToPicture).build();
        List<File> allPathsInPicturesDirectory = singletonList(pictureFile);
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
        File picture1 = File.builder().path("/pic1").build();
        File picture2 = File.builder().path("/pic2").build();
        File picture3 = File.builder().path("/pic3").build();
        List<File> allPathsInPicturesDirectory = asList(picture1, picture2, picture3);
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

    @Test
    void should_index_in_the_search_engine_the_recognized_text_in_the_picture() {
        // Given
        String pathToPictureFile = "/pic1.jpeg";
        File pictureFile = File.builder().path(pathToPictureFile).build();
        List<File> allPathsInPicturesDirectory = singletonList(pictureFile);
        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        String textInPicture = "recognized text in the picture";
        given(opticalCharacterRecognition.imageToText(pathToPictureFile))
                .willReturn(textInPicture);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(searchEngine).should()
                .index(argThat(pictureContent -> pictureContent.getDescription().equals(textInPicture)));
    }

    @Test
    void should_index_in_the_search_engine_the_recognized_text_from_each_picture() {
        // Given
        File picture1 = File.builder().path("/pic1.jpeg").build();
        File picture2 = File.builder().path("/pic2.jpeg").build();
        File picture3 = File.builder().path("/pic3.jpeg").build();
        List<File> allPathsInPicturesDirectory = asList(picture1, picture2, picture3);
        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        String textInPicture1 = "recognized text in the the first picture";
        String textInPicture2 = "recognized text in the the second picture";
        String textInPicture3 = "recognized text in the the third picture";
        given(opticalCharacterRecognition.imageToText(any()))
                .willReturn(textInPicture1)
                .willReturn(textInPicture2)
                .willReturn(textInPicture3);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(searchEngine).should()
                .index(argThat(pictureContent -> pictureContent.getDescription().equals(textInPicture1)));
        then(searchEngine).should()
                .index(argThat(pictureContent -> pictureContent.getDescription().equals(textInPicture2)));
        then(searchEngine).should()
                .index(argThat(pictureContent -> pictureContent.getDescription().equals(textInPicture3)));
    }

    @Test
    void should_index_in_the_search_engine_the_picture_file_name() {
        // Given
        String pathToPictureFile = "/users/me/pictures/pic1.jpeg";
        String pictureFileName = "pic1.jpeg";
        File pictureFile = File.builder().name(pictureFileName).path(pathToPictureFile).build();
        List<File> allPathsInPicturesDirectory = singletonList(pictureFile);

        given(finder.listFilePaths(PICTURES_DIRECTORY_PATH))
                .willReturn(allPathsInPicturesDirectory);

        // When
        analyzer.index(PICTURES_DIRECTORY_PATH);

        // Then
        then(searchEngine).should()
                .index(argThat(pictureContent -> pictureFileName.equals(pictureContent.getName())));
    }
}