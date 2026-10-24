# GEO research summary

**Date:** 2026-10-24. Baseline and engine runs: [`../GEO_PROMPTS.md`](../GEO_PROMPTS.md).
Eight engines answered 191 prompts; none mentioned Crease. The site was one day old, so this
is a baseline, not a verdict. Per-page reports: [home](home.md), [prior-art](prior-art.md),
[iphone-duo](iphone-duo.md), [off-site](off-site.md).

| Rank | Page | Finding | Class | Effort | Why now |
|---|---|---|---|---|---|
| 1 | README, landing (`samples/web/.../index.html`, `site.css`), `.github/ISSUE_TEMPLATE/config.yml` | Every docs link points at `crease.vercel.app`, a stranger's create-react-app site. Real host: `crease-black.vercel.app`. | copy (bug) | S | User-visible: readers and crawlers following our links land on someone else's site. The README reaches Maven Central and GitHub only with the next release. |
| 2 | GitHub repo | No topics. Add `compose-multiplatform`, `kotlin-multiplatform`, `foldable`, `foldable-devices`, `iphone-duo`, `hinge`, `jetpack-windowmanager`, `android-foldables`. | function (config) | S | Mistral cited github.com/topics/foldable-devices; none of its repos is a KMP library. |
| 3 | prior-art | "Kotlin Multiplatform has no wrapper" and "Nothing common for KMP" are now contestable: `bmcreations/hinge` (created 2026-10-01, 0 stars, no license, not on Maven Central, needs an app-side Swift bridge) exists and is cited by Mistral and Claude. Name it with dated facts, add a comparison table (Flutter `foldable`, `expo-hinge`, `bmcreations/hinge`, Crease) and question-style headings matching prompts #4, #18, #19, #26. | copy | S | Accuracy, and this page is our answer to the "which library" prompts. |
| 4 | home | Maven coordinates are not in the home page HTML. Add the install block with `{{version}}`, say "Kotlin Multiplatform library" and "iPhone Duo" in the first sentence and in the meta description. | copy | S | Engines quote the install line they find; the home page is the canonical entry. |
| 5 | iphone-duo | Add "Using UIHingeInteraction from Kotlin" (prompt #9: no app-side Swift needed) and "What iOS 27.1 added" (#15, #27), plus a dated pointer to YouTrack CMP-10776 ("Ensure compatibility with iPhone Duo", Open, target CMP 1.14.0). | copy | S | Engines answer these prompts from Apple docs and classmethod.jp; we have the Kotlin-specific answer. |
| 6 | klibs.io | Not listed yet. Likely cause: Google's Maven Central mirror still 404s for 1.0.0/1.1.0 (checked 2026-10-24; Maven Central itself is 200). Re-check in a few days; request indexing if still missing. | function | S | Claude cited a klibs.io page for "KMP library for foldable phones". |
| 7 | demo (`crease-demo.vercel.app`) | No canonical, no robots.txt, no sitemap, no JSON-LD, no og:image, title just "Crease". Canonical to itself, add `SoftwareSourceCode` JSON-LD and a descriptive title. | copy | S | It is the GitHub homepage URL, so it gets crawled first. |
| 8 | docs sitemap | No `lastmod`; no IndexNow. Add both (pattern in the geo-audit skill's `libraries.md`). | function | M | Speeds discovery of the fixes above; does not guarantee citations. |
| 9 | off-site | kotlinlang Slack thread on Material 3 Adaptive having no iOS implementation (cited by Claude 4×) and YouTrack CMP-10776 are where people ask. Posting is the owner's call; read the thread first (it could not be fetched). | skip for now | — | Outward action. |
| 10 | off-site | `terrakok/kmp-awesome` wants about 50 stars and an external user. | skip | — | Not eligible yet. |

Unverified: the Slack thread (429) and the proandroiddev article (403) could not be read.
