# Interview: JavaFX Property Listeners

JavaFX properties notify listeners when their values change.

CurioNotes uses:

```java
editorPane.textProperty().addListener(
    (observable, oldText, newText) -> {
        String html = markdownEngine.parseToHtml(newText);
        previewPane.setHtml(html);
        dirty = true;
    }
);
```

This gives real-time preview updates and marks the editing session dirty.

A key detail is that `editorPane.setText(content)` also changes the property, so the listener fires when opening a note. `openNote()` therefore resets `dirty = false` after loading the content.

**Interview answer:** A JavaFX property listener lets the application react to state changes without polling the UI continuously.
