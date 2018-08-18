package org.evolutionary.domain;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class OpticalCharacterRecognitionTest {

    private static final String PATH_TO_PICTURE_FILE = "/my-secret-folder/picture.jpeg";

    private OpticalCharacterRecognition opticalCharacterRecognition;

    private ITesseract tesseractInstance;

    @BeforeEach
    void setUp() {
        tesseractInstance = mock(ITesseract.class);
        opticalCharacterRecognition = new OpticalCharacterRecognition(tesseractInstance);
    }

    @Test
    void imageToText_should_return_the_recognized_text_using_tesseract_on_the_given_file() throws TesseractException {
        // Given
        File pictureFile = new File(PATH_TO_PICTURE_FILE);
        String ocrResultFromTesseract = "Confidential";
        given(tesseractInstance.doOCR(pictureFile)).willReturn(ocrResultFromTesseract);

        // When
        String recognizedText = opticalCharacterRecognition.imageToText(PATH_TO_PICTURE_FILE);

        // Then
        assertThat(recognizedText).isEqualTo(ocrResultFromTesseract);
    }

    @Test
    void imageToText_should_return_the_recognized_text_without_trailing_blanks() throws TesseractException {
        // Given
        File pictureFile = new File(PATH_TO_PICTURE_FILE);
        given(tesseractInstance.doOCR(pictureFile)).willReturn(" Confidential content  \n  ");

        // When
        String recognizedText = opticalCharacterRecognition.imageToText(PATH_TO_PICTURE_FILE);

        // Then
        assertThat(recognizedText).isEqualTo("Confidential content");
    }
}