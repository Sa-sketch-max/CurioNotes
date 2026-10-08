# Mistake: The Text Listener Fires During `setText()`

While opening a note:

```java
editorPane.setText(content);
```

changes the JavaFX text property, so the listener runs and temporarily executes:

```java
dirty = true;
```

The final sequence is:

```text
setText()
 → listener
 → dirty = true
 → openNote() continues
 → dirty = false
```

This is correct. The reset at the end of `openNote()` establishes the final state.

**Lesson:** JavaFX property listeners react to programmatic property changes too, not only direct user typing.
