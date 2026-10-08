# CurioNotes — Session 2
# Unsaved Changes, Dirty State & Application Safety

> **Project:** CurioNotes  
> **Session:** 2  
> **Primary theme:** Protecting user work from accidental data loss  
> **Technology:** Java 21, JavaFX, Maven  
> **Architecture:** UI + Controller + Service + Model  
> **Status:** Core Session 2 implementation complete

---

# 1. Session Overview

Session 2 focused on one of the most important responsibilities of a note-taking application:

> **Never allow the user to accidentally lose work.**

At the end of Session 1, CurioNotes already had working note CRUD operations:

- Load notes
- Open notes
- Edit notes
- Save notes
- Ctrl+S
- Create notes
- Delete notes
- Rename notes
- Refresh the sidebar

However, there was a serious usability problem.

A user could:

1. Open a note.
2. Modify it.
3. Forget to save.
4. Switch to another note.
5. Delete the current note.
6. Rename the current note.
7. Close the application.

Without protection, those actions could discard changes silently.

Session 2 therefore introduced an application-wide **dirty-state system** and used it to protect every operation that could cause the user's unsaved work to disappear.

---

# 2. Executive Summary

The major feature introduced in this session is:

```text
Dirty State
```

The application now knows whether the currently open note has unsaved changes.

The final protection system covers:

```text
Editing
   ↓
dirty = true
   ↓
User attempts a risky operation
   ↓
Save / Don't Save / Cancel
```

The protected operations are:

```text
Switch note
Delete current note
Rename current note
Close application
```

The architecture evolved into a clear chain:

```text
JavaFX UI
   ↓
Workspace / MainLayout
   ↓
WorkspaceController
   ↓
FileService
   ↓
Filesystem
```

The controller owns editing state, while the service owns file operations.

Application closing follows a separate lifecycle path:

```text
Stage
  ↓
MainApp
  ↓
MainLayout.canClose()
  ↓
Workspace.canClose()
  ↓
WorkspaceController.isDirty()
```

This separation keeps the code understandable and prepares CurioNotes for future features such as:

- Auto-save
- Git version history
- SQLite persistence
- Multiple workspaces
- Tabs
- Recovery
- Local AI
- Search indexing

---

# 3. Goals of Session 2

The original goals were:

## Primary Goal

Prevent accidental loss of unsaved changes.

## Specific Goals

- Detect editor changes.
- Maintain dirty state.
- Reset dirty state after saving.
- Reset dirty state after opening a note.
- Protect note switching.
- Protect deleting the current dirty note.
- Protect renaming the current dirty note.
- Protect application closing.
- Provide Save / Don't Save / Cancel choices.
- Learn JavaFX close-request events.
- Understand `event.consume()`.
- Keep state management out of the UI components.
- Preserve the existing architecture instead of creating unnecessary classes.

---

# 4. Starting Point — End of Session 1

At the beginning of Session 2, CurioNotes already had a functional note workflow.

The main flow was:

```text
Sidebar
   ↓
Workspace
   ↓
WorkspaceController
   ↓
FileService
   ↓
notes/*.md
```

The controller already knew about the current note:

```java
private Note currentNote;
```

It already supported:

```java
openNote(...)
saveCurrentNote(...)
createNote(...)
deleteNote(...)
renameNote(...)
```

But there was no concept of:

```text
"Has the user changed this note since the last save?"
```

That was the missing state.

---

# 5. The Core Problem

Imagine the user opens:

```text
Java.md
```

The file contains:

```markdown
# Java

Learning Java.
```

The user types:

```markdown
# Java

Learning Java.
Collections are important.
```

The editor now contains newer content than the file on disk.

Therefore:

```text
Editor content != Saved file
```

That difference is the definition of an unsaved change.

We need a way to represent that:

```java
private boolean dirty;
```

---

# 6. Understanding Dirty State

The word **dirty** is commonly used in software to mean:

> The in-memory state has changed since the last persistence operation.

For CurioNotes:

