import AxeBuilder from '@axe-core/playwright'
import { expect, test, type Page } from '@playwright/test'

const EXAMPLES = [
  'list-detail',
  'tabletop',
  'stacked-fallback',
  'avoid-fold',
  'hinge-angle',
  'reading-lamp',
  'fold-geometry',
]

// Skiko warns about an API it uses itself. Nothing else may reach the console as an error.
const KNOWN_ERRORS = [/Accessing `memory` via `wasmExports` is deprecated/]

const collectErrors = (page: Page) => {
  const errors: string[] = []
  page.on('pageerror', (error) => errors.push(error.message))
  page.on('console', (message) => {
    if (message.type() === 'error' && !KNOWN_ERRORS.some((known) => known.test(message.text()))) {
      errors.push(message.text())
    }
  })
  return errors
}

test('every Compose viewport mounts without errors', async ({ page }) => {
  const errors = collectErrors(page)
  await page.goto('/')
  await expect(page).toHaveTitle(/^Crease/)
  await expect(page.locator('#playground canvas')).toHaveCount(1, { timeout: 30_000 })
  for (const id of EXAMPLES) {
    await expect(page.locator(`[data-example="${id}"] canvas`), id).toHaveCount(1)
  }
  expect(errors).toEqual([])
})

test('the version comes from the build', async ({ page }) => {
  await page.goto('/')
  const version = await page.evaluate(() => (window as { CREASE_VERSION?: string }).CREASE_VERSION)
  expect(version).toMatch(/^\d+\.\d+\.\d+/)
  await expect(page.locator('#install-line')).toHaveText(`implementation("io.github.jantursky:crease:${version}")`)
})

test('the theme switch flips the page and remembers the choice', async ({ page }) => {
  await page.emulateMedia({ colorScheme: 'light' })
  await page.goto('/')
  const html = page.locator('html')
  await expect(html).toHaveAttribute('data-theme', 'light')

  const toggle = page.getByRole('button', { name: 'Switch to dark theme' })
  await toggle.click()
  await expect(html).toHaveAttribute('data-theme', 'dark')
  await expect(page.getByRole('button', { name: 'Switch to light theme' })).toBeVisible()

  await page.reload()
  await expect(html).toHaveAttribute('data-theme', 'dark')
})

test('the page follows the system theme until the visitor picks one', async ({ page }) => {
  await page.emulateMedia({ colorScheme: 'dark' })
  await page.goto('/')
  await expect(page.locator('html')).toHaveAttribute('data-theme', 'dark')
  await page.emulateMedia({ colorScheme: 'light' })
  await expect(page.locator('html')).toHaveAttribute('data-theme', 'light')
})

test('each example shows the file that runs in it, highlighted', async ({ page }) => {
  await page.goto('/')
  const example = page.locator('#example-tabletop')
  const toggle = example.getByRole('button', { name: 'View source' })
  const code = example.locator('pre code')

  await expect(code).toBeHidden()
  await toggle.click()
  await expect(example.getByRole('button', { name: 'Hide source' })).toHaveAttribute('aria-expanded', 'true')
  await expect(example.getByRole('tab', { name: 'TabletopExample.kt' })).toHaveAttribute('aria-selected', 'true')
  await expect(code).toContainText('fun TabletopExample()')
  await expect(code.locator('.tok-keyword').first()).toBeVisible()
  await expect(code.locator('.tok-string').first()).toBeVisible()

  // The imports start folded away.
  const imports = code.getByRole('button', { name: /^\+ \d+ imports$/ })
  await expect(code.locator('.imports-body')).toBeHidden()
  await imports.click()
  await expect(code.locator('.imports-body')).toBeVisible()
  await expect(code.getByRole('button', { name: /^− \d+ imports$/ })).toHaveAttribute('aria-expanded', 'true')
})

test('copy puts the source on the clipboard and confirms it', async ({ page, context, browserName }) => {
  test.skip(browserName !== 'chromium', 'clipboard permissions are Chromium only')
  await context.grantPermissions(['clipboard-read', 'clipboard-write'])
  await page.goto('/')

  // By class, not by name: the name changes to "Copied".
  const install = page.locator('.install .copy')
  await expect(install).toHaveText('Copy')
  await install.click()
  await expect(install).toHaveText('Copied')
  await expect(install).toHaveClass(/copied/)
  await expect(install.locator('svg.check')).toBeVisible()
  expect(await page.evaluate(() => navigator.clipboard.readText())).toMatch(/^implementation\("io\.github\.jantursky:crease:/)
  await expect(install).toHaveText('Copy', { timeout: 3_000 })

  const example = page.locator('#example-list-detail')
  await example.getByRole('button', { name: 'View source' }).click()
  await example.locator('.source-bar').getByRole('button', { name: 'Copy' }).click()
  const copied = await page.evaluate(() => navigator.clipboard.readText())
  expect(copied).toContain('package com.jantursky.crease.samples.web.examples')
  expect(copied).toContain('fun ListDetailExample()')
})

test('the page never scrolls sideways', async ({ page }) => {
  await page.goto('/')
  await expect(page.locator('#playground canvas')).toHaveCount(1, { timeout: 30_000 })
  const overflow = await page.evaluate(() => document.documentElement.scrollWidth - window.innerWidth)
  expect(overflow).toBeLessThanOrEqual(0)
})

for (const theme of ['light', 'dark'] as const) {
  test(`no accessibility violations in the ${theme} theme`, async ({ page }) => {
    await page.emulateMedia({ colorScheme: theme })
    await page.goto('/')
    await page.locator('#example-list-detail').getByRole('button', { name: 'View source' }).click()
    // The Compose canvases draw their own pixels, so axe checks the page around them.
    const results = await new AxeBuilder({ page }).exclude('#playground').exclude('[data-example]').analyze()
    expect(results.violations.map((v) => `${v.id}: ${v.nodes.map((n) => n.target.join(' ')).join(', ')}`)).toEqual([])
  })
}
