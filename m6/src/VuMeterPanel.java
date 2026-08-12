import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * VuMeterPanel owns everything GaugeRenderer deliberately doesn't:
 * the SignalGenerator, the current level/peak/spinner state, and the
 * Timer that advances all of it. paintComponent() just hands the
 * current snapshot of that state to GaugeRenderer.render().
 *
 * This is genuinely minimal Swing - just enough (JPanel, paintComponent,
 * a Timer) to have somewhere to draw. Swing itself - components,
 * layouts, event handling - is Module 7's subject.
 */
public class VuMeterPanel extends JPanel {

    private static final int FRAME_DELAY_MS = 33; // ~30 fps

    private final SignalGenerator generator = new SignalGenerator();
    private final Timer timer = new Timer(FRAME_DELAY_MS, e -> tick());

    private double level = 0;
    private double peak = 0;
    private double peakAlpha = 0;
    private double spinnerAngle = 0;

    public VuMeterPanel() {
        setPreferredSize(new Dimension(180, 360));
    }

    private void tick() {
        level = generator.next();

        if (level >= peak) {
            peak = level;
            peakAlpha = 1.0; // freshly hit peak - fully visible
        } else {
            peak = Math.max(level, peak - 0.01);       // peak slowly falls
            peakAlpha = Math.max(0, peakAlpha - 0.03);  // marker slowly fades
        }

        spinnerAngle += 0.15;

        repaint(); // schedules paintComponent() - see the chapter's note on why this is safe here
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // lets JPanel clear/prepare the surface first

        // Graphics is the older, more limited drawing API; almost
        // everything actually used here (gradients, transforms, stroke
        // control) only exists on its subclass, Graphics2D. Every
        // paintComponent override in real Swing code performs this cast.
        Graphics2D g2 = (Graphics2D) g;
        GaugeRenderer.render(g2, getWidth(), getHeight(), level, peak, peakAlpha, spinnerAngle);
    }

    /**
     * Swing calls addNotify() when this component is actually added to
     * a displayable window, and removeNotify() when it's taken back out.
     * Starting/stopping the Timer here - rather than in the constructor -
     * means a VuMeterPanel that's created but never shown never wastes
     * cycles animating, and one that's removed from its window doesn't
     * keep a Timer (and its reference to this panel) alive forever.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        timer.start();
    }

    @Override
    public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }
}