```text
dirty = false
```

means:

```text
Editor content is considered synchronized with disk.
```

While:

```text
dirty = true
```

means:

```text
Editor contains changes that have not been saved.
```

This is a small variable, but it enables an important application-safety system.

---

# 7. Where Should Dirty State Live?

This was an architectural decision.

Possible locations included:

```text
EditorPane
FileService
MainApp
Workspace
WorkspaceController
```

The best location is:

```text
WorkspaceController
```

because the controller already coordinates:

```text
Editor
Current Note
FileService
Preview
Editing workflow
```

The dirty state describes the relationship between:

```text
Current editor content
        and
Persisted note
```

That is controller-level application state.

---

# 8. Adding the Dirty Field

Inside `WorkspaceController`:

```java
private boolean dirty;
```

The field is intentionally private.

We do not want other classes doing this:

```java
controller.dirty = true;
```

Instead, the controller controls its own state.

---

# 9. Detecting Editor Changes

The existing editor listener was already being used for live Markdown preview.

It originally did something like:

```java
editorPane.textProperty().addListener(
    (observable, oldText, newText) -> {

        String html =
                markdownEngine.parseToHtml(newText);

        previewPane.setHtml(html);
    }
);
```

Session 2 extended it:

```java
public void initialize() {

    editorPane.textProperty().addListener(
        (observable, oldText, newText) -> {

            String html =
                    markdownEngine.parseToHtml(newText);

            previewPane.setHtml(html);

            dirty = true;
        }
    );
}
```

Now every editor change marks the current editing session as dirty.

---

# 10. Why Use a JavaFX Property Listener?

JavaFX properties are observable.

The editor's text property can notify the application whenever its value changes.

```java
editorPane.textProperty()
```

returns the text property.

We attach a listener:

```java
.addListener(...)
```

The listener receives:

```text
observable
oldText
newText
```

This gives us a reactive workflow.

Instead of repeatedly checking:

```text
"Did the editor change?"
"Did the editor change?"
"Did the editor change?"
```

we simply react when JavaFX tells us:

```text
"The text changed."
```

---

# 11. An Important Discovery: `setText()` Also Fires the Listener

This became one of the most important debugging lessons of Session 2.

Consider:

```java
editorPane.setText(content);
```

It is tempting to think:

```text
Only user typing triggers the listener.
```

But that is not true.

`setText()` changes the same JavaFX property.

Therefore:

```text
editorPane.setText(content)
        ↓
textProperty changes
        ↓
listener executes
        ↓
dirty = true
```

This means opening an unchanged note can temporarily make it look dirty.

---

# 12. The Open Note Flow

The controller's `openNote()` became:

```java
public void openNote(Note note) {

    System.out.println(
            "Opening: " + note.getName()
    );

    currentNote = note;

    String content =
            fileService.readNote(note);

    editorPane.setText(content);

    dirty = false;

    System.out.println(
            "Note opened. Dirty state: " + dirty
    );
}
```

The important sequence is:

```text
Read file
   ↓
Set editor text
   ↓
Listener fires
   ↓
dirty = true
   ↓
openNote() continues
   ↓
dirty = false
```

The final state is correct.

---

# 13. Why Reset Dirty After Opening?

Opening a note means the application has loaded the saved version from disk.

Therefore, after the operation completes:

```java
dirty = false;
```

The user has not made any new edits yet.

This establishes a clean baseline.

---

# 14. Resetting Dirty After Saving

The save method became:

```java
public void saveCurrentNote() {

    if (currentNote == null) {
        System.out.println(
                "No note is currently open."
        );
        return;
    }

    String content =
            editorPane.getText();

    fileService.saveNote(
            currentNote,
            content
    );

    dirty = false;

    System.out.println(
            "Note saved. Dirty state: " + dirty
    );
}
```

The important part is:

```java
fileService.saveNote(currentNote, content);
dirty = false;
```

Only after the save operation succeeds do we consider the editing session clean.

---

# 15. Why Save Resets Dirty State

