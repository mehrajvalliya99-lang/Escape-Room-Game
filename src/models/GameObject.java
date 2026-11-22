import java.awt.Point;
import java.awt.Rectangle;

/**
 * Represents an interactive 2D object within Room 1 – The Study.
 * Stores id, name, description, position, size, interactive state, and visible state.
 * Uses its bounds for mouse hit detection and provides puzzle hooks for Member 2.
 */
public class GameObject {
    private final String id;
    private String name;
    private String description;
    private Rectangle bounds;
    private boolean interactive;
    private boolean visible;
    private boolean hovered;
    private boolean clicked;
    private Runnable puzzleHook;

    public GameObject(String id, String name, String description, int x, int y, int width, int height) {
        this(id, name, description, new Rectangle(x, y, width, height), null);
    }

    public GameObject(String id, String name, String description, Rectangle bounds) {
        this(id, name, description, bounds, null);
    }

    public GameObject(String id, String name, String description, Rectangle bounds, Runnable puzzleHook) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.bounds = bounds != null ? bounds : new Rectangle(0, 0, 0, 0);
        this.interactive = true;
        this.visible = true;
        this.hovered = false;
        this.clicked = false;
        this.puzzleHook = puzzleHook;
    }

    /**
     * Checks if the given logical point is inside the object's bounds.
     * Only returns true if the object is both visible and interactive.
     */
    public boolean contains(Point p) {
        if (!visible || !interactive || bounds == null || p == null) {
            return false;
        }
        return bounds.contains(p);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public int getX() {
        return bounds.x;
    }

    public int getY() {
        return bounds.y;
    }

    public int getWidth() {
        return bounds.width;
    }

    public int getHeight() {
        return bounds.height;
    }

    public boolean isInteractive() {
        return interactive;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public Runnable getPuzzleHook() {
        return puzzleHook;
    }

    public void setPuzzleHook(Runnable puzzleHook) {
        this.puzzleHook = puzzleHook;
    }

    /**
     * Triggers the puzzle hook action for future Member 2 integration.
     */
    public void interact() {
        if (puzzleHook != null) {
            puzzleHook.run();
        }
    }
}
