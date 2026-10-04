# Interview Questions: CurioNotes Architecture

## Why not put everything in MainApp?

`MainApp` should start the application, not contain all business logic.

## Why use controllers?

Controllers coordinate UI events and application behavior.

## Why use services?

Services encapsulate operations such as filesystem or future database access.

## Why use models?

Models represent domain concepts. `Note` represents a note instead of exposing raw paths everywhere.

## Why is separation useful?

It prevents large God classes and lets features such as Study, DSA, Habits, Expenses, Search, and Git history grow independently.
