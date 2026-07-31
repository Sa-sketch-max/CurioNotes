package com.editor.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    public Sidebar() {

        // Apply CSS class
        getStyleClass().add("sidebar");

        // Padding around the sidebar
        setPadding(new Insets(15));

        // Space between components
        setSpacing(8);

        // Application title
        Label title = new Label("CurioNotes");
        title.getStyleClass().add("sidebar-title");

        // Navigation buttons
        Button dashboard = createButton("🏠 Dashboard");
        Button notes = createButton("📝 Notes");
        Button dsa = createButton("🧠 DSA");
        Button study = createButton("📚 Study");
        Button docs = createButton("📖 Documentation");
        Button journal = createButton("📅 Journal");
        Button habits = createButton("✅ Habits");
        Button expenses = createButton("💰 Expenses");

        // Spacer pushes Settings to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button settings = createButton("⚙ Settings");

        getChildren().addAll(
                title,
                dashboard,
                notes,
                dsa,
                study,
                docs,
                journal,
                habits,
                expenses,
                spacer,
                settings
        );
    }

    private Button createButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);

        button.getStyleClass().add("sidebar-button");

        return button;
    }

}