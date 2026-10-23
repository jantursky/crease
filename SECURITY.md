# Security policy

## Supported versions

Only the latest release of `io.github.jantursky:crease` gets fixes.

## Reporting a vulnerability

Please don't open a public issue. Use GitHub's [private vulnerability reporting](https://github.com/jantursky/crease/security/advisories/new), or email jan.tursky@gmail.com with a description and, if you can, a minimal reproduction.

You'll get an answer within a week. Once a fix is released, the advisory is published with credit, unless you'd rather stay anonymous.

## Things to know

- The library reads sensors and window state on the device and passes them to your composables. It makes no network calls, collects no data and needs no permissions. The Android hinge sensor is read without a permission.
- The iOS side calls iOS 27.1 hinge and region APIs through the Objective-C runtime, not by linking them. Every lookup is checked first, so on older systems the library does nothing instead of crashing.
- The library doesn't render or parse user content. What you draw in the folds is up to your app.
