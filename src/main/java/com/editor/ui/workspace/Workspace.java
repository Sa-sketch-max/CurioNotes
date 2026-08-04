package com.editor.ui.workspace;

import com.editor.markdown.MarkdownEngine;
import javafx.scene.control.SplitPane;
import com.editor.service.FileService;

public class Workspace extends SplitPane {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    private final MarkdownEngine markdownEngine;
    private final FileService fileService;
    private final WorkspaceController controller;

    public Workspace(){
        editorPane = new EditorPane();
        previewPane = new PreviewPane();
        markdownEngine = new MarkdownEngine();
        controller = new WorkspaceController(
                editorPane,
                previewPane,
                markdownEngine
        );

        controller.initialize();


        fileService = new FileService();

        fileService.loadNotes().forEach(note ->
                System.out.println(note.getName())
        );

        editorPane.textProperty().addListener((observable, oldText, newText) -> {

            String html = markdownEngine.parseToHtml(newText);

            previewPane.setHtml(html);

        });

        getItems().addAll(editorPane,previewPane);
        setDividerPositions(0.6);
    }
}
