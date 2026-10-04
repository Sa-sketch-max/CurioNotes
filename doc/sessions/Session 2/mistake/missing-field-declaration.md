# Mistake: Missing Field Declaration

The code referenced `deleteNoteButton` before declaring it.

The compiler reported:

```text
cannot find symbol
```

The fix was:

```java
private final Button deleteNoteButton;
```

Then initialize it in the constructor:

```java
deleteNoteButton = createButton("🗑 Delete Note");
```

General lesson:

```text
Declare
→ Initialize
→ Configure
→ Add to layout
```
