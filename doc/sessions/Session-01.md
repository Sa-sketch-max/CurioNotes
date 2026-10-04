Absolutely bro. This is the right time to document it because **Day 1 has now produced a real architectural milestone**, not just a collection of code.

# CurioNotes — Day 1 Engineering Documentation
## Note Management System: CRUD + Rename

---

# 1. What was the goal of Day 1?

The goal was to turn CurioNotes from a basic Markdown editor into an application capable of managing actual notes.

Initially, we had the editor and Markdown preview, but the application couldn't properly manage notes.

So Day 1 focused on:

```text
Create Note
Open Note
Edit Note
Save Note
Delete Note
Rename Note
```

The final goal was:

```text
User
 ↓
CurioNotes UI
 ↓
Note Management Logic
 ↓
File Service
 ↓
Markdown files on disk
```

Instead of immediately building complicated features like SQLite, Git, AI, search, etc., we first established the **fundamental note lifecycle**.

---

# 2. Why did we start with CRUD?

Every knowledge-management application needs some form of CRUD.

CRUD means:

| Operation | CurioNotes |
|---|---|
| Create | Create a new note |
| Read | Open/read a note |
| Update | Edit and save a note |
| Delete | Delete a note |
| Rename | Rename a note |

Although Rename isn't technically part of the standard CRUD acronym, it is a fundamental note-management operation.

The reasoning was:

> Before adding advanced features, the application must be able to reliably manage its basic data.

For example, there's little point building Git version history if we can't even safely create and delete a note.

---

# 3. Architecture Before Day 1

The application was originally moving toward putting too much responsibility inside UI classes.

We intentionally moved toward a layered structure.

Our architecture became:

```text
┌─────────────────────────────┐
│            UI               │
│                             │
│ Sidebar                     │
│ Workspace                   │
│ EditorPane                  │
│ PreviewPane                 │
└──────────────┬──────────────┘
               │
               ↓
┌─────────────────────────────┐
│        Controller           │
│                             │
│ WorkspaceController         │
└──────────────┬──────────────┘
               │
               ↓
┌─────────────────────────────┐
│          Service            │
│                             │
│ FileService                 │
└──────────────┬──────────────┘
               │
               ↓
┌─────────────────────────────┐
│        File System          │
│                             │
│ notes/*.md                  │
└─────────────────────────────┘
```

This separation is extremely important.

---

# 4. Project Structure

Current structure:

```text
com.editor
│
├── app/
│
├── controller/
│
├── git/
│   └── GitService.java
│
├── markdown/
│   └── MarkdownEngine.java
│
├── model/
│   └── Note.java
│
├── service/
│   └── FileService.java
│
└── ui/
    │
    ├── components/
    │   ├── CurioButton.java
    │   ├── CurioCard.java
    │   ├── CurioIconButton.java
    │   ├── CurioMenuItem.java
    │   └── CurioSearchBar.java
    │
    ├── theme/
    │   ├── Theme.java
    │   └── ThemeManager.java
    │
    ├── workspace/
    │   ├── EditorPane.java
    │   ├── PreviewPane.java
    │   ├── Workspace.java
    │   └── WorkspaceController.java
    │
    ├── MainLayout.java
    ├── MenuBarBuilder.java
    ├── Sidebar.java
    ├── StatusBar.java
    └── TopBar.java
```

The important Day 1 classes were:

```text
Note
FileService
Sidebar
Workspace
WorkspaceController
```

---

# 5. `Note` — Our Model

The `Note` class represents a note inside the application.

```java
package com.editor.model;

import java.nio.file.Path;

public class Note {

    private final Path path;

    public Note(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getName() {
        return path.getFileName().toString();
    }

    @Override
    public String toString() {
        return getName();
    }
}
```

## Why did we create this class?

We could have passed `Path` objects everywhere.

For example:

```java
Path notePath
```

But that would mean every part of the application needs to understand that a note is represented by a filesystem path.

Instead:

```java
Note
```

represents the **domain concept**.

