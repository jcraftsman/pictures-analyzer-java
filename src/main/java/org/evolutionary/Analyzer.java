package org.evolutionary;

public class Analyzer {
    private final Finder finder;
    private final OpticalCharacterRecognition opticalCharacterRecognition;
    private final SearchEngine searchEngine;
    private final SafeBox safeBox;

    public Analyzer(Finder finder, OpticalCharacterRecognition opticalCharacterRecognition, SearchEngine searchEngine, SafeBox safeBox) {

        this.finder = finder;
        this.opticalCharacterRecognition = opticalCharacterRecognition;
        this.searchEngine = searchEngine;
        this.safeBox = safeBox;
    }

    public void index(String picturesDirectoryPath) {
        Iterable<String> pathsToFilesInDirectory = this.finder.listFilePaths(picturesDirectoryPath);
        for (String pathToPicture : pathsToFilesInDirectory) {
            String publishedPictureUrl = this.safeBox.upload(pathToPicture);
            String textInPicture = this.opticalCharacterRecognition.imageToText(pathToPicture);
            this.searchEngine.index(new PictureContent(null, publishedPictureUrl, textInPicture));
        }
    }
}
