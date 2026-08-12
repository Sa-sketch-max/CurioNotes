package com.editor.ui.workspace;

import com.editor.markdown.MarkdownEngine;
import com.editor.service.FileService;
import com.editor.model.Note;

public class WorkspaceController {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    private final MarkdownEngine markdownEngine;
    private final FileService fileService;
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
        String content = fileService.readNote(note);
        System.out.println(content);
        editorPane.setText(content);
        System.out.println("Editor updated");
    }

}