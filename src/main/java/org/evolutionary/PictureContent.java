package org.evolutionary;

import java.util.Objects;

public class PictureContent {

    private final String name;
    private final String url;
    private final String description;

    public PictureContent(String name, String url, String description) {
        this.name = name;
        this.url = url;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PictureContent that = (PictureContent) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(url, that.url) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, url, description);
    }

    @Override
    public String toString() {
        return "PictureContent{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
