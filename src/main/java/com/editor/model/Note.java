package com.editor.model;


import java.nio.file.Path;

public class Note {
    private final Path path;

    public Note(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return path.getFileName().toString();
    }

    @Override
    public String toString() {
        return getName();
    }
}