Before saving:

```text
Editor
   ↓
new content

File
   ↓
old content

dirty = true
```

After saving:

```text
Editor
   ↓
new content

File
   ↓
new content

dirty = false
```

The two states are synchronized.

---

# 16. Encapsulation with `isDirty()`

Instead of exposing the field:

```java
private boolean dirty;
```

we add:

```java
public boolean isDirty() {
    return dirty;
}
```

This gives other classes read-only access to the state.

For example:

```java
if (controller.isDirty()) {
    // protect the operation
}
```

This is much better than allowing external classes to modify the variable directly.

---

# 17. Why Encapsulation Matters

Suppose we made this public:

```java
public boolean dirty;
```

Then any UI class could do:

```java
controller.dirty = false;
```

without saving anything.

That could create data loss bugs.

Keeping the field private means the controller controls all state transitions.

This follows a core object-oriented principle:

> Keep state private and expose controlled behavior.

---

# 18. First Protection — Switching Notes

The first major dangerous operation was note switching.

Previously:

```text
Click Note B
   ↓
Open Note B
```

Now:

```text
Click Note B
   ↓
Is current note dirty?
   ↓
 ┌───────────────┐
 │               │
No              Yes
 │               │
 ↓               ↓
Open B       Ask user
```

---

# 19. Save / Don't Save / Cancel

When unsaved changes exist, CurioNotes displays:

```text
Save
Don't Save
Cancel
```

These choices are semantically different.

### Save

Persist the changes, then continue.

### Don't Save

Discard the current in-memory changes, then continue.

### Cancel

Abort the requested operation.

This pattern became the foundation for all destructive or state-changing operations in Session 2.

---

# 20. Note Switching Implementation

The sidebar selection listener became conceptually:

```java
sidebar.setNoteSelectedListener(note -> {

    if (controller.isDirty()) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle("Unsaved Changes");

        alert.setHeaderText(
                "You have unsaved changes."
        );

        alert.setContentText(
                "Do you want to save your changes before opening "
                        + note.getName() + "?"
        );

        ButtonType saveChangesButton =
                new ButtonType("Save");

        ButtonType discardChangesButton =
                new ButtonType("Don't Save");

        ButtonType cancelChangesButton =
                new ButtonType("Cancel");

        alert.getButtonTypes().setAll(
                saveChangesButton,
                discardChangesButton,
                cancelChangesButton
        );

        Optional<ButtonType> result =
                alert.showAndWait();

        if (result.isPresent()
                && result.get() == saveChangesButton) {

            controller.saveCurrentNote();
            controller.openNote(note);

        } else if (result.isPresent()
                && result.get() == discardChangesButton) {

            controller.openNote(note);

        } else {

            System.out.println(
                    "Opening cancelled."
            );
        }

    } else {

        controller.openNote(note);
    }
});
```

---

# 21. Understanding `Alert`

JavaFX provides:

```java
Alert
```

for common dialogs.

We use:

```java
new Alert(Alert.AlertType.CONFIRMATION);
```

because the user is making a decision.

The default buttons are replaced with our custom buttons:

```java
alert.getButtonTypes().setAll(
        saveChangesButton,
        discardChangesButton,
        cancelChangesButton
);
```

---

# 22. Understanding `ButtonType`

A `ButtonType` represents an action available in the dialog.

For example:

```java
ButtonType saveChangesButton =
        new ButtonType("Save");
```

The button's label is:

```text
Save
```

The object itself is used to identify what the user selected.

---

# 23. Understanding `Optional<ButtonType>`

The result of:

```java
alert.showAndWait();
```

is:

```java
Optional<ButtonType>
```

This is important because there may be no selected result.

The safe pattern is:

```java
if (result.isPresent()) {
    ButtonType selected = result.get();
}
```

The code compares the actual object:

```java
result.get() == saveChangesButton
```

and decides what to do.

---

# 24. Testing Note Switching

Three paths were tested.

## Save

