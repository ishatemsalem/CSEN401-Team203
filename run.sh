#!/usr/bin/env bash
# Run DooR DasH (JavaFX) from the project root. Requires JDK 17+ (course: JDK 21; JDK 25 works with JavaFX 25).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home 2>/dev/null || true)}"
if [[ -z "${JAVA_HOME}" || ! -x "${JAVA_HOME}/bin/java" ]]; then
  echo "Set JAVA_HOME to a JDK install (e.g. export JAVA_HOME=\$(/usr/libexec/java_home -v 25))" >&2
  exit 1
fi

# OpenJFX SDK — extract under tools/ (e.g. tools/javafx-sdk-25.0.2/)
FX_SDK="$(ls -d "${ROOT}/tools"/javafx-sdk-* 2>/dev/null | head -1)"
if [[ -z "${FX_SDK}" || ! -d "${FX_SDK}/lib" ]]; then
  echo "JavaFX SDK not found under ${ROOT}/tools/javafx-sdk-*/" >&2
  echo "Download from https://openjfx.io/ and extract so lib/ contains javafx.controls.jar" >&2
  exit 1
fi

FX_LIB="${FX_SDK}/lib"
OUT="${ROOT}/out"
MODULES="javafx.controls,javafx.graphics,javafx.media,javafx.base"

mkdir -p "${OUT}"

echo "Compiling (skipping game.tests — those need JUnit)..."
# shellcheck disable=SC2046
"${JAVA_HOME}/bin/javac" \
  -d "${OUT}" \
  --release 21 \
  --module-path "${FX_LIB}" \
  --add-modules "${MODULES}" \
  $(find "${ROOT}/src" -name "*.java" ! -path "*/game/tests/*")

echo "Starting JavaFX application..."
exec "${JAVA_HOME}/bin/java" \
  --module-path "${FX_LIB}" \
  --add-modules "${MODULES}" \
  --enable-native-access javafx.graphics,javafx.media \
  --class-path "${OUT}" \
  game.gui.Main "$@"
