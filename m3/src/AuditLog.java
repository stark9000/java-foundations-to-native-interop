import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A tiny append-only, timestamped log file. It implements Closeable,
 * which means it can be used in a try-with-resources statement (see
 * Main) - the writer is guaranteed to be closed and flushed even if
 * an exception is thrown partway through the program.
 */
public class AuditLog implements Closeable {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BufferedWriter writer;

    public AuditLog(Path path) throws IOException {
        writer = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void log(String message) {
        String line = "[" + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "] " + message;
        try {
            writer.write(line);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            // A logger failing shouldn't crash the whole application -
            // we report the problem and keep going, rather than letting
            // an I/O hiccup on the log file take down the inventory app.
            System.err.println("Warning: failed to write audit log entry: " + e.getMessage());
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
