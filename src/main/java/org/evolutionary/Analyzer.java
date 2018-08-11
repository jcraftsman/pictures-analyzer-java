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
        String pathToPicture = this.finder.listFilePaths(picturesDirectoryPath).iterator().next();
        this.safeBox.upload(pathToPicture);
    }
}
