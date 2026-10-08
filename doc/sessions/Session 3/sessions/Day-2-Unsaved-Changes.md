# CurioNotes — Day 2 Development Session

## Goal

Protect CurioNotes users from losing unsaved changes.

Day 1 already provided note CRUD: load, open, edit, save, create, delete, rename, and Ctrl+S.

Day 2 focused on dirty-state management and safe exit paths.

## 1. Dirty State

Added:

```java
private boolean dirty;
```

The editor text listener now marks the session dirty:

```java
editorPane.textProperty().addListener((observable, oldText, newText) -> {
    String html = markdownEngine.parseToHtml(newText);
    previewPane.setHtml(html);
    dirty = true;
});
```

## 2. Opening and Saving Reset the State

Opening:

```java
String content = fileService.readNote(note);
editorPane.setText(content);
dirty = false;
```

Saving:

```java
String content = editorPane.getText();
fileService.saveNote(currentNote, content);
dirty = false;
```

Opening requires the reset because `setText()` itself triggers the listener.

## 3. `isDirty()` and `isCurrentNote()`

```java
public boolean isDirty() {
    return dirty;
}
```

and:

```java
public boolean isCurrentNote(Note note) {
    return currentNote != null
            && currentNote.getPath().equals(note.getPath());
}
```

These keep the internal state private while allowing the UI workflow to make decisions.

## 4. Note Switching

Selecting another note now checks dirty state.

If dirty, the user gets:

```text
Save | Don't Save | Cancel
```

Save persists before opening the new note. Don't Save discards the in-memory changes. Cancel keeps the current note open.

## 5. Delete Protection

Delete uses:

```java
controller.isDirty()
        && controller.isCurrentNote(note)
```

This prevents a dirty note A from blocking deletion of unrelated note B.

## 6. Rename Protection

Rename uses the same Save / Don't Save / Cancel pattern.

After renaming, the sidebar reloads and the renamed note is reopened so the controller's current note points at the new path.

## 7. Application Close Protection

`MainLayout` now keeps a `Workspace` field and exposes:

```java
public boolean canClose() {
    return workspace.canClose();
}
```

`Workspace.canClose()` returns `true` when closing is safe and `false` when the user cancels.

`MainApp` intercepts the close request:

```java
stage.setOnCloseRequest(event -> {
    if (!mainLayout.canClose()) {
        event.consume();
    }
});
```

`event.consume()` is what prevents the default close operation after Cancel.

## 8. Debugging Lessons

### Listener during `setText()`
Programmatic changes trigger JavaFX listeners, so opening a note temporarily makes `dirty` true before `openNote()` resets it.

### State context
Dirty state belongs to the current note, which is why delete and rename use `isCurrentNote()` as well as `isDirty()`.

### Dialog implementation
Small JavaFX API/variable naming errors were fixed by following compiler errors one at a time.

## 9. Testing

Confirmed working:

- dirty state after editing
- reset after save
- reset after opening
- Save / Don't Save / Cancel while switching
- delete protection
- rename protection
- application close protection
- Cancel keeps the application open

## 10. Final Day 2 Status

```text
Dirty state                    ✅
Detect editor changes          ✅
Reset after save               ✅
Reset after opening            ✅
Switching notes protection     ✅
Delete protection              ✅
Rename protection              ✅
Application close protection   ✅
Save / Don't Save / Cancel     ✅
Visible dirty indicator        ⬜
```

The visible dirty indicator is intentionally still a future task.

Temporary debug output such as:

```java
System.out.println("Dirty state: " + dirty);
```

should be removed during cleanup.

## 11. Git

Code commit:

```bash
git add src/
git commit -m "feat: implement unsaved changes protection"
git push origin main
```

Documentation commit:

```bash
git add doc/
git commit -m "docs: document unsaved changes protection"
git push origin main
```
