package com.editor;

import com.editor.ui.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        MainLayout mainLayout = new MainLayout();

        Scene scene =
                new Scene(mainLayout.getRoot(), 1200, 800);

        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {

            if (!mainLayout.canClose()) {
                event.consume();
            }
        });

        stage.show();
    }