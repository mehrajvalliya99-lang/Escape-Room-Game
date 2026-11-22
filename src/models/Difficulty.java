/**
 * Represents the three game difficulty levels with their corresponding time limits
 * and clue/puzzle complexity settings.
 */
public enum Difficulty {
    EASY("Easy", 20, "20 MINUTES", "More clues \u2022 Simpler puzzles"),
    MEDIUM("Medium", 15, "15 MINUTES", "Balanced challenge"),
    HARD("Hard", 12, "12 MINUTES", "Fewer clues \u2022 More challenging");

    private final String displayName;
    private final int durationMinutes;
    private final String timeLabel;
    private final String description;

    Difficulty(String displayName, int durationMinutes, String timeLabel, String description) {
        this.displayName = displayName;
        this.durationMinutes = durationMinutes;
        this.timeLabel = timeLabel;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getTimeLabel() {
        return timeLabel;
    }

    public String getDescription() {
        return description;
    }
}