```text
Edit
 ↓
dirty = true
 ↓
Select another note
 ↓
Save
 ↓
saveCurrentNote()
 ↓
dirty = false
 ↓
open target note
```

This worked.

## Don't Save

```text
Edit
 ↓
dirty = true
 ↓
Select another note
 ↓
Don't Save
 ↓
open target note
```

This worked.

## Cancel

```text
Edit
 ↓
dirty = true
 ↓
Select another note
 ↓
Cancel
 ↓
Opening cancelled
```

The current note remained open.

---

# 25. Second Protection — Delete

Delete is dangerous because the current note can contain unsaved content.

However, there is an important subtlety.

Suppose:

```text
Current note = A
A is dirty
User selects B
User deletes B
```

The unsaved content belongs to A.

Therefore, simply checking:

```java
controller.isDirty()
```

would be wrong.

---

# 26. Introducing `isCurrentNote()`

The controller received:

```java
public boolean isCurrentNote(Note note) {

    return currentNote != null
            && currentNote.getPath()
                    .equals(note.getPath());
}
```

This compares the paths of the notes.

The application can now ask:

```text
Is there unsaved content?
AND
Is this the note containing that unsaved content?
```

---

# 27. Correct Delete Condition

The condition becomes:

```java
if (controller.isDirty()
        && controller.isCurrentNote(note)) {
```

This is an important architectural detail.

The application does not treat dirty state as global.

It treats dirty state as state belonging to the active editing session.

---

# 28. Delete Flow

The complete flow is:

```text
Delete clicked
      ↓
Is current note dirty?
      ↓
No ─────────────→ Normal delete confirmation
      │
     Yes
      ↓
Save / Don't Save / Cancel
      ↓
 ┌────┼────────────┐
Save  Don't Save  Cancel
 ↓       ↓           ↓
save   delete      stop
 ↓       ↓
delete
```

The sidebar is refreshed after successful deletion.

---

# 29. Third Protection — Rename

Rename can also cause problems when the current note is dirty.

For example:

```text
Current:
Java.md

Editor:
unsaved Java changes
```

Then the user renames:

```text
Java.md
```

to:

```text
Java-Collections.md
```

The application must not silently discard the unsaved editor content.

---

# 30. Rename Flow

The rename implementation first asks for the new name.

Then:

```java
if (controller.isDirty()
        && controller.isCurrentNote(note)) {
```

If true:

```text
Save
Don't Save
Cancel
```

If Save:

```java
controller.saveCurrentNote();

Note renamedNote =
        controller.renameNote(note, newName);

sidebar.setNotes(
        fileService.loadNotes()
);

controller.openNote(renamedNote);
```

The renamed note becomes the active note.

---

# 31. Why Reload the Sidebar?

The filename changed.

Before:

```text
Java.md
```

After:

```text
Java-Collections.md
```

The sidebar must therefore reload the notes:

```java
sidebar.setNotes(
        fileService.loadNotes()
);
```

This keeps the UI synchronized with the filesystem.

---

# 32. Updating the Current Note After Rename

The controller's `renameNote()` already returns a new `Note`:

```java
Note renamedNote =
        fileService.renameNote(
                note,
                newName
        );
```

If the renamed note was the current note:

```java
if (currentNote != null &&
        currentNote.getPath().equals(note.getPath())) {

    currentNote = renamedNote;
}
```

This is important because the old path no longer exists.

---

# 33. Fourth Protection — Application Close

The final and most important protection is closing the entire application.

A user might:

```text
Edit
 ↓
Forget Save
 ↓
Click X
```

Without protection:

```text
Application closes
 ↓
Changes disappear
```

Session 2 prevents that.

---

# 34. Why MainApp Handles the Close Event

The JavaFX `Stage` represents the application window.

`MainApp` creates it:

```java
Stage stage
```

Therefore, `MainApp` is the correct place to intercept:

```java
setOnCloseRequest(...)
```

But `MainApp` should not know how note editing works.

So we delegate.

---

# 35. MainLayout Keeps the Workspace

