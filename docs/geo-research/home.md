# Home (`home`)

**Date:** 2026-10-24 · **Our page:** https://crease-black.vercel.app/
**Indexed for Google:** unknown (vercel.app host, no Search Console) · **Bing AI citations (30 d):** n/a

## Queries
Crease was absent (`–`) in every run.

| Engine | Language | Query | Result |
|---|---|---|---|
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #1 What is the best library for foldable support in Compose Multiplatform? | – |
| Copilot, Mistral | en | #1 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #4 Is there a Kotlin Multiplatform library for foldable phones? | – |
| Copilot, Mistral | en | #4 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #10 Compose Multiplatform library for iPhone Duo hinge and fold | – (Mistral ×2 and Claude ×3 cited bmcreations/hinge across their runs) |
| Copilot, Mistral | en | #10 (same) | – |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #18 Kotlin Multiplatform equivalent of the Flutter foldable package | – (Copilot, Mistral: not run) |
| ChatGPT, Perplexity, Gemini, DeepSeek, Claude | en | #19 Kotlin Multiplatform alternative to expo-hinge | – (Copilot, Mistral: not run) |

## Pages examined
| # | Engine(s) | URL | What it is |
|---|---|---|---|
| 1 | Mistral, Claude | https://github.com/bmcreations/hinge | Its README opens with one sentence on what it is and which platforms, then a three-line module list, then the design idea. See `off-site.md`. |
| 2 | Perplexity, Claude | https://pub.dev/packages/foldable | Landing copy: one-line summary, platform ("iOS only, others get an empty, harmless API"), version, license, topics, then labelled sections (why it exists, install, scope, verification). |
| 3 | Perplexity | https://docs.expo.dev/versions/latest/sdk/hinge/ | Overview, a platform caveat (Android never reports `closed`), install for four package managers, usage, API list. |
| 4 | Mistral | https://github.com/topics/foldable-devices | Topic page with 21 repos; see `off-site.md`. |

## Our page today
Source `apps/docs/docs/index.mdx`. Lead paragraph says what Crease does and that it falls back to "no hinge" elsewhere. Version sentence uses `{{version}}`. One Kotlin example (`FoldAwareTwoPane`). "What you get" bullets (status and angle, fold geometry, `Posture`, three layout tools, cutouts, `ProvideFoldState`). A platforms table (Android minSdk 23 with WindowManager and `TYPE_HINGE_ANGLE` API 30+, iOS 27.1 `UIHingeInteraction` and reserved regions, desktop and wasmJs none). Four cards. No FAQ, no install snippet, so the Maven coordinates are **not** in the HTML. Description: "Hinge angle, fold position and fold-aware layouts for Compose Multiplatform." The iPhone Duo is named in the body but not in the description.

## Findings
| # | Class | Finding | Source page | Reason / verification |
|---|---|---|---|---|
| 1 | copy | Add a 3-line install block (`implementation("io.github.jantursky:crease:{{version}}")`, optional `crease-cover`) to the home page. It is the one fact an engine needs to answer "which library, what do I add". Effort S. | #1, #3 | Expo's page puts install right after the overview. Coordinates verified on Maven Central (`crease` 1.1.0 and `crease-cover`). |
| 2 | copy | Make the first sentence carry the query words: "Crease is a Kotlin Multiplatform library for Compose Multiplatform that reads hinge angle, fold position and posture on Android foldables and the iPhone Duo." Put "iPhone Duo" in the meta description too. Effort S. | #2, #1 | Both competitors state what, which platforms and which framework in the first sentence. Our wording is accurate but omits "Kotlin Multiplatform" and "library". |
| 3 | copy | Add a short "Scope and limits" paragraph in the style pub.dev and the Expo page use: what each platform cannot report (older iOS: no hinge; desktop and web: none; Android angle needs API 30 and the sensor). Most of this exists in the platforms table; a labelled section makes it quotable. Effort S. | #2, #3 | Verified against our own docs. Do not add a "tested on device" claim unless the owner confirms it. pub.dev states simulator verification; our iPhone Duo page describes simulator testing but does not say what was run. |
| 4 | function | GitHub repo topics (see `off-site.md`) and `homepage` already points to the demo; set topics so the repo page, not only the site, answers the queries. Effort S (config). | #4 | Our repo has none (checked with `gh repo view`). |
| 5 | copy | Add `SoftwareSourceCode` JSON-LD (repo, language, platforms). Effort S. | none | No evidence engines use it. Low value. |
| 6 | skip | Do not add a home-page comparison table or FAQ; those belong on `prior-art`. Do not add per-device or per-country landing pages. | none | Avoids duplicating `prior-art` and respects the principles. |

## Recommendation
Do findings 1 and 2 first: the home page should state "Kotlin Multiplatform library", the iPhone Duo and the coordinates in its text. Everything else on this page is secondary to `prior-art` and the off-site work.

**Checked against a source:** Maven Central coordinates and versions, pub.dev and Expo page structure, our own index.mdx. **Not checked:** whether engines quote install blocks or JSON-LD (inference only).
