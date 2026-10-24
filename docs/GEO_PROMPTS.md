# GEO prompt set

Fixed questions asked of AI answer engines to see whether Crease is mentioned or cited.
Never name Crease in a prompt. Ask in a fresh chat, unpersonalized where possible, and
re-run the same set monthly.

Record per run: **M** = Crease mentioned (strict hit), **M\*** = bare "Crease" in a
foldable/Compose context without a strict hit, **C** = own URL cited (write the path),
**#** = position among recommendations, **–** = absent; otherwise why it did not run.

## Brand check

- Strict (counts as **M**): `/io\.github\.jantursky|jantursky\/crease|crease-(black|demo)\.vercel\.app/i`
- Loose (verify by reading, counts as **M\*** at most): `/\bcrease\b/i`

"Crease" is an ordinary word for foldables ("the crease of the screen") and an origami term,
and other projects share it: the npm package `crease` (CSS), `obsidian-creases`, and
`crease.vercel.app`, which is a stranger's create-react-app site, not our docs. A loose
hit is only a mention if the text names the library, the Maven coordinates or the repo.

## Prompts

| # | Lang | Prompt |
|---|---|---|
| 1 | en | What is the best library for foldable support in Compose Multiplatform? |
| 2 | en | How do I detect the hinge angle in a Kotlin Multiplatform app? |
| 3 | en | How do I support the iPhone Duo in a Compose Multiplatform app? |
| 4 | en | Is there a Kotlin Multiplatform library for foldable phones? |
| 5 | en | How can I read the fold position on Android and iOS from shared Kotlin code? |
| 6 | en | Jetpack WindowManager alternative that works on iOS with Compose Multiplatform |
| 7 | en | How do I build a two-pane layout that splits at the fold in Compose Multiplatform? |
| 8 | en | How do I implement tabletop mode in Jetpack Compose? |
| 9 | en | How do I use UIHingeInteraction from Kotlin/Native? |
| 10 | en | Compose Multiplatform library for iPhone Duo hinge and fold |
| 11 | en | How do I detect book posture and tabletop posture on a foldable in Kotlin? |
| 12 | en | How do I keep a button out of the fold area in Compose? |
| 13 | en | How do I preview a foldable layout in Compose without a foldable device? |
| 14 | en | How do I test fold-aware Compose UI in unit tests? |
| 15 | en | What APIs did iOS 27.1 add for the iPhone Duo hinge? |
| 16 | en | How do I show content on the outer display of a foldable from Compose? |
| 17 | en | How do I use the iPhone Duo camera capture accessory from Kotlin? |
| 18 | en | Kotlin Multiplatform equivalent of the Flutter foldable package |
| 19 | en | Kotlin Multiplatform alternative to expo-hinge |
| 20 | en | How do I make a Compose Multiplatform app fold-aware on Android foldables and iPhone? |
| 21 | en | Best way to handle Galaxy Z Fold and Pixel Fold layouts in a cross-platform Kotlin app |
| 22 | en | How do I get the hinge angle sensor value in Jetpack Compose? |
| 23 | en | How do I test my app on the iPhone Duo simulator in Xcode? |
| 24 | en | How do I emulate a foldable posture on the Android emulator? |
| 25 | en | Open-source libraries for foldable devices in 2026 |
| 26 | en | Which Kotlin Multiplatform libraries support the iPhone Duo? |
| 27 | en | How do I handle the reserved regions of the iPhone Duo display in a cross-platform app? |
| 28 | de | Wie unterstütze ich faltbare Smartphones und das iPhone Duo in einer Compose-Multiplatform-App? |
| 29 | ja | Compose Multiplatformで折りたたみスマホとiPhone Duoのヒンジ角度を取得する方法は？ |
| 30 | zh | 如何在 Kotlin Multiplatform 中检测折叠屏的铰链角度和折痕位置？ |

## Baseline (2026-10-24)

Site is one day old: repo `jantursky/crease` public, `io.github.jantursky:crease` 1.0.0 and
1.1.0 on Maven Central (1.1.0 published 2026-10-24), 0 GitHub stars.

- **Bing AI Performance / Search Console:** not available. Both sites are on `*.vercel.app`
  hosts (`crease-black.vercel.app` docs, `crease-demo.vercel.app` demo), which cannot be
  verified as owned domains. Baseline is crawl and page checks only.
- **Analytics referrers:** none set up on either site.
- **Docs URL:** every link to the docs (README, landing page, issue template) points at
  `crease.vercel.app`, which serves someone else's create-react-app ("React App"). The
  Vercel project `crease` actually serves `crease-black.vercel.app`.