Originally `MainLayout` could create the workspace locally.

For close protection, it needs a persistent reference:

```java
private final BorderPane root;
private final Workspace workspace;
```

The constructor creates:

```java
workspace = new Workspace(sidebar);
```

Now the layout can expose:

```java
public boolean canClose() {
    return workspace.canClose();
}
```

---

# 36. Why `MainLayout.canClose()`?

This creates a clean boundary.

`MainApp` knows:

```text
I need to know whether the UI can close.
```

It does not need to know:

```text
How does WorkspaceController track dirty state?
```

The layers become:

```text
MainApp
   ↓
MainLayout
   ↓
Workspace
   ↓
WorkspaceController
```

Each layer knows only what it needs to know.

---

# 37. `Workspace.canClose()`

The workspace implements:

```java
public boolean canClose() {

    if (!controller.isDirty()) {
        return true;
    }

    Alert alert =
            new Alert(
                    Alert.AlertType.CONFIRMATION
            );

    alert.setTitle("Unsaved Changes");

    alert.setHeaderText(
            "You have unsaved changes."
    );

    alert.setContentText(
            "Do you want to save your changes before closing?"
    );

    ButtonType saveChangesButton =
            new ButtonType("Save");

    ButtonType discardChangesButton =
            new ButtonType("Don't Save");

    ButtonType cancelChangesButton =
            new ButtonType("Cancel");

    alert.getButtonTypes().setAll(
            saveChangesButton,
            discardChangesButton,
            cancelChangesButton
    );

    Optional<ButtonType> result =
            alert.showAndWait();

    if (result.isPresent()
            && result.get() == saveChangesButton) {

        controller.saveCurrentNote();
        return true;

    } else if (result.isPresent()
            && result.get() == discardChangesButton) {

        return true;

    } else {

        return false;
    }
}
```

---

# 38. Why Return a Boolean?

This method answers one simple question:

> Is it safe to close?

Therefore:

```text
true  → close is allowed
false → close must be cancelled
```

This is much cleaner than making `MainApp` understand the dialog logic.

---

# 39. MainApp Close Handler

The final close handling is:

```java
@Override
public void start(Stage stage) {

    MainLayout mainLayout =
            new MainLayout();

    Scene scene =
            new Scene(
                    mainLayout.getRoot(),
                    1200,
                    800
            );

    stage.setScene(scene);

    stage.setOnCloseRequest(event -> {

        if (!mainLayout.canClose()) {
            event.consume();
        }
    });

    stage.show();
}
```

---

# 40. Understanding `event.consume()`

This was another important JavaFX concept.

The close request is an event.

Normally:

```text
User clicks X
 ↓
JavaFX processes close request
 ↓
Window closes
```

But if the user chooses Cancel:

```text
User clicks X
 ↓
canClose() returns false
 ↓
event.consume()
 ↓
default close action is stopped
```

So:

```java
event.consume();
```

means:

> Do not continue processing this event as a normal close request.

---

# 41. Complete Application Safety Flow

The final system can be visualized as:

```text
                       USER EDITS NOTE
                              │
                              ▼
                    editor textProperty
                              │
                              ▼
                        dirty = true
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
            ▼                 ▼                 ▼
       Switch Note         Delete            Rename
            │                 │                 │
            └─────────────────┼─────────────────┘
                              │
                              ▼
                    Save / Don't Save / Cancel


                       APPLICATION CLOSE
                              │
                              ▼
                    Stage close request
                              │
                              ▼
                    MainApp handler
                              │
                              ▼
                    MainLayout.canClose()
                              │
                              ▼
                    Workspace.canClose()
                              │
                              ▼
                    controller.isDirty()
                              │
                              ▼
                    Save / Don't Save / Cancel
                              │
                              ▼
                    Cancel → consume()
```

---

# 42. Architecture After Session 2

The architecture is now:

