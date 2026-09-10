# Veronica — Kotlin/JS port

Kotlin conversion of the *Veronica — AI Decision Platform* landing page for SAIL
Shipping (originally Vite + React 18 + JSX + Framer Motion + lucide-react).

## What changed (JSX → Kotlin)

| Original (React/JSX)                 | Kotlin equivalent                                  |
| ------------------------------------ | -------------------------------------------------- |
| `App.jsx`, `main.jsx`                | `App.kt`, `main.kt` (Kotlin/JS + `kotlin-react`)   |
| `components/*.jsx`                   | `components/*.kt` (`FC { }` DSL components)        |
| Framer Motion `motion.*`             | CSS keyframes/transitions + `useInViewOnce` hook   |
| lucide-react icons                   | Hand-written inline SVG components (`Icons.kt`)    |
| Tailwind (via Vite)                  | Tailwind CLI scanning the `.kt` sources            |
| `package.json` + `vite.config.js`    | Gradle (`build.gradle.kts`, Kotlin 2.4 + webpack)  |

Everything else (markup, Tailwind classes, colours, the custom isometric ship
SVG with its SMIL animations) is preserved 1:1.

## Layout

```
src/jsMain/kotlin/veronica/
  main.kt              entry point (StrictMode + App into #root)
  App.kt               Navbar + Hero + Features + Dashboard
  core/Hooks.kt        styleOf/cls helpers + useInViewOnce scroll hook
  components/
    Navbar.kt          sticky nav, dropdowns, scroll cross-fade
    Hero.kt            headline reveal + isometric ship graphic
    Features.kt        "Built for the decisions…" 4-card grid
    Dashboard.kt       digest cards + CTA + footer
    Icons.kt           lucide-style SVG icon components + ship logo
css/input.css          Tailwind directives + custom/motion CSS (ported index.css)
web/                   index.html, styles.css (generated), ship.svg favicon
```

## Build & run

Prerequisites: JDK 17+, Node 18+, Gradle 8.x.

```bash
npm install                 # tailwindcss (CSS tooling only)
npm run css                 # generate web/styles.css from css/input.css
gradle jsBrowserDistribution# Kotlin compile + webpack bundle
bash scripts/assemble-dist.sh  # copies bundle + html + css into ./dist
python3 -m http.server 3000 -d dist   # or: npx serve dist
```

or simply: `npm run build` → serves `./dist`.

## Notes

- React 19 + Kotlin 2.4 (`org.jetbrains.kotlin-wrappers:kotlin-react:2026.9.0-19.2.8`).
- Framer-motion behaviours were re-implemented with CSS: `.char-reveal`,
  `.blur-in`, `.reveal-card(.sm)`, `.fade-in`, `.anim-*` (mount entrances),
  `.nav-enter`, `.dd-in`, `.card-bob`, `.pixel-dissolve`, `.float-bob`.
- Scroll-driven reveals use a small `useInViewOnce` hook (IntersectionObserver
  semantics via scroll listener + `getBoundingClientRect`).
- The ship wireframe keeps its native SMIL `<animate>` elements.