- **Docs (`crease-black.vercel.app`):** robots allows all with `Content-Signal: search=yes,
  ai-input=yes, ai-train=yes`, sitemap with 16 URLs and no `lastmod`, `llms.txt`,
  `llms-full.txt`, `.md` per page, agent-skills index, canonical tags, server-rendered text,
  JSON-LD `WebSite` + `WebPage`/`TechArticle`, Open Graph with image. No `noindex`, no
  `x-robots-tag`. No IndexNow. Home page HTML does not contain the Maven coordinates.
- **Demo (`crease-demo.vercel.app`):** server-rendered intro text with the Maven coordinates,
  but no `robots.txt`, no sitemap, no canonical, no JSON-LD, no `og:image`, title is
  just "Crease".
- **Maven POM:** `<url>` points at the GitHub repo (fine).
- **Version claims:** README install line `1.1.0` matches `VERSION_NAME`; docs read the
  version from `gradle.properties`.

## Run notes

**2026-10-24.** Chrome profile of the site owner (Ján Turský).

- ChatGPT: Free plan, temporary chat (unpersonalized), `hints=search`. All 30 ran. Source chips are not anchors, so the check read the answer text only. Answers point at Jetpack WindowManager, Material 3 Adaptive and the JetBrains YouTrack issue "Support occlusion regions API for iPhone Duo" (CMP 1.14.0). Loose hits on #12 were the word "crease" for the hinge, not the library.
- Perplexity: Free plan, signed in; searches are saved to the account history (no temporary mode). All 30 ran; citations read from `data-pplx-citation-url`. Cited sources: developer.android.com (foldables guide, WindowManager), kotlinlang.org, developer.apple.com/iphone-duo, pub.dev `foldable`, docs.expo.dev hinge, a Surface Duo TwoPaneLayout blog post.
- Gemini: Free plan, signed in, temporary chat ("Dočasný čet"), Slovak UI, location Bratislava. All 30 ran (Chrome shut down at #7; resumed from #7 in a new tab). Loose hits on #7, #10, #15, #18 were the generic word.
- Copilot: Free, personal Microsoft account apptiary@gmail.com (picked from the account chooser after the owner's yes; optional cookies rejected). Not a temporary chat. Answer text only, no source links. Prompts 1–11 ran; from #12 the free plan returned "You've reached your daily limit" (resets 2:00). #12–#30 are still open.
- Claude (Max account of the owner, incognito, Opus 5.5 with web search; the owner agreed to use it): all 30 ran, five tabs in parallel; one web-fetch permission (YouTrack CMP-10776) allowed once. Not a free-tier measurement. Sources it cited include github.com/bmcreations/hinge (3 answers), klibs.io (arch-toolkit), the kotlinlang Slack thread on Material 3 Adaptive having no iOS implementation, dev.classmethod.jp on the iOS 27.1 APIs, pub.dev foldable / dual_screen_hinge / twin_screen, awesome.ecosyste.ms.
- Meta AI: guest prompt asks to log in or create an account; not run.
- Grok: guest session asks to sign up before answering; not run (no account creation).
- Mistral (Le Chat "Vibe", Free, signed in): #1–#10 ran, then "Messages limit reached" (resets in 3 h). Sources it cited include github.com/bmcreations/hinge, github.com/topics/foldable-devices, ienground/compose-hig, Google's foldables codelab and guide.
- DeepSeek (Free, signed in, Search on, DeepThink off): all 30 ran. Sources it cited include touchlab.co adaptive layouts, a proandroiddev KMP adaptive-layout article (also on daily.dev), tkuenneth repos, tornikegomareli.github.io/foldy-landing, Tencent Kuikly's iPhone Duo blog post, expo-hinge, axiom-sdk.

## Runs

| Date | Engine | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 | 20 | 21 | 22 | 23 | 24 | 25 | 26 | 27 | 28 | 29 | 30 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 2026-10-24 | ChatGPT (Free, temp) | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – |
| 2026-10-24 | Perplexity (Free) | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – |
| 2026-10-24 | Gemini (Free, temp) | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – |
| 2026-10-24 | Copilot (Free, personal) | – | – | – | – | – | – | – | – | – | – | – | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run | not run |
| 2026-10-24 | Claude (Max, incognito) | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – |
| 2026-10-24 | Mistral (Free) | – | – | – | – | – | – | – | – | – | – | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit | limit |
| 2026-10-24 | DeepSeek (Free) | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – | – |
