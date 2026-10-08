# Dirty State Architecture

## Purpose
CurioNotes must prevent accidental data loss when a user edits a note and leaves without saving.

## State
`WorkspaceController` owns:

```java
private boolean dirty;
```

`true` means the current editor content has unsaved changes. `false` means the current editing session has no known unsaved changes.

## Why the Controller Owns It
The editor handles text editing, `FileService` handles filesystem operations, and `WorkspaceController` coordinates the editing workflow. Dirty state describes the relationship between the editor and persisted note, so it belongs in the controller.

## State Transitions

```text
Open note
  ↓
read file
  ↓
set editor text
  ↓
text listener fires
  ↓
dirty temporarily becomes true
  ↓
openNote() resets dirty = false
```

Typing:

```text
Editor change → text listener → dirty = true
```

Saving:

```text
saveCurrentNote() → FileService.saveNote() → dirty = false
```

## Encapsulation

The field remains private and callers use:

```java
public boolean isDirty() {
    return dirty;
}
```

The current-note check is:

```java
public boolean isCurrentNote(Note note) {
    return currentNote != null
            && currentNote.getPath().equals(note.getPath());
}
```

Delete and rename protection use both checks because dirty state belongs to the current editing session, not every note in the sidebar.

## Current Limitation
A visible dirty indicator such as `*` beside the note name is not implemented yet. Temporary console logging was used during verification.