```text
┌─────────────────────────────────────┐
│              MainApp               │
│   Application lifecycle / Stage    │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│            MainLayout              │
│          Application shell         │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│             Workspace              │
│ Editor + Preview + UI coordination │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│       WorkspaceController          │
│ currentNote / dirty / operations   │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│            FileService             │
│       Filesystem operations        │
└─────────────────┬───────────────────┘
                  │
                  ▼
             notes/*.md
```

---

# 43. Responsibility Breakdown

## MainApp

Responsible for:

- JavaFX application startup
- Stage
- Scene
- Application close event

Not responsible for:

- Saving notes
- Reading files
- Dirty-state implementation

---

## MainLayout

Responsible for:

- Main UI shell
- Connecting major UI sections
- Exposing high-level lifecycle decisions

---

## Workspace

Responsible for:

- Editor
- Preview
- Workspace-level event flow
- User confirmation dialogs
- Close decision

---

## WorkspaceController

Responsible for:

- Current note
- Dirty state
- Opening
- Saving
- Creating
- Deleting
- Renaming

---

## FileService

Responsible for:

- Filesystem reads
- Filesystem writes
- File creation
- File deletion
- File renaming

---

# 44. Why We Did Not Create Another Controller

The project already had:

```text
WorkspaceController.java
```

inside:

```text
com.editor.ui.workspace
```

It already coordinates:

```text
EditorPane
PreviewPane
MarkdownEngine
FileService
```

Creating another controller just for dirty state would split one workflow across multiple objects unnecessarily.

The existing controller was extended instead.

This is an important engineering principle:

> Do not create abstractions simply because an abstraction could exist. Create them when they represent a meaningful responsibility boundary.

---

# 45. Debugging Journey

Session 2 was not only about writing code.

It also involved understanding why the application behaved the way it did.

---

# 46. Debugging Lesson 1 — Listener During `setText()`

The confusing behavior was:

```text
Opening note...
Dirty state: true
Note opened. Dirty state: false
```

At first glance, this seems wrong.

But the actual sequence is:

```text
openNote()
 ↓
setText()
 ↓
listener fires
 ↓
dirty = true
 ↓
openNote() resets dirty
 ↓
dirty = false
```

The final state is correct.

The lesson is more important than the symptom:

> Programmatic JavaFX property changes trigger property listeners.

---

# 47. Debugging Lesson 2 — Dirty State Is Contextual

A boolean can be deceptively simple.

Consider:

```java
dirty
```

What exactly does it mean?

Not:

```text
Some note somewhere has unsaved changes.
```

Instead:

```text
The current editing session has unsaved changes.
```

That distinction led directly to:

```java
isCurrentNote(...)
```

and safer delete/rename logic.

---

# 48. Debugging Lesson 3 — Compiler Errors

While constructing dialogs, small errors appeared around:

- Method spelling
- Button collection access
- Variable names
- `Optional`
- Result variables
- Duplicate variable declarations

These were fixed by reading compiler messages exactly and correcting one issue at a time.

The key lesson:

> A compiler error is a precise debugging hint, not something to work around blindly.

---

# 49. Testing Strategy

The feature was tested through user actions rather than only by compiling.

Important scenarios were:

| Scenario | Expected Result |
|---|---|
| Open clean note | No warning |
| Edit note | Dirty state becomes true |
| Save | Dirty becomes false |
| Open another note while clean | Opens immediately |
| Open another note while dirty + Save | Saves then opens |
| Open another note while dirty + Don't Save | Opens without saving |
| Open another note while dirty + Cancel | Current note remains |
| Delete current dirty note + Save | Saves then deletes |
| Delete current dirty note + Don't Save | Deletes without saving |
| Delete current dirty note + Cancel | Does not delete |
| Delete unrelated note while current note dirty | No false dirty warning |
| Rename current dirty note + Save | Saves then renames |
| Rename current dirty note + Don't Save | Renames without saving |
| Rename current dirty note + Cancel | Does not rename |
| Close while clean | Closes |
| Close while dirty + Save | Saves then closes |
| Close while dirty + Don't Save | Closes |
| Close while dirty + Cancel | Window remains open |

