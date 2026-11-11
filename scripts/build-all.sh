#!/usr/bin/env bash
# Build ZeroChurn + InfoForge for every supported Minecraft target.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

mkdir -p dist

resolve_api() {
  case "$1" in
    1.21.11) echo "0.141.6+1.21.11" ;;
    26.1) echo "0.145.1+26.1" ;;
    26.2) echo "0.161.0+26.2" ;;
    26.3) echo "0.166.0+26.3" ;;
    *)
      echo "Unsupported Minecraft version: $1" >&2
      return 1
      ;;
  esac
}

build_26() {
  local mc="$1"
  local api
  api="$(resolve_api "$mc")"
  local ver="1.2.0+${mc}"

  export JAVA_HOME="${ROOT}/.jdks/jdk-25.0.5+8/Contents/Home"
  export PATH="$JAVA_HOME/bin:$PATH"

  echo
  echo "===== Building ZeroChurn + InfoForge ${ver} (26.x toolchain) ====="
  ./gradlew --no-daemon clean :zerochurn:jar :infoforge:jar \
    -Pminecraft_version="$mc" \
    -Pfabric_api_version="$api" \
    -Pfull_version="$ver" \
    -Pmod_version="1.2.0"

  cp -f "zerochurn/build/libs/zerochurn-${ver}.jar" "dist/zerochurn-${ver}.jar"
  cp -f "infoforge/build/libs/infoforge-${ver}.jar" "dist/infoforge-${ver}.jar"
  echo "Wrote dist/zerochurn-${ver}.jar"
  echo "Wrote dist/infoforge-${ver}.jar"
}

build_12111() {
  local ver="1.2.0+1.21.11"
  export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
  export PATH="$JAVA_HOME/bin:$PATH"

  echo
  echo "===== Building ZeroChurn + InfoForge ${ver} (1.21.11 remap toolchain) ====="
  ./gradlew --no-daemon -p legacy121 clean :zerochurn:remapJar :infoforge:remapJar \
    -Pfull_version="$ver" \
    -Pmod_version="1.2.0"

  cp -f "legacy121/zerochurn/build/libs/zerochurn-${ver}.jar" "dist/zerochurn-${ver}.jar"
  cp -f "legacy121/infoforge/build/libs/infoforge-${ver}.jar" "dist/infoforge-${ver}.jar"
  echo "Wrote dist/zerochurn-${ver}.jar"
  echo "Wrote dist/infoforge-${ver}.jar"
}

build_one() {
  case "$1" in
    1.21.11) build_12111 ;;
    26.1|26.2|26.3) build_26 "$1" ;;
    *)
      echo "Unsupported Minecraft version: $1" >&2
      exit 1
      ;;
  esac
}

TARGETS=("1.21.11" "26.1" "26.2" "26.3")
if [[ $# -gt 0 ]]; then
  TARGETS=("$@")
fi

for mc in "${TARGETS[@]}"; do
  build_one "$mc"
done

echo
echo "===== Dist artifacts ====="
ls -la dist/zerochurn-1.2.0+{1.21.11,26.1,26.2,26.3}.jar dist/infoforge-1.2.0+{1.21.11,26.1,26.2,26.3}.jar 2>/dev/null \
  || ls -la dist/*1.2.0*.jar