The UI doesn't need to think:

> "This is a Path pointing to a Markdown file."

It can simply think:

> "This is a Note."

That's a major architectural improvement.

---

# 6. `Path`

The class contains:

```java
private final Path path;
```

`Path` comes from:

```java
java.nio.file.Path
```

It represents the location of a file or directory.

For example:

```text
notes/Java.md
```

could be represented by:

```java
Path
```

---

# 7. `getName()`

```java
public String getName() {
    return path.getFileName().toString();
}
```

Suppose:

```text
path = notes/Java.md
```

Then:

```java
path.getFileName()
```

returns:

```text
Java.md
```

and:

```java
.toString()
```

converts it to a normal Java `String`.

So:

```java
note.getName()
```

returns:

```text
Java.md
```

---

# 8. `toString()`

```java
@Override
public String toString() {
    return getName();
}
```

This is useful because JavaFX controls may call `toString()` when displaying objects.

So if a `ListView` contains:

```java
Note
```

instead of displaying:

```text
com.editor.model.Note@4e25154f
```

we get:

```text
Java.md
```

---

# 9. FileService

The `FileService` is responsible for filesystem operations.

This is one of the most important architectural decisions we made.

The UI should **not** do this:

```java
Files.delete(...)
```

The controller shouldn't contain filesystem implementation details either.

Instead:

```text
Workspace
      ↓
WorkspaceController
      ↓
FileService
      ↓
Files API
```

---

# 10. FileService — Directory Initialization

```java
private final Path notesDirectory;
```

The service stores the directory containing our notes.

Constructor:

```java
public FileService() {

    System.out.println("FileService constructor called");

    notesDirectory = Path.of("notes");

    try {
        Files.createDirectories(notesDirectory);
    } catch (IOException e) {
        throw new RuntimeException(
            "Failed to create notes directory"
        );
    }
}
```

## What happens?

When CurioNotes starts:

```text
notes/
```

is created if it doesn't already exist.

`createDirectories()` is useful because it doesn't fail just because the directory already exists.

---

# 11. Loading Notes

```java
public List<Note> loadNotes() {

    try (Stream<Path> paths = Files.list(notesDirectory)) {

        List<Note> notes = paths
                .filter(path -> path.toString().endsWith(".md"))
                .map(Note::new)
                .collect(Collectors.toList());

        System.out.println("Loaded notes: " + notes.size());

        return notes;

    } catch (IOException e) {
        throw new RuntimeException(
            "Failed to load notes.", e
        );
    }
}
```

The process is:

```text
notes/
 ├── Java.md
 ├── DSA.md
 ├── Spring.md
 └── image.png
```

`Files.list()` gets the directory contents.

Then:

```java
.filter(...)
```

keeps Markdown files.

So:

```text
Java.md       ✅
DSA.md        ✅
image.png     ❌
photo.jpg     ❌
```

Then:

```java
.map(Note::new)
```

converts:

```text
Path
```

into:

```text
Note
```

Finally:

```java
.collect(Collectors.toList())
```

creates:

```java
List<Note>
```

---

# 12. Why `Note::new`?

This:

```java
.map(Note::new)
```

is a method/constructor reference.

It is equivalent to:

```java
.map(path -> new Note(path))
```

This was a useful Java concept learned during the implementation.

---

# 13. Reading a Note

```java
public String readNote(Note note) {

    try {
        return Files.readString(note.getPath());

    } catch (IOException e) {

        throw new RuntimeException(
            "Failed to read note.", e
        );
    }
}
```

The service receives:

```java
Note
```

gets its path:

```java
note.getPath()
```

and reads the contents.

For:

```text
Java.md
```

containing:

```markdown
# Java

Java is a programming language.
```

the method returns that entire text.

---

# 14. Saving a Note

```java
public void saveNote(Note note, String content) {

    try {
        Files.writeString(note.getPath(), content);

    } catch (IOException e) {

        throw new RuntimeException(
            "Failed to save note.", e
        );
    }
}
```

