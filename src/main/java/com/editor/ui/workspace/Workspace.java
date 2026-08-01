package com.editor.ui.workspace;

import javafx.scene.control.SplitPane;

public class Workspace extends SplitPane {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    public Workspace(){
        editorPane = new EditorPane();
        previewPane = new PreviewPane();

        getItems().addAll(editorPane,previewPane);
        setDividerPositions(0.6);
    }
}
