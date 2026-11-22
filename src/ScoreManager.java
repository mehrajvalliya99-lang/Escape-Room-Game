import java.util.HashSet;
import java.util.Set;

/**
 * Manages player scoring, puzzle points, item discovery rewards,
 * level completion bonuses, and hint penalties.
 * Prevents negative scores and duplicate point exploits.
 */
public class ScoreManager {

    public static final int PUZZLE_SOLVE_POINTS = 100;
    public static final int ITEM_DISCOVERY_POINTS = 50;
    public static final int LEVEL_COMPLETION_POINTS = 150;

    private int score;
    private final Set<String> scoredPuzzles;
    private final Set<String> scoredItems;
    private final Set<Integer> scoredLevels;

    public ScoreManager() {
        this.score = 0;
        this.scoredPuzzles = new HashSet<>();
        this.scoredItems = new HashSet<>();
        this.scoredLevels = new HashSet<>();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public void addScore(int points) {
        if (points > 0) {
            this.score += points;
        }
    }

    public void deductScore(int points) {
        if (points > 0) {
            this.score = Math.max(0, this.score - points);
        }
    }

    /**
     * Awards points for solving a puzzle. Only awards points once per puzzle ID.
     * @return true if points were awarded, false if already rewarded.
     */
    public boolean awardPuzzleScore(String puzzleId) {
        if (puzzleId == null || scoredPuzzles.contains(puzzleId)) {
            return false;
        }
        scoredPuzzles.add(puzzleId);
        addScore(PUZZLE_SOLVE_POINTS);
        return true;
    }

    /**
     * Awards points for discovering an item or clue. Only awards points once per item ID.
     */
    public boolean awardItemDiscoveryScore(String itemId) {
        if (itemId == null || scoredItems.contains(itemId)) {
            return false;
        }
        scoredItems.add(itemId);
        addScore(ITEM_DISCOVERY_POINTS);
        return true;
    }

    /**
     * Awards level completion bonus. Only awards points once per level number.
     */
    public boolean awardLevelCompletionScore(int level) {
        if (scoredLevels.contains(level)) {
            return false;
        }
        scoredLevels.add(level);
        addScore(LEVEL_COMPLETION_POINTS);
        return true;
    }

    /**
     * Calculates final win score including time bonus (1 point per 2 remaining seconds).
     */
    public int calculateFinalWinScore(int remainingSeconds) {
        int timeBonus = Math.max(0, remainingSeconds / 2);
        return score + timeBonus;
    }

    public void reset() {
        this.score = 0;
        this.scoredPuzzles.clear();
        this.scoredItems.clear();
        this.scoredLevels.clear();
    }
}
