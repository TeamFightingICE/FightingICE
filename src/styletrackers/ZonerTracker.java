package styletrackers;

import fighting.Character;
import enumerate.State;
import java.util.LinkedList;
import enumerate.Action;

/**
 * Tracks zoner related rewards: estimated projectile damage (approximation using long-range hits),
 * distance favoring being farther than threshold, and anti-air attack counts.
 */
public class ZonerTracker implements CharacterStyleTracker {
    private int projectileScore;
    private int antiAirCount;
    private int proximityScore;
    private LinkedList<Integer> distanceHistory;
    private static final int DISTANCE_WINDOW = 60;
    private static final int DISTANCE_THRESHOLD = 210;
    private int frameCounter = 0;
    private int prevHitCount = 0;
    private Action prevAction = null;
    private State prevOpponentState = null;

    public ZonerTracker() {
        this.projectileScore = 0;
        this.antiAirCount = 0;
        this.proximityScore = 0;
        this.distanceHistory = new LinkedList<>();
    }

    /**
     * Update tracked values each frame.
     * - Adds to projectileScore when a long-range projectile attack is performed (once per action).
     * - Counts anti-air hits when opponent was in AIR state at the hit.
     * - Updates proximityScore based on being farther than DISTANCE_THRESHOLD across a time window.
     */
    @Override
    public void update(Character player, Character opponent) {
        // Track distance
        int distance = Math.abs(player.getX() - opponent.getX());
        distanceHistory.add(distance);
        if (distanceHistory.size() > DISTANCE_WINDOW) {
            distanceHistory.removeFirst();
        }

        // +10 on projectiles trigger only once per projectile
        Action action = player.getAction();
        if ((action == Action.STAND_D_DF_FA || action == Action.STAND_D_DF_FB || action == Action.STAND_D_DF_FC) && prevAction != action) {
            projectileScore += 3;
        }

        // Detect new hits and classify them
        int currentHitCount = player.getHitCount();
        if (currentHitCount > prevHitCount) {
            // Anti-air: if opponent was in AIR when hit registered
            if (opponent.getState() == State.AIR) {
                antiAirCount += 1;
            }
        }

        // Update proximityScore every window
        frameCounter++;
        if (frameCounter >= DISTANCE_WINDOW) {
            int farFrames = 0;
            for (int d : distanceHistory) {
                if (d >= DISTANCE_THRESHOLD) farFrames++;
            }
            if (farFrames >= DISTANCE_WINDOW / 2) {
                proximityScore += 2;
            } else {
                proximityScore -= 2;
            }
            distanceHistory.clear();
            frameCounter = 0;
        }

        prevHitCount = currentHitCount;
        prevOpponentState = opponent.getState();
        prevAction = action;
    }

    public int getProjectileScore() {
        return projectileScore;
    }

    public int getAntiAirCount() {
        return antiAirCount;
    }

    public int getProximityScore() {
        return proximityScore;
    }

    /**
     * Convenience reward combining tracked metrics. Positive when AI stays far (>=210)
     * and accumulates projectile/anti-air success.
     */
    public int getCombinedReward() {
        return projectileScore + antiAirCount * 2 + proximityScore;
    }

    public void normalizeScore(int actualFrames) {
        if (actualFrames == 0) return; // Avoid division by zero
        double factor = 3600.0 / actualFrames;
        projectileScore = (int)(projectileScore * factor);
        antiAirCount = (int)(antiAirCount * factor);
        proximityScore = (int)(proximityScore * factor);
    }

    @Override
    public void reset() {
        projectileScore = 0;
        antiAirCount = 0;
        proximityScore = 0;
        distanceHistory.clear();
        frameCounter = 0;
        prevHitCount = 0;
        prevOpponentState = null;
    }
}