package com.editor.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import com.editor.model.Note;
import javafx.scene.control.ListView;
import com.editor.model.Note;
import java.util.List;
import java.util.function.Consumer;

public class Sidebar extends VBox {
    private final ListView<Note> notesList;
    private Consumer<Note> noteSelectedListener;
    public Sidebar() {

        notesList = new ListView<>();

        notesList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldNote, newNote) -> {

                    System.out.println("Selection changed");

                    if (newNote != null) {
                        System.out.println("Selected: " + newNote.getName());
                    }

                    if (newNote != null && noteSelectedListener != null) {

                        System.out.println("Calling listener...");

                        noteSelectedListener.accept(newNote);

                    }

                });

        notesList.setCellFactory(list -> new javafx.scene.control.ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                }else{
                    setText("\uD83D\uDCC4" + note.getName());
                }
            }
        });
        VBox.setVgrow(notesList, Priority.ALWAYS);
        // Apply CSS class
        getStyleClass().add("sidebar");

        // Padding around the sidebar
        setPadding(new Insets(15));

        // Space between components
        setSpacing(8);

        // Application title
        Label title = new Label("CurioNotes");

        title.getStyleClass().add("sidebar-title");



        // Spacer pushes Settings to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button settings = createButton("⚙ Settings");

        getChildren().addAll(
                title,
                notesList,
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

    public void setNotes(List<Note> notes) {

        System.out.println("Notes received: " + notes.size());

        notesList.getItems().setAll(notes);

    }

    public void setNoteSelectedListener(Consumer<Note> listener) {

        this.noteSelectedListener = listener;

    }

}