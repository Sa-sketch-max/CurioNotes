# Interview: Controller vs Service

`WorkspaceController` coordinates the editing workflow:

- current note
- dirty state
- open
- save
- delete
- rename

`FileService` handles filesystem operations:

```java
readNote(...)
saveNote(...)
createNote(...)
deleteNote(...)
renameNote(...)
```

The controller decides *when* an operation should happen; the service performs the filesystem work.

This separation makes CurioNotes easier to maintain and prepares it for future SQLite repositories and JGit version history.
