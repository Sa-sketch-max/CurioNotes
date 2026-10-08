# Decision: Dirty State Belongs in WorkspaceController

## Decision
Store unsaved-change state in `WorkspaceController`:

```java
private boolean dirty;
```

## Reason
The controller already coordinates the editor, current note, Markdown preview, and file operations.

The alternatives are weaker:

- `EditorPane`: presentation/editing component should not own persistence state.
- `FileService`: knows disk state, not uncommitted editor state.
- `MainApp`: owns application lifecycle, not note editing.

Keeping dirty state in the controller gives one source of truth for the current editing session and leaves room for future auto-save and Git version history.
