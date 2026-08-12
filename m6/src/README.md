# Module 6 — Live 2D VU Meter Canvas

A NEW, separate project thread from the Inventory app (Modules 1-5).
This one starts the Java2D / Swing graphics track, which merges back
into the Inventory project in Module 7.

## Build & Run
```
javac *.java
java Main                 # live animated window
java ScreenshotTool       # headless: writes vu-meter-preview.png, no window/display needed
```
Requires JDK 17+.

## Files
- SignalGenerator.java  – simulated audio level source
- GaugeRenderer.java    – pure rendering function (shapes, gradients, transparency, transform, text)
- VuMeterPanel.java     – owns the Timer + animation state; delegates drawing to GaugeRenderer
- Main.java             – minimal JFrame host
- ScreenshotTool.java   – headless BufferedImage rendering + PNG export + manual pixel manipulation demo

See the accompanying Word document (Module6-Java-2D-Graphics.docx)
for the full theory writeup and design rationale.
