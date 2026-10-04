# Decision: Why FileService Exists

Filesystem operations were isolated in `FileService` so UI classes do not directly manage files.

```java
loadNotes()
readNote()
saveNote()
createNote()
deleteNote()
renameNote()
```

This gives storage one clear responsibility, simplifies UI code, improves testability, and leaves room for a future repository/database layer.
