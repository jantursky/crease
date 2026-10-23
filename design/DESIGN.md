# Crease design system

Used by the sample apps, the web demo and the docs site. Quiet product-gallery look: white canvas,
near-monochrome greys, one blue for links and compact controls, flat surfaces, no drop shadows.
Elevation comes from a white surface on a pale grey band, large corner radii and sparse hairlines.

Code form: `design/tokens.css` (web) and `samples/design` (Compose `CreaseTheme`). Every surface uses
the tokens; no ad-hoc colors.

## Color

| Token | Light | Dark | Role |
| --- | --- | --- | --- |
| `canvas` | `#ffffff` | `#000000` | Default page and screen background |
| `band` | `#f5f5f7` | `#161617` | Alternate full-width bands, footer, muted blocks |
| `raised` | `#fafafc` | `#1d1d1f` | Opened navigation, secondary pale fill |
| `card` | `#ffffff` | `#1d1d1f` | Cards on a `band` background |
| `hairline` | `#d6d6d6` | `#424245` | 1px dividers and restrained outlines |
| `control` | `#e6e6e8` | `#2c2c2e` | Disabled fills, subdued surfaces, slider tracks |
| `ink` | `#1d1d1f` | `#f5f5f7` | Headlines, body copy, icons |
| `slate` | `#707070` | `#a1a1a6` | Secondary copy, captions |
| `steel` | `#86868b` | `#86868b` | Input outlines, inactive indicators, outlined pills |
| `link` | `#0066cc` | `#2997ff` | Inline links and text buttons |
| `action` | `#0071e3` | `#0071e3` | Filled compact pills only (text on it is `#ffffff`) |
| `signal` | `#b64400` | `#ff791b` | Bare-text status labels ("New", "Folded") |
| `fold` | `rgba(0,113,227,0.12)` | `rgba(41,151,255,0.18)` | Fold and cutout overlays in demos |

## Typography

Inter (SIL Open Font License; npm `@fontsource-variable/inter`, bundled as a font resource in the
samples). Fallback stack: `Inter, "Helvetica Neue", Arial, sans-serif`. Tabular numerals for angles
and measurements (`font-variant-numeric: tabular-nums`).

| Role | Size / line height | Weight | Tracking |
| --- | --- | --- | --- |
| display | 56 / 60 (web hero 80 / 84) | 600 | -1.2px at 80, -0.8px at 56 |
| heading | 40 / 44 | 600 | 0 |
| title | 24 / 28 | 600 | 0 |
| kicker | 21 / 24 | 600 | +0.23px |
| body | 17 / 25 | 400 | -0.37px |
| body-small | 14 / 18 | 400 | -0.22px |
| caption | 12 / 16 | 400 | -0.12px |
| label | 12 / 16 | 600 | -0.12px |

Never weight 700 for headlines.

## Spacing

4px base. Scale: 4, 8, 12, 16, 20, 24, 28, 32, 40, 48, 64, 80. Section gap 90 (web) / 48 (app).
Card padding 28 (web) / 20 (app). Gap between related elements 20. Navigation bar height 44.

## Radius

| Element | Radius |
| --- | --- |
| Cards, media frames | 28 |
| Floating containers, sheets, nav when floating | 20 |
| Links (focus ring) | 10 |
| Buttons, pills, segmented controls | 9999 (full) |
| Inputs | full |
| Status labels | 0 (bare text, no box) |

## Borders and elevation

- No drop shadows anywhere.
- Outline rings only: `0 0 0 1px control` (subtle) and `0 0 0 1px steel` (outlined pill).
- 1px `hairline` dividers. Cards have no border.
- Elevation = `card` on `band` + 28 radius.

## Components

- **Filled pill**: `action` fill, white 12/16 text, full radius, 8×16 padding. Primary call to action only.
- **Outlined pill**: transparent, `ink` text, 1px `steel` ring, full radius.
- **Text link**: `link`, no underline until hover.
- **Card**: `card` on `band`, radius 28, padding 28/20, no border, no shadow.
- **Segmented control**: `control` track, full radius, selected segment `card` with subtle ring.
- **Slider**: 4px `control` track, `ink` active track, 20px `card` thumb with subtle ring.
- **Status label**: `signal` 12/16 weight 600, bare text.
- **Fold overlay** (demos): `fold` fill with 1px dashed `link` edge, shows fold and cutout bounds.

## Do

- `canvas` as the default background; `band` for alternate sections and behind cards.
- Large, left-aligned display statements with generous whitespace.
- Keep cards at 28 radius and shadowless.
- `action` only for compact filled pills; `link` for inline links.

## Don't

- No gradients.
- No shadows on cards, sheets or floating containers.
- Don't shrink the card radius to 8 or 12, and no square cards.
- Don't use `action` as a large background or as the color of every button.
- No colored boxes behind status labels.
- No boxed panels over imagery.
