# Interview: Stage Close Request

`Stage.setOnCloseRequest()` registers a handler for a window close request:

```java
stage.setOnCloseRequest(event -> {
    if (!mainLayout.canClose()) {
        event.consume();
    }
});
```

`MainApp` owns the JavaFX `Stage`, so it is responsible for intercepting the lifecycle event. The actual editing decision remains inside `Workspace`.

**Interview answer:** I intercept the stage close request, delegate the decision to the workspace, and consume the event when the user cancels so unsaved data cannot be lost.
