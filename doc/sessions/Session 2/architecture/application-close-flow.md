# Application Close Protection

## Problem
Without protection, editing a note and clicking the window X could lose unsaved changes.

## MainApp

```java
stage.setOnCloseRequest(event -> {
    if (!mainLayout.canClose()) {
        event.consume();
    }
});
```

`MainApp` owns the `Stage`, so it handles the application lifecycle.

## MainLayout

`MainLayout` keeps the existing workspace:

```java
private final Workspace workspace;
```

and exposes:

```java
public boolean canClose() {
    return workspace.canClose();
}
```

This prevents `MainApp` from knowing about the editor internals.

## Workspace

`Workspace.canClose()` checks dirty state and shows Save / Don't Save / Cancel.

- Save → save and return `true`
- Don't Save → return `true`
- Cancel → return `false`

## Event Consumption

```java
if (!mainLayout.canClose()) {
    event.consume();
}
```

`consume()` stops the default close action, so Cancel keeps CurioNotes open.