This implements the Update part of CRUD.

Flow:

```text
User edits editor
       ↓
Ctrl + S
       ↓
WorkspaceController
       ↓
FileService.saveNote()
       ↓
Files.writeString()
       ↓
Java.md updated
```

---

# 15. Ctrl + S

We added a keyboard event filter in `Workspace`.

Conceptually:

```java
addEventFilter(KeyEvent.KEY_PRESSED, event -> {

    KeyCodeCombination saveShortcut =
            new KeyCodeCombination(
                    KeyCode.S,
                    KeyCombination.CONTROL_DOWN
            );

    if (saveShortcut.match(event)) {

        controller.saveCurrentNote();

        event.consume();
    }
});
```

The important idea is:

```text
Ctrl + S
   ↓
controller.saveCurrentNote()
```

The UI does not directly write the file.

---

# 16. `currentNote`

`WorkspaceController` contains:

```java
private Note currentNote;
```

This is extremely important.

It tells CurioNotes:

> Which note is currently being edited?

For example:

```text
currentNote → Java.md
```

Then when Ctrl+S happens:

```java
fileService.saveNote(currentNote, content);
```

CurioNotes knows exactly which file to save.

---

# 17. Opening a Note

```java
public void openNote(Note note) {

    System.out.println(
        "Opening: " + note.getName()
    );

    currentNote = note;

    String content =
            fileService.readNote(note);

    editorPane.setText(content);

    System.out.println("Editor updated");
}
```

Flow:

```text
User clicks Java.md
       ↓
Sidebar selection listener
       ↓
WorkspaceController.openNote()
       ↓
currentNote = Java.md
       ↓
FileService.readNote()
       ↓
editorPane.setText()
```

---

# 18. Markdown Preview

The editor has a text listener:

```java
editorPane.textProperty().addListener(
    (observable, oldText, newText) -> {

        String html =
                markdownEngine.parseToHtml(newText);

        previewPane.setHtml(html);
    }
);
```

This means:

```text
User types Markdown
       ↓
TextProperty changes
       ↓
Listener executes
       ↓
MarkdownEngine
       ↓
HTML
       ↓
PreviewPane
```

So the Markdown editor and preview are loosely coupled.

---

# 19. Sidebar Architecture

One of the most important things we learned was:

> Sidebar should not perform business operations itself.

For example, Sidebar shouldn't do:

```java
Files.delete(...)
```

Instead, Sidebar communicates using callbacks.

---

# 20. `Consumer<Note>`

We added:

```java
private Consumer<Note> noteSelectedListener;
```

and:

```java
private Consumer<Note> deleteNoteListener;
```

and:

```java
private Consumer<Note> renameNoteListener;
```

`Consumer<T>` means:

> I accept one value of type `T` and return nothing.

For example:

```java
Consumer<Note>
```

means:

```text
Input: Note
Output: nothing
```

---

# 21. Why callbacks?

Suppose Sidebar directly knew about `WorkspaceController`.

Then:

```text
Sidebar
 ↓
WorkspaceController
```

would tightly couple them.

Instead:

```text
Sidebar
 ↓
Callback
 ↓
Workspace
 ↓
Controller
```

Sidebar simply says:

> "A note was selected."

It doesn't care what happens afterward.

This is **loose coupling**.

---

# 22. New Note

The New Note flow became:

```text
＋ New Note
      ↓
TextInputDialog
      ↓
Workspace
      ↓
WorkspaceController.createNote()
      ↓
FileService.createNote()
      ↓
notes/MyNote.md
      ↓
reload sidebar
      ↓
open new note
```

Controller:

```java
public Note createNote(String name) {

    return fileService.createNote(name);
}
```

The controller delegates the actual filesystem work.

---

# 23. Delete Note

Sidebar callback:

