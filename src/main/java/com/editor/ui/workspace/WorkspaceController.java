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

        });

    }

    public void openNote(Note note) {
        System.out.println("Opening: " + note.getName());

        currentNote = note;

        String content = fileService.readNote(note);

        editorPane.setText(content);
        System.out.println("Editor updated");
    }

    public void saveCurrentNote() {

        if (currentNote == null) {
            System.out.println("No note is currently open.");
            return;
        }

        String content = editorPane.getText();

        fileService.saveNote(currentNote, content);

        System.out.println("Saved: " + currentNote.getName());
    }

//    public void saveNote(Note note, String content) {
//
//        try {
//
//            Files.writeString(note.getPath(), content);
//
//        } catch (IOException e) {
//
//            throw new RuntimeException(
//                    "Failed to save note.",
//                    e
//            );
//
//        }
//
//    }

}