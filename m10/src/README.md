# Module 10 — Ship It (Packaging & Distribution)

The finale. Before packaging anything, this wires Module 9's real
NativeSignalGenerator into Module 7's Swing app (replacing the
decorative SignalGenerator), via one new interface, LevelSource.
Then: jdeps -> jlink -> jpackage, producing a single self-contained
native application - no separate JDK required to run it.

## Build & Run
```
./build.sh
./build/dist/InventoryApp/bin/InventoryApp
```
Requires gcc and a full JDK (not JRE-only - jlink/jpackage/jdeps all
ship with the JDK). On Windows/macOS, adjust the native compiler and
JNI include paths per Module 9's build toolchain section.

## What changed from Module 7
- LevelSource.java       – NEW: the one-method interface
- SignalGenerator.java   – now implements LevelSource (no other change)
- VuMeterPanel.java      – now accepts a LevelSource via constructor (defaults to SignalGenerator)
- InventoryPanel.java    – Activity panel now uses `new VuMeterPanel(new NativeSignalGenerator())`
- NativeSignalGenerator.java – carried over from Module 9, now implements LevelSource
- native/nativebridge.c  – trimmed to just the one native method this app uses

Every other file is unchanged from Module 7.

See the accompanying Word document (Module10-Packaging-Distribution.docx)
for the full theory writeup, including a real jpackage path-resolution
gotcha this chapter's own testing ran into and fixed.