```java
sidebar.setDeleteNoteListener(note -> {

    Alert alert =
            new Alert(Alert.AlertType.CONFIRMATION);

    alert.setTitle("Delete Note");
    alert.setHeaderText("Delete this note?");
    alert.setContentText(note.getName());

    Optional<ButtonType> result =
            alert.showAndWait();

    if (result.isPresent()
            && result.get() == ButtonType.OK) {

        controller.deleteNote(note);

        sidebar.setNotes(
                fileService.loadNotes()
        );
    }
});
```

The important architectural flow is:

```text
Sidebar
 ↓
Delete callback
 ↓
Confirmation
 ↓
Controller
 ↓
FileService
 ↓
Files.delete()
```

---

# 24. Why confirmation?

Without confirmation:

```text
Click Delete
 ↓
File immediately disappears
```

That's dangerous.

With confirmation:

```text
Click Delete
 ↓
"Delete this note?"
 ↓
Cancel / OK
```

This prevents accidental deletion.

---

# 25. Controller Delete

We added:

```java
public void deleteNote(Note note) {

    fileService.deleteNote(note);

    if (currentNote != null &&
            currentNote.getPath()
                    .equals(note.getPath())) {

        currentNote = null;

        editorPane.clear();
    }
}
```

The second half is important.

Suppose:

```text
currentNote → Java.md
```

and the user deletes Java.md.

We don't want:

```text
Java.md deleted
currentNote → deleted file ❌
```

Instead:

```text
Java.md deleted
       ↓
currentNote = null
       ↓
editor cleared
```

Because the editor already has a text listener, clearing it also causes the preview to update.

---

# 26. Rename Note

Rename was implemented through:

```java
Files.move(
    oldPath,
    newPath
);
```

For example:

```text
Java.md
   ↓
Java Basics.md
```

The method:

```java
public Note renameNote(
        Note note,
        String newName) {

    if (!newName.endsWith(".md")) {
        newName += ".md";
    }

    Path newPath =
            notesDirectory.resolve(newName);

    try {

        Files.move(
                note.getPath(),
                newPath
        );

        return new Note(newPath);

    } catch (IOException e) {

        throw new RuntimeException(
                "Failed to rename note.",
                e
        );
    }
}
```

---

# 27. Why does rename return a new `Note`?

This is subtle but important.

Before:

```text
Note
 ↓
Java.md
```

After rename:

```text
Java Basics.md
```

The old `Note` contains the old path.

Therefore we create:

```java
return new Note(newPath);
```

The controller then updates:

```java
currentNote = renamedNote;
```

if the renamed note was currently open.

---

# 28. Rename Flow

Complete flow:

```text
User selects Java.md
        ↓
Click Rename
        ↓
TextInputDialog
        ↓
Enter "Java Basics"
        ↓
Workspace
        ↓
WorkspaceController.renameNote()
        ↓
FileService.renameNote()
        ↓
Files.move()
        ↓
Java.md → Java Basics.md
        ↓
Sidebar reload
        ↓
Renamed note opened
```

---

# 29. Important Debugging We Did

Day 1 wasn't just about writing code.

We encountered real Java errors.

## Error 1 — `cannot find symbol deleteNoteButton`

The code referenced:

```java
deleteNoteButton
```

but the field had not been declared.

We fixed it by adding:

```java
private final Button deleteNoteButton;
```

---

# 30. Error 2 — Variable might not have been initialized

We declared:

```java
private final Button deleteNoteButton;
```

but attempted to use it before assigning it.

Because `final` fields must be initialized exactly once, Java complained.

Correct order:

```java
deleteNoteButton =
        createButton("🗑 Delete Note");

deleteNoteButton.setOnAction(...);
```

The lesson:

> Declaration and initialization are different things.

```java
Button button;       // declaration

button = new Button(); // initialization
```

---

# 31. Error 3 — Too many compilation errors

At one point braces were placed incorrectly in `Sidebar`.

That produced many seemingly unrelated errors.

The lesson was important:

> When Java suddenly reports dozens of errors, inspect the first structural error first.

A misplaced:

```java
}
```

can cause the compiler to interpret the rest of the class incorrectly.

---

