# Application Architecture

```
MainApp
        │
        ▼
MainLayout
│
├── TopBar
├── Sidebar
├── Workspace
└── StatusBar
        │
        ▼
 Workspace
 ┌───────────────┐
 │               │
 ▼               ▼
EditorPane   PreviewPane
       │
       ▼
MarkdownEngine
```

---

## Component Responsibilities

### MainApp

Starts the JavaFX application.

---

### MainLayout

Builds the application's overall layout.

---

### TopBar

Displays global actions such as Search, Git, Theme and Settings.

---

### Sidebar

Navigation between different sections of the application.

---

### Workspace

Coordinates the editor and preview.

Acts as the mediator between UI components.

---

### EditorPane

Allows the user to write Markdown.

---

### MarkdownEngine

Converts Markdown into HTML.

Contains no UI logic.

---

### PreviewPane

Displays rendered HTML using JavaFX WebView.