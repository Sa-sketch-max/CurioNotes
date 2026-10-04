# Decision: Why Sidebar Uses Callbacks

Sidebar needs to notify the workspace about note events without knowing what the workspace does.

It therefore exposes callbacks:

```java
Consumer<Note> noteSelectedListener;
Consumer<Note> deleteNoteListener;
Consumer<Note> renameNoteListener;
Runnable newNoteListener;
```

`Consumer<Note>` is used when a `Note` is required. `Runnable` is used when no argument is required.

Example:

```java
sidebar.setNoteSelectedListener(note -> {
    controller.openNote(note);
});
```

This reduces coupling between UI components.
