package com.editor.ui.workspace;

import com.editor.markdown.MarkdownEngine;
import com.editor.model.Note;

import com.editor.ui.Sidebar;
import javafx.scene.control.SplitPane;
import com.editor.service.FileService;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;


public class Workspace extends SplitPane {

    private final EditorPane editorPane;
    private final PreviewPane previewPane;
    private final MarkdownEngine markdownEngine;
    private final FileService fileService;
    private final WorkspaceController controller;
    private final Sidebar sidebar;

    public Workspace(Sidebar sidebar) {
        this.sidebar = sidebar;
        editorPane = new EditorPane();
        previewPane = new PreviewPane();
        markdownEngine = new MarkdownEngine();
        fileService = new FileService();
        controller = new WorkspaceController(
                editorPane,
                previewPane,
                markdownEngine,
                fileService
        );

        addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            KeyCodeCombination saveShortcut =
                    new KeyCodeCombination(
                            KeyCode.S,
                            KeyCombination.CONTROL_DOWN
                    );

            if (saveShortcut.match(event)) {

                controller.saveCurrentNote();

                event.consume();
            }
        });

        controller.initialize();

        sidebar.setNoteSelectedListener(note -> {

            System.out.println("Lambda reached");

            controller.openNote(note);

        });




        sidebar.setDeleteNoteListener(note -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setTitle("Delete Note");
            alert.setHeaderText("Delete this note?");
            alert.setContentText(note.getName());

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                controller.deleteNote(note);

                sidebar.setNotes(fileService.loadNotes());
            }
        });

        sidebar.setNewNoteListener(() -> {

            TextInputDialog dialog = new TextInputDialog();

            dialog.setTitle("New Note");
            dialog.setHeaderText("Create a new Markdown note");
            dialog.setContentText("Note name:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(name -> {

                Note note = controller.createNote(name);

                sidebar.setNotes(fileService.loadNotes());

                controller.openNote(note);

            });

        });

        sidebar.setNotes(fileService.loadNotes());

        getItems().addAll(editorPane, previewPane);
        setDividerPositions(0.6);


        sidebar.setRenameNoteListener(note -> {

            String currentName = note.getName();

            if (currentName.endsWith(".md")) {
                currentName = currentName.substring(
                        0,
                        currentName.length() - 3
                );
            }

            TextInputDialog dialog = new TextInputDialog(currentName);

            dialog.setTitle("Rename Note");
            dialog.setHeaderText("Rename this note");
            dialog.setContentText("New name:");

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(newName -> {

                Note renamedNote =
                        controller.renameNote(note, newName);

                sidebar.setNotes(fileService.loadNotes());

                controller.openNote(renamedNote);

            });
        });


    }
}
