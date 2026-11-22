import java.util.HashSet;
import java.util.Set;

/**
 * Tracks player progression through the 4 mansion levels:
 * LEVEL 1: The Study
 * LEVEL 2: The Secret Library
 * LEVEL 3: The Mystery Chamber
 * LEVEL 4: The Final Escape
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
     * Checks if all final escape conditions are satisfied to exit the mansion.
     * Requirements:
     * 1. Combination lock solved ("combination_lock")
     * 2. Symbol panels sequence solved ("symbol_panels")
     * 3. Master key placed / multi-part pedestal puzzle solved ("pedestal_final")
     */
    public boolean canEscapeFinalDoor(PuzzleManager pm, InventoryManager im) {
        if (pm == null) return false;

        boolean lockSolved = pm.isSolved("combination_lock");
        boolean symbolsSolved = pm.isSolved("symbol_panels");
        boolean pedestalSolved = pm.isSolved("pedestal_final") || (im != null && im.hasItemType(InventoryItem.ItemType.FINAL_KEY));

        return lockSolved && symbolsSolved && pedestalSolved;
    }

    public void reset() {
        this.currentLevel = 1;
        this.unlockedLevels.clear();
        this.completedLevels.clear();
        this.unlockedLevels.add(1);
    }
}
