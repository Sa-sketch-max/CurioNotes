# Interview: Alert, ButtonType and Optional

CurioNotes uses JavaFX `Alert` for destructive-operation confirmation.

```java
Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

ButtonType save = new ButtonType("Save");
ButtonType discard = new ButtonType("Don't Save");
ButtonType cancel = new ButtonType("Cancel");

alert.getButtonTypes().setAll(save, discard, cancel);

Optional<ButtonType> result = alert.showAndWait();
```

`Optional<ButtonType>` makes the possibility of no result explicit.

The application then checks the selected button and performs the corresponding action.
