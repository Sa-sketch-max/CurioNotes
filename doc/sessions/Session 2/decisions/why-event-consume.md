# Decision: Use `event.consume()` on Cancelled Close

JavaFX sends a close request when the user clicks the window X.

CurioNotes intercepts it:

```java
stage.setOnCloseRequest(event -> {
    if (!mainLayout.canClose()) {
        event.consume();
    }
});
```

`canClose()` returns `false` only when the user cancels.

Therefore:

```text
Cancel → false → consume event → window stays open
Save/Don't Save → true → event continues → window closes
```

This is cleaner than manually trying to reopen or undo a closed window.
