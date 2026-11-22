/**
 * Represents the active player in Escape the Room – Mystery Puzzle Adventure.
 * Stores player profile, selected difficulty, and current score.
 */
public class Player {
    private String name;
    private Difficulty difficulty;
    private int score;

    public Player(String name, Difficulty difficulty) {
        this.name = name;
        this.difficulty = difficulty != null ? difficulty : Difficulty.EASY;
        this.score = 0; // Score begins at 0
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
