#!/usr/bin/env bash
# Membuat paket Windows Examify: installer (.exe, butuh WiX Toolset) dan versi portable (.zip).
# Pakai: JAVA_HOME=<JDK 21+> bash packaging/build-windows.sh   (SKIP_INSTALLER=1 untuk portable saja)
set -euo pipefail
cd "$(dirname "$0")/.."

VERSION=$(sed -n 's:.*<version>\(.*\)</version>.*:\1:p' pom.xml | head -1)
JBIN="${JAVA_HOME:?Set JAVA_HOME ke JDK 21+}/bin"
DIST=target/dist
./mvnw -q -B clean package
mkdir -p "$DIST" target/input target/fx
./mvnw -q -B dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/libs
cp target/libs/*-win.jar target/fx/
cp target/examify-"$VERSION".jar target/libs/jna-*.jar target/input/

# Runtime ramping: modul JDK + JavaFX (module path), aplikasi & JNA di classpath
"$JBIN/jlink" --module-path target/fx \
  --add-modules java.base,java.desktop,java.logging,java.xml,java.net.http,java.scripting,jdk.unsupported,jdk.jsobject,javafx.controls,javafx.fxml,javafx.web,javafx.media \
  --strip-debug --no-header-files --no-man-pages --compress zip-6 --output target/runtime

COMMON=(--name Examify --app-version "$VERSION" --vendor "Kelompok 8"
  --description "E-Learning & Computer-Based Test (CBT)"
  --icon packaging/examify.ico --input target/input
  --main-jar examify-"$VERSION".jar --main-class com.examify.app.Launcher
  --runtime-image target/runtime --app-content data
  --java-options '-Dexamify.home=$ROOTDIR'
  --java-options '--enable-native-access=ALL-UNNAMED,javafx.graphics,javafx.web')

# Portable (app-image -> zip)
"$JBIN/jpackage" --type app-image --dest target/appimage "${COMMON[@]}"
(cd target/appimage && /c/Windows/System32/tar.exe -a -cf "../dist/Examify-$VERSION-windows-x64-portable.zip" Examify)

# Installer (per-user, folder data tetap bisa ditulis)
if [ "${SKIP_INSTALLER:-0}" = 1 ]; then ls -la "$DIST"; exit 0; fi
"$JBIN/jpackage" --type exe --dest "$DIST" "${COMMON[@]}" \
  --win-per-user-install --win-dir-chooser --win-menu --win-menu-group Examify --win-shortcut \
  --win-upgrade-uuid 6f1c2b8e-4d3a-4e7b-9a51-3c0e8f2d7a10
mv "$DIST/Examify-$VERSION.exe" "$DIST/Examify-$VERSION-windows-x64-setup.exe"
ls -la "$DIST"
