package org.evolutionary;

public class Analyzer {
    private final Finder finder;
    private final SearchEngine searchEngine;
    private final SafeBox safeBox;

    public Analyzer(Finder finder, SearchEngine searchEngine, SafeBox safeBox) {

        this.finder = finder;
        this.searchEngine = searchEngine;
        this.safeBox = safeBox;
    }

    public void index(String picturesDirectoryPath) {
        Iterable<String> pathsToFilesInDirectory = this.finder.listFilePaths(picturesDirectoryPath);
        for (String pathToPicture : pathsToFilesInDirectory) {
            this.safeBox.upload(pathToPicture);
        }
    }
}
