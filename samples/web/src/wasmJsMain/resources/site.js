// The theme switch, source panels, copy buttons and the version, for the Crease site. sources.js is generated
// from the example files at build time, so each panel shows the file that runs in the example above it.
(function () {
  const sources = window.CREASE_SOURCES || {}
  const version = window.CREASE_VERSION
  const root = document.documentElement

  if (version) {
    document.querySelectorAll('[data-version]').forEach((el) => (el.textContent = version))
  }

  // Theme. index.html has already set data-theme before the first paint; the Compose viewports watch it.
  const STORAGE_KEY = 'crease-theme'
  const toggle = document.querySelector('.theme-toggle')
  const savedTheme = () => {
    try {
      return localStorage.getItem(STORAGE_KEY)
    } catch {
      return null
    }
  }
  const applyTheme = (theme) => {
    root.dataset.theme = theme
    toggle?.setAttribute('aria-label', theme === 'dark' ? 'Switch to light theme' : 'Switch to dark theme')
  }
  applyTheme(root.dataset.theme === 'dark' ? 'dark' : 'light')
  toggle?.addEventListener('click', () => {
    const next = root.dataset.theme === 'dark' ? 'light' : 'dark'
    applyTheme(next)
    try {
      localStorage.setItem(STORAGE_KEY, next)
    } catch {}
  })
  // Follow the system until the visitor picks a theme.
  matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (event) => {
    if (!savedTheme()) applyTheme(event.matches ? 'dark' : 'light')
  })

  // Kotlin highlighting.
  const KEYWORDS = new Set(
    (
      'package import fun val var private internal public class enum object companion if else when ' +
      'return true false null for in is as by'
    ).split(' '),
  )

  const escape = (text) => text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')

  // Comments, strings, annotations, numbers, then words. Good enough for short Kotlin files.
  const TOKEN = /(\/\*[\s\S]*?\*\/|\/\/[^\n]*)|("(?:\\.|[^"\\])*")|(@\w+)|(\b\d[\d_]*(?:\.\d+)?f?\b)|(\b[A-Za-z_]\w*\b)/g

  const highlight = (code) => {
    let out = ''
    let last = 0
    for (const match of code.matchAll(TOKEN)) {
      out += escape(code.slice(last, match.index))
      const [text, comment, string, annotation, number, word] = match
      const end = match.index + text.length
      let kind = null
      if (comment) kind = 'comment'
      else if (string) kind = 'string'
      else if (annotation) kind = 'annotation'
      else if (number) kind = 'number'
      else if (word && KEYWORDS.has(word)) kind = 'keyword'
      else if (word && /^[A-Z]/.test(word)) kind = 'type'
      else if (word && /^\s*[({]/.test(code.slice(end, end + 2))) kind = 'call'
      out += kind ? `<span class="tok-${kind}">${escape(text)}</span>` : escape(text)
      last = end
    }
    return out + escape(code.slice(last))
  }

  // The import block folds away behind a toggle, so the code that matters is what the panel opens on.
  const render = (code) => {
    const lines = code.split('\n')
    const first = lines.findIndex((line) => line.startsWith('import '))
    if (first < 0) return highlight(code)
    let last = first
    while (last + 1 < lines.length && (lines[last + 1].startsWith('import ') || lines[last + 1] === '')) last++
    while (lines[last] === '') last--
    const count = lines.slice(first, last + 1).filter((line) => line.startsWith('import ')).length
    // The toggle and the import lines are blocks, so the text around them keeps its own line breaks.
    const before = lines.slice(0, first).join('\n') + '\n'
    const imports = lines.slice(first, last + 1).join('\n')
    const after = lines.slice(last + 1).join('\n')
    return (
      highlight(before) +
      `<span class="imports" data-collapsed="true"><button type="button" class="imports-toggle" aria-expanded="false">` +
      `+ ${count} imports</button><span class="imports-body">${highlight(imports)}</span></span>` +
      highlight(after)
    )
  }

  const CHECK = '<svg class="check" viewBox="0 0 16 16" aria-hidden="true"><path d="M3.5 8.5 6.5 11.5 12.5 4.5"/></svg>'

  const copyText = async (button, text) => {
    const label = button.dataset.label || button.textContent.trim()
    button.dataset.label = label
    try {
      await navigator.clipboard.writeText(text)
      button.classList.add('copied')
      button.innerHTML = `${CHECK}<span>Copied</span>`
    } catch {
      button.textContent = 'Copy failed'
    }
    clearTimeout(button.resetTimer)
    button.resetTimer = setTimeout(() => {
      button.classList.remove('copied')
      button.textContent = label
    }, 1600)
  }

  document.querySelectorAll('[data-copy-target]').forEach((button) => {
    button.addEventListener('click', () => {
      const target = document.getElementById(button.dataset.copyTarget)
      if (target) copyText(button, target.textContent.trim())
    })
  })

  document.querySelectorAll('.source[data-files]').forEach((panel, index) => {
    const files = panel.dataset.files
      .split(',')
      .map((name) => name.trim())
      .filter((name) => sources[name])
    if (files.length === 0) return
    const id = `source-${index}`
    let active = 0

    panel.dataset.open = 'false'
    panel.innerHTML = `
      <button type="button" class="source-toggle" aria-expanded="false" aria-controls="${id}">View source</button>
      <div class="source-panel" id="${id}">
        <div class="source-bar">
          <div class="tabs" role="tablist" aria-label="Source files"></div>
          <button type="button" class="copy">Copy</button>
        </div>
        <pre role="tabpanel" tabindex="0"><code></code></pre>
      </div>`

    const sourceToggle = panel.querySelector('.source-toggle')
    const tabs = panel.querySelector('.tabs')
    const code = panel.querySelector('code')
    const copy = panel.querySelector('.copy')

    const show = (i) => {
      active = i
      code.innerHTML = render(sources[files[i]])
      tabs.querySelectorAll('button').forEach((tab, j) => tab.setAttribute('aria-selected', String(i === j)))
    }

    files.forEach((name, i) => {
      const tab = document.createElement('button')
      tab.type = 'button'
      tab.role = 'tab'
      tab.textContent = name
      tab.addEventListener('click', () => show(i))
      tabs.append(tab)
    })

    code.addEventListener('click', (event) => {
      const button = event.target.closest('.imports-toggle')
      if (!button) return
      const block = button.parentElement
      const collapsed = block.dataset.collapsed !== 'true'
      block.dataset.collapsed = String(collapsed)
      button.setAttribute('aria-expanded', String(!collapsed))
      button.textContent = button.textContent.replace(/^[+−]/, collapsed ? '+' : '−')
    })

    sourceToggle.addEventListener('click', () => {
      const open = panel.dataset.open !== 'true'
      panel.dataset.open = String(open)
      sourceToggle.setAttribute('aria-expanded', String(open))
      sourceToggle.textContent = open ? 'Hide source' : 'View source'
    })
    copy.addEventListener('click', () => copyText(copy, sources[files[active]]))
    show(0)
  })
})()
