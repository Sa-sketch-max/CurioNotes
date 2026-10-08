# Mistake: Dirty State Must Be Associated With the Current Note

Suppose note A is open and dirty, then the user deletes note B.

Checking only:

```java
controller.isDirty()
```

would incorrectly treat B as the dirty note.

The correct condition is:

```java
controller.isDirty()
        && controller.isCurrentNote(note)
```

This ensures the save/discard protection applies only when the operation targets the note whose editor content is unsaved.

**Lesson:** State flags must be interpreted in the correct context.
