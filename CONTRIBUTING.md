# Contributing

Thanks for helping. Bug reports, docs fixes and pull requests are all welcome.

## Before you start

- For anything bigger than a small fix, open an issue first so we can agree on the approach.
- Fold and hinge behavior depends on the device. If you can, say which device and OS version you tested on, and attach the hinge angle or fold bounds you saw.

## Setup

You need JDK 17 and an Android SDK. The Gradle wrapper handles the rest.

For iOS you need Xcode. The iPhone Duo hinge APIs ship with iOS 27.1, so testing them on a simulator needs Xcode 27.1 or newer with its Device Hub simulator. Older Xcode versions still build the iOS targets and run the rest of the tests.

The docs site uses [Bun](https://bun.sh) 1.3+ and Node.js 22.19+ (the version is in `.node-version`).

```sh
./gradlew :crease:jvmTest                 # desktop tests, quickest feedback
./gradlew :samples:web:wasmJsBrowserDevelopmentRun   # web sample in the browser
```

## Where things live

| path                          | What                                                                                  |
| ----------------------------- | ------------------------------------------------------------------------------------- |
| `crease/src/commonMain`       | The public API and the fold rules: posture, two-pane and tabletop layouts, `avoidFold`. |
| `crease/src/androidMain`      | Jetpack WindowManager folding features and the `TYPE_HINGE_ANGLE` sensor.              |
| `crease/src/iosMain`          | The iPhone Duo glue. Objective-C lives in `crease/src/nativeInterop/cinterop/hinge.def`. |
| `crease/src/jvmMain`, `wasmJsMain` | Platform stubs. They report no hinge unless a state is provided.                  |
| `samples/`                    | The Recipes and Photos apps for Android and iOS, and the web demo.                    |
| `apps/docs`                   | The docs site. Its code samples are compiled against the library.                     |
| `design/`                     | The design system the samples and the docs use.                                       |

## Checks

```sh
./gradlew spotlessCheck :crease:apiCheck :crease:jvmTest :crease:testAndroidHostTest :crease:koverVerify   # what CI runs
./gradlew :crease:iosSimulatorArm64Test    # on macOS
./gradlew spotlessApply                    # format the code (ktlint_official)
./gradlew :crease:apiDump                  # after changing the public API
bun run build:docs                         # from the repository root
bun run test:web                           # browser tests of the web demo, after :samples:web:wasmJsBrowserDistribution
```

CI runs all of them on every pull request. `koverVerify` fails the build when coverage drops below the gate in the Gradle build.

## Conventions

- Kotlin in the official style. Run `./gradlew spotlessApply` before you push.
- Everything that isn't public API is `internal`. Changing the public API means running `./gradlew :crease:apiDump` and reviewing the diff in `crease/api`.
- Every behavior change comes with a test. Pure fold rules are tested in `commonTest`; platform code gets tests where it can run.
- Add a line under `## [Unreleased]` in `CHANGELOG.md` for anything users will notice.
- Breaking changes need a new major version and an entry under **Changed**.

## License

By contributing you agree that your contributions are licensed under the MIT license.
