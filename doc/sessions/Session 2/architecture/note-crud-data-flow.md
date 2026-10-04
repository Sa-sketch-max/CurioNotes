# Note CRUD Data Flow

## Create

```text
New Note
→ TextInputDialog
→ Workspace
→ WorkspaceController.createNote()
→ FileService.createNote()
→ notes/Name.md
→ reload Sidebar
→ open note
```

## Open

```text
ListView selection
→ Sidebar callback
→ Workspace
→ WorkspaceController.openNote()
→ FileService.readNote()
→ EditorPane
→ Markdown listener
→ PreviewPane
```

## Save

```text
Ctrl+S
→ Workspace key filter
→ saveCurrentNote()
→ EditorPane.getText()
→ FileService.saveNote()
```

## Delete

```text
Delete
→ confirmation
→ controller.deleteNote()
→ FileService.deleteNote()
→ clear currentNote/editor if active
→ reload Sidebar
```

## Rename

```text
Rename
→ dialog
→ controller.renameNote()
→ FileService.renameNote()
→ update currentNote if active
→ reload Sidebar
→ open renamed note
```
