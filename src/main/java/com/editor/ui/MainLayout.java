package com.editor.ui;

import javafx.scene.Parent;

import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.SplitPane;
import javafx.scene.web.WebView;

public class MainLayout {

    private final BorderPane root;

    public MainLayout() {

        root = new BorderPane();

        // Top
        root.setTop(MenuBarBuilder.build());

        // Left
        root.setLeft(new Sidebar());

        // Center
        SplitPane splitPane = new SplitPane();

        TextArea editor = new TextArea();
        editor.setPromptText("Start writing...");

        WebView preview = new WebView();

        splitPane.getItems().addAll(editor, preview);

        splitPane.setDividerPositions(0.6);

        root.setCenter(splitPane);

        // Bottom
        root.setBottom(new StatusBar());
    }

    public Parent getRoot() {
        return root;
    }

}