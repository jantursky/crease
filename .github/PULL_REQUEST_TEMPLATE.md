## What and why

<!-- What this changes, and the problem it solves. Link the issue if there is one. -->

## Checklist

- [ ] `./gradlew spotlessCheck :crease:apiCheck :crease:jvmTest :crease:testAndroidHostTest :crease:koverVerify` passes
- [ ] `./gradlew :crease:iosSimulatorArm64Test` passes, if iOS code changed
- [ ] `./gradlew :crease:apiDump` was run, if the public API changed
- [ ] New behavior has tests
- [ ] Docs in `apps/docs` are updated, if the behavior users see changed
- [ ] `CHANGELOG.md` has an entry under `## [Unreleased]`
