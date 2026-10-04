# CurioNotes Package Structure

```text
com.editor
├── app/
├── controller/
├── git/
├── markdown/
├── model/
├── service/
└── ui/
    ├── components/
    ├── theme/
    └── workspace/
```

## Responsibilities

- `app` — application startup.
- `controller` — application-level controllers.
- `git` — JGit/version history.
- `markdown` — Markdown processing.
- `model` — domain objects such as `Note`.
- `service` — filesystem/business operations.
- `ui` — JavaFX presentation.

The current `WorkspaceController` remains under `ui.workspace` because it is directly coupled to the workspace UI.
