package com.editor;

import com.editor.ui.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        MainLayout mainLayout = new MainLayout();

        Scene scene = new Scene(mainLayout.getRoot(), 1400, 800);

        scene.getStylesheets().add(
                getClass().getResource("/css/dark-theme.css").toExternalForm()
        );

        stage.setTitle("CurioNotes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}