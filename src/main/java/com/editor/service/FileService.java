package com.editor.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileService {

    public String readFile(File file) throws IOException {

        return Files.readString(file.toPath());

    }

    public void saveFile(File file, String content) throws IOException {

        Files.writeString(file.toPath(), content);

    }

}