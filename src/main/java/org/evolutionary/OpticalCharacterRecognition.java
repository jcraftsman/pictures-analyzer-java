package org.evolutionary;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OpticalCharacterRecognition {

    private final ITesseract tesseractInstance;

    public OpticalCharacterRecognition(ITesseract tesseractInstance) {
        this.tesseractInstance = tesseractInstance;
    }

    public String imageToText(String pathToPictureFile) {
        java.io.File imageFile = new File(pathToPictureFile);
        try {
            String result = tesseractInstance.doOCR(imageFile);
            return result.trim();
        } catch (TesseractException e) {
            throw new UnsupportedOperationException();
        }
    }
}
