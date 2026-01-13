package styletrackers;

import enumerate.Action;
import enumerate.State;
import fighting.Character;
import java.util.LinkedList;

/**
 * Tracks grappler related rewards: throws, proximity, and hard knockdowns.
 */
public class GrapplerTracker implements CharacterStyleTracker {
    private int throwScore;
    private int proximityScore;
    private int hardKnockdownScore;
    private LinkedList<Integer> distanceHistory;
    private static final int DISTANCE_WINDOW = 60;
    private static final int PROXIMITY_THRESHOLD = 240;
    private int frameCounter = 0;
    private int prevHitCount = 0;
    private State prevOpponentState = null;

    /**
     * Constructor to initialize the grappler tracker.
     */
    public GrapplerTracker() {
        this.throwScore = 0;
        this.proximityScore = 0;
        this.hardKnockdownScore = 0;
        this.distanceHistory = new LinkedList<>();
    }

    /**
     * Call this every frame to update stats.
     * @param player The grappler character
     * @param opponent The opponent character
     */
    @Override
    public void update(Character player, Character opponent) {
        // +10 on throws trigger only once per throw 
        boolean isThrowAction = (player.getAction() == Action.THROW_A || player.getAction() == Action.THROW_B);
        if (isThrowAction && player.getHitCount() > prevHitCount) {
            throwScore += 3;
        }

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

        // +1 on hard knockdowns (trigger only once per transition into DOWN)
        if (opponent.getState() == State.DOWN && (prevOpponentState != State.DOWN)) {
            hardKnockdownScore += 1;
        }

        // Update previous values for next frame
        prevHitCount = player.getHitCount();
        prevOpponentState = opponent.getState();
    }

    /**
     * Get the current throw score.
     * @return throw score
     */
    public int getThrowScore() {
        return throwScore;
    }

    /**
     * Get the current proximity score.
     * @return proximity score
     */
    public int getProximityScore() {
        return proximityScore;
    }

    /**
     * Get the current hard knockdown score.
     * @return hard knockdown score
     */
    public int getHardKnockdownScore() {
        return hardKnockdownScore;
    }

    public void normalizeScore(int actualFrames) {
        if (actualFrames == 0) return; // Avoid division by zero
        double factor = 3600.0 / actualFrames;
        throwScore = (int)(throwScore * factor);
        proximityScore = (int)(proximityScore * factor);
        hardKnockdownScore = (int)(hardKnockdownScore * factor);
    }

    /**
     * Reset all tracked stats.
     */
    @Override
    public void reset() {
        throwScore = 0;
        proximityScore = 0;
        hardKnockdownScore = 0;
        distanceHistory.clear();
        frameCounter = 0;
        prevHitCount = 0;
        prevOpponentState = null;
    }
}