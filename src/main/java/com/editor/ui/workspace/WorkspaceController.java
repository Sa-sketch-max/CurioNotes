package com.editor.ui.workspace;

import com.editor.markdown.MarkdownEngine;

public class WorkspaceController {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    private final MarkdownEngine markdownEngine;

    public WorkspaceController(EditorPane editorPane,
                               PreviewPane previewPane,
                               MarkdownEngine markdownEngine) {

        this.editorPane = editorPane;
        this.previewPane = previewPane;
        this.markdownEngine = markdownEngine;
    }

    public void initialize() {

        editorPane.textProperty().addListener((observable, oldText, newText) -> {

            String html = markdownEngine.parseToHtml(newText);

            previewPane.setHtml(html);

        });

    }

}