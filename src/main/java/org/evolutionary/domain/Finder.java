package org.evolutionary.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Finder {
    public Iterable<File> listFiles(String directoryPath) {
        try {
            return Files.list(Paths.get(directoryPath))
                    .map(this::mapPathToFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UnsupportedOperationException();
        }
    }

    private File mapPathToFile(Path path) {
        return File.builder()
                .name(path.getFileName().toString())
                .path(path.toAbsolutePath().toString())
                .build();
    }
}