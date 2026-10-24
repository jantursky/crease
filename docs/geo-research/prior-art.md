# Why Crease (`prior-art`)

**Date:** 2026-10-24 · **Our page:** https://crease-black.vercel.app/prior-art
**Indexed for Google:** unknown (vercel.app host, no Search Console) · **Bing AI citations (30 d):** n/a

## Queries
Crease was absent (`–`) in every run. "Cited instead" is taken from the run notes in `docs/GEO_PROMPTS.md`, which record sources per engine, not per prompt.

| Engine | Language | Query | Result |
|---|---|---|---|
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #1 What is the best library for foldable support in Compose Multiplatform? | – |
| Copilot, Mistral | en | #1 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #4 Is there a Kotlin Multiplatform library for foldable phones? | – |
| Copilot, Mistral | en | #4 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #10 Compose Multiplatform library for iPhone Duo hinge and fold | – (Mistral and Claude cited bmcreations/hinge in their runs) |
| Copilot, Mistral | en | #10 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #18 Kotlin Multiplatform equivalent of the Flutter foldable package | – (Copilot, Mistral: not run) |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #19 Kotlin Multiplatform alternative to expo-hinge | – (Copilot, Mistral: not run) |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #26 Which Kotlin Multiplatform libraries support the iPhone Duo? | – (Claude cited bmcreations/hinge; DeepSeek cited the Kuikly blog) |

## Pages examined
| # | Engine(s) | URL | What it is |
|---|---|---|---|
| 1 | Mistral ×2, Claude ×3 | https://github.com/bmcreations/hinge | Direct KMP/Compose Multiplatform competitor. Details in `off-site.md`. Fetched via `gh api`. |
| 2 | Perplexity, Claude | https://pub.dev/packages/foldable | Flutter package, v1.0.5, iOS only (other platforms get an empty API), MIT, 32 likes, topics foldable/iphone-duo/ios/responsive/layout. Fetched. |
| 3 | Perplexity | https://docs.expo.dev/versions/latest/sdk/hinge/ | `expo-hinge` docs: Android and iOS, hooks and listener API, a note that Android never reports `closed`. Fetched. |
| 4 | DeepSeek | https://tornikegomareli.github.io/foldy-landing/ | Not a competitor. "Foldy" is a Swift/SwiftUI/UIKit visual-effect library (Metal shader, iOS 17+, MIT) that imitates a fold animation on any iPhone. No hinge reading, no KMP. Fetched. |
| 5 | Claude, DeepSeek | https://touchlab.co/adaptive-layouts-cmp | Dec 2024 article by Kevin Schildhorn on the Compose adaptive scaffolds (list-detail, supporting pane, window size class) and how they behave on iOS. Says nothing about hinge or posture. Fetched. |
| 6 | Claude, DeepSeek | proandroiddev "Building a Production-Grade Adaptive Layout System in KMP: From Phone to Foldable" | **Not fetched** (HTTP 403). From a search snippet only: Android posture via Jetpack WindowManager, iOS multitasking window size, single/split pane switching. No iPhone Duo hinge, as far as the snippet shows. Unverified. |
| 7 | DeepSeek | https://kuikly.tds.qq.com/home/blog | Blog index shows only a banner, no iPhone Duo post. A republished Tencent TDS article (besthub.dev, dated 2026-09-10) pitches Kuikly, a vendor KMP UI framework, for foldable adaptation and mentions the iPhone Duo once, for its status bar. No hinge API. Vendor claims, not a library for our use case. |

## Our page today
Sections: "On Android" (WindowManager plus `TYPE_HINGE_ANGLE`), "On the iPhone Duo" (says Kotlin Multiplatform has no wrapper), "In other frameworks" (Flutter `foldable`, `nitro_fold_duo`, `iphone_duo_ui_pro`; Expo `expo-hinge`), "On the outer display", "What was missing" (says "Nothing common for Kotlin Multiplatform"), scope note pointing to Compose adaptive libraries. No FAQ, no table, no date stamp, no Maven coordinates, no mention of any Kotlin competitor. Description: "What exists for foldables and the iPhone Duo today, and the gap Crease fills." Schema per baseline: WebSite plus TechArticle.

