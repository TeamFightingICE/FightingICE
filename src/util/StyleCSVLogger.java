package util;

import styletrackers.CharacterStyleTracker;
import styletrackers.RushdownTracker;
import styletrackers.GrapplerTracker;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logger for writing style tracker information to a CSV file.
 */
public class StyleCSVLogger {
    private PrintWriter writer;
    private boolean headerWritten = false;

    /**
     * Creates a new logger for the given file path.
     * @param filePath Path to the CSV file to write.
     * @throws IOException if file cannot be opened.
     */
    public StyleCSVLogger(String filePath) throws IOException {
        this.writer = new PrintWriter(new FileWriter(filePath, false));
    }

    /**
     * Writes the CSV header based on the tracker type.
     * @param tracker The style tracker instance.
     */
    public void writeHeader(CharacterStyleTracker tracker) {
        if (headerWritten) return;
        if (tracker instanceof RushdownTracker) {
            writer.println("comboCount,proximityScore,corneredFrames");
        } else if (tracker instanceof GrapplerTracker) {
            writer.println("throwScore,proximityScore,hardKnockdownScore");
        } else {
            writer.println("styleMetric1,styleMetric2,styleMetric3");
        }
        headerWritten = true;
    }

    /**
     * Writes the current style information to the CSV.
     * @param tracker The style tracker instance.
     */
    public void log(CharacterStyleTracker tracker) {
        if (tracker instanceof RushdownTracker rush) {
            writer.printf("%d,%d,%d\n", rush.getComboCount(), rush.getProximityScore(), rush.getCorneredFrames());
        } else if (tracker instanceof GrapplerTracker grappler) {
            writer.printf("%d,%d,%d\n", grappler.getThrowScore(), grappler.getProximityScore(), grappler.getHardKnockdownScore());
        } else {
            writer.println("0,0,0");
        }
    }

    /**
     * Flushes and closes the logger.
     */
    public void close() {
        writer.flush();
        writer.close();
    }
}
