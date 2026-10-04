Absolutely. The previous documentation explained **what** we built, but you want the **code itself taught properly**—what every important line does, why we wrote it, what Java/JavaFX concept it uses, and how the pieces connect.

Let's treat this as the **Day 1 code walkthrough**.

---

# CurioNotes — Day 1 Code Deep Dive

Our main classes are:

```text
Note.java
    ↓
FileService.java
    ↓
WorkspaceController.java
    ↓
Workspace.java
    ↕
Sidebar.java
```

Think of it as:

```text
                UI
                 │
       ┌─────────┴─────────┐
       ↓                   ↓
   Sidebar              Workspace
       │                   │
       └─────────┬─────────┘
                 ↓
        WorkspaceController
                 ↓
           FileService
                 ↓
          notes/*.md
```

---

# 1. `Note.java`

Our complete class:

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

Let's understand **every part**.

---

## `package`

```java
package com.editor.model;
```

This tells Java where the class belongs.

Our project structure is:

```text
src
└── main
    └── java
        └── com
            └── editor
                └── model
                    └── Note.java
```

Therefore:

```java
package com.editor.model;
```

matches the directory structure.

Packages help organize classes and prevent naming conflicts.

---

# 2. Importing `Path`

```java
import java.nio.file.Path;
```

`Path` is Java's modern way of representing a file/directory location.

For example:

```text
notes/Java.md
```

can be represented as:

```java
Path path;
```

Think of `Path` as:

> "Where is this file located?"

It isn't the actual file contents.

---

# 3. Creating the class

```java
public class Note {
```

We're creating our own domain object.

Instead of passing around:

```java
Path
```

everywhere, we created:

```java
Note
```

because CurioNotes deals with **notes**, not just files.

---

# 4. The `path` field

```java
private final Path path;
```

This means every `Note` object contains a `Path`.

For example:

```text
Note
 │
 └── path → notes/Java.md
```

### Why `private`?

Because other classes shouldn't directly manipulate the field.

Bad:

```java
note.path
```

Instead:

```java
note.getPath()
```

This is encapsulation.

---

# 5. Why `final`?

```java
private final Path path;
```

`final` means the reference can only be assigned once.

When we do:

```java
this.path = path;
```

we can't later do:

```java
this.path = anotherPath;
```

This makes the `Note` object more predictable.

---

# 6. Constructor

```java
public Note(Path path) {
    this.path = path;
}
```

Suppose FileService has:

```java
Path path = Path.of("notes/Java.md");
```

Then:

```java
Note note = new Note(path);
```

Java calls:

```java
public Note(Path path)
```

Inside:

```java
this.path = path;
```

### What's `this`?

`this` means:

> the current object.

So:

```java
this.path
```

means the object's field.

While:

```java
path
```

means the constructor parameter.

Therefore:

```java
this.path = path;
```

means:

```text
object's path = parameter path
```

---

# 7. `getPath()`

```java
public Path getPath() {
    return path;
}
```

Other classes can't directly access:

```java
private Path path;
```

So we provide a getter.

Example:

```java
note.getPath()
```

returns:

```text
notes/Java.md
```

This is used by FileService:

```java
Files.readString(note.getPath());
```

---

# 8. `getName()`

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

returns the filename:

```text
Java.md
```

Then:

```java
.toString()
```

converts it into:

```java
String
```

So:

```java
note.getName()
```

returns:

```text
Java.md
```

---

# 9. `toString()`

```java
@Override
public String toString() {
    return getName();
}
```

Every Java object inherits `toString()` from `Object`.

Without overriding it, printing:

```java
System.out.println(note);
```

might give:

```text
com.editor.model.Note@5e2de80c
```

Not useful.

We override it:

```java
return getName();
```

Now:

```java
System.out.println(note);
```

prints:

```text
Java.md
```

This is especially useful for JavaFX controls such as `ListView`.

---

# 10. `FileService.java`

This is where the actual filesystem work happens.

Conceptually:

```text
FileService
│
├── loadNotes()
├── readNote()
├── saveNote()
├── createNote()
├── deleteNote()
└── renameNote()
```

This class is basically our:

> **filesystem manager**

---

# 11. Imports

We have:

