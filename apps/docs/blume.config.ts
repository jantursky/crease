import { readFileSync } from 'node:fs'

import { defineConfig } from 'blume'

const properties = readFileSync(new URL('../../gradle.properties', import.meta.url), 'utf8')
const version = /^VERSION_NAME=(.+)$/m.exec(properties)?.[1]?.trim()
if (!version) throw new Error('VERSION_NAME is missing from gradle.properties')

export default defineConfig({
  title: 'Crease',
  description:
    'Hinge angle, fold position and fold-aware layouts for Compose Multiplatform, on Android foldables and iPhone Duo.',
  logo: '/logo.svg',
  github: {
    owner: 'jantursky',
    repo: 'crease',
    dir: 'apps/docs',
  },
  navigation: {
    actions: [
      { href: 'https://crease-demo.vercel.app', label: 'Demo' },
      { href: 'https://central.sonatype.com/artifact/com.jantursky/crease', label: 'Maven Central' },
    ],
  },
  content: {
    root: 'docs',
  },
  theme: {
    accent: '#0066cc',
    radius: 'lg',
    mode: 'system',
  },
  variables: {
    version,
  },
})
