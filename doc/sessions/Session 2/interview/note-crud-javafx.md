# Interview Questions: Note CRUD and JavaFX

## Why `ListView<Note>`?

Because operations need the complete `Note` object, not only its display name.

## Why a custom `ListCell`?

To control how each note is displayed without changing the model.

## What is a JavaFX property listener?

It reacts when a JavaFX observable property changes, such as selected item or text.

## Why `Consumer<Note>`?

It represents an operation accepting one `Note` and returning nothing.

## Why `Runnable`?

It represents an action with no argument and no return value.

## Why `Optional` for dialogs?

`showAndWait()` may return without a value when the user cancels.

## Why `currentNote`?

The editor contains text, but the application must know which file that text belongs to.

## Why constructor injection?

Dependencies are explicitly supplied to the controller, making dependencies visible and testing easier.

## What happens on Ctrl+S?

```text
KeyEvent
→ Workspace
→ WorkspaceController.saveCurrentNote()
→ EditorPane.getText()
→ FileService.saveNote()
```
