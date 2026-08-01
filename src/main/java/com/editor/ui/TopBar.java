package com.editor.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TopBar extends HBox {
    public TopBar() {
        getStyleClass().add("top-bar");

        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(10,20,10,20));
        setSpacing(15);

        //App Name
        Label title = new Label("CurioNotes");
        title.getStyleClass().add("app-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().add("top-button");

        Button gitButton = new Button("Git");
        gitButton.getStyleClass().add("top-button");

        Button themeButton = new Button("Theme");
        themeButton.getStyleClass().add("top-button");

        Button settingsButton = new Button("Settings");
        settingsButton.getStyleClass().add("top-button");

        getChildren().addAll(
                title,
                spacer,
                searchButton,
                gitButton,
                themeButton,
                settingsButton
        );



    }
}
