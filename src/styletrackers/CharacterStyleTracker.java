package styletrackers;

import fighting.Character;

/**
 * Interface for tracking player style metrics.
 */
public interface CharacterStyleTracker {
    /**
     * Update the tracker with the current state of the player and opponent.
     */
    void update(Character player, Character opponent);

    /**
     * Reset the tracker to its initial state.
     */
    void reset();

}