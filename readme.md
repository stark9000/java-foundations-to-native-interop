# Java: Foundations to Native Interop — Course Outline

### (Learn-by-Doing Edition — every module ships a project)

A progressive course taking a learner from zero to comfortable with Swing 2D graphics and JNI native interop. Each module pairs theory with a hands-on build, and every project's output feeds into the next module where possible, so the course accumulates into the Capstone rather than producing disposable exercises.

---

## Module 0 — Setup & Tooling
**Theory:** JDK vs JRE, installing a JDK (Temurin/OpenJDK), IDE setup (NetBeans/IntelliJ/VS Code), `javac`/`java`, classpath basics, build tools overview (Maven vs Gradle vs plain javac).

**Project — "First Build Pipeline":** Set up a project skeleton (chosen build tool) that just prints a banner and the running JVM's version/OS info to console. Trivial on purpose — the goal is a working, repeatable build you'll keep extending all course long, not a throwaway "Hello World."

## Module 1 — Core Language Fundamentals
**Theory:** Variables, primitive types, casting, operators (incl. bitwise), control flow, arrays, methods/overloading/varargs, Strings.

**Project — "CLI Unit Converter":** A command-line tool converting between units (temperature, distance, bitwise-flag decoder for a mock permissions byte). Exercises control flow, arrays, string parsing, and bit operations in one small tool.

## Module 2 — Object-Oriented Programming
**Theory:** Classes/objects/constructors, encapsulation, inheritance, polymorphism, abstract classes vs interfaces, `equals`/`hashCode`/`toString`, records, enums, nested classes.

**Project — "Inventory System":** Model a small inventory (e.g., electronics parts — resistors, ICs, relays) with a class hierarchy: abstract `Component`, subclasses with different behavior, an enum for category, proper `equals`/`hashCode` for dedup, and a console menu to add/list/search items.

## Module 3 — Core Libraries & Collections
**Theory:** Collections framework, generics, iterators, `Comparable`/`Comparator`, exceptions, autoboxing, `java.time`.

**Project — "Inventory System v2":** Extend Module 2's project: back it with real collections (`Map` for lookup, `List` for ordering), add custom sorting via `Comparator`, add proper exception handling for bad input, and log timestamped entries with `java.time`.

## Module 4 — Functional & Stream-Style Java
**Theory:** Lambdas, functional interfaces, method references, Streams (map/filter/reduce/collect), `Optional`.

**Project — "Inventory Reports":** Add a reporting feature to the inventory app using Streams — group components by category, compute totals/averages, filter low-stock items — replacing manual loops from v2 with stream pipelines.

## Module 5 — I/O, Concurrency & Serialization
**Theory:** File I/O (`java.nio.file`), serialization basics, threads/`Runnable`/`ExecutorService`, synchronization basics, `SwingWorker` preview.

**Project — "Persistent Inventory + Background Scan":** Save/load the inventory to a file (JSON or simple custom format — avoid default Java serialization, discuss why). Add a simulated "background scan" task (e.g., a fake slow inventory audit) run on a separate thread with a progress callback, laying groundwork for `SwingWorker` in Module 7.

## Module 6 — Java 2D Graphics
**Theory:** `Graphics` vs `Graphics2D`, the rendering pipeline, shapes/paths/strokes/fills, color/gradients/transparency, transforms, text rendering, images/`BufferedImage`, animation loop patterns, rendering hints.

**Project — "Live 2D VU Meter Canvas":** A standalone `JPanel`-based canvas rendering an animated VU-meter-style bar or needle gauge driven by a fake signal generator (sine wave/random walk), using gradients, antialiasing, and a proper animation loop. Directly reusable in your Capstone if you go the audio-visualizer route.

## Module 7 — Swing (Desktop UI)
**Theory:** EDT threading model, core components, layout managers, event handling, custom-painted components, menus/dialogs, `JTable`/`JTree`, Look and Feel/theming, packaging basics.

**Project — "Inventory Desktop App":** Wrap the Module 3–5 inventory logic in a full Swing GUI: `JTable` for listing, forms for add/edit, `JFileChooser` for save/load, a dark theme, and the Module 6 VU-meter canvas embedded as a live "activity" widget — merging the two project tracks into one app.

## Module 8 — JVM Internals (Conceptual Bridge to Native Code)
**Theory:** Class loading, bytecode basics, memory model (stack/heap/GC), why JNI exists, `System.loadLibrary`, platform-dependent native lib naming.

**Project — "Bytecode Detective":** Compile a small class, inspect it with `javap -c`, and predict/verify behavior (e.g., how autoboxing or a `switch` compiles). No native code yet — this is a short investigative exercise to demystify what's "under" the JVM before crossing into JNI.

## Module 9 — JNI (Java Native Interface)
**Theory:** JNI architecture, declaring `native` methods, header generation, `JNIEnv*`/types/signatures, passing primitives/strings/arrays/objects, callbacks into Java, reference management, exceptions across the boundary, native build toolchain, debugging, JNI vs JNA vs FFM/Panama.

**Project — "Native Audio Level Reader":** Write a native method (C, built with MinGW-w64/gcc) that reads a real signal — e.g., a Win32 API call for system volume/mic level, or a simple native computation (fast array processing) — and feeds it into the Module 6 VU-meter canvas in place of the fake signal generator. This is the moment the whole course converges: native code driving a live Swing-rendered UI.

## Module 10 — Packaging & Distribution
**Theory:** `jlink`, `jpackage`, bundling native libraries alongside a JAR, cross-platform native lib paths.

**Project — "Ship It":** Package the Module 9 app (Swing UI + native lib) into a native Windows executable/installer — a scaled-down version of what Wraptor already does, giving you a natural point to compare your own tool's approach against `jpackage`.

## Capstone Project
By this point the modules have already converged into one running app: **a Swing desktop application with a live-animated 2D-rendered gauge, driven by real data pulled through a native library call, packaged as a standalone executable.**

Options to extend it as a true capstone:
- Swap the native data source for something richer (real audio input via a native API, or system hardware stats)
- Add persistence/history (reusing Module 5's file I/O) so the app logs sessions over time
- Polish theming/UX using everything from Module 7

Because it's built incrementally rather than assigned cold, the capstone should feel like "finishing" a project you've been building the whole course, not starting a new one.

---

### Design principle behind this structure
Two project **threads** run in parallel and merge at Module 7:
1. **Inventory app** (Modules 1–5) — teaches core language/OOP/collections/IO without graphics getting in the way.
2. **VU-meter canvas** (Module 6 onward) — teaches graphics/animation/native-interop as a single visual thread that's satisfying to watch progress.

They merge into one Swing app at Module 7, and native code takes over the meter's data source at Module 9. This avoids the common course pitfall of a new disconnected toy project every chapter — everything either builds toward or plugs into the final app.

---

### Notes on sequencing
- Modules 0–5 are standard "any Java course" material — can be compressed or skipped entirely if the audience already knows Java.
- Module 6 (2D Graphics) is placed *before* Swing deliberately, since Swing's custom painting depends on understanding `Graphics2D` first.
- Module 8 exists purely as a conceptual on-ramp so JNI doesn't feel like a black box — it's short, not a full "JVM internals" course.
- If the audience is more advanced, Modules 0–4 can become a single "fast review" module, freeing time to go deeper on JNI edge cases (threading with attached/detached native threads, JNI in multi-module native builds, etc.).
