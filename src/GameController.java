/**
 * Central controller managing game state and high-level screen navigation.
 * Coordinates all Member 2 gameplay managers: PuzzleManager, HintManager,
 * InventoryManager, GameTimer, ScoreManager, and LevelManager.
 */
public class GameController {
    private GameFrame gameFrame;
    private GameState currentState;
    private GameState currentRoomState = GameState.PLAYING;
    private Player currentPlayer;
    private Puzzle currentPuzzle;

    // Backend Managers (Member 2)
    private final PuzzleManager puzzleManager;
    private final HintManager hintManager;
    private final InventoryManager inventoryManager;
    private final GameTimer gameTimer;
    private final ScoreManager scoreManager;
    private final LevelManager levelManager;

    public GameController() {
        this.currentState = GameState.HOME;
        this.puzzleManager = new PuzzleManager();
        this.hintManager = new HintManager();
        this.inventoryManager = new InventoryManager();
        this.gameTimer = new GameTimer();
        this.scoreManager = new ScoreManager();
        this.levelManager = new LevelManager();
    }

    public void setGameFrame(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    /**
     * Initializes and starts a new game session with the given player and difficulty.
     */
    public void startNewGame(Player player) {
        setPlayer(player);

        // Reset all gameplay managers
        puzzleManager.reset();
        hintManager.reset();
        inventoryManager.reset();
        scoreManager.reset();
        levelManager.reset();

        // Start countdown timer according to chosen difficulty
        Difficulty diff = (player != null) ? player.getDifficulty() : Difficulty.EASY;
        gameTimer.startTimer(
            diff,
            this::handleTimeout,
            this::onTimerTick
        );

        // Transition to Level 1 (The Study)
        changeState(GameState.PLAYING);
    }

    /**
     * Called when the countdown timer expires.
     */
    public void handleTimeout() {
        gameTimer.stopTimer();
        int finalScore = scoreManager.getScore();
        String rooms = levelManager.getCompletedRoomsString();
        triggerGameOver(finalScore, rooms);
    }

    /**
     * Called on each timer tick (every second).
     */
    private void onTimerTick() {
        if (gameFrame != null) {
            gameFrame.repaint();
        }
    }

    /**
     * Changes the application state and requests the frame to switch screens.
     * Synchronizes room state, level tracking, and modal screen refreshes.
     *
     * @param newState the target GameState
     */
    public void changeState(GameState newState) {
        if (newState == null) {
            return;
        }

        // Room state & level tracking
        if (newState == GameState.PLAYING) {
            this.currentRoomState = newState;
            levelManager.setCurrentLevel(1);
        } else if (newState == GameState.LEVEL_2) {
            this.currentRoomState = newState;
            levelManager.setCurrentLevel(2);
        } else if (newState == GameState.LEVEL_3) {
            this.currentRoomState = newState;
            levelManager.setCurrentLevel(3);
        } else if (newState == GameState.LEVEL_4) {
            this.currentRoomState = newState;
            levelManager.setCurrentLevel(4);
        }

        this.currentState = newState;

        if (gameFrame != null) {
            // Screen-specific refresh when opening modals
            if (newState == GameState.INVENTORY) {
                javax.swing.JPanel panel = gameFrame.getScreen(GameState.INVENTORY);
                if (panel instanceof InventoryPanel) {
                    ((InventoryPanel) panel).refreshInventory();
                }
            } else if (newState == GameState.PUZZLE) {
                javax.swing.JPanel panel = gameFrame.getScreen(GameState.PUZZLE);
                if (panel instanceof PuzzlePanel) {
                    ((PuzzlePanel) panel).onPuzzleOpened();
                }
            }

            gameFrame.showScreen(newState);
            gameFrame.repaint();
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
        if (puzzle != null && puzzleManager.getPuzzle(puzzle.getId()) == null) {
            // Register ad-hoc puzzle if not already present
            puzzleManager.registerPuzzle(puzzle, levelManager.getCurrentLevel(), puzzle.getVisualClue());
        }
        changeState(GameState.PUZZLE);
    }

    /**
     * Triggers the Win screen with final player statistics.
     */
    public void triggerWin(int score, String time, String rooms) {
        gameTimer.stopTimer();
        if (currentPlayer != null) {
            currentPlayer.setScore(score);
        }
        if (gameFrame != null) {
            WinPanel wp = (WinPanel) gameFrame.getScreen(GameState.WIN);
            if (wp != null) {
                int puzzles = (puzzleManager != null) ? puzzleManager.getSolvedCount() : 0;
                int hints = (hintManager != null) ? hintManager.getTotalHintsUsed() : 0;
                wp.setGameResults(score, time, rooms, puzzles, hints);
            }
        }
        changeState(GameState.WIN);
    }

    /**
     * Triggers the Game Over screen with player statistics.
     */
    public void triggerGameOver(int score, String rooms) {
        gameTimer.stopTimer();
        if (currentPlayer != null) {
            currentPlayer.setScore(score);
        }
        if (gameFrame != null) {
            GameOverPanel gop = (GameOverPanel) gameFrame.getScreen(GameState.GAME_OVER);
            if (gop != null) {
                gop.setGameResults(score, rooms);
            }
        }
        changeState(GameState.GAME_OVER);
    }

    // =========================================================================
    // MANAGER GETTERS
    // =========================================================================

    public PuzzleManager getPuzzleManager() {
        return puzzleManager;
    }

    public HintManager getHintManager() {
        return hintManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public GameTimer getGameTimer() {
        return gameTimer;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
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
