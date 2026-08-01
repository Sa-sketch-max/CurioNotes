package com.editor.ui.workspace;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PreviewPane extends StackPane {
    public PreviewPane(){
        Label preview = new Label("Markdown Preview");

        getChildren().add(preview);
    }
}
