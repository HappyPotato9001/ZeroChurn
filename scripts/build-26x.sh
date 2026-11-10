#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

export JAVA_HOME="${JAVA_HOME:-$ROOT/.jdks/jdk-25.0.5+8/Contents/Home}"
export PATH="$JAVA_HOME/bin:$PATH"

mkdir -p dist

build_one() {
  local mc="$1"
  local api="$2"
  local ver="1.0.0+${mc}"
  echo "===== Building ZeroChurn ${ver} ====="
  ./gradlew clean jar \
    -Pminecraft_version="$mc" \
    -Pfabric_api_version="$api" \
    -Pversion="$ver"
  cp -f "build/libs/zerochurn-${ver}.jar" "dist/zerochurn-${ver}.jar"
  echo "Wrote dist/zerochurn-${ver}.jar"
}

build_one "26.1" "0.145.1+26.1"
build_one "26.2" "0.161.0+26.2"
build_one "26.3" "0.166.0+26.3"
# Stable 26.4 is not released yet; build against the latest snapshot.
build_one "26.4-snapshot-7" "0.166.0+26.4"

ls -la dist/zerochurn-1.0.0+26.*.jar
