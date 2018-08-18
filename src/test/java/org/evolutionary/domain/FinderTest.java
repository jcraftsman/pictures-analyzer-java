package org.evolutionary.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class FinderTest {

    private static final ClassLoader CLASS_LOADER = FinderTest.class.getClassLoader();
    private static final String PICTURES_DIRECTORY_PATH = CLASS_LOADER.getResource("pictures").getPath();
    private static final Path ROOT_FILES_DIRECTORY_PATH = Paths.get(CLASS_LOADER.getResource("files").getPath());
    private static final String TEMPORARY_DIRECTORY_PREFIX = "generated";
    private static final String PREFIX_OF_GENERATED_TEMPORARY_FILE = "temp-pic";

    private Finder localFilesFinder;

    private Path tempDirectory;

    @BeforeEach
    void setUp() throws IOException {
        localFilesFinder = new Finder();
        tempDirectory = Files.createTempDirectory(ROOT_FILES_DIRECTORY_PATH, TEMPORARY_DIRECTORY_PREFIX);
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteTempDirectoryAndItsContent();
    }

    @Test
    void should_return_files_with_name_and_path() {
        // Given
        String pictureFileName = "top_secret.png";
        String pathToPictureFile = Paths.get(PICTURES_DIRECTORY_PATH, pictureFileName).toString();

        // When
        Iterable<File> filesInDirectory = localFilesFinder.listFiles(PICTURES_DIRECTORY_PATH);

        // Then
        assertThat(filesInDirectory).extracting("name").containsExactly(pictureFileName);
        assertThat(filesInDirectory).extracting("path").containsExactly(pathToPictureFile);
    }

    @Test
    void should_return_all_the_files_in_the_directory() throws IOException {
        // Given
        int numberOfGeneratedTemporaryFiles = 10;
        Collection<String> generatedFileNames = generateTemporaryFiles(numberOfGeneratedTemporaryFiles);

        // When
        Iterable<File> filesInDirectory = localFilesFinder.listFiles(tempDirectory.toString());

        // Then
        assertThat(filesInDirectory).hasSize(numberOfGeneratedTemporaryFiles);
        assertThat(filesInDirectory)
                .extracting("name")
                .containsExactlyInAnyOrderElementsOf(generatedFileNames);
    }

    private Collection<String> generateTemporaryFiles(int numberOfGeneratedTemporaryFiles) throws IOException {
        Collection<String> generatedFileNames = new ArrayList<>();
        for (int index = 1; index <= numberOfGeneratedTemporaryFiles; index++) {
            String suffix = "-" + index + ".jpeg";
            Path temporaryFile = Files.createTempFile(tempDirectory, PREFIX_OF_GENERATED_TEMPORARY_FILE, suffix);
            generatedFileNames.add(temporaryFile.getFileName().toString());
        }
        return generatedFileNames;
    }

    private void deleteTempDirectoryAndItsContent() throws IOException {
        for (Path pathToTempFile : Files.list(tempDirectory).collect(Collectors.toList())) {
            Files.delete(pathToTempFile);
        }
        Files.deleteIfExists(tempDirectory);
    }
}