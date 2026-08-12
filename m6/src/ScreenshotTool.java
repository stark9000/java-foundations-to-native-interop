import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Renders the gauge to an in-memory BufferedImage and saves it as a
 * PNG - no JFrame, no display, no event loop required. This works
 * specifically because GaugeRenderer is a pure function that only
 * needs a Graphics2D to draw into; a BufferedImage's Graphics2D works
 * exactly as well as a live window's.
 *
 * Genuinely useful on its own (headless snapshot generation, e.g. for
 * documentation or a CI pipeline with no display attached) - and also
 * how this chapter's renderer was checked for correctness while it
 * was being written.
 */
public class ScreenshotTool {

    public static void main(String[] args) throws IOException {
        // Three panels side by side at different levels, so a single
        // image shows the low/mid/clip color zones and peak marker at
        // once instead of needing three separate runs.
        BufferedImage composite = renderPanelStrip(
                new double[]{0.20, 0.65, 0.95},
                new double[]{0.30, 0.70, 0.98}
        );
        Path outPath = Path.of("vu-meter-preview.png");
        ImageIO.write(composite, "png", outPath.toFile());
        System.out.println("Saved " + outPath.toAbsolutePath());

        BufferedImage darkened = darken(composite, 0.5);
        Path darkPath = Path.of("vu-meter-preview-dark.png");
        ImageIO.write(darkened, "png", darkPath.toFile());
        System.out.println("Saved " + darkPath.toAbsolutePath());
    }

    private static BufferedImage renderPanelStrip(double[] levels, double[] peaks) {
        int panelW = 180, panelH = 360;
        BufferedImage image = new BufferedImage(panelW * levels.length, panelH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            for (int i = 0; i < levels.length; i++) {
                Graphics2D panelG2 = (Graphics2D) g2.create(i * panelW, 0, panelW, panelH);
                try {
                    GaugeRenderer.render(panelG2, panelW, panelH, levels[i], peaks[i], 1.0, Math.PI / 4);
                } finally {
                    panelG2.dispose(); // each g2.create() must be paired with dispose()
                }
            }
        } finally {
            g2.dispose();
        }
        return image;
    }

    /**
     * Manual pixel manipulation: reads and rewrites every pixel by
     * hand via getRGB()/setRGB(), rather than drawing with Graphics2D
     * at all. This is the lower-level sibling of everything
     * GaugeRenderer does - useful for effects (like a simple
     * brightness scale) that aren't naturally expressed as shapes.
     */
    private static BufferedImage darken(BufferedImage src, double factor) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int argb = src.getRGB(x, y);

                // Unpacking a packed 32-bit ARGB pixel with shifts and
                // masks - the exact bitwise technique from Module 1's
                // permission-flag decoder, applied to pixels instead of
                // permission bits.
                int a = (argb >> 24) & 0xFF;
                int r = (int) (((argb >> 16) & 0xFF) * factor);
                int g = (int) (((argb >> 8) & 0xFF) * factor);
                int b = (int) ((argb & 0xFF) * factor);

                int packed = (a << 24) | (r << 16) | (g << 8) | b;
                out.setRGB(x, y, packed);
            }
        }
        return out;
    }
}
