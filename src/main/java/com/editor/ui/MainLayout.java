package com.editor.ui;

import com.editor.ui.workspace.Workspace;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;


public class MainLayout {

    private final BorderPane root;
    private final Workspace workspace;

    public MainLayout() {

        root = new BorderPane();

        // Top
        root.setTop(new TopBar());

        Sidebar sidebar = new Sidebar();

        workspace = new Workspace(sidebar);
        root.setLeft(sidebar);
        root.setCenter(workspace);

        // Bottom
        root.setBottom(new StatusBar());
    }

    public Parent getRoot() {
        return root;
    }

    public boolean canClose() {
        return workspace.canClose();
    }

}