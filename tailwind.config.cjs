/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./web/index.html",
    "./src/jsMain/kotlin/**/*.kt",
  ],
  theme: {
    extend: {
      colors: {
        hull: '#070914',
        hullMid: '#0c1130',
        hullGlow: '#1d2bd8',
        ocean: '#3b5bfe',
        oceanLight: '#6d8bff',
        oceanBright: '#7fa8ff',
        accent: '#93c5fd',
        sectionLight: '#eef1fb',
        bodyDark: '#c7cbe0',
        bodyLight: '#4b4f66',
        headlineDark: '#ffffff',
        headlineLight: '#12142b',
        btnDark: '#12142b',
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