## Findings
| # | Class | Finding | Source page | Reason / verification |
|---|---|---|---|---|
| 1 | copy | The claims "Kotlin Multiplatform has no wrapper" and "Nothing common for Kotlin Multiplatform" are now wrong or at least contestable. bmcreations/hinge is a Kotlin Multiplatform SDK with a Compose Multiplatform adapter for Android and the iPhone Duo. Add a short, factual entry for it and soften the claims. Say what differs without disparaging: as of 2026-10-24 it has no published artifact on Maven Central (group `io.github.bmcreations` returns nothing), no license file, no releases, 0 stars, and its README has the app write and register a Swift `HingeBridge` for iOS, while Crease needs no app-side iOS code. Date-stamp it. Effort S. | #1 | Checked: GitHub API (created 2026-10-01, 13 commits, no releases, license null), its README and build files, Maven Central search. A one-day-old claim can change; date it. |
| 2 | copy | Answer the exact queries as headings with a direct first sentence: "Is there a Kotlin Multiplatform library for foldables?", "Which Kotlin Multiplatform libraries support the iPhone Duo?", "Kotlin Multiplatform alternative to Flutter `foldable` / `expo-hinge`". Engines quote pages whose headings match the question. Effort S. | #2, #3 | The two package pages put "why it exists" and "scope" in plain labelled sections. That they are quoted because of this is an inference, not measured. |
| 3 | copy | Add a comparison table: package, framework, platforms, what it reads (angle, posture, regions), two-pane widget, install coordinate. Rows for Flutter `foldable` (iOS only, per pub.dev), `expo-hinge` (Android and iOS, per Expo docs), bmcreations/hinge, Crease. Add `react-native-arrangement-view` and `capacitor-foldable` only after reading their READMEs; only their GitHub topics and star counts (49 for the first) were seen. Effort S. | #2, #3, #1 | Verified rows: foldable, expo-hinge, bmcreations/hinge. Others unverified, so leave them out for now. |
| 4 | copy | Put `io.github.jantursky:crease` and the repo link in the page text, so an engine can answer "what do I add?" from this page. Version via the existing `{{version}}` placeholder. Effort S. | #1, #2 | Verified: POM on Maven Central, latest 1.1.0. |
| 5 | copy | Link the JetBrains issue CMP-10776 ("Ensure compatibility with iPhone Duo") under "On the iPhone Duo" as the place Compose's own support is tracked, and say Crease is a library on top, not a replacement. Describe its status only with a date. Effort S. | YouTrack | Checked via the YouTrack REST API: state Open, Meta Issue, priority Major, target 1.14.0, 9 votes, assignee Andrei Salavei, created 2026-09-10, one comment. It lists insets, reserved regions, transitions, drag and drop, partially open state, `UIHingeInteraction`. It mentions no third-party library. |
| 6 | function | Add a `SoftwareSourceCode` (or `SoftwareApplication`) JSON-LD block with `codeRepository`, `programmingLanguage` and `runtimePlatform`. Effort S (config). | none | No evidence that engines read JSON-LD for this. Cheap, low expected value. |
| 7 | skip | Foldy, Kuikly, touchlab and the proandroiddev article are not competitors. Do not add them to the table. Touchlab and the proandroiddev piece are only useful as links under "use Compose's adaptive libraries for window size, Crease for the fold", which the page already says. | #4, #5, #6, #7 | Foldy and touchlab fetched. proandroiddev unverified. |
| 8 | skip | Per-framework pages ("Crease vs expo-hinge", "Crease vs Flutter foldable"). One comparison section is enough. | none | Conflicts with the no-thin-pages rule. |

## Recommendation
Rewrite "What was missing" around findings 1 to 4: name the Kotlin competitor honestly, add the question headings, one comparison table and the Maven coordinate. All of it is S effort and every number is checked. Re-check bmcreations/hinge before each release of the page, since it is three weeks old and moving.

**Checked against a source:** all bmcreations/hinge facts, CMP-10776 fields, pub.dev foldable summary, Expo hinge page structure, Foldy and touchlab contents, Crease POM. **Not checked:** proandroiddev article body, Slack thread, react-native-arrangement-view and capacitor-foldable features, any claim about what engines quote.
