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

    public List<Note> loadNotes() {

        try (Stream<Path> paths = Files.list(notesDirectory)) {

            List<Note> notes = paths
                    .filter(path -> path.toString().endsWith(".md"))
                    .map(Note::new)
                    .collect(Collectors.toList());

            System.out.println("Loaded notes: " + notes.size());

            return notes;

        } catch (IOException e) {

            throw new RuntimeException("Failed to load notes.", e);
        }
    }

    public String readNote(Note note) {

        System.out.println("Reading: " + note.getPath());

        try {

            String text = Files.readString(note.getPath());

            System.out.println("Read successful");
            System.out.println(text);

            return text;

        } catch (IOException e) {

            e.printStackTrace();

            throw new RuntimeException("Failed to read note.", e);

        }

    }

    public void saveNote(Note note, String content) {

        try {

            Files.writeString(note.getPath(), content);

        } catch (IOException e) {

            throw new RuntimeException("Failed to save note.", e);
        }
    }

    public Note createNote(String name) {
        if (!name.endsWith(".md")) {
            name += ".md";
        }

        Path notePath = notesDirectory.resolve(name);

        try {
            Files.createFile(notePath);
            return new Note(notePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create note.", e);
        }
    }

    public void deleteNote(Note note) {
        try {
            Files.delete(note.getPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete note.", e);
        }
    }


    public Note renameNote(Note note, String newName) {

        if (!newName.endsWith(".md")) {
            newName += ".md";
        }

        Path newPath = notesDirectory.resolve(newName);

        try {
            Files.move(note.getPath(), newPath);

            return new Note(newPath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to rename note.", e);
        }
    }



}