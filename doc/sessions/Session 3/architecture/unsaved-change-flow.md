# Unsaved Change Flow

## Note Switching

When a sidebar note is selected, `Workspace` checks:

```java
if (controller.isDirty()) {
    // Save / Don't Save / Cancel
} else {
    controller.openNote(note);
}
```

### Save
```java
controller.saveCurrentNote();
controller.openNote(note);
```

### Don't Save
```java
controller.openNote(note);
```

### Cancel
Nothing is opened, so the current editing session remains intact.

## Delete Protection

Deleting the current dirty note checks:

```java
controller.isDirty()
        && controller.isCurrentNote(note)
```

This avoids showing a save dialog for note A when the user is deleting unrelated note B.

## Rename Protection

Renaming the current dirty note also uses Save / Don't Save / Cancel. After a successful rename, the sidebar is refreshed and the renamed note is opened.

## Application Close

```text
Window X
  ↓
MainApp.setOnCloseRequest()
  ↓
MainLayout.canClose()
  ↓
Workspace.canClose()
  ↓
controller.isDirty()
  ↓
Save / Don't Save / Cancel
```

If Cancel is selected, `event.consume()` prevents the window from closing.
