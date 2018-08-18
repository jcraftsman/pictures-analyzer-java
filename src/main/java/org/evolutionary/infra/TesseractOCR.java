package org.evolutionary.infra;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.evolutionary.domain.OpticalCharacterRecognition;

import java.io.File;

public class TesseractOCR implements OpticalCharacterRecognition {

    private final ITesseract tesseractInstance;

    public TesseractOCR(ITesseract tesseractInstance) {
        this.tesseractInstance = tesseractInstance;
    }

    @Override
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
