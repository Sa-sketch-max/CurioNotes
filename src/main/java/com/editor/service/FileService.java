package com.editor.service;

import com.editor.model.Note;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileService {

    private final Path notesDirectory;

    public FileService(){
        System.out.println("FileService constructor called");
        notesDirectory = Path.of("notes");

        try{
            Files.createDirectories(notesDirectory);
        }catch(IOException e){
            throw new RuntimeException("Failed to create notes directory");

        }
    }

    public List<Note> loadNotes(){
        try(Stream<Path> paths = Files.list(notesDirectory)){
            return paths
                    .filter(path -> path.toString().endsWith(".md"))
                    .map(Note::new)
                    .collect(Collectors.toList());

        }catch(IOException e){

            throw new RuntimeException("Failed to load notes.", e);
        }
    }

}