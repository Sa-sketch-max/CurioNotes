package com.editor.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuBarBuilder {

    public static MenuBar createMenuBar() {

        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        MenuItem open = new MenuItem("Open...");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As...");
        MenuItem exit = new MenuItem("Exit");

        fileMenu.getItems().addAll(
                newFile,
                open,
                save,
                saveAs,
                exit
        );

        // Edit Menu
        Menu editMenu = new Menu("Edit");

        editMenu.getItems().addAll(
                new MenuItem("Undo"),
                new MenuItem("Redo"),
                new MenuItem("Cut"),
                new MenuItem("Copy"),
                new MenuItem("Paste")
        );

        // View Menu
        Menu viewMenu = new Menu("View");

        viewMenu.getItems().addAll(
                new MenuItem("Toggle Preview"),
                new MenuItem("Dark Mode")
        );

        // Help Menu
        Menu helpMenu = new Menu("Help");

        helpMenu.getItems().add(
                new MenuItem("About")
        );

        menuBar.getMenus().addAll(
                fileMenu,
                editMenu,
                viewMenu,
                helpMenu
        );

        return menuBar;
    }

}