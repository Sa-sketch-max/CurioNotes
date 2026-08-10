package com.editor.ui.workspace;

import com.editor.markdown.MarkdownEngine;

import com.editor.ui.Sidebar;
import javafx.scene.control.SplitPane;
import com.editor.service.FileService;


public class Workspace extends SplitPane {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    private final MarkdownEngine markdownEngine;
    private final FileService fileService;
    private final WorkspaceController controller;
    private final Sidebar sidebar;

    public Workspace(Sidebar sidebar) {
        this.sidebar = sidebar;
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



        sidebar.setNotes(fileService.loadNotes());

        getItems().addAll(editorPane, previewPane);
        setDividerPositions(0.6);



        getItems().addAll(editorPane,previewPane);
        setDividerPositions(0.6);
    }
}