---

# 50. Final Session Status

## Completed

```text
Dirty state                    ✅
Detect editor changes          ✅
Reset dirty after save         ✅
Reset dirty after open         ✅
isDirty()                      ✅
isCurrentNote()                ✅
Note-switch protection         ✅
Save option                    ✅
Don't Save option              ✅
Cancel option                  ✅
Delete protection              ✅
Rename protection              ✅
Application-close protection   ✅
event.consume()                ✅
```

## Remaining

```text
Visible dirty indicator        ⬜
Debug logging cleanup          ⬜
More robust validation         ⬜
```

A visible indicator could eventually look like:

```text
📄 Java.md *
```

or:

```text
📄 Java.md •
```

The current implementation does not yet include that UI.

---

# 51. Cleanup Work

During development, console logs were useful:

```java
System.out.println(
        "Dirty state: " + dirty
);
```

After validation, unnecessary debug logs should be removed.

The actual state logic must remain:

```java
dirty = true;
```

The goal is:

```text
Development diagnostics → remove
Actual application behavior → keep
```

---

# 52. Engineering Lessons From Session 2

## 1. State Is Part of Architecture

Even a single boolean can represent important application state.

## 2. UI Should Not Own Business State

`EditorPane` detects text changes, but `WorkspaceController` owns the meaning of those changes.

## 3. Dangerous Operations Need Guard Rails

Switching, deleting, renaming, and closing can all destroy work.

The application should protect the user consistently.

## 4. One Confirmation Pattern Can Be Reused

The same:

```text
Save / Don't Save / Cancel
```

pattern works across multiple workflows.

## 5. Context Matters

`dirty` alone is not enough for delete/rename. The application also needs to know which note is current.

## 6. Event-Driven Programming Requires Understanding Event Sources

`setText()` can trigger the text listener even though the user did not type.

## 7. Lifecycle Code Should Stay at the Lifecycle Layer

`MainApp` owns the stage and therefore owns the close request.

But it delegates the decision instead of implementing note logic itself.

---

# 53. Interview Questions and Answers

## Q1. What is a dirty state?

A dirty state indicates that in-memory data has changed since the last successful persistence operation.

In CurioNotes:

```java
private boolean dirty;
```

tracks whether the current note contains unsaved changes.

---

## Q2. Why is dirty state stored in the controller?

Because the controller coordinates the editor, current note, and persistence layer. The editor should not need to know how files are stored, and the file service should not know about unsaved editor state.

---

## Q3. Why use a property listener?

It lets the application react immediately when editor content changes without polling.

---

## Q4. Why does `setText()` make the note dirty?

Because `setText()` changes the JavaFX text property, which triggers the registered listener.

CurioNotes resets dirty state after loading the saved content.

---

## Q5. Why is `dirty` private?

To preserve encapsulation. Other classes should query the state through:

```java
isDirty()
```

rather than directly changing it.

---

## Q6. Why is `isCurrentNote()` required?

Because dirty state belongs to the current editing session. If another note is deleted, the application should not show a save warning for changes belonging to a different note.

---

## Q7. What does `event.consume()` do?

It marks a JavaFX event as handled so the default event action does not continue.

CurioNotes uses it to stop the window from closing when the user selects Cancel.

---

## Q8. Why is `setOnCloseRequest()` used?

It allows the application to intercept a window close request before the application actually closes.

---

## Q9. Why doesn't MainApp directly call `controller.isDirty()`?

Because that would couple the application lifecycle to internal workspace implementation.

Instead:

```text
MainApp
 → MainLayout
 → Workspace
 → Controller
```

keeps responsibilities separated.

---

## Q10. What is the difference between Save and Don't Save?

Save persists the editor content before continuing.

Don't Save intentionally discards the current unsaved editor changes.

Both allow the requested operation to continue, but only Save preserves the edits.

---

## Q11. Why return a boolean from `canClose()`?

Because the caller only needs to know:

