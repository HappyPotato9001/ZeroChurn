#!/usr/bin/env bash
# Convenience wrapper: build one Minecraft target (default 26.3).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
exec "$ROOT/scripts/build-all.sh" "${1:-26.3}"
