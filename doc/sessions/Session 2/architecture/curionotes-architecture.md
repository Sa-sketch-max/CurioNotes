# CurioNotes Architecture

CurioNotes is an offline-first JavaFX desktop application.

Current Note CRUD flow:

```text
JavaFX UI
   ↓
Workspace / Sidebar
   ↓
WorkspaceController
   ↓
FileService
   ↓
Java NIO / notes/*.md
```

## Responsibilities

- **UI:** presentation and user interaction.
- **WorkspaceController:** coordinates workspace behavior and owns `currentNote`.
- **FileService:** handles filesystem operations.
- **Note:** represents a note and its path.
- **MarkdownEngine:** converts Markdown to HTML.

The separation prevents `Workspace` from becoming a God class and makes future SQLite, JGit, search, autosave, and other modules easier to add.
