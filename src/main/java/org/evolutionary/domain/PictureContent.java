package org.evolutionary.domain;

import lombok.Data;

@Data
public class PictureContent {

    private final String name;
    private final String url;
    private final String description;

    public PictureContent(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description;
    }
}
