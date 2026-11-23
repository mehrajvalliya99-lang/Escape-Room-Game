import java.util.HashSet;
import java.util.Set;

/**
 * Tracks player progression through the 4 facility levels in
 * "THE LAST EXPERIMENT — A Detective Mystery Adventure":
 *
 * LEVEL 1: The Detective's Study
 * LEVEL 2: The Forgotten Library
 * LEVEL 3: The Abandoned Laboratory
 * LEVEL 4: The Ancient Vault
 *
 * Controls room gating, prevents skipping required objectives,
 * tracks completed rooms, and evaluates final escape conditions.
 */
public class LevelManager {

    public static final int TOTAL_LEVELS = 4;

    private int currentLevel;
    private final Set<Integer> unlockedLevels;
    private final Set<Integer> completedLevels;

    public LevelManager() {
        this.currentLevel = 1;
        this.unlockedLevels = new HashSet<>();
        this.completedLevels = new HashSet<>();

        // Level 1 starts unlocked
        this.unlockedLevels.add(1);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            this.currentLevel = level;
        }
    }

    public boolean isLevelUnlocked(int level) {
        return unlockedLevels.contains(level);
    }

    public void unlockLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            unlockedLevels.add(level);
        }
    }

    public boolean isLevelCompleted(int level) {
        return completedLevels.contains(level);
    }

    public void completeLevel(int level) {
        if (level >= 1 && level <= TOTAL_LEVELS) {
            completedLevels.add(level);
            // Automatically unlock subsequent level
            if (level + 1 <= TOTAL_LEVELS) {
                unlockedLevels.add(level + 1);
            }
        }
    }

    public int getCompletedRoomsCount() {
        return completedLevels.size();
    }

    /**
     * Returns formatted completed rooms string, e.g. "3/4".
     */
    public String getCompletedRoomsString() {
        return completedLevels.size() + "/" + TOTAL_LEVELS;
    }

    /**
     * Checks if player can transition to a target level.
     */
    public boolean canEnterLevel(int level) {
        return isLevelUnlocked(level);
    }

    /**
     * Checks if all final escape conditions are satisfied to escape the facility.
     * Requirements:
     * 1. Multi-Stage Vault Combination Lock solved ("vault_combination" or "combination_lock")
     * 2. Containment Relays sequence solved ("containment_relays" or "symbol_panels")
     * 3. Artifact arrangement / Vault Key placed on Pedestal ("artifact_arrangement" or "pedestal_final")
     */
    public boolean canEscapeFinalDoor(PuzzleManager pm, InventoryManager im) {
        if (pm == null) return false;

        boolean lockSolved = pm.isSolved("vault_combination") || pm.isSolved("combination_lock");
        boolean relaysSolved = pm.isSolved("containment_relays") || pm.isSolved("symbol_panels");
        boolean artifactSolved = pm.isSolved("artifact_arrangement")
                              || pm.isSolved("pedestal_final")
                              || (im != null && (im.hasItemType(InventoryItem.ItemType.VAULT_KEY) || im.hasItemType(InventoryItem.ItemType.FINAL_KEY)));

        return lockSolved && relaysSolved && artifactSolved;
    }

    public void reset() {
        this.currentLevel = 1;
        this.unlockedLevels.clear();
        this.completedLevels.clear();
        this.unlockedLevels.add(1);
    }
}
