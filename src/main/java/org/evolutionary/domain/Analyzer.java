package org.evolutionary.domain;

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
        Iterable<File> pathsToFilesInDirectory = this.finder.listFiles(picturesDirectoryPath);
        for (File pictureFile : pathsToFilesInDirectory) {
            String pathToPicture = pictureFile.getPath();
            String publishedPictureUrl = this.safeBox.upload(pathToPicture);
            String textInPicture = this.opticalCharacterRecognition.imageToText(pathToPicture);
            PictureContent content = new PictureContent(pictureFile.getName(), publishedPictureUrl, textInPicture);
            this.searchEngine.index(content);
        }
    }
}