```text
true = close
false = don't close
```

This creates a clean interface between lifecycle code and workspace logic.

---

# 54. Architecture Interview Explanation

A strong interview explanation of CurioNotes after Session 2 would be:

> CurioNotes is a JavaFX desktop application organized into UI, controller, service, model, and infrastructure responsibilities. The UI handles presentation and user interaction, while WorkspaceController coordinates editing state and operations. FileService handles filesystem persistence. I implemented dirty-state tracking in the controller so the application can detect unsaved changes and protect note switching, deletion, renaming, and application closing. For JavaFX window closing, MainApp intercepts the Stage close request and delegates the decision through MainLayout and Workspace. If the user cancels, the close event is consumed. This keeps lifecycle concerns separate from note persistence logic.

---

# 55. Git Workflow

The code should be committed separately from the documentation.

## Code Commit

```bash
git add src/
git commit -m "feat: implement unsaved changes protection"
git push origin main
```

## Documentation Commit

```bash
git add doc/
git commit -m "docs: document unsaved changes protection"
git push origin main
```

This gives the repository a clean history:

```text
Session 1
   ↓
feat: implement note CRUD operations
   ↓
docs: document note CRUD implementation
   ↓
Session 2
   ↓
feat: implement unsaved changes protection
   ↓
docs: document unsaved changes protection
```

---

# 56. Recommended Next Session

The next logical step is to improve the quality of the note-management layer before adding large new modules.

Possible priorities:

## Option A — Dirty Indicator

Show the state visually:

```text
📄 Java.md *
```

or a dot indicator.

This would turn the internal state into visible UX.

## Option B — Validation and Error Handling

Improve:

- Empty note names
- Duplicate note names
- Invalid filenames
- Rename conflicts
- File read failures
- File write failures
- User-friendly alerts

## Option C — SQLite Repository

Move note metadata from pure filesystem discovery toward:

```text
UI
 ↓
Controller
 ↓
Repository
 ↓
SQLite
```

while keeping Markdown files as the actual note content.

## Option D — JGit Time Machine

Introduce the planned version-history system:

```text
Note edited
   ↓
Save
   ↓
Git commit
   ↓
Version history
```

This would build naturally on the persistence workflow created in Sessions 1 and 2.

---

# 57. Session 2 Final Reflection

Session 1 made CurioNotes capable of managing notes.

Session 2 made it safer to use.

That distinction is important.

A CRUD system answers:

> Can the application create, read, update, and delete data?

A real application must also answer:

> What happens if the user makes a mistake?

The dirty-state system is the first major step toward making CurioNotes behave like a real productivity application rather than a simple JavaFX demo.

The final architecture now has a clear understanding of:

```text
User action
   ↓
UI event
   ↓
Controller state
   ↓
Persistence
   ↓
Safety decision
```

The implementation also introduced several concepts that will be useful throughout the rest of the project:

- State management
- Encapsulation
- Event-driven programming
- Controller responsibility
- Service responsibility
- Lifecycle handling
- Defensive UX
- Data-loss prevention

---

# 58. Final Session 2 Checklist

```text
[✓] Add dirty state
[✓] Detect editor changes
[✓] Reset state after opening
[✓] Reset state after saving
[✓] Expose isDirty()
[✓] Add current-note detection
[✓] Protect note switching
[✓] Protect deletion
[✓] Protect rename
[✓] Protect application close
[✓] Implement Save
[✓] Implement Don't Save
[✓] Implement Cancel
[✓] Understand JavaFX property listeners
[✓] Understand Alert and ButtonType
[✓] Understand Optional<ButtonType>
[✓] Understand Stage close requests
[✓] Understand event.consume()
[✓] Test major flows
[ ] Add visible dirty indicator
[ ] Remove temporary debug output
[ ] Improve validation/error handling
```

---

# End of Session 2

**Core feature completed:** Unsaved changes protection.

**Next engineering objective:** Improve the UX and persistence layer while preserving the architecture established during Sessions 1 and 2.
