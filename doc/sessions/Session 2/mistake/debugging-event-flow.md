# Mistake: Debugging Event Flow

When note selection appeared not to work, trace messages were added at each boundary.

Expected trace:

```text
Selection changed
Selected: Test.md
Calling listener...
Lambda reached
Opening: Test.md
Editor updated
```

The important debugging pattern is:

```text
User action
→ JavaFX event
→ listener
→ callback
→ controller
→ service
→ UI/state update
```

Instead of guessing, find the first point where the expected trace stops.
