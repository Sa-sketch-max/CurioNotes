# Project Structure

```
com.editor
│
├── MainApp
│
├── markdown
│      └── MarkdownEngine
│
├── service
│
├── ui
│      │
│      ├── components
│      │
│      ├── workspace
│      │      ├── Workspace
│      │      ├── EditorPane
│      │      └── PreviewPane
│      │
│      ├── theme
│      │
│      ├── MainLayout
│      ├── Sidebar
│      ├── TopBar
│      └── StatusBar
│
└── util
```

---

## Why this structure?

Each package has one responsibility.

This keeps the application modular and easy to maintain.

Future developers can quickly locate related classes.