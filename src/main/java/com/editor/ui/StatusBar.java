package com.editor.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StatusBar extends HBox {

    public StatusBar() {

        getStyleClass().add("status-bar");

        setPadding(new Insets(8));

        Label status = new Label("Ready");

        getChildren().add(status);
    }
}