# 32. Error 4 — Save code in the wrong layer

We initially encountered problems because filesystem save logic was being placed in the wrong class.

The correct separation became:

```text
WorkspaceController
       ↓
FileService.saveNote()
       ↓
Files.writeString()
```

Instead of putting filesystem implementation directly into the controller.

This was an important architectural correction.

---

# 33. Debugging Method We Learned

Instead of randomly changing code, we traced execution.

For example:

```text
Button clicked?
      ↓
Selection changed?
      ↓
Callback called?
      ↓
Controller called?
      ↓
Service called?
      ↓
File read?
      ↓
Editor updated?
```

We used logs such as:

```java
System.out.println("Selection changed");
```

```java
System.out.println("Calling listener...");
```

```java
System.out.println("Opening: " + note.getName());
```

```java
System.out.println("Editor updated");
```

This helped distinguish:

```text
Code problem
```

from:

```text
Data/input problem
```

At one point the code was actually working, but the Markdown file itself was empty.

That was a very useful debugging lesson.

---

# 34. Separation of Responsibilities

This is probably the biggest architectural lesson from Day 1.

### Sidebar

Responsible for:

```text
UI
 ↓
buttons
 ↓
selection
 ↓
callbacks
```

Not responsible for:

```text
filesystem
database
business logic
```

### Workspace

Responsible for:

```text
connecting UI components
dialogs
keyboard shortcuts
coordinating callbacks
```

### WorkspaceController

Responsible for:

```text
application operations
current note
delegating to services
```

### FileService

Responsible for:

```text
filesystem operations
```

### Note

Responsible for:

```text
representing a note
```

---

# 35. Why not put everything in Sidebar?

We could technically write:

```java
deleteButton.setOnAction(e -> {
    Files.delete(...);
});
```

But that would make Sidebar responsible for:

```text
UI
+
business logic
+
filesystem
```

Eventually Sidebar would become a **God class**.

Instead:

```text
UI → Controller → Service
```

keeps responsibilities clear.

---

# 36. Final Note Lifecycle

CurioNotes now has this lifecycle:

```text
                 CREATE
                   │
                   ↓
              notes/*.md
                   │
                   ↓
                 OPEN
                   │
                   ↓
              EDIT NOTE
                   │
                   ↓
                SAVE
                   │
                   ↓
             UPDATED FILE
                   │
          ┌────────┴────────┐
          ↓                 ↓
       RENAME             DELETE
          ↓                 ↓
    NEW FILE PATH       FILE REMOVED
```

---

# 37. Complete Application Flow

Putting everything together:

```text
                         USER
                           │
                           ↓
                  ┌────────────────┐
                  │   CurioNotes   │
                  └───────┬────────┘
                          │
             ┌────────────┴────────────┐
             ↓                         ↓
        ┌─────────┐              ┌───────────┐
        │ Sidebar │              │ Workspace │
        └────┬────┘              └─────┬─────┘
             │                         │
             │ callbacks              │
             └───────────┬─────────────┘
                         ↓
              ┌────────────────────┐
              │ WorkspaceController│
              └──────────┬─────────┘
                         ↓
                  ┌─────────────┐
                  │ FileService │
                  └──────┬──────┘
                         ↓
                    ┌──────────┐
                    │ notes/   │
                    │ *.md     │
                    └──────────┘
```

---

# 38. What We Learned Technically

### Java

- Classes and objects
- `final` fields
- Constructor injection
- `Path`
- `Files`
- `IOException`
- Streams
- `Collectors`
- Constructor references
- Lambdas
- `Consumer<T>`
- `Runnable`
- `Optional`
- Exception handling

### JavaFX

- `ListView`
- `ListCell`
- Event handlers
- Property listeners
- Dialogs
- `Alert`
- `TextInputDialog`
- `ButtonType`
- Event filters
- Layout management

### Architecture

- Separation of concerns
- MVC-ish architecture
- Controller/service separation
- Dependency injection
- Loose coupling
- Callbacks
- Single Responsibility Principle

