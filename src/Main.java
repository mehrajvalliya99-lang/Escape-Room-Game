import javax.swing.SwingUtilities;

/**
 * Application entry point for Escape the Room – Mystery Puzzle Adventure.
 * Launches the game on Swing's Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            GameFrame frame = new GameFrame(controller);
            controller.setGameFrame(frame);
            controller.changeState(GameState.HOME);
            frame.setVisible(true);
        });
    }
}
