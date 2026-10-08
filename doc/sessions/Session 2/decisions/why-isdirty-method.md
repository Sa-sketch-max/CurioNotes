# Decision: Expose `isDirty()` Instead of the Field

The field stays private:

```java
private boolean dirty;
```

The controller exposes:

```java
public boolean isDirty() {
    return dirty;
}
```

This preserves encapsulation. UI classes can ask whether the session is dirty without directly changing internal state.

Later, the implementation could change from a boolean to content comparison or another strategy while callers continue using `isDirty()`.
