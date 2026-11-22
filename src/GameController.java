/**
 * Central controller managing game state and high-level screen navigation.
 * Designed as a clean coordination layer where managers (PuzzleManager,
 * InventoryManager, HintManager, GameTimer, ScoreManager, LevelManager)
 * can be plugged in during later steps.
 */
public class GameController {
    private GameFrame gameFrame;
    private GameState currentState;
    private GameState currentRoomState = GameState.PLAYING;
    private Player currentPlayer;
    private Puzzle currentPuzzle;

    // =========================================================================
    // Extension points for Member 2 (to be integrated in future steps):
    // =========================================================================
    // TODO: Member 2 - puzzle logic
    // TODO: Member 2 - hint system
    // TODO: Member 2 - inventory logic
    // TODO: Member 2 - timer
    // TODO: Member 2 - scoring
    // TODO: Member 2 - level progression
    // TODO: Member 2 - final escape condition

    public GameController() {
        this.currentState = GameState.HOME;
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    /**
     * Changes the application state and requests the frame to switch screens.
     *
     * @param newState the target GameState
     */
    public void changeState(GameState newState) {
        if (newState == null) {
            return;
        }
        if (newState == GameState.PLAYING || newState == GameState.LEVEL_2 || newState == GameState.LEVEL_3 || newState == GameState.LEVEL_4) {
            this.currentRoomState = newState;
        }
        this.currentState = newState;
        if (gameFrame != null) {
            gameFrame.showScreen(newState);
        }
    }

    public GameState getCurrentRoomState() {
        return currentRoomState;
    }

    public void returnToCurrentRoom() {
        changeState(currentRoomState);
    }

    /**
     * Opens a specific puzzle and switches to GameState.PUZZLE.
     */
    public void openPuzzle(Puzzle puzzle) {
        this.currentPuzzle = puzzle;
        changeState(GameState.PUZZLE);
    }

    /**
     * Triggers the Win screen with final player statistics.
     * // TODO: Member 2 - trigger WinPanel after final escape
     * // TODO: Member 2 - connect actual score and timer results
     */
    public void triggerWin(int score, String time, String rooms) {
        if (gameFrame != null) {
            WinPanel wp = (WinPanel) gameFrame.getScreen(GameState.WIN);
            if (wp != null) {
                wp.setGameResults(score, time, rooms);
            }
        }
        changeState(GameState.WIN);
    }

    /**
     * Triggers the Game Over screen with player statistics.
     * // TODO: Member 2 - trigger GameOverPanel when timer expires
     * // TODO: Member 2 - connect actual score and timer results
     */
    public void triggerGameOver(int score, String rooms) {
        if (gameFrame != null) {
            GameOverPanel gop = (GameOverPanel) gameFrame.getScreen(GameState.GAME_OVER);
            if (gop != null) {
                gop.setGameResults(score, rooms);
            }
        }
        changeState(GameState.GAME_OVER);
    }

    public Puzzle getCurrentPuzzle() {
        return currentPuzzle;
    }

    public void setCurrentPuzzle(Puzzle currentPuzzle) {
        this.currentPuzzle = currentPuzzle;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    public Player getPlayer() {
        return currentPlayer;
    }
}
