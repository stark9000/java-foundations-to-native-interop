import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing components must only be created/touched on the Event
        // Dispatch Thread (EDT) - invokeLater schedules this to run
        // there instead of on the thread that ran main(). Module 7
        // covers the EDT in depth; this is a preview.
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("VU Meter (Module 6 Project)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new VuMeterPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
