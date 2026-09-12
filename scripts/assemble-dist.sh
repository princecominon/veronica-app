#!/usr/bin/env bash
# Assembles the deployable static site into ./dist
set -euo pipefail
cd "$(dirname "$0")/.."
DIST=dist
rm -rf "$DIST"
mkdir -p "$DIST"
cp build/dist/js/productionExecutable/cargonex.js "$DIST/"
cp web/index.html web/styles.css web/ship.svg "$DIST/"
echo "Static site ready in ./$DIST"
