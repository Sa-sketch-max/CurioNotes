# Decision: Why WorkspaceController Exists

`Workspace` should not own every piece of application logic.

`WorkspaceController` owns:

```java
private Note currentNote;
```

and coordinates:

```text
openNote()
saveCurrentNote()
createNote()
deleteNote()
renameNote()
```

The result is:

```text
Workspace → Controller → FileService
```

This keeps UI presentation separate from application behavior and makes future features such as autosave and dirty-state tracking easier.
