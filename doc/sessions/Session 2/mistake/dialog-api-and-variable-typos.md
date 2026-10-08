# Mistake: Small JavaFX API and Variable Errors

During dialog implementation, several compile-time mistakes occurred around exact JavaFX API names and variable names, such as misspelled methods and inconsistent variables.

The debugging process was:

1. Read the exact compiler message.
2. Determine whether it is a method, variable, or declaration problem.
3. Check the actual JavaFX API.
4. Fix one compiler error at a time.
5. Rebuild and test the resulting behavior.

**Lesson:** Small compile-time errors are useful feedback. Do not guess; follow the compiler message precisely.
