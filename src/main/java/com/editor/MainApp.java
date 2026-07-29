package com.editor;

import com.editor.markdown.MarkdownEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MainApp extends Application {

    private File currentFile;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CurioNotes - Markdown Editor");

        TextArea textArea = new TextArea();
        textArea.setPromptText("Start writing your markdown here...");

        MarkdownEngine mdEngine = new MarkdownEngine();
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            String parsedHtml = mdEngine.parsetoHtml(newValue);
            System.out.println("Live output");
            System.out.println(parsedHtml);
        });

        // Create Menu Bar
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Markdown Files", "*.md")
        );

        // Open File Logic
        openItem.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                currentFile = file;
                try {
                    String content = Files.readString(file.toPath());
                    textArea.setText(content);
                    primaryStage.setTitle("CurioNotes - " + file.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Save File Logic
        saveItem.setOnAction(e -> {
            if (currentFile == null) {
                currentFile = fileChooser.showSaveDialog(primaryStage);
            }
            if (currentFile != null) {
                try {
                    Files.writeString(currentFile.toPath(), textArea.getText());
                    primaryStage.setTitle("CurioNotes - " + currentFile.getName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        fileMenu.getItems().addAll(openItem, saveItem);
        menuBar.getMenus().add(fileMenu);

        VBox sidebar = new VBox();
        sidebar.setSpacing(200);

        sidebar.setStyle("-fx-background-color: #2f3437;");

        Label  sidebarTitle = new Label("CurioNotes");
        sidebarTitle.setStyle("-fx-text-fill: #ebeced; -fx-font-weight: bold; -fx-padding: 10px;");

        ListView<String> fileList = new ListView<>();
        fileList.getItems().addAll("Welcome.md", "Ideas.md", "JavaFx_Tips.md");

        fileList.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-text-fill: #ebeced;");

        sidebar.getChildren().addAll(sidebarTitle, fileList);


        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(textArea);

        Scene scene = new Scene(root, 900, 600);
        String css = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}