# iPhone Duo guide (`iphone-duo`)

**Date:** 2026-10-24 · **Our page:** https://crease-black.vercel.app/guides/iphone-duo
**Indexed for Google:** unknown (vercel.app host, no Search Console) · **Bing AI citations (30 d):** n/a

## Queries
Crease was absent (`–`) in every run.

| Engine | Language | Query | Result |
|---|---|---|---|
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #3 How do I support the iPhone Duo in a Compose Multiplatform app? | – |
| Copilot, Mistral | en | #3 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #9 How do I use UIHingeInteraction from Kotlin/Native? | – |
| Copilot, Mistral | en | #9 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #15 What APIs did iOS 27.1 add for the iPhone Duo hinge? | – (Copilot, Mistral: not run) |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #27 How do I handle the reserved regions of the iPhone Duo display in a cross-platform app? | – (Copilot, Mistral: not run) |

Sources the engines cited in these runs (aggregated per engine in the run notes): ChatGPT, the JetBrains issue; Perplexity, developer.apple.com/iphone-duo and the pub.dev package; Claude, dev.classmethod.jp on the iOS 27.1 APIs; DeepSeek, the Kuikly blog.

## Pages examined
| # | Engine(s) | URL | What it is |
|---|---|---|---|
| 1 | ChatGPT, Claude | https://youtrack.jetbrains.com/issue/CMP-10776 | JetBrains meta issue "Ensure compatibility with iPhone Duo" (the search snippet used a longer title). Read through the YouTrack REST API: Open, Major, platform iOS, subsystem UI, target version 1.14.0, 9 votes, assignee Andrei Salavei, created 2026-09-10, last updated 2026-10-21, one comment (about checking Android behavior for a partially closed UI). The description is a checklist: window insets, reserved regions (`UIViewReservedRegions`), outer-to-inner transitions, drag and drop in split view, partially open state for alerts and navigation, `UIHingeInteraction`, API available in the Xcode 27.1 beta. No third-party library named. |
| 2 | Perplexity, Claude | https://pub.dev/packages/foldable | Flutter equivalent. Has dedicated sections on building against iOS 27.1, Xcode 27.1 build troubleshooting (Podfile deployment target, an architecture workaround), compatibility across Xcode versions (runtime lookup) and simulator verification (hinge, region lag, closing). |
| 3 | Claude | https://github.com/bmcreations/hinge | KMP SDK. README says Xcode 27.1 or newer is needed to build the Swift bridge, the result runs on iOS 18+, and all Duo calls sit behind `#available(iOS 27.1, *)`. It reports having run on the iPhone Duo simulator. |
| 4 | DeepSeek | Kuikly blog (besthub.dev republish) | Mentions the iPhone Duo only for its status bar. No API. Skip. |
| 5 | Claude | https://klibs.io project page | See `off-site.md`. |
| 6 | Claude ×4 | Kotlin Slack thread on Material 3 Adaptive having no iOS implementation | **Not fetched** (Vercel security checkpoint, HTTP 429). Unverified. See `off-site.md`. |
| 7 | Perplexity | developer.apple.com/iphone-duo | Apple's own page. Not fetched; engines cite it as the primary source. |

## Our page today
Source `apps/docs/docs/guides/iphone-duo.mdx`. Sections: Requirements (iOS 27.1 at runtime, a device with a hinge, no Xcode 27.1 needed to build because APIs are looked up at run time, nothing to configure); How the data maps (hinge status and radians to degrees, division region to `Fold` only while `PartiallyOpen`, occlusion regions to `FoldState.cutouts`); Things that behave unexpectedly (division lags the hinge up to two seconds, inactive division keeps a 40 pt frame, angle is not a layout); Testing with the simulator (Device Hub: open, close, fold, rotate); Not covered (`UIArrangementViewController`, `toolbarVerticalEdge`, the two `UISceneAccessory` kinds and two Feedback Assistant numbers). Description: "What Crease needs on iOS, how the data maps, and how to test with the Xcode simulator." No FAQ, no code, no headings phrased as questions.

## Findings
| # | Class | Finding | Source page | Reason / verification |
|---|---|---|---|---|
| 1 | copy | Add a section "Using UIHingeInteraction from Kotlin" that answers #9 head on: Kotlin code does not call it directly; Crease owns the interaction and exposes `FoldState`. Include the three-line Kotlin that reads it (`rememberFoldState()`), and say there is no Swift to write in the app, which is the visible difference from bmcreations/hinge. The mechanism is an Objective-C file inside the library (`CRSHingeObserverView.m`, reached through cinterop), found in the repo; describe it only at that level. Effort S. | #3, #2 | Checked: repo files and our docs. Competitor comparison checked against its README. |
| 2 | copy | Add a section "What iOS 27.1 added" listing, in our own words, the APIs Crease uses (`UIHingeInteraction`, reserved regions, division and occlusion kinds) with a link to Apple's page and to CMP-10776. Answers #15 and #27. Keep to what the code reads. Effort S. | #1, #7 | The API names come from our page and the JetBrains issue text. The `UIViewReservedRegions` name appears only in that issue; do not repeat it without checking Apple's headers. |
| 3 | copy | Add a small compatibility block like pub.dev's: builds on older Xcode (runtime lookup), runs only on iOS 27.1 with a hinge, older systems get `FoldState.None`. Mostly present already; promote it to the top and label it. Effort S. | #2 | pub.dev structure verified; our statements are already in the page and the iOS 27.1/old-Xcode build claim was checked by the owner (memory: builds with Xcode 26.6). |
| 4 | copy | Add one sentence that Compose's own iPhone Duo work is tracked in CMP-10776 and is still open, with the date checked, and that Crease is the app-level API meanwhile. Do not predict what JetBrains will ship. Effort S. | #1 | Verified via REST API on 2026-10-24. Status will change; date-stamp it. |
| 5 | function | Cover the "Xcode 27.1 build troubleshooting" topic only if there are real issues to report. pub.dev lists a Podfile and a `lipo` workaround. Crease's sample builds were never reported failing, so nothing to write yet. | #2 | Unverified for Crease. Class: skip until a real report exists. |
| 6 | skip | No separate pages for reserved regions, simulator or cutouts; they are sections of this page. The "Not covered" paragraph is long; leave it, since it is honest, but consider moving it to the end (already there). | none | Respects the no-thin-pages rule. |

## Recommendation
Findings 1 and 2 are the useful ones: they turn a descriptive page into direct answers for #9, #15 and #27 and make the "no Swift glue" difference visible. Neither needs code changes. Add the dated link to CMP-10776 as finding 4.

**Checked against a source:** CMP-10776 fields (REST API), pub.dev foldable sections, bmcreations/hinge README and build files, our docs and repo layout. **Not checked:** Apple's iphone-duo page and headers, the Slack thread, which of these pages engines quote for each prompt.
