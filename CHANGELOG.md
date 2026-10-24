# Changelog

All notable changes to `io.github.jantursky:crease` are listed here. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and versions follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- `crease-cover`, a second artifact (`io.github.jantursky:crease-cover`): `rememberCoverDisplayState()` reports whether the outer display of a foldable can show content (`Unsupported`, `Unavailable`, `Available`, `Active`), and `CoverDisplay` shows a composable there. On iOS 27.1 it registers a camera capture scene accessory, which the system presents only with an active camera session. On Android it presents on the rear display area through WindowManager. `ProvideCoverDisplayState` feeds a fixed state to previews and tests. `rememberCameraDirections()` reports which cameras face the viewer on the current display, through `AVCaptureDeviceDirectionCoordinator` on iOS 27.1, and `null` elsewhere. The iOS and Android presentation and the camera directions are not tested on a device.

## [1.0.0] - 2026-10-23

### Added

- Hinge state on Android foldables and iPhone Duo: `rememberHinge()` and `rememberFoldState()` give a `Hinge` with its `HingeStatus` (`Closed`, `PartiallyOpen`, `FullyOpen`) and, where the device has a sensor, its angle in degrees.
- Fold geometry: a `Fold` has bounds in window pixels, an orientation, and flags for whether it separates content and whether it occludes it. `FoldState` lists the folds and the camera cutouts, and gives the `Posture` (`Closed`, `Flat`, `Book`, `Tabletop`).
- `FoldState.closed()`, `FoldState.flat()`, `FoldState.book()` and `FoldState.tabletop()` build a state for previews and tests, and `ProvideFoldState` supplies one to a part of the composition tree.
- `FoldAwareTwoPane` splits its content at a separating fold, with a `TwoPaneFallback` (`FirstOnly`, `SideBySide`, `Stacked`) for when there is no fold.
- `TabletopLayout` shows its top and bottom content above and below a tabletop fold, and a flat layout otherwise.
- `Modifier.avoidFold()` keeps an element's content on the larger side of a separating fold.
- Targets: Android, iOS (iPhone Duo), JVM and wasmJs. JVM and wasmJs report no hinge unless a state is provided.

[Unreleased]: https://github.com/jantursky/crease/compare/1.0.0...HEAD
[1.0.0]: https://github.com/jantursky/crease/releases/tag/1.0.0
