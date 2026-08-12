import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * All drawing logic lives here, as a pure function of its arguments:
 * given the same width, height, level, peak, peakAlpha, and
 * spinnerAngle, render() always draws exactly the same image. It has
 * no Timer, no SignalGenerator, no internal state, and no randomness.
 *
 * That's deliberate, and mirrors the "separate data/logic from the
 * thing that drives it over time" pattern from earlier chapters
 * (Main vs Converter in Module 1, Inventory vs AuditLog in Module 3):
 * VuMeterPanel owns the animation and state; GaugeRenderer only knows
 * how to paint one frame. It's also what makes this class trivial to
 * test without a display - see ScreenshotTool.
 */
public class GaugeRenderer {

    private static final Color BACKGROUND = new Color(20, 20, 24);
    private static final Color TRACK_EMPTY = new Color(45, 45, 52);
    private static final Color TEXT_COLOR = new Color(230, 230, 235);
    private static final Color SPINNER_COLOR = new Color(80, 200, 255);

    public static void render(Graphics2D g2, int width, int height,
                               double level, double peak, double peakAlpha,
                               double spinnerAngleRadians) {

        // Antialiasing hints: try commenting these out and re-running
        // ScreenshotTool - the rounded bar corners and needle-thin
        // spinner line render visibly jagged without them.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(BACKGROUND);
        g2.fillRect(0, 0, width, height);

        double trackWidth = width * 0.5;
        double trackX = (width - trackWidth) / 2.0;
        double trackTop = height * 0.06;
        double trackBottom = height * 0.80;
        double trackHeight = trackBottom - trackTop;

        drawTrack(g2, trackX, trackTop, trackWidth, trackHeight);
        drawFill(g2, trackX, trackTop, trackWidth, trackBottom, trackHeight, level);
        drawPeakMarker(g2, trackX, trackWidth, trackBottom, trackHeight, peak, peakAlpha);
        drawReadout(g2, width, height, level);
        drawSpinner(g2, width, height, spinnerAngleRadians);
    }

    private static void drawTrack(Graphics2D g2, double x, double top, double w, double h) {
        g2.setColor(TRACK_EMPTY);
        g2.fill(new RoundRectangle2D.Double(x, top, w, h, 10, 10));
    }

    private static void drawFill(Graphics2D g2, double x, double top, double w,
                                  double bottom, double trackHeight, double level) {
        double fillHeight = level * trackHeight;
        double fillTop = bottom - fillHeight;
        if (fillHeight <= 0) return;

        // The gradient's two endpoints are pinned to the TRACK's fixed
        // top/bottom, not to the current fill boundary - so the color
        // at a given absolute height stays consistent as the level
        // rises and falls, the way a real VU meter's printed color
        // bands would.
        LinearGradientPaint gradient = new LinearGradientPaint(
                new Point2D.Double(x, bottom),
                new Point2D.Double(x, top),
                new float[]{0f, 0.7f, 1f},
                new Color[]{new Color(70, 200, 90), new Color(230, 200, 40), new Color(220, 60, 50)});

        g2.setPaint(gradient);
        g2.fill(new RoundRectangle2D.Double(x, fillTop, w, fillHeight, 10, 10));
    }

    private static void drawPeakMarker(Graphics2D g2, double x, double w, double bottom,
                                        double trackHeight, double peak, double peakAlpha) {
        if (peakAlpha <= 0) return;

        double y = bottom - peak * trackHeight;

        // AlphaComposite controls how much of what we draw next blends
        // with what's already on the canvas. peakAlpha fades from 1.0
        // (just hit) to 0.0 (faded out) - VuMeterPanel decays it over
        // time; this method just draws whatever value it's handed.
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) peakAlpha));
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine((int) x, (int) y, (int) (x + w), (int) y);
        g2.setComposite(original); // always restore - see the chapter's "why" note
    }

    private static void drawReadout(Graphics2D g2, int width, int height, double level) {
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, height / 20)));
        String text = Math.round(level * 100) + "%";

        // FontMetrics tells us how wide the text will actually render,
        // so it can be centered exactly instead of guessed at.
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.setColor(TEXT_COLOR);
        g2.drawString(text, (width - textWidth) / 2f, height * 0.93f);
    }

    private static void drawSpinner(Graphics2D g2, int width, int height, double angleRadians) {
        double cx = width * 0.85;
        double cy = height * 0.10;
        double radius = Math.min(width, height) * 0.06;

        // An explicit AffineTransform, built and applied by hand, rather
        // than the g2.translate()/g2.rotate() convenience methods -
        // spelled out once, here, so the class actually being
        // manipulated is visible instead of hidden behind shorthand.
        AffineTransform original = g2.getTransform();
        AffineTransform spin = new AffineTransform(original);
        spin.translate(cx, cy);
        spin.rotate(angleRadians);
        g2.setTransform(spin);

        g2.setColor(SPINNER_COLOR);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval((int) -radius, (int) -radius, (int) (radius * 2), (int) (radius * 2));
        g2.drawLine(0, 0, (int) radius, 0);

        g2.setTransform(original); // restore, so nothing drawn after this is affected
    }
}
