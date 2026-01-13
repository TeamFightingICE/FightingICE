package styletrackers;

import fighting.Character;
import java.util.LinkedList;
import setting.GameSetting;

/**
 * Tracks rushdown related rewards: combo count, distance, and cornering.
 */
public class RushdownTracker implements CharacterStyleTracker {
    private int comboCount;
    private int prevHitCount = 0;
    private int corneredFrames;
    private int proximityScore;
    private LinkedList<Integer> distanceHistory;
    private static final int DISTANCE_WINDOW = 60;
    private static final int PROXIMITY_THRESHOLD = 210;
    private int frameCounter = 0;

    /**
     * Constructor to initialize the rushdown tracker.
     */
    public RushdownTracker() {
        this.comboCount = 0;
        this.corneredFrames = 0;
        this.proximityScore = 0;
        this.distanceHistory = new LinkedList<>();
    }

    /**
     * Call this every frame to update stats.
     * @param player The rushdown character
     * @param opponent The opponent character
     */
    @Override
    public void update(Character player, Character opponent) {
        // Update combo count 10 points per 4+ hit combo
        if (player.getHitCount() >= 4 && prevHitCount < 4) {
            comboCount += 3;
        }
        prevHitCount = player.getHitCount();

        // Track distance for proximity reward/penalty 
        int distance = Math.abs(player.getX() - opponent.getX());
        distanceHistory.add(distance);
        if (distanceHistory.size() > DISTANCE_WINDOW) {
            distanceHistory.removeFirst();
        }

        // Update proximity score
        frameCounter++;
        if (frameCounter >= DISTANCE_WINDOW) {
            int proximityFrames = 0;
            for (int d : distanceHistory) {
                if (d <= PROXIMITY_THRESHOLD) proximityFrames++;
            }
            if (proximityFrames >= DISTANCE_WINDOW / 2) {
                proximityScore += 2; 
            } else {
                proximityScore -= 2; 
            }
            distanceHistory.clear();
            frameCounter = 0;
        }

        // Update cornered frames
        if (opponent.getHitAreaLeft() <= 0 || opponent.getHitAreaRight() >= GameSetting.STAGE_WIDTH) {
            corneredFrames++;
        }
    }

    /**
     * Get the current combo count.
     * @return combo count
     */
    public int getComboCount() {
        return comboCount;
    }

    /**
     * Get the current proximity score.
     * @return proximity score
     */
    public int getProximityScore() {
        return proximityScore;
    }

    /**
     * Get the number of frames the opponent has been cornered.
     * @return cornered frames
     */
    public int getCorneredFrames() {
        return corneredFrames;
    }

    public void normalizeScore(int actualFrames) {
        if (actualFrames == 0) return; // Avoid division by zero
        double factor = 3600.0 / actualFrames;
        comboCount = (int)(comboCount * factor);
        proximityScore = (int)(proximityScore * factor);
        corneredFrames = (int)(corneredFrames * factor);
    }

    /**
     * Reset all tracked stats.
     */
    @Override
    public void reset() {
        comboCount = 0;
        corneredFrames = 0;
        proximityScore = 0;
        distanceHistory.clear();
        frameCounter = 0;
        prevHitCount = 0;
    }
}