# Mistake: Final Variable Not Initialized

After declaring:

```java
private final Button deleteNoteButton;
```

the field was used before initialization, causing:

```text
variable deleteNoteButton might not have been initialized
```

Correct order:

```java
deleteNoteButton = createButton("🗑 Delete Note");

deleteNoteButton.setOnAction(event -> {
    ...
});
```

A `final` field must be assigned exactly once before the constructor finishes.

Lesson: when adding a final UI field, initialize it before using it.