```java
import com.editor.model.Note;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
```

Let's understand them.

### `Note`

```java
import com.editor.model.Note;
```

Because FileService creates and returns `Note` objects.

### `IOException`

```java
import java.io.IOException;
```

Filesystem operations can fail.

For example:

```text
file doesn't exist
permission denied
disk problem
```

Java represents many such problems through `IOException`.

### `Files`

```java
import java.nio.file.Files;
```

This is where methods such as:

```java
Files.readString()
Files.writeString()
Files.createFile()
Files.delete()
Files.move()
Files.list()
```

come from.

---

# 12. `notesDirectory`

```java
private final Path notesDirectory;
```

This represents:

```text
notes/
```

Instead of repeatedly writing:

```java
Path.of("notes")
```

we store it once.

---

# 13. Constructor

```java
public FileService() {

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

When we do:

```java
FileService fileService = new FileService();
```

the constructor executes.

First:

```java
notesDirectory = Path.of("notes");
```

creates a Path representing:

```text
notes/
```

Then:

```java
Files.createDirectories(notesDirectory);
```

creates that directory if necessary.

---

# 14. Why `try/catch`?

Filesystem operations can throw `IOException`.

Java forces us to deal with it.

```java
try {
    ...
} catch (IOException e) {
    ...
}
```

means:

> Try this operation. If an IOException happens, handle it here.

---

# 15. `loadNotes()`

```java
public List<Note> loadNotes() {
```

The method returns:

```java
List<Note>
```

Meaning:

```text
multiple Note objects
```

Example:

```text
[
    Java.md,
    DSA.md,
    Spring.md
]
```

---

## `Files.list()`

```java
try (Stream<Path> paths = Files.list(notesDirectory)) {
```

This asks the filesystem:

> Give me the files/directories inside `notes/`.

Suppose:

```text
notes/
├── Java.md
├── DSA.md
├── Spring.md
└── image.png
```

The stream contains paths for these items.

---

# 16. Why `Stream<Path>`?

A stream lets us process the paths one by one.

Then:

```java
.filter(...)
.map(...)
.collect(...)
```

forms a pipeline.

Think:

```text
All files
   ↓
Filter Markdown files
   ↓
Convert Path → Note
   ↓
Collect into List
```

---

# 17. `.filter()`

```java
.filter(path -> path.toString().endsWith(".md"))
```

This is a lambda.

It means:

> Keep the path if its string representation ends with `.md`.

For example:

```text
Java.md      → true
DSA.md       → true
image.png    → false
photo.jpg    → false
```

So only Markdown files survive.

---

# 18. `.map()`

```java
.map(Note::new)
```

We have:

```text
Path
```

but our application wants:

```text
Note
```

So we convert:

```text
Path → Note
```

`Note::new` means:

```java
path -> new Note(path)
```

This is a constructor reference.

---

# 19. `.collect()`

```java
.collect(Collectors.toList());
```

The stream isn't our final data structure.

We convert it into:

```java
List<Note>
```

So:

```text
Stream<Path>
      ↓
filter
      ↓
map
      ↓
List<Note>
```

---

# 20. `readNote()`

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

The important line:

```java
Files.readString(note.getPath());
```

First:

```java
note.getPath()
```

might return:

```text
notes/Java.md
```

Then:

```java
Files.readString(...)
```

reads the entire file.

Result:

```text
# Java

Java is a programming language.
```

That String goes back to the controller.

---

# 21. `saveNote()`

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

There are two inputs:

```java
Note note
String content
```

Example:

```text
note → Java.md

content →
# Java

Java is awesome.
```

Then:

```java
Files.writeString(...)
```

writes the content into that file.

---

# 22. `createNote()`

Conceptually:

```java
public Note createNote(String name)
```

takes:

```text
Java
```

and produces:

```text
notes/Java.md
```

The important operation:

```java
Path notePath = notesDirectory.resolve(name);
```

---

# 23. `resolve()`

This is important.

If:

```java
notesDirectory = Path.of("notes");
```

and:

```java
name = "Java.md";
```

then:

```java
notesDirectory.resolve(name)
```

produces:

```text
notes/Java.md
```

Think:

```text
notes/
 +
Java.md
 =
notes/Java.md
```

---

# 24. `Files.createFile()`

```java
Files.createFile(notePath);
```

This actually creates the file.

So:

```text
Before:

notes/
```

becomes:

```text
notes/
└── Java.md
```

Then:

```java
return new Note(notePath);
```

converts that path into our domain object.

---

# 25. Delete

Our service method:

```java
public void deleteNote(Note note) {

    try {
        Files.delete(note.getPath());

    } catch (IOException e) {
        throw new RuntimeException(
            "Failed to delete note.", e
        );
    }
}
```

The important part:

```java
Files.delete(note.getPath());
```

Again:

```text
Note
 ↓
getPath()
 ↓
Path
 ↓
Files.delete()
```

---

# 26. Rename

Rename uses:

```java
Files.move(oldPath, newPath);
```

Suppose:

```text
oldPath = notes/Java.md

newPath = notes/Java Basics.md
```

Then:

```java
Files.move(
    oldPath,
    newPath
);
```

results in:

```text
notes/
└── Java Basics.md
```

---

# 27. Why does FileService return `Note` after rename?

Because the path changed.

Before:

```text
Note
 ↓
notes/Java.md
```

After:

```text
Note
 ↓
notes/Java Basics.md
```

So we create a new `Note`:

```java
return new Note(newPath);
```

---

# 28. `WorkspaceController`

Now we move upward.

The controller connects the UI to services.

Its fields:

```java
private final EditorPane editorPane;
private final PreviewPane previewPane;
private final MarkdownEngine markdownEngine;
private final FileService fileService;

private Note currentNote;
```

Think of this as the controller's knowledge:

```text
Controller knows:

Editor
Preview
Markdown parser
File system
Current note
```

---

# 29. Constructor Injection

```java
public WorkspaceController(
        EditorPane editorPane,
        PreviewPane previewPane,
        MarkdownEngine markdownEngine,
        FileService fileService) {

    this.editorPane = editorPane;
    this.previewPane = previewPane;
    this.markdownEngine = markdownEngine;
    this.fileService = fileService;
}
```

This is **dependency injection**.

Instead of the controller doing:

```java
new FileService()
```

it receives a FileService.

Why?

Because the controller shouldn't be responsible for creating every dependency.

The outside code does:

```java
new WorkspaceController(
    editorPane,
    previewPane,
    markdownEngine,
    fileService
);
```

This makes the controller more flexible and testable.

---

# 30. `initialize()`

```java
public void initialize() {

    editorPane.textProperty().addListener(
        (observable, oldText, newText) -> {

            String html =
                    markdownEngine.parseToHtml(newText);

            previewPane.setHtml(html);
        }
    );
}
```

This is JavaFX's **property listener system**.

The editor has a text property.

Whenever text changes:

```text
oldText → newText
```

the listener runs.

---

# 31. Why `newText`?

Suppose the editor contains:

```text
Hello
```

User types:

```text
Hello World
```

JavaFX detects:

```text
oldText = "Hello"
newText = "Hello World"
```

Our listener receives both.

We only care about the new content:

```java
markdownEngine.parseToHtml(newText);
```

---

# 32. Markdown Pipeline

```text
Editor
  ↓
newText
  ↓
MarkdownEngine
  ↓
HTML
  ↓
PreviewPane
```

For example:

```markdown
# Hello
```

becomes something like:

```html
<h1>Hello</h1>
```

Then PreviewPane renders the HTML.

---

# 33. `openNote()`

```java
public void openNote(Note note) {

    currentNote = note;

    String content =
            fileService.readNote(note);

    editorPane.setText(content);
}
```

This method performs three important operations.

### 1.

```java
currentNote = note;
```

Remember which note is open.

### 2.

```java
fileService.readNote(note);
```

Read its contents.

### 3.

```java
editorPane.setText(content);
```

Put those contents into the editor.

---

# 34. Why does setting editor text also update preview?

Because of our listener.

We have:

```text
openNote()
    ↓
editorPane.setText()
    ↓
textProperty changes
    ↓
listener runs
    ↓
MarkdownEngine
    ↓
PreviewPane
```

This is a beautiful example of JavaFX's reactive property model.

We don't have to manually call:

```java
previewPane.setHtml(...)
```

inside `openNote()`.

The property listener handles it.

---

# 35. `saveCurrentNote()`

```java
public void saveCurrentNote() {

    if (currentNote == null) {
        System.out.println(
            "No note is currently open."
        );
        return;
    }

    String content = editorPane.getText();

    fileService.saveNote(
        currentNote,
        content
    );
}
```

The first thing is a safety check:

```java
if (currentNote == null)
```

Why?

Because maybe the user presses:

```text
Ctrl + S
```

without opening a note.

Then:

```text
currentNote = null
```

and we shouldn't try:

```java
fileService.saveNote(null, content);
```

---

# 36. `return`

```java
return;
```

means:

> Stop this method immediately.

So:

```text
No current note
      ↓
print message
      ↓
return
      ↓
method ends
```

---

# 37. Getting editor content

```java
String content = editorPane.getText();
```

The editor contains the latest user input.

For example:

```markdown
# Java

I learned OOP today.
```

That becomes a String.

Then:

```java
fileService.saveNote(
    currentNote,
    content
);
```

writes it to disk.

---

# 38. `createNote()`

```java
public Note createNote(String name) {
    return fileService.createNote(name);
}
```

This looks almost useless, but architecturally it is important.

Workspace doesn't need to know how files are created.

It simply says:

```text
Controller:
"Create this note."
```

FileService handles:

```text
How exactly do I create the file?
```

That's separation of responsibilities.

---

# 39. `deleteNote()`

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

There are two parts.

### Delete the actual file

```java
fileService.deleteNote(note);
```

### Check whether it was the currently open note

```java
currentNote != null
```

AND:

```java
currentNote.getPath()
    .equals(note.getPath())
```

Both must be true.

---

# 40. Why `.equals()`?

We want to compare the actual paths.

```java
currentNote.getPath()
    .equals(note.getPath())
```

means:

> Are these two paths equal?

We generally use `.equals()` for object/value comparison rather than `==`.

---

# 41. `currentNote = null`

If the current note was deleted:

```java
currentNote = null;
```

means:

> There is no longer an active note.

That's exactly what we want.

---

# 42. `editorPane.clear()`

```java
editorPane.clear();
```

removes the current editor content.

Then JavaFX fires the text-property listener.

So:

```text
clear editor
     ↓
text changes
     ↓
Markdown listener
     ↓
preview becomes empty
```

---

# 43. Sidebar

Sidebar is primarily a UI component.

Its important fields are:

```java
private final ListView<Note> notesList;
private final Button newNoteButton;
private final Button deleteNoteButton;
private final Button renameNoteButton;
```

These are the UI controls.

---

# 44. `ListView<Note>`

This is an important JavaFX concept.

```java
ListView<Note>
```

means:

> This ListView contains Note objects.

Instead of:

```java
ListView<String>
```

we keep the actual domain objects.

For example:

```text
ListView
│
├── Note(Java.md)
├── Note(DSA.md)
└── Note(Spring.md)
```

This is much better than storing only strings.

---

# 45. Why keep `Note` objects?

Suppose the user clicks:

```text
Java.md
```

We don't just want the name.

We also need:

```text
Java.md
 ↓
notes/Java.md
```

The `Note` object already contains that path.

So the UI can pass the entire object:

```java
noteSelectedListener.accept(newNote);
```

---

# 46. Selection listener

The Sidebar has something like:

```java
notesList.getSelectionModel()
    .selectedItemProperty()
    .addListener(
        (observable, oldNote, newNote) -> {

            if (newNote != null &&
                    noteSelectedListener != null) {

                noteSelectedListener.accept(newNote);
            }
        }
    );
```

This means:

> Whenever the selected note changes, tell whoever is listening.

---

# 47. What are `oldNote` and `newNote`?

Suppose:

```text
Before:
Java.md selected

User clicks:
DSA.md
```

Then:

```text
oldNote = Java.md
newNote = DSA.md
```

We care about:

```java
newNote
```

because that's what the user wants to open.

---

# 48. Why check `newNote != null`?

Sometimes selection can become empty.

For example:

```text
Delete current note
```

After deletion there may be no selected item.

So:

```java
if (newNote != null)
```

prevents us from doing:

```java
newNote.getName()
```

when `newNote` is null.

---

# 49. Why check `noteSelectedListener != null`?

Because the Sidebar might be created before anyone registers a listener.

If we did:

```java
noteSelectedListener.accept(newNote);
```

while it was null, we'd get:

```text
NullPointerException
```

So:

```java
if (noteSelectedListener != null)
```

means:

> Only notify someone if someone has actually registered.

---

# 50. Setter for the callback

```java
public void setNoteSelectedListener(
        Consumer<Note> listener) {

    this.noteSelectedListener = listener;
}
```

Workspace uses it:

```java
sidebar.setNoteSelectedListener(note -> {
    controller.openNote(note);
});
```

So Sidebar doesn't know about the controller.

It simply stores the callback.

---

# 51. Lambda

This:

```java
note -> {
    controller.openNote(note);
}
```

means approximately:

```text
"When you give me a Note,
I'll call controller.openNote(note)."
```

That's why `Consumer<Note>` fits perfectly.

---

# 52. New Note Callback

```java
sidebar.setNewNoteListener(() -> {

    TextInputDialog dialog =
            new TextInputDialog();

    ...
});
```

Here we use:

```java
Runnable
```

because New Note doesn't need to pass an object into the callback.

`Runnable` basically means:

```text
Do this action.
```

Whereas:

```java
Consumer<Note>
```

means:

```text
Do this action with this Note.
```

---

# 53. Delete callback

Delete needs a Note:

```java
sidebar.setDeleteNoteListener(note -> {
```

because we need to know:

```text
Which note should be deleted?
```

Then:

```java
controller.deleteNote(note);
```

---

# 54. Rename callback

Same concept:

```java
sidebar.setRenameNoteListener(note -> {
```

We need the selected Note so we know:

```text
Which file should be renamed?
```

---

# 55. `TextInputDialog`

For Rename:

```java
TextInputDialog dialog =
        new TextInputDialog(currentName);
```

The constructor argument:

```text
currentName
```

becomes the initial value.

So if:

```text
Java.md
```

is selected, the dialog displays:

```text
Java
```

rather than forcing the user to type everything again.

---

# 56. Removing `.md`

We did something like:

```java
if (currentName.endsWith(".md")) {
    currentName = currentName.substring(
            0,
            currentName.length() - 3
    );
}
```

Why `-3`?

Because:

```text
.md
```

contains three characters:

```text
.
m
d
```

Example:

```text
Java.md
```

Length:

```text
7
```

Remove the last three:

```text
Java
```

---

# 57. `showAndWait()`

```java
Optional<String> result =
        dialog.showAndWait();
```

This displays the dialog and waits for the user's response.

The result is optional because the user could:

```text
OK
```

or:

```text
Cancel
```

---

# 58. Why `Optional<String>`?

Because there might not be a value.

For example:

```text
User clicks Cancel
```

There is no entered result we should use.

So:

```java
Optional<String>
```

represents:

```text
Value exists
OR
Value doesn't exist
```

---

# 59. `ifPresent()`

We then do:

```java
result.ifPresent(newName -> {
    ...
});
```

This means:

> If the user actually supplied a result, execute this code.

It's safer than blindly doing:

```java
result.get()
```

---

# 60. Complete Rename Flow in Code

The UI:

```java
Optional<String> result =
        dialog.showAndWait();

result.ifPresent(newName -> {

    Note renamedNote =
            controller.renameNote(
                    note,
                    newName
            );

    sidebar.setNotes(
            fileService.loadNotes()
    );

    controller.openNote(renamedNote);
});
```

Notice the separation:

```text
Dialog
 ↓
Controller
 ↓
Service
 ↓
Filesystem
```

---

# 61. `MainLayout`

Our layout:

```java
public MainLayout() {

    root = new BorderPane();

    root.setTop(new TopBar());

    Sidebar sidebar = new Sidebar();

    Workspace workspace =
            new Workspace(sidebar);

    root.setLeft(sidebar);
    root.setCenter(workspace);

    root.setBottom(new StatusBar());
}
```

The important architectural detail is:

```java
Sidebar sidebar = new Sidebar();
```

Then:

```java
new Workspace(sidebar);
```

We're passing the **same Sidebar instance** to Workspace.

---

# 62. Why the same Sidebar?

If we did:

```java
root.setLeft(new Sidebar());
```

and:

```java
new Workspace(new Sidebar());
```

we'd have two different Sidebar objects.

That's wrong.

Instead:

```text
              sidebar
                 │
        ┌────────┴────────┐
        ↓                 ↓
 MainLayout          Workspace
```

Both refer to the same UI component.

---

# 63. Overall Dependency Flow

The objects are constructed roughly like this:

```text
MainLayout
   │
   ├── Sidebar
   │
   └── Workspace
          │
          ├── EditorPane
          ├── PreviewPane
          ├── MarkdownEngine
          ├── FileService
          └── WorkspaceController
```

Then:

```text
Sidebar ↔ Workspace
Workspace → Controller
Controller → FileService
Controller → MarkdownEngine
```

This is dependency injection in practice.

---

# 64. The Most Important Mental Model

Whenever you write a feature, think:

### UI asks:

> "What happened?"

### Controller decides:

> "What operation should happen?"

### Service performs:

> "How do I actually do it?"

For Delete:

```text
Sidebar:
"I know the user clicked Delete."

Workspace:
"I need confirmation."

Controller:
"We need to delete this Note."

FileService:
"I know how to delete the file."
```

That's the architecture you should keep in your head while building the rest of CurioNotes.

---

# 65. One Feature, Traced Completely

Let's trace **Delete** from mouse click to filesystem.

```text
USER
 ↓
Clicks 🗑 Delete Note
 ↓
Button.setOnAction(...)
 ↓
Sidebar gets selected Note
 ↓
deleteNoteListener.accept(note)
 ↓
Workspace callback executes
 ↓
Alert confirmation
 ↓
User clicks OK
 ↓
controller.deleteNote(note)
 ↓
WorkspaceController.deleteNote()
 ↓
fileService.deleteNote(note)
 ↓
Files.delete(note.getPath())
 ↓
File removed
 ↓
Controller checks currentNote
 ↓
If necessary:
currentNote = null
editor.clear()
 ↓
Workspace reloads notes
 ↓
Sidebar displays updated list
```

That entire chain is what you built.

---

# 66. The Core Principle You Should Remember

Don't memorize every line.

Memorize **why the line exists**.

For example, don't just memorize:

```java
private final FileService fileService;
```

Understand:

> WorkspaceController needs filesystem operations, so it receives a FileService dependency.

Don't just memorize:

```java
Consumer<Note>
```

Understand:

> Sidebar needs a way to notify another component about a selected Note without knowing who will handle it.

Don't just memorize:

```java
Files.move()
```

Understand:

> Rename changes a file's path, so we move it from the old path to the new path.

Don't just memorize:

```java
currentNote = null;
```

Understand:

> Once the active note is deleted, the application must no longer consider that deleted file to be the current document.

That's the level of understanding we're aiming for.

---

## Your Day 1 architecture in one picture

```text
                         CURIONOTES
                              │
                              ↓
                    ┌──────────────────┐
                    │    MainLayout    │
                    └────────┬─────────┘
                             │
             ┌───────────────┴──────────────┐
             ↓                              ↓
       ┌───────────┐                  ┌────────────┐
       │  Sidebar  │                  │ Workspace  │
       └─────┬─────┘                  └─────┬──────┘
             │                              │
             │ callbacks                    │
             └──────────────┬───────────────┘
                            ↓
                  ┌──────────────────┐
                  │WorkspaceController│
                  └────────┬─────────┘
                           │
              ┌────────────┴────────────┐
              ↓                         ↓
       ┌──────────────┐          ┌───────────────┐
       │ FileService  │          │MarkdownEngine │
       └───────┬──────┘          └───────┬───────┘
               │                         │
               ↓                         ↓
          notes/*.md                 HTML Preview
```

**This is the part I want you to understand deeply.** Once this architecture becomes natural to you, Day 2, SQLite, JGit, Search, Study Mode, DSA Mode, etc. become much easier because you'll know **where each piece of code belongs**.