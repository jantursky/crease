// Builds the web demo, serves it and saves the README screenshots to .github/assets/, each in the
// light and the dark theme. Run it with `bun run screenshots` whenever the site or the samples change.

/* global document -- used inside callbacks that run in the page */

import { spawnSync } from 'node:child_process'
import { mkdirSync, statSync } from 'node:fs'
import { join } from 'node:path'
import { fileURLToPath } from 'node:url'

import { chromium } from '@playwright/test'
import sharp from 'sharp'

import { serve } from './serve-demo.mjs'

const root = fileURLToPath(new URL('..', import.meta.url))
const out = join(root, '.github/assets')
const docsOut = join(root, 'samples/screenshots')

/** Each view is a freshly loaded page, optionally with a deep link into the playground. */
const VIEWS = [
  {
    // The top of the page, as a visitor first sees it.
    name: 'overview',
  },
  {
    // The recipes sample in book posture: the steps on one side of the fold, the photo on the other.
    name: 'recipes',
    query: '?demo=recipes&posture=book',
    element: '.product-card',
  },
  {
    // The photos sample in tabletop: the photo above the fold, the controls below.
    name: 'photos',
    query: '?demo=photos&posture=tabletop',
    element: '.product-card',
  },
  {
    // One example with its source open.
    name: 'example',
    element: '#example-list-detail',
    prepare: async (page) => {
      await page.locator('#example-list-detail').getByRole('button', { name: 'View source' }).click()
    },
  },
]

/** The docs examples page: the playground without its title bar, in each posture. Names are what the docs link to. */
const DOCS = [
  { name: 'recipe-book', query: '?demo=recipes&posture=book' },
  { name: 'recipe-tabletop', query: '?demo=recipes&posture=tabletop' },
  // The simulator's device keeps its width, so the single pane shows closed.
  { name: 'recipe-closed', query: '?demo=recipes&posture=closed' },
  { name: 'editor-book', query: '?demo=photos&posture=book' },
  { name: 'editor-tabletop', query: '?demo=photos&posture=tabletop' },
].map((view) => ({ ...view, dir: docsOut, element: '#playground', darkSuffix: true }))

const build = () => {
  const result = spawnSync('./gradlew', [':samples:web:wasmJsBrowserDistribution'], { cwd: root, stdio: 'inherit' })
  if (result.status !== 0) process.exit(result.status ?? 1)
}

const capture = async (browser, url, view, theme) => {
  const context = await browser.newContext({
    viewport: view.viewport ?? { width: 1280, height: 800 },
    deviceScaleFactor: 2,
    colorScheme: theme,
    reducedMotion: 'reduce',
  })
  const page = await context.newPage()
  await page.goto(url + (view.query ?? ''), { waitUntil: 'networkidle' })
  // Every Compose viewport has drawn once it has its canvas.
  await page.locator('#playground canvas').waitFor({ timeout: 30_000 })
  await page.evaluate(() => document.fonts.ready)
  await view.prepare?.(page)
  const target = view.element ? page.locator(view.element) : page
  if (view.element) await target.scrollIntoViewIfNeeded()
  // Let Compose settle on its first frames and any animation finish.
  await page.waitForTimeout(1500)
  // The sticky top bar would land in the middle of an element taller than the viewport.
  const png = await target.screenshot({ animations: 'disabled', style: view.element ? '.nav { display: none }' : '' })
  await context.close()

  const file = view.darkSuffix
    ? join(view.dir, `${view.name}${theme === 'dark' ? '-dark' : ''}.png`)
    : join(out, `${view.name}-${theme}.png`)
  await sharp(png).png({ palette: true, quality: 90, effort: 10, compressionLevel: 9 }).toFile(file)
  console.log(`${file.slice(root.length)}  ${Math.round(statSync(file).size / 1024)} kB`)
}

if (!process.argv.includes('--no-build')) build()
mkdirSync(out, { recursive: true })
const server = await serve()
const url = `http://127.0.0.1:${server.address().port}/`
const browser = await chromium.launch()
try {
  for (const view of [...VIEWS, ...DOCS]) {
    for (const theme of ['light', 'dark']) await capture(browser, url, view, theme)
  }
} finally {
  await browser.close()
  server.close()
}
