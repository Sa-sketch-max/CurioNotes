package com.editor.ui;

import com.editor.model.Note;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class Sidebar extends VBox {

    private final ListView<Note> notesList;
    private final Button newNoteButton;
    private final Button deleteNoteButton;
    private final Button renameNoteButton;

    private Consumer<Note> noteSelectedListener;
    private Runnable newNoteListener;
    private Consumer<Note> deleteNoteListener;
    private Consumer<Note> renameNoteListener;


    public Sidebar() {

        notesList = new ListView<>();

        // New Note button
        newNoteButton = createButton("＋ New Note");

        newNoteButton.setOnAction(event -> {

            if (newNoteListener != null) {
                newNoteListener.run();
            }

        });

        // Delete Note button
        deleteNoteButton = createButton("🗑 Delete Note");

        deleteNoteButton.setOnAction(event -> {

            Note selectedNote =
                    notesList.getSelectionModel().getSelectedItem();

            if (selectedNote != null && deleteNoteListener != null) {

                deleteNoteListener.accept(selectedNote);

            }

        });

        renameNoteButton = createButton("✏ Rename Note");

        renameNoteButton.setOnAction(event -> {

            Note selectedNote =
                    notesList.getSelectionModel().getSelectedItem();

            if (selectedNote != null && renameNoteListener != null) {
                renameNoteListener.accept(selectedNote);
            }

        });

        // Note selection listener
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

        // Custom note cells
        notesList.setCellFactory(list -> new ListCell<Note>() {

            @Override
            protected void updateItem(Note note, boolean empty) {

                super.updateItem(note, empty);

                if (empty || note == null) {
                    setText(null);
                } else {
                    setText("📄 " + note.getName());
                }
            }
        });

        VBox.setVgrow(notesList, Priority.ALWAYS);

        // Sidebar styling
        getStyleClass().add("sidebar");

        setPadding(new Insets(15));

        setSpacing(8);

        // Title
        Label title = new Label("CurioNotes");

        title.getStyleClass().add("sidebar-title");

        // Spacer
        Region spacer = new Region();

        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Settings
        Button settings = createButton("⚙ Settings");

        // Add components
        getChildren().addAll(
                title,
                newNoteButton,
                deleteNoteButton,
                renameNoteButton,
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

    public void setNewNoteListener(Runnable listener) {

        this.newNoteListener = listener;
    }

    public void setDeleteNoteListener(Consumer<Note> listener) {

        this.deleteNoteListener = listener;
    }


    public void setRenameNoteListener(Consumer<Note> listener) {
        this.renameNoteListener = listener;
    }
}