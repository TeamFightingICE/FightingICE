package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import setting.LaunchSetting;
import styletrackers.CharacterStyleTracker;
import styletrackers.GrapplerTracker;
import styletrackers.RushdownTracker;

/**
 * Logger for writing style tracker information to a CSV file.
 */
public class StyleCSVLogger {
    private PrintWriter writer;
    private boolean headerWritten = false;
    private boolean dualHeaderWritten = false;

    /**
     * Creates a new logger for the given file path.
     * @param filePath Path to the CSV file to write.
     * @throws IOException if file cannot be opened.
     */
    public StyleCSVLogger(String timeInfo) throws IOException {
        String filePath = GetCSVFilePath(timeInfo);
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
     * Writes the CSV header for two trackers (P1 and P2) if both exist.
     */
    public void writeDualHeader(CharacterStyleTracker tracker1, CharacterStyleTracker tracker2) {
        if (dualHeaderWritten) return;
        StringBuilder sb = new StringBuilder();
        if (tracker1 instanceof RushdownTracker) {
            sb.append("P1_comboCount,P1_proximityScore,P1_corneredFrames");
        } else if (tracker1 instanceof GrapplerTracker) {
            sb.append("P1_throwScore,P1_proximityScore,P1_hardKnockdownScore");
        } else {
            sb.append("P1_styleMetric1,P1_styleMetric2,P1_styleMetric3");
        }
        sb.append(",");
        if (tracker2 instanceof RushdownTracker) {
            sb.append("P2_comboCount,P2_proximityScore,P2_corneredFrames");
        } else if (tracker2 instanceof GrapplerTracker) {
            sb.append("P2_throwScore,P2_proximityScore,P2_hardKnockdownScore");
        } else {
            sb.append("P2_styleMetric1,P2_styleMetric2,P2_styleMetric3");
        }
        writer.println(sb.toString());
        dualHeaderWritten = true;
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
     * Logs both trackers' style information in a single row.
     */
    public void logBoth(CharacterStyleTracker tracker1, CharacterStyleTracker tracker2) {
        StringBuilder sb = new StringBuilder();
        if (tracker1 instanceof RushdownTracker rush) {
            sb.append(rush.getComboCount()).append(",").append(rush.getProximityScore()).append(",").append(rush.getCorneredFrames());
        } else if (tracker1 instanceof GrapplerTracker grappler) {
            sb.append(grappler.getThrowScore()).append(",").append(grappler.getProximityScore()).append(",").append(grappler.getHardKnockdownScore());
        } else {
            sb.append("0,0,0");
        }
        sb.append(",");
        if (tracker2 instanceof RushdownTracker rush2) {
            sb.append(rush2.getComboCount()).append(",").append(rush2.getProximityScore()).append(",").append(rush2.getCorneredFrames());
        } else if (tracker2 instanceof GrapplerTracker grappler2) {
            sb.append(grappler2.getThrowScore()).append(",").append(grappler2.getProximityScore()).append(",").append(grappler2.getHardKnockdownScore());
        } else {
            sb.append("0,0,0");
        }
        writer.println(sb.toString());
    }

    public String GetCSVFilePath(String timeInfo) {
        String filePath = "./log/style/style_log_"  + LaunchSetting.aiNames[0] + "_" + LaunchSetting.aiNames[1] + "_" + timeInfo + ".csv";
        return filePath;
    }

    /**
     * Flushes and closes the logger.
     */
    public void close() {
        writer.flush();
        writer.close();
    }
}
