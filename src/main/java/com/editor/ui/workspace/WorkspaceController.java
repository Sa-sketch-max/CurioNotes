package com.editor.ui.workspace;

import com.editor.markdown.MarkdownEngine;
import com.editor.service.FileService;
import com.editor.model.Note;


public class WorkspaceController {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    private final MarkdownEngine markdownEngine;
    private final FileService fileService;


    private Note currentNote;
    private boolean dirty;

    public WorkspaceController(EditorPane editorPane,
                               PreviewPane previewPane,
                               MarkdownEngine markdownEngine,
                               FileService fileService) {

        this.editorPane = editorPane;
        this.previewPane = previewPane;
        this.markdownEngine = markdownEngine;
        this.fileService = fileService;
    }

    public void initialize() {

        editorPane.textProperty().addListener((observable, oldText, newText) -> {

            String html = markdownEngine.parseToHtml(newText);

            previewPane.setHtml(html);

            dirty = true;

            System.out.println("Dirty state: " + dirty);

        });

    }

    public void openNote(Note note) {
        System.out.println("Opening: " + note.getName());

        currentNote = note;

        String content = fileService.readNote(note);

        editorPane.setText(content);
        dirty = false;
        System.out.println("Note opened. Dirty state: " + dirty);
    }

    public void saveCurrentNote() {

        if (currentNote == null) {
            System.out.println("No note is currently open.");
            return;
        }

        String content = editorPane.getText();

        fileService.saveNote(currentNote, content);

        dirty = false;

        System.out.println("Note saved. Dirty state: " + dirty);
    }

    public Note createNote(String name) {

        return fileService.createNote(name);

    }

    public void deleteNote(Note note) {

        fileService.deleteNote(note);

        if (currentNote != null &&
                currentNote.getPath().equals(note.getPath())) {

            currentNote = null;
            editorPane.clear();
        }
    }

    public Note renameNote(Note note, String newName) {

        Note renamedNote = fileService.renameNote(note, newName);

        if (currentNote != null &&
                currentNote.getPath().equals(note.getPath())) {

            currentNote = renamedNote;
        }

        return renamedNote;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean hasUnsavedChanges() {
        return dirty;
    }


    public boolean isCurrentNote(Note note) {

        return currentNote != null
                && currentNote.getPath().equals(note.getPath());
    }



}