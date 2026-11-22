import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main application window for Escape the Room – Mystery Puzzle Adventure.
 * Uses CardLayout to provide clean screen-switching architecture.
 */
public class GameFrame extends JFrame {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;
    public static final String GAME_TITLE = "Escape the Room \u2013 Mystery Puzzle Adventure";

    private final GameController controller;
    private final CardLayout cardLayout;
    private final JPanel mainContainer;
    private final Map<GameState, JPanel> screenPanels;

    public GameFrame(GameController controller) {
        this.controller = controller;
        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);
        this.screenPanels = new EnumMap<>(GameState.class);

        initUI();
    }

    private void initUI() {
        setTitle(GAME_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Dark mystery-themed background
        Color darkBackground = new Color(18, 16, 26);
        mainContainer.setBackground(darkBackground);
        mainContainer.setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));

        // Initialize placeholder panels for all supported GameStates
        for (GameState state : GameState.values()) {
            JPanel placeholder = new JPanel();
            placeholder.setBackground(darkBackground);
            registerScreen(state, placeholder);
        }

        // Register initial game screens
        registerScreen(GameState.HOME, new HomePanel(controller));
        registerScreen(GameState.INSTRUCTIONS, new InstructionsPanel(controller));
        registerScreen(GameState.PLAYER_DETAILS, new PlayerDetailsPanel(controller));
        registerScreen(GameState.PLAYING, new RoomPanel(controller));
        registerScreen(GameState.LEVEL_2, new LibraryPanel(controller));
        registerScreen(GameState.LEVEL_3, new MysteryChamberPanel(controller));
        registerScreen(GameState.LEVEL_4, new FinalEscapePanel(controller));
        registerScreen(GameState.INVENTORY, new InventoryPanel(controller));
        registerScreen(GameState.PUZZLE, new PuzzlePanel(controller));
        registerScreen(GameState.WIN, new WinPanel(controller));
        registerScreen(GameState.GAME_OVER, new GameOverPanel(controller));

        setContentPane(mainContainer);
        pack();
        setLocationRelativeTo(null); // Center on screen
    }

    /**
     * Registers or replaces a screen panel for a specific GameState.
     *
     * @param state the GameState key
     * @param panel the JPanel representing the screen
     */
    public void registerScreen(GameState state, JPanel panel) {
        JPanel previous = screenPanels.put(state, panel);
        if (previous != null) {
            mainContainer.remove(previous);
        }
        mainContainer.add(panel, state.name());
        mainContainer.revalidate();
    }

    /**
     * Displays the screen corresponding to the given GameState.
     *
     * @param state the GameState to display
     */
    public void showScreen(GameState state) {
        if (state != null) {
            cardLayout.show(mainContainer, state.name());
        }
    }

    public JPanel getScreen(GameState state) {
        return screenPanels.get(state);
    }

    public GameController getController() {
        return controller;
    }
}