### Debugging

- Read compiler errors carefully
- Fix the first meaningful error
- Trace event flow
- Use logs strategically
- Separate code bugs from data problems
- Verify one layer at a time

---

# 39. Interview Questions From This Session

You could now be asked:

### Q1. Why use a service layer?

**Answer:**

The service layer encapsulates business/infrastructure operations so UI components don't directly interact with the filesystem. This improves separation of concerns, testability, and maintainability.

---

### Q2. Why use `Consumer<Note>`?

**Answer:**

`Consumer<Note>` allows Sidebar to notify another component that a Note-related event occurred without tightly coupling Sidebar to a specific controller.

---

### Q3. Why use `Files.move()` for rename?

**Answer:**

A filesystem rename can be represented as moving a file from its old path to a new path, so `Files.move(oldPath, newPath)` performs the operation.

---

### Q4. Why does `WorkspaceController` maintain `currentNote`?

**Answer:**

Because operations such as Save need to know which note is currently being edited.

---

### Q5. Why shouldn't Sidebar delete files directly?

**Answer:**

That would mix UI and filesystem responsibilities. Keeping file operations in `FileService` follows separation of concerns and makes the architecture easier to maintain.

---

### Q6. What happens when the current note is deleted?

```text
File deleted
      ↓
currentNote = null
      ↓
editor cleared
      ↓
preview updated
```

---

# 40. Day 1 Status

### Completed

```text
Load Notes                 ✅
Open Note                  ✅
Edit Note                  ✅
Save Note                  ✅
Ctrl + S                   ✅

Create Note                ✅
New Note Dialog            ✅
Create .md file            ✅
Refresh Sidebar            ✅

Delete Note                ✅
Delete Confirmation        ✅
Delete File                ✅
Current Note Cleanup       ✅

Rename Note                ✅
Rename Dialog              ✅
Rename File                ✅
Current Note Update        ✅
```

### Validation

We started implementing validation for note names:

```text
Trim whitespace
Reject blank names
Reject / and \
Automatically add .md
```

However, **friendly UI error handling for invalid input/duplicate names is still a follow-up task**, rather than something we should falsely mark as completely finished.

---

# 41. Git Checkpoint

We reached a major milestone and created the commit:

```bash
git add .
git commit -m "feat: implement note CRUD operations"
```

This is a good checkpoint because the application now has a functioning note-management foundation.

---

# 42. Day 1 Final Architecture

The biggest transformation was:

### Before

```text
JavaFX UI
   ↓
Files
```

### After

```text
JavaFX UI
   ↓
Workspace
   ↓
WorkspaceController
   ↓
FileService
   ↓
File System
```

That's a significant improvement.

You're no longer just making a JavaFX application that "works."

You're starting to build it like an actual software project.

---

# 43. Day 1 Lesson

The most important lesson from today isn't actually CRUD.

It's this:

> **Don't just make code work. Make each part responsible for the right thing.**

We deliberately didn't jump directly into:

```text
SQLite
JGit
AI
Search
GitHub Sync
```

Instead, we built the foundation first.

Now when we eventually add SQLite, for example, the architecture can evolve toward:

```text
Workspace
    ↓
Controller
    ↓
NoteRepository
    ↓
SQLite
```

And when JGit arrives:

```text
Controller
    ↓
GitService
    ↓
Repository
```

The foundation we built today makes those future features much easier to add without turning CurioNotes into one giant class.

## 🚀 Day 1 milestone

```text
                 CURIONOTES
                     │
              ┌──────┴──────┐
              │ NOTE SYSTEM │
              └──────┬──────┘
                     │
       ┌─────────────┼─────────────┐
       ↓             ↓             ↓
     CREATE        UPDATE        DELETE
       │             │             │
       └─────────────┼─────────────┘
                     ↓
                   RENAME
                     │
                     ↓
              Markdown Files
```

**Day 1 = Foundation established.**  
Next session can build on this instead of rewriting it.



















































































































