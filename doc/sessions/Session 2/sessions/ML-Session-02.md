# ML Session 02 — Note CRUD

## Goal

Build the first complete Markdown Note CRUD workflow.

## Completed

```text
Load notes          ✓
Open note           ✓
Edit note           ✓
Save note           ✓
Ctrl+S              ✓
New Note            ✓
Create .md          ✓
Delete Note         ✓
Delete confirmation ✓
Active-note cleanup ✓
Rename Note         ✓
Rename dialog       ✓
Rename file         ✓
Active-note update  ✓
```

## Architecture

```text
MainLayout
├── TopBar
├── Sidebar
├── Workspace
│   ├── EditorPane
│   └── PreviewPane
└── StatusBar

Workspace
    ↓
WorkspaceController
    ↓
FileService
    ↓
notes/*.md
```

## Main Code Concepts

### `Note`

Represents a note and its filesystem path.

### `FileService`

Handles:

```java
loadNotes()
readNote()
saveNote()
createNote()
deleteNote()
renameNote()
```

### `WorkspaceController`

Owns the active note:

```java
private Note currentNote;
```

and coordinates opening, saving, creating, deleting, and renaming.

### `Sidebar`

Uses `ListView<Note>` and callbacks:

```java
Consumer<Note>
Runnable
```

so it does not need to know workspace implementation details.

## Create Flow

```text
New Note
→ TextInputDialog
→ controller.createNote()
→ fileService.createNote()
→ notes/Name.md
→ reload Sidebar
→ open created note
```

## Delete Flow

A confirmation dialog is shown before deletion.

If the deleted note is active:

```java
currentNote = null;
editorPane.clear();
```

This prevents the editor from representing a file that no longer exists.

## Rename Flow

`FileService` moves the file and returns a new `Note`.

If the renamed note is active:

```java
currentNote = renamedNote;
```

The Sidebar is refreshed and the renamed note is opened.

## Errors Encountered

### Missing field

`deleteNoteButton` was used before being declared.

### Final initialization

After declaration, it had to be initialized before use.

These errors reinforced:

```text
Declare → Initialize → Configure → Use
```

## Debugging

The event chain was traced using temporary logs:

```text
Sidebar
→ callback
→ Workspace
→ Controller
→ Service
→ Editor
```

This is the preferred approach for event-driven debugging.

## Validation Status

Validation for blank names, whitespace, path separators, duplicate names, extension handling, and friendly error dialogs is the next refinement. The validation code was drafted but should only be considered complete after testing.

## Concepts Learned

- Java NIO `Path` and `Files`
- JavaFX `ListView`
- custom `ListCell`
- property listeners
- callbacks
- `Consumer<T>`
- `Runnable`
- `Optional`
- constructor injection
- controller/service separation
- active state with `currentNote`
- event-flow debugging

## Git Checkpoint

```bash
git add .
git commit -m "feat: implement note CRUD operations"
```

Optional:

```bash
git push
```

## Session Lesson

The biggest lesson was architectural:

```text
UI → Controller → Service → Filesystem
```

CRUD was the feature, but separation of responsibilities was the foundation for the rest of CurioNotes.
