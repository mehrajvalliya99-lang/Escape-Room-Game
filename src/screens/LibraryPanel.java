import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Playable Level 2 – The Secret Library for "Escape the Room – Mystery Puzzle Adventure".
 *
 * Distinct visual environment featuring towering multi-tier bookshelves,
 * an antique rolling ladder, central reading table with illuminated manuscript,
 * grandfather clock, historical painting, locked cabinet, gothic moonlit window,
 * and a hidden bookshelf passage mechanism.
 *
 * Uses pure 2D Java Swing, AWT, and Graphics2D with layered lighting, hover effects,
 * inspection descriptions, HUD, and seamless testing navigation.
 */
public class LibraryPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<GameObject> gameObjects;
    private final MenuButton inventoryButton;
    private final MenuButton returnRoom1Button;
    private final MenuButton level3Button;

    // Message bar state tracking
    private static final String DEFAULT_PROMPT = "Explore the Forgotten Library. Classified facility archives and towering shelves hide critical evidence...";
    private String lastClickedDescription = DEFAULT_PROMPT;
    private GameObject hoveredObject = null;
    private Point lastMousePoint = new Point(0, 0);
    private Point lastClickedPoint = new Point(0, 0);

    // Tactile click feedback highlight
    private GameObject clickedObject = null;
    private int clickFeedbackTicks = 0;

    // Viewport scaling and letterboxing
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Ambient floating dust particles and gentle lighting flicker
    private Timer ambientTimer;
    private float flickerPhase = 0.0f;
    private final List<DustParticle> dustParticles;

    public LibraryPanel(GameController controller) {
        this.controller = controller;
        this.gameObjects = new ArrayList<>();
        this.dustParticles = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 14, 24));
        setDoubleBuffered(true);
        setFocusable(true);

        initGameObjects();

        // Inventory Button (Bottom right)
        inventoryButton = new MenuButton(
            "INVENTORY",
            new Rectangle(1040, 648, 180, 48),
            new Color(90, 34, 136),
            new Color(50, 16, 82),
            new Color(118, 48, 178),
            new Color(72, 22, 118),
            new Color(36, 10, 60),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.INVENTORY)
        );

        // Temporary Testing Return Button: Return to Room 1 (The Study)
        returnRoom1Button = new MenuButton(
            "\u2190 Room 1",
            new Rectangle(760, 24, 105, 34),
            new Color(60, 45, 95),
            new Color(35, 24, 60),
            new Color(85, 62, 135),
            new Color(50, 34, 85),
            new Color(25, 16, 45),
            new Color(212, 175, 55),
            () -> controller.changeState(GameState.PLAYING)
        );

        // Navigation Button: Forward to Level 3 (The Abandoned Laboratory)
        level3Button = new MenuButton(
            "Level 3 \u2192",
            new Rectangle(875, 24, 105, 34),
            new Color(60, 45, 95),
            new Color(35, 24, 60),
            new Color(85, 62, 135),
            new Color(50, 34, 85),
            new Color(25, 16, 45),
            new Color(212, 175, 55),
            () -> {
                if (controller != null && controller.getLevelManager() != null) {
                    boolean hasCard = controller.getInventoryManager().hasItem("lab_card")
                                   || controller.getInventoryManager().hasItem("laboratory_card");
                    boolean cabinetSolved = controller.getPuzzleManager().isSolved("hidden_shelf")
                                         || controller.getPuzzleManager().isSolved("locked_cabinet");
                    boolean manuscriptSolved = controller.getPuzzleManager().isSolved("caesar_cipher")
                                            || controller.getPuzzleManager().isSolved("manuscript");
                    boolean shelfSolved = controller.getPuzzleManager().isSolved("book_arrangement")
                                       || controller.getPuzzleManager().isSolved("bookshelf")
                                       || controller.getPuzzleManager().isSolved("hidden_bookshelf");

                    if (controller.getLevelManager().isLevelUnlocked(3)) {
                        controller.changeState(GameState.LEVEL_3);
                    } else if (hasCard || cabinetSolved || (manuscriptSolved && shelfSolved)) {
                        controller.getLevelManager().completeLevel(2);
                        if (controller.getScoreManager() != null) {
                            controller.getScoreManager().awardLevelCompletionScore(2);
                        }
                        lastClickedDescription = "The heavy reinforced laboratory security bulkhead swings open! Level 2 Complete! (+150 pts)";
                        controller.changeState(GameState.LEVEL_3);
                    } else {
                        lastClickedDescription = "The Laboratory is locked. Unlock the Archive Cabinet (4921) to obtain the Lab Access Card!";
                        repaint();
                    }
                }
            }
        );

        initDustParticles();
        initMouseListeners();
        initAmbientTimer();
    }

    private void initGameObjects() {
        gameObjects.clear();

        // 1. HIDDEN BOOKSHELF SECTION (Foreground child of Bookshelf)
        gameObjects.add(new GameObject(
            "hidden_bookshelf",
            "Hidden Bookshelf Section",
            "Concealed archive volumes with embossed diamond markings.",
            new Rectangle(95, 250, 125, 165),
            () -> {
                controller.openPuzzle(new Puzzle(
                    "hidden_bookshelf",
                    "Secret Pattern Puzzle",
                    "Align the concealed archive spines to release the hidden latch.",
                    Puzzle.PuzzleType.PATTERN,
                    "[ \u25C6 ] [ \u25C7 ] [ \u25C6 ] [ \u25C7 ]",
                    "Look for recurring diamond foil patterns along the spines."
                ));
            }
        ));

        // 2. OLD MANUSCRIPT (Foreground child on Reading Table)
        gameObjects.add(new GameObject(
            "manuscript",
            "Encrypted Manuscript",
            "A handwritten scientific manuscript encrypted with a Caesar shift.",
            new Rectangle(550, 415, 145, 80),
            () -> {
                controller.openPuzzle(new Puzzle(
                    "caesar_cipher",
                    "Caesar Cipher Puzzle",
                    "Decipher the suspect's handwritten cipher: 'D U W L I D F W' (Shift -3).",
                    Puzzle.PuzzleType.CIPHER,
                    "ARTIFACT",
                    "Shift each letter backward by 3 positions in the alphabet."
                ));
            }
        ));

        // 3. LIBRARY LADDER
        gameObjects.add(new GameObject(
            "ladder",
            "Library Ladder",
            "Access ladder reaching the highest archival shelving.",
            new Rectangle(265, 115, 75, 445),
            () -> {
                if (controller != null && controller.getInventoryManager() != null) {
                    if (!controller.getInventoryManager().hasItem("cipher_manuscript")) {
                        controller.getInventoryManager().addItem(InventoryItem.createCipherManuscript());
                        if (controller.getScoreManager() != null) {
                            controller.getScoreManager().awardItemDiscoveryScore("cipher_manuscript");
                        }
                        lastClickedDescription = "High on shelf 8, you find a torn folder labeled 'EXPERIMENT' and circuit schematics! (+50 pts)";
                    } else {
                        lastClickedDescription = "The high shelf holds Dr. Vance's research schematics for the laboratory breakers.";
                    }
                    repaint();
                }
            }
        ));

        // 4. GRANDFATHER CLOCK
        gameObjects.add(new GameObject(
            "grandfather_clock",
            "Grandfather Clock",
            "The antique library clock is stopped at 11:45.",
            new Rectangle(345, 110, 80, 330),
            () -> {
                controller.openPuzzle(new Puzzle(
                    "grandfather_clock",
                    "Library Clock Puzzle",
                    "Deduce the true time setting from the pendulum and gear alignment.",
                    Puzzle.PuzzleType.COMBINATION,
                    "11:45",
                    "The hour hand points to XI and the minute hand points to IX (11:45)."
                ));
            }
        ));

        // 5. HISTORICAL PAINTING
        gameObjects.add(new GameObject(
            "painting",
            "Archive Painting",
            "A portrait of the facility's founding research team.",
            new Rectangle(445, 105, 190, 135),
            () -> {
                lastClickedDescription = "The portrait plaque is engraved with archive index code: 4 - 9 - 2 - 1.";
                repaint();
            }
        ));

        // 6. LOCKED CABINET
        gameObjects.add(new GameObject(
            "locked_cabinet",
            "Archive Cabinet",
            "A reinforced steel archive cabinet secured with a 4-digit combination padlock.",
            new Rectangle(895, 390, 110, 180),
            () -> {
                controller.openPuzzle(new Puzzle(
                    "hidden_shelf",
                    "Archive Cabinet Lock",
                    "Enter the 4-digit catalog index code to unlock the security cabinet.",
                    Puzzle.PuzzleType.NUMBER_CODE,
                    "4921",
                    "The code matches the index found on the founding team portrait."
                ));
            }
        ));

        // 7. WINDOW
        gameObjects.add(new GameObject(
            "window",
            "Window",
            "High arched window overlooking the laboratory wing.",
            new Rectangle(655, 90, 155, 245),
            () -> {
                lastClickedDescription = "Across the rain-swept courtyard, the laboratory's emergency warning beacons flash amber.";
                repaint();
            }
        ));

        // 8. CENTRAL READING TABLE
        gameObjects.add(new GameObject(
            "reading_table",
            "Reading Table",
            "Dr. Vance's active workspace with scattered archive files.",
            new Rectangle(400, 400, 475, 185),
            () -> {
                lastClickedDescription = "Dr. Vance's notes record: 'Laboratory Level 3 access card is locked inside Cabinet 4921.'";
                repaint();
            }
        ));

        // 9. BOOKSHELF (Main left shelf, also interactive on right tall shelf)
        gameObjects.add(new GameObject(
            "bookshelf",
            "Archive Bookshelf",
            "Shelves filled with classified research volumes numbered I through V.",
            new Rectangle(50, 95, 215, 470),
            () -> {
                controller.openPuzzle(new Puzzle(
                    "book_arrangement",
                    "Book Arrangement Puzzle",
                    "Arrange the research volumes in chronological sequence (1 through 5).",
                    Puzzle.PuzzleType.LOGIC_DEDUCTION,
                    "12345",
                    "Arrange volumes I through V in standard ascending numerical order."
                ));
            }
        ) {
            private final Rectangle rightShelf = new Rectangle(1015, 95, 215, 470);
            @Override
            public boolean contains(Point p) {
                return super.contains(p) || (isVisible() && isInteractive() && rightShelf.contains(p));
            }
        });
    }

    private void initDustParticles() {
        dustParticles.clear();
        int[][] pData = {
            {280, 310, 2}, {350, 360, 3}, {190, 220, 2}, {460, 350, 3},
            {600, 440, 2}, {720, 250, 2}, {860, 410, 3}, {970, 320, 2},
            {520, 210, 2}, {150, 440, 2}
        };
        for (int[] p : pData) {
            dustParticles.add(new DustParticle(p[0], p[1], p[2]));
        }
    }

    private void initAmbientTimer() {
        ambientTimer = new Timer(50, e -> {
            flickerPhase += 0.08f;
            if (flickerPhase > 1000.0f) flickerPhase = 0.0f;

            for (DustParticle p : dustParticles) {
                p.update();
            }

            if (clickFeedbackTicks > 0) {
                clickFeedbackTicks--;
                if (clickFeedbackTicks == 0 && clickedObject != null) {
                    clickedObject.setClicked(false);
                    clickedObject = null;
                }
            }

            repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (ambientTimer != null && !ambientTimer.isRunning()) {
            ambientTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (ambientTimer != null && ambientTimer.isRunning()) {
            ambientTimer.stop();
        }
    }

    // =========================================================================
    // MOUSE INTERACTION & SCALING
    // =========================================================================

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                lastMousePoint = lp;
                boolean anyHover = false;

                // Reset all hover states
                for (GameObject obj : gameObjects) {
                    obj.setHovered(false);
                }

                // Top-most hit detection (foreground objects first)
                GameObject found = null;
                for (GameObject obj : gameObjects) {
                    if (obj.contains(lp)) {
                        found = obj;
                        obj.setHovered(true);
                        anyHover = true;
                        break;
                    }
                }
                hoveredObject = found;

                boolean invH = inventoryButton.contains(lp);
                inventoryButton.setHovered(invH);
                if (invH) anyHover = true;

                boolean retH = returnRoom1Button.contains(lp);
                returnRoom1Button.setHovered(retH);
                if (retH) anyHover = true;

                boolean l3H = level3Button.contains(lp);
                level3Button.setHovered(l3H);
                if (l3H) anyHover = true;

                setCursor(anyHover ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());
                lastClickedPoint = lp;

                for (GameObject obj : gameObjects) {
                    if (obj.contains(lp)) {
                        lastClickedDescription = obj.getDescription();

                        if (clickedObject != null) {
                            clickedObject.setClicked(false);
                        }
                        clickedObject = obj;
                        clickedObject.setClicked(true);
                        clickFeedbackTicks = 6; // ~300ms

                        obj.interact();
                        repaint();
                        return;
                    }
                }

                if (inventoryButton.contains(lp)) {
                    inventoryButton.setPressed(true);
                    repaint();
                }
                if (returnRoom1Button.contains(lp)) {
                    returnRoom1Button.setPressed(true);
                    repaint();
                }
                if (level3Button.contains(lp)) {
                    level3Button.setPressed(true);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (inventoryButton.isPressed()) {
                    inventoryButton.setPressed(false);
                    repaint();
                    if (inventoryButton.contains(lp)) {
                        inventoryButton.trigger();
                    }
                }
                if (returnRoom1Button.isPressed()) {
                    returnRoom1Button.setPressed(false);
                    repaint();
                    if (returnRoom1Button.contains(lp)) {
                        returnRoom1Button.trigger();
                    }
                }
                if (level3Button.isPressed()) {
                    level3Button.setPressed(false);
                    repaint();
                    if (level3Button.contains(lp)) {
                        level3Button.trigger();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (GameObject obj : gameObjects) {
                    obj.setHovered(false);
                }
                hoveredObject = null;
                inventoryButton.setHovered(false);
                inventoryButton.setPressed(false);
                returnRoom1Button.setHovered(false);
                returnRoom1Button.setPressed(false);
                level3Button.setHovered(false);
                level3Button.setPressed(false);
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void updateScaleAndOffset() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        double targetRatio = (double) LOGICAL_WIDTH / LOGICAL_HEIGHT;
        double currentRatio = (double) w / h;

        if (currentRatio > targetRatio) {
            scale = (double) h / LOGICAL_HEIGHT;
            offsetX = (int) ((w - LOGICAL_WIDTH * scale) / 2.0);
            offsetY = 0;
        } else {
            scale = (double) w / LOGICAL_WIDTH;
            offsetX = 0;
            offsetY = (int) ((h - LOGICAL_HEIGHT * scale) / 2.0);
        }
    }

    private Point toLogicalCoordinates(Point p) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return p;

        double targetRatio = (double) LOGICAL_WIDTH / LOGICAL_HEIGHT;
        double currentRatio = (double) w / h;
        double currentScale;
        int currentOffsetX = 0;
        int currentOffsetY = 0;

        if (currentRatio > targetRatio) {
            currentScale = (double) h / LOGICAL_HEIGHT;
            currentOffsetX = (int) ((w - LOGICAL_WIDTH * currentScale) / 2.0);
        } else {
            currentScale = (double) w / LOGICAL_WIDTH;
            currentOffsetY = (int) ((h - LOGICAL_HEIGHT * currentScale) / 2.0);
        }

        if (currentScale <= 0) return p;
        int lx = (int) ((p.x - currentOffsetX) / currentScale);
        int ly = (int) ((p.y - currentOffsetY) / currentScale);
        return new Point(lx, ly);
    }

    // =========================================================================
    // RENDERING PIPELINE
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        updateScaleAndOffset();

        // 1. Fill letterbox margin
        g2d.setColor(new Color(6, 7, 14));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. Viewport transformation
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 3. Draw Room Layers
        drawLibraryWalls(g2d);
        drawLibraryFloor(g2d);
        drawGothicWindow(g2d);
        drawBookshelves(g2d);
        drawGrandfatherClock(g2d);
        drawHistoricalPainting(g2d);
        drawReadingTable(g2d);
        drawManuscript(g2d);
        drawScholarLamp(g2d);
        drawLockedCabinet(g2d);
        drawLibraryLadder(g2d);
        drawAtmosphericLighting(g2d);
        drawDustParticles(g2d);
        drawInteractivityFeedback(g2d);

        // 4. Draw HUD & Controls
        drawHUD(g2d);
        drawMessageBar(g2d);
        drawButton(g2d, inventoryButton);
        drawButton(g2d, returnRoom1Button);
        drawButton(g2d, level3Button);

        g2d.dispose();
    }

    // =========================================================================
    // LAYER 1: WALLS & WAINSCOTING (Deep Teal & Navy Palette)
    // =========================================================================
    private void drawLibraryWalls(Graphics2D g) {
        // Deep teal-to-navy wall gradient
        LinearGradientPaint wallGrad = new LinearGradientPaint(
            0, 0, 0, 520,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(12, 28, 38), new Color(16, 24, 38), new Color(8, 14, 22)}
        );
        g.setPaint(wallGrad);
        g.fillRect(0, 0, LOGICAL_WIDTH, 520);

        // Vertical wall molding panels
        g.setColor(new Color(25, 45, 60, 80));
        g.setStroke(new BasicStroke(1.2f));
        for (int x = 290; x < 1000; x += 115) {
            g.drawRect(x, 80, 85, 340);
            g.drawRect(x + 4, 84, 77, 332);
        }

        // Horizontal wainscot rail & baseboard
        g.setColor(new Color(28, 18, 12));
        g.fillRect(0, 490, LOGICAL_WIDTH, 30);
        g.setColor(new Color(212, 175, 55, 120));
        g.setStroke(new BasicStroke(1.6f));
        g.drawLine(0, 490, LOGICAL_WIDTH, 490);
    }

    // =========================================================================
    // LAYER 2: PARQUET WOOD FLOORING
    // =========================================================================
    private void drawLibraryFloor(Graphics2D g) {
        LinearGradientPaint floorGrad = new LinearGradientPaint(
            0, 520, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(48, 28, 16), new Color(34, 18, 10), new Color(18, 10, 6)}
        );
        g.setPaint(floorGrad);
        g.fillRect(0, 520, LOGICAL_WIDTH, 200);

        // Floor plank perspective lines
        g.setColor(new Color(18, 10, 6, 160));
        g.setStroke(new BasicStroke(1.2f));
        for (int y = 535; y < LOGICAL_HEIGHT; y += 18) {
            g.drawLine(0, y, LOGICAL_WIDTH, y);
        }

        // Large antique library rug under reading table
        int rx = 360;
        int ry = 515;
        int rw = 560;
        int rh = 145;
        g.setColor(new Color(4, 5, 10, 140));
        g.fillRoundRect(rx + 2, ry + 4, rw, rh, 18, 18);

        LinearGradientPaint rugGrad = new LinearGradientPaint(
            rx, ry, rx, ry + rh,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(55, 20, 35), new Color(35, 12, 22)}
        );
        g.setPaint(rugGrad);
        g.fillRoundRect(rx, ry, rw, rh, 18, 18);

        // Rug gold border & fringe
        g.setColor(new Color(212, 175, 55, 180));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(rx, ry, rw, rh, 18, 18);
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(rx + 5, ry + 5, rw - 10, rh - 10, 14, 14);
    }

    // =========================================================================
    // LAYER 3: GOTHIC ARCHED MOONLIT WINDOW
    // =========================================================================
    private void drawGothicWindow(Graphics2D g) {
        int wx = 655;
        int wy = 90;
        int ww = 155;
        int wh = 245;

        // Outer stone arch
        g.setColor(new Color(22, 28, 38));
        GeneralPath arch = new GeneralPath();
        arch.moveTo(wx, wy + wh);
        arch.lineTo(wx, wy + 80);
        arch.curveTo(wx, wy + 20, wx + ww / 2 - 20, wy, wx + ww / 2, wy);
        arch.curveTo(wx + ww / 2 + 20, wy, wx + ww, wy + 20, wx + ww, wy + 80);
        arch.lineTo(wx + ww, wy + wh);
        arch.closePath();
        g.fill(arch);

        // Inner night sky glass
        int pad = 8;
        GeneralPath glass = new GeneralPath();
        glass.moveTo(wx + pad, wy + wh - pad);
        glass.lineTo(wx + pad, wy + 80);
        glass.curveTo(wx + pad, wy + 26, wx + ww / 2 - 16, wy + pad, wx + ww / 2, wy + pad);
        glass.curveTo(wx + ww / 2 + 16, wy + pad, wx + ww - pad, wy + 26, wx + ww - pad, wy + 80);
        glass.lineTo(wx + ww - pad, wy + wh - pad);
        glass.closePath();

        LinearGradientPaint nightSky = new LinearGradientPaint(
            wx, wy, wx, wy + wh,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(15, 30, 60), new Color(25, 45, 85), new Color(8, 16, 32)}
        );
        g.setPaint(nightSky);
        g.fill(glass);

        // Crescent moon & stars in window
        g.setColor(new Color(255, 245, 190, 220));
        Area moon = new Area(new java.awt.geom.Ellipse2D.Double(wx + 95, wy + 40, 26, 26));
        moon.subtract(new Area(new java.awt.geom.Ellipse2D.Double(wx + 102, wy + 38, 24, 24)));
        g.fill(moon);

        // Tiny stars
        g.setColor(new Color(255, 255, 255, 200));
        g.fillOval(wx + 35, wy + 55, 2, 2);
        g.fillOval(wx + 65, wy + 70, 2, 2);
        g.fillOval(wx + 45, wy + 110, 2, 2);
        g.fillOval(wx + 120, wy + 95, 2, 2);

        // Leaded glass diamond lattice mullions
        g.setColor(new Color(15, 20, 30, 160));
        g.setStroke(new BasicStroke(1.4f));
        g.draw(glass);

        // Vertical & horizontal mullions
        g.drawLine(wx + ww / 2, wy + 10, wx + ww / 2, wy + wh - pad);
        g.drawLine(wx + pad, wy + 140, wx + ww - pad, wy + 140);
        g.drawLine(wx + pad, wy + 190, wx + ww - pad, wy + 190);

        // Stone frame border
        g.setColor(new Color(45, 55, 75));
        g.setStroke(new BasicStroke(2.0f));
        g.draw(arch);

        // Cool blue moonlight beam falling toward reading table
        GeneralPath beam = new GeneralPath();
        beam.moveTo(wx + 20, wy + 140);
        beam.lineTo(wx + ww - 20, wy + 140);
        beam.lineTo(wx + ww + 60, 610);
        beam.lineTo(wx - 140, 610);
        beam.closePath();

        LinearGradientPaint beamGrad = new LinearGradientPaint(
            wx, wy + 140, wx - 40, 610,
            new float[]{0.0f, 0.4f, 1.0f},
            new Color[]{new Color(130, 185, 255, 55), new Color(100, 160, 240, 28), new Color(60, 110, 200, 0)}
        );
        g.setPaint(beamGrad);
        g.fill(beam);
    }

    // =========================================================================
    // LAYER 4: TWO TALL BOOKSHELVES & HIDDEN SECTION
    // =========================================================================
    private void drawBookshelves(Graphics2D g) {
        // 1. LEFT TALL BOOKSHELF (West)
        drawBookshelfUnit(g, 50, 95, 215, 470, true);

        // 2. RIGHT TALL BOOKSHELF (East)
        drawBookshelfUnit(g, 1015, 95, 215, 470, false);
    }

    private void drawBookshelfUnit(Graphics2D g, int x, int y, int w, int h, boolean isLeft) {
        // Drop shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRect(x + 4, y + 6, w, h);

        // Outer Dark Mahogany Frame
        LinearGradientPaint frameGrad = new LinearGradientPaint(
            x, y, x + w, y,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(55, 32, 20), new Color(75, 46, 30), new Color(42, 24, 14)}
        );
        g.setPaint(frameGrad);
        g.fillRect(x, y, w, h);

        // Carved top crown pediment
        g.setColor(new Color(85, 54, 34));
        g.fillRect(x - 6, y - 10, w + 12, 14);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRect(x - 6, y - 10, w + 12, 14);

        // Bookshelf gold borders & fluted side pillars
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRect(x, y, w, h);

        // 4 interior shelf tiers
        int shelves = 4;
        int tierH = (h - 20) / shelves;
        for (int i = 0; i < shelves; i++) {
            int sy = y + 14 + i * tierH;

            // Shelf cavity (deep dark shadow)
            g.setColor(new Color(18, 12, 10));
            g.fillRect(x + 12, sy, w - 24, tierH - 12);

            // Draw multi-colored books on shelf
            drawBooksOnTier(g, x + 14, sy + 4, w - 28, tierH - 16, i + (isLeft ? 0 : 5));

            // Wooden shelf plank
            LinearGradientPaint plank = new LinearGradientPaint(
                x + 8, sy + tierH - 12, x + w - 8, sy + tierH - 12,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{new Color(90, 56, 34), new Color(110, 72, 45), new Color(70, 42, 24)}
            );
            g.setPaint(plank);
            g.fillRect(x + 8, sy + tierH - 12, w - 16, 12);
            g.setColor(new Color(170, 125, 50));
            g.setStroke(new BasicStroke(1.0f));
            g.drawRect(x + 8, sy + tierH - 12, w - 16, 12);
        }

        // Highlight for Hidden Bookshelf section (Left shelf, Tier 2)
        if (isLeft) {
            // Subtle golden seam indicating secret doorway mechanism
            g.setColor(new Color(255, 215, 64, 90));
            g.setStroke(new BasicStroke(1.4f));
            g.drawRoundRect(95, 250, 125, 165, 8, 8);
        }

        // Brass track rail running across top for the library ladder
        if (isLeft) {
            g.setColor(new Color(212, 175, 55));
            g.setStroke(new BasicStroke(3.0f));
            g.drawLine(x - 10, y + 25, x + w + 80, y + 25);
            g.setColor(new Color(255, 235, 140));
            g.fillOval(x + w + 74, y + 22, 6, 6);
        }
    }

    private void drawBooksOnTier(Graphics2D g, int x, int y, int w, int h, int seed) {
        Color[] bookSpines = {
            new Color(140, 28, 38), // Crimson
            new Color(28, 65, 120), // Navy
            new Color(34, 95, 56),  // Forest green
            new Color(135, 85, 25), // Leather brown
            new Color(105, 38, 120),// Royal violet
            new Color(185, 145, 45),// Gilded gold
            new Color(42, 85, 95)   // Deep teal
        };

        int curX = x;
        int bookIdx = seed;
        while (curX < x + w - 12) {
            int bw = 9 + ((bookIdx * 7) % 8);
            int bh = h - ((bookIdx * 5) % 10);
            Color bc = bookSpines[bookIdx % bookSpines.length];

            // Book spine fill
            g.setColor(bc);
            g.fillRect(curX, y + (h - bh), bw, bh);

            // Spine highlight & edge shadow
            g.setColor(new Color(0, 0, 0, 70));
            g.drawLine(curX + bw - 1, y + (h - bh), curX + bw - 1, y + h);
            g.setColor(new Color(255, 255, 255, 45));
            g.drawLine(curX + 1, y + (h - bh), curX + 1, y + h);

            // Gold foil ribs on spine
            if (bw > 10) {
                g.setColor(new Color(255, 215, 64, 160));
                g.drawLine(curX + 2, y + (h - bh) + 6, curX + bw - 3, y + (h - bh) + 6);
                g.drawLine(curX + 2, y + h - 6, curX + bw - 3, y + h - 6);
            }

            curX += bw + 2;
            bookIdx++;
        }
    }

    // =========================================================================
    // LAYER 5: GRANDFATHER CLOCK
    // =========================================================================
    private void drawGrandfatherClock(Graphics2D g) {
        int cx = 345;
        int cy = 110;
        int cw = 80;
        int ch = 330;

        // Shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRect(cx + 3, cy + 4, cw, ch);

        // Dark walnut casing
        LinearGradientPaint caseGrad = new LinearGradientPaint(
            cx, cy, cx + cw, cy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(60, 36, 20), new Color(85, 52, 30), new Color(48, 26, 14)}
        );
        g.setPaint(caseGrad);
        g.fillRect(cx, cy, cw, ch);

        // Top triangular pediment
        GeneralPath ped = new GeneralPath();
        ped.moveTo(cx - 4, cy);
        ped.lineTo(cx + cw / 2, cy - 18);
        ped.lineTo(cx + cw + 4, cy);
        ped.closePath();
        g.fill(ped);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.draw(ped);

        // Gold finial on top
        g.setColor(new Color(255, 215, 64));
        g.fillOval(cx + cw / 2 - 4, cy - 24, 8, 8);

        // Clock Face Dial (Circular brass & ivory)
        int dialSize = 58;
        int dx = cx + (cw - dialSize) / 2;
        int dy = cy + 16;

        g.setColor(new Color(255, 248, 230));
        g.fillOval(dx, dy, dialSize, dialSize);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(dx, dy, dialSize, dialSize);

        // Roman numerals & clock hands (frozen at 11:45)
        Font rf = new Font("Georgia", Font.BOLD, 9);
        g.setFont(rf);
        g.setColor(new Color(40, 25, 14));
        g.drawString("XII", dx + 22, dy + 13);
        g.drawString("VI", dx + 25, dy + dialSize - 5);
        g.drawString("IX", dx + 6, dy + 32);
        g.drawString("III", dx + dialSize - 18, dy + 32);

        // Hands
        int centerDX = dx + dialSize / 2;
        int centerDY = dy + dialSize / 2;
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(centerDX, centerDY, centerDX - 15, centerDY); // Hour hand to IX
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(centerDX, centerDY, centerDX - 10, centerDY - 16); // Minute hand to XI
        g.fillOval(centerDX - 2, centerDY - 2, 4, 4);

        // Glass Door & Swinging Pendulum
        int gx = cx + 12;
        int gy = cy + 85;
        int gw = cw - 24;
        int gh = ch - 100;

        g.setColor(new Color(16, 10, 8));
        g.fillRect(gx, gy, gw, gh);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(gx, gy, gw, gh);

        // Brass pendulum rod & bob
        int pendX = cx + cw / 2;
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(pendX, gy + 10, pendX, gy + gh - 26);
        g.fillOval(pendX - 10, gy + gh - 26, 20, 20);
        g.setColor(new Color(160, 115, 30));
        g.drawOval(pendX - 10, gy + gh - 26, 20, 20);

        // Glass reflection sheen
        g.setColor(new Color(255, 255, 255, 35));
        g.drawLine(gx + 4, gy + 8, gx + gw - 6, gy + 45);
    }

    // =========================================================================
    // LAYER 6: HISTORICAL PAINTING
    // =========================================================================
    private void drawHistoricalPainting(Graphics2D g) {
        int px = 445;
        int py = 105;
        int pw = 190;
        int ph = 135;

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 180));
        g.fillRect(px + 3, py + 4, pw, ph);

        // Outer ornate gold frame
        LinearGradientPaint frameGrad = new LinearGradientPaint(
            px, py, px + pw, py + ph,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 235, 140), new Color(212, 175, 55), new Color(145, 105, 30)}
        );
        g.setPaint(frameGrad);
        g.fillRect(px, py, pw, ph);

        // Inner frame bevel
        g.setColor(new Color(80, 50, 16));
        g.fillRect(px + 8, py + 8, pw - 16, ph - 16);

        // Canvas artwork (Founder examining astrolabe)
        int cx = px + 12;
        int cy = py + 12;
        int cw = pw - 24;
        int ch = ph - 24;

        LinearGradientPaint canvasBg = new LinearGradientPaint(
            cx, cy, cx, cy + ch,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(24, 32, 54), new Color(18, 22, 36), new Color(10, 12, 20)}
        );
        g.setPaint(canvasBg);
        g.fillRect(cx, cy, cw, ch);

        // Starlit astronomical globe/astrolabe on canvas
        g.setColor(new Color(255, 225, 110, 180));
        g.drawOval(cx + cw / 2 - 20, cy + 25, 40, 40);
        g.drawOval(cx + cw / 2 - 20, cy + 33, 40, 24);
        g.drawLine(cx + cw / 2, cy + 20, cx + cw / 2, cy + 70);

        // Scholar silhouette
        g.setColor(new Color(8, 10, 16));
        g.fillOval(cx + 35, cy + 28, 22, 22); // head
        GeneralPath coat = new GeneralPath();
        coat.moveTo(cx + 25, cy + ch);
        coat.lineTo(cx + 38, cy + 50);
        coat.lineTo(cx + 56, cy + 50);
        coat.lineTo(cx + 70, cy + ch);
        coat.closePath();
        g.fill(coat);

        // Frame gold pinstripe
        g.setColor(new Color(255, 225, 80));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(px, py, pw, ph);
    }

    // =========================================================================
    // LAYER 7: CENTRAL READING TABLE & SCHOLAR LAMP
    // =========================================================================
    private void drawReadingTable(Graphics2D g) {
        int tx = 400;
        int ty = 400;
        int tw = 475;
        int th = 185;

        // Table drop shadow on rug
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRoundRect(tx + 4, ty + 15, tw, th - 10, 16, 16);

        // Turned wooden table legs
        drawTableLeg(g, tx + 18, ty + 25, 145);
        drawTableLeg(g, tx + tw - 38, ty + 25, 145);
        drawTableLeg(g, tx + 115, ty + 30, 140);
        drawTableLeg(g, tx + tw - 135, ty + 30, 140);

        // Table apron & drawer front
        LinearGradientPaint apronGrad = new LinearGradientPaint(
            tx + 12, ty + 22, tx + tw - 12, ty + 60,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(55, 32, 18), new Color(75, 45, 26), new Color(45, 24, 12)}
        );
        g.setPaint(apronGrad);
        g.fillRoundRect(tx + 12, ty + 22, tw - 24, 40, 6, 6);
        g.setColor(new Color(30, 16, 8));
        g.drawRoundRect(tx + 12, ty + 22, tw - 24, 40, 6, 6);

        // Brass drawer pull rings
        drawDrawerPull(g, tx + 120, ty + 42);
        drawDrawerPull(g, tx + tw / 2, ty + 42);
        drawDrawerPull(g, tx + tw - 120, ty + 42);

        // Polished Tabletop slab (beveled perspective)
        LinearGradientPaint topGrad = new LinearGradientPaint(
            tx, ty, tx, ty + 24,
            new float[]{0.0f, 0.4f, 1.0f},
            new Color[]{new Color(110, 70, 42), new Color(85, 52, 30), new Color(55, 32, 18)}
        );
        g.setPaint(topGrad);
        g.fillRoundRect(tx, ty, tw, 24, 12, 12);

        // Top edge gold reflection line
        g.setColor(new Color(255, 220, 120, 130));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(tx + 6, ty + 2, tx + tw - 6, ty + 2);
    }

    private void drawTableLeg(Graphics2D g, int x, int y, int h) {
        LinearGradientPaint leg = new LinearGradientPaint(
            x, y, x + 20, y,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(42, 24, 14), new Color(75, 46, 28), new Color(34, 18, 10)}
        );
        g.setPaint(leg);
        g.fillRect(x, y, 20, h);
        g.setColor(new Color(22, 12, 6));
        g.drawRect(x, y, 20, h);

        // Turned wood ring accents
        g.setColor(new Color(212, 175, 55, 140));
        g.fillRect(x - 2, y + 25, 24, 5);
        g.fillRect(x - 2, y + 65, 24, 5);
    }

    private void drawDrawerPull(Graphics2D g, int x, int y) {
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(x - 6, y - 6, 12, 12);
        g.fillOval(x - 2, y - 9, 4, 4);
    }

    private void drawScholarLamp(Graphics2D g) {
        int lx = 435;
        int ly = 315;

        // Lamp brass base
        g.setColor(new Color(212, 175, 55));
        g.fillOval(lx + 20, ly + 85, 36, 12);
        g.setColor(new Color(140, 100, 25));
        g.drawOval(lx + 20, ly + 85, 36, 12);

        // Curved brass arm
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(4.0f));
        g.drawArc(lx + 32, ly + 25, 24, 65, 270, 180);

        // Emerald banker's glass shade
        LinearGradientPaint shade = new LinearGradientPaint(
            lx + 10, ly + 20, lx + 66, ly + 40,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(36, 125, 75), new Color(52, 170, 100), new Color(20, 85, 50)}
        );
        g.setPaint(shade);
        g.fillRoundRect(lx + 10, ly + 20, 56, 22, 8, 8);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(lx + 10, ly + 20, 56, 22, 8, 8);

        // Warm radial light bloom from lamp across reading table
        float flicker = (float) Math.sin(flickerPhase * 2.0f) * 6.0f;
        RadialGradientPaint lampBloom = new RadialGradientPaint(
            lx + 38, ly + 50, 180 + flicker,
            new float[]{0.0f, 0.4f, 0.75f, 1.0f},
            new Color[]{
                new Color(255, 230, 110, 120),
                new Color(255, 195, 55, 55),
                new Color(240, 140, 25, 18),
                new Color(200, 100, 10, 0)
            }
        );
        g.setPaint(lampBloom);
        g.fillOval((int) (lx + 38 - 180), (int) (ly + 50 - 180), 360, 360);
    }

    // =========================================================================
    // LAYER 8: OLD MANUSCRIPT & QUILL
    // =========================================================================
    private void drawManuscript(Graphics2D g) {
        int mx = 550;
        int my = 415;
        int mw = 145;
        int mh = 80;

        // Shadow on table
        g.setColor(new Color(15, 10, 6, 140));
        g.fillRoundRect(mx + 3, my + 3, mw, mh, 6, 6);

        // Open double parchment document
        LinearGradientPaint parch = new LinearGradientPaint(
            mx, my, mx + mw, my + mh,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(245, 235, 205), new Color(230, 215, 180), new Color(200, 180, 140)}
        );
        g.setPaint(parch);
        g.fillRoundRect(mx, my, mw, mh, 6, 6);

        g.setColor(new Color(145, 115, 65));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(mx, my, mw, mh, 6, 6);

        // Center spine fold
        g.setColor(new Color(120, 95, 55));
        g.drawLine(mx + mw / 2, my + 4, mx + mw / 2, my + mh - 4);

        // Left page: Astronomical circular cipher diagram
        g.setColor(new Color(60, 45, 30));
        g.setStroke(new BasicStroke(1.0f));
        g.drawOval(mx + 18, my + 18, 36, 36);
        g.drawOval(mx + 23, my + 23, 26, 26);
        g.drawLine(mx + 36, my + 14, mx + 36, my + 58);
        g.drawLine(mx + 14, my + 36, mx + 58, my + 36);

        // Right page: Faded cursive calligraphy text lines
        g.setColor(new Color(75, 55, 38, 190));
        g.setStroke(new BasicStroke(1.4f));
        for (int y = my + 18; y < my + mh - 12; y += 10) {
            g.drawLine(mx + mw / 2 + 12, y, mx + mw - 14, y);
        }

        // Inkwell & Feather Quill beside manuscript
        g.setColor(new Color(22, 16, 28));
        g.fillOval(mx + mw + 8, my + 15, 16, 16);
        g.setColor(new Color(212, 175, 55));
        g.drawOval(mx + mw + 8, my + 15, 16, 16);

        // Quill leaning out of inkwell
        g.setColor(new Color(240, 235, 220));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(mx + mw + 16, my + 20, mx + mw + 32, my - 15);
    }

    // =========================================================================
    // LAYER 9: LOCKED CABINET (Oak & Brass)
    // =========================================================================
    private void drawLockedCabinet(Graphics2D g) {
        int cx = 895;
        int cy = 390;
        int cw = 110;
        int ch = 180;

        // Shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRect(cx + 4, cy + 5, cw, ch);

        // Carved dark oak frame
        LinearGradientPaint oak = new LinearGradientPaint(
            cx, cy, cx + cw, cy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(50, 30, 16), new Color(70, 42, 24), new Color(38, 20, 10)}
        );
        g.setPaint(oak);
        g.fillRect(cx, cy, cw, ch);

        // Cabinet door panel
        g.setColor(new Color(30, 16, 8));
        g.fillRect(cx + 8, cy + 10, cw - 16, ch - 20);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRect(cx + 8, cy + 10, cw - 16, ch - 20);

        // Brass strap hinges
        g.setColor(new Color(212, 175, 55));
        g.fillRect(cx + 6, cy + 30, 26, 8);
        g.fillRect(cx + 6, cy + ch - 40, 26, 8);

        // Heavy central brass padlock & keyhole
        int lcx = cx + cw - 26;
        int lcy = cy + ch / 2;
        g.setColor(new Color(255, 215, 64));
        g.fillRoundRect(lcx - 10, lcy - 12, 20, 24, 6, 6);
        g.setColor(new Color(150, 105, 25));
        g.drawRoundRect(lcx - 10, lcy - 12, 20, 24, 6, 6);

        // Keyhole
        g.setColor(new Color(20, 12, 16));
        g.fillOval(lcx - 3, lcy - 4, 6, 6);
        g.drawLine(lcx, lcy, lcx, lcy + 6);
    }

    // =========================================================================
    // LAYER 10: ANTIQUE LIBRARY ROLLING LADDER
    // =========================================================================
    private void drawLibraryLadder(Graphics2D g) {
        int lx = 265;
        int ly = 115;
        int lw = 75;
        int lh = 445;

        // Ladder shadow
        g.setColor(new Color(4, 5, 12, 130));
        g.drawLine(lx + 8, ly + 8, lx + lw + 4, ly + lh + 6);

        // Top brass roller hooks attached to rail
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(3.0f));
        g.drawArc(lx + 4, ly - 8, 14, 16, 0, 180);
        g.drawArc(lx + lw - 20, ly - 8, 14, 16, 0, 180);

        // Wooden side rails
        LinearGradientPaint rail = new LinearGradientPaint(
            lx, ly, lx + lw, ly,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(75, 45, 26), new Color(55, 32, 18)}
        );
        g.setPaint(rail);
        g.setStroke(new BasicStroke(6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(lx + 10, ly, lx + 22, ly + lh);
        g.drawLine(lx + lw - 14, ly, lx + lw - 2, ly + lh);

        // Brass rungs
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(3.2f));
        int rungs = 10;
        for (int i = 1; i <= rungs; i++) {
            float t = (float) i / (rungs + 1);
            int x1 = (int) (lx + 10 + t * 12);
            int x2 = (int) (lx + lw - 14 + t * 12);
            int ry = (int) (ly + t * lh);
            g.drawLine(x1, ry, x2, ry);
        }

        // Bottom roller wheels
        g.setColor(new Color(180, 135, 45));
        g.fillOval(lx + 16, ly + lh - 4, 12, 12);
        g.fillOval(lx + lw - 8, ly + lh - 4, 12, 12);
    }

    // =========================================================================
    // LAYER 11: ATMOSPHERIC LIGHTING & DUST
    // =========================================================================
    private void drawAtmosphericLighting(Graphics2D g) {
        // Deep purple ambient vignette around corners
        RadialGradientPaint ambVig = new RadialGradientPaint(
            640.0f, 360.0f, 750.0f,
            new float[]{0.0f, 0.65f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(14, 10, 28, 90), new Color(6, 4, 14, 210)}
        );
        g.setPaint(ambVig);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
    }

    private void drawDustParticles(Graphics2D g) {
        g.setColor(new Color(255, 235, 170, 75));
        for (DustParticle p : dustParticles) {
            g.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }
    }

    // =========================================================================
    // LAYER 12: INTERACTIVITY FEEDBACK (Hover Outline & Click Highlight)
    // =========================================================================
    private void drawInteractivityFeedback(Graphics2D g) {
        // Click feedback flash
        if (clickedObject != null && clickFeedbackTicks > 0) {
            Rectangle cr = clickedObject.getBounds();
            if ("bookshelf".equals(clickedObject.getId()) && lastClickedPoint != null && lastClickedPoint.x >= 900) {
                cr = new Rectangle(1015, 95, 215, 470);
            }
            g.setColor(new Color(255, 240, 160, 50));
            g.fillRoundRect(cr.x - 3, cr.y - 3, cr.width + 6, cr.height + 6, 12, 12);

            g.setColor(new Color(255, 235, 90, 230));
            g.setStroke(new BasicStroke(2.8f));
            g.drawRoundRect(cr.x - 3, cr.y - 3, cr.width + 6, cr.height + 6, 12, 12);
        }

        // Hover outline & tooltip badge
        if (hoveredObject != null) {
            Rectangle r = hoveredObject.getBounds();
            if ("bookshelf".equals(hoveredObject.getId()) && lastMousePoint != null && lastMousePoint.x >= 900) {
                r = new Rectangle(1015, 95, 215, 470);
            }

            // Subtle golden outline
            g.setColor(new Color(255, 215, 64, 185));
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 12, 12);

            // Subtle brightening fill
            g.setColor(new Color(255, 235, 140, 25));
            g.fillRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 12, 12);

            // Floating badge above object
            String label = hoveredObject.getName();
            Font tipFont = new Font("Georgia", Font.BOLD, 14);
            g.setFont(tipFont);
            FontMetrics fm = g.getFontMetrics(tipFont);

            int tipW = fm.stringWidth(label) + 20;
            int tipH = 26;
            int tipX = r.x + (r.width - tipW) / 2;
            int tipY = Math.max(76, r.y - 32);

            g.setColor(new Color(4, 5, 12, 160));
            g.fillRoundRect(tipX + 2, tipY + 2, tipW, tipH, 10, 10);

            g.setColor(new Color(22, 16, 32, 230));
            g.fillRoundRect(tipX, tipY, tipW, tipH, 10, 10);

            g.setColor(new Color(255, 215, 64));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(tipX, tipY, tipW, tipH, 10, 10);

            g.setColor(new Color(255, 245, 220));
            g.drawString(label, tipX + 10, tipY + 18);
        }
    }

    // =========================================================================
    // LAYER 13: HUD & BOTTOM MESSAGE BAR
    // =========================================================================
    private void drawHUD(Graphics2D g) {
        int hudX = 30;
        int hudY = 16;
        int hudW = 1220;
        int hudH = 50;
        int arc = 16;

        g.setColor(new Color(16, 12, 24, 215));
        g.fillRoundRect(hudX, hudY, hudW, hudH, arc, arc);

        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(hudX, hudY, hudW, hudH, arc, arc);

        // 1. Player Name
        Player player = controller.getPlayer();
        String playerName = (player != null && !player.getName().isEmpty()) ? player.getName() : "Detective";
        String playerDisplay = "PLAYER: " + playerName;

        drawHUDKeyIcon(g, hudX + 18, hudY + 15);
        Font hudFont = new Font("Georgia", Font.BOLD, 17);
        g.setFont(hudFont);
        g.setColor(new Color(255, 245, 220));
        g.drawString(playerDisplay, hudX + 46, hudY + 32);

        // 2. Room Title: LEVEL 2 — THE FORGOTTEN LIBRARY
        String roomTitle = "LEVEL 2 \u2014 THE FORGOTTEN LIBRARY";
        Font titleFont = new Font("Georgia", Font.BOLD, 20);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        int rtx = hudX + (hudW - tfm.stringWidth(roomTitle)) / 2 - 40;

        g.setColor(new Color(10, 8, 14, 210));
        g.drawString(roomTitle, rtx + 1, hudY + 34);

        g.setColor(new Color(255, 215, 64));
        g.drawString(roomTitle, rtx, hudY + 33);

        // 3. Score & Time placeholders
        String scoreDisplay = "SCORE: " + (controller != null && controller.getScoreManager() != null ? controller.getScoreManager().getScore() : 0);
        String timeDisplay = "TIME: " + (controller != null && controller.getGameTimer() != null ? controller.getGameTimer().getFormattedRemainingTime() : "00:00");

        Font rightFont = new Font("Arial", Font.BOLD, 16);
        g.setFont(rightFont);
        FontMetrics rfm = g.getFontMetrics(rightFont);

        int timeW = rfm.stringWidth(timeDisplay);
        int scoreW = rfm.stringWidth(scoreDisplay);

        int timeX = hudX + hudW - timeW - 24;
        int scoreX = timeX - scoreW - 28;

        g.setColor(new Color(245, 215, 95));
        g.drawString(scoreDisplay, scoreX, hudY + 32);

        drawHUDClockIcon(g, timeX - 22, hudY + 17);
        g.setColor(new Color(255, 245, 220));
        g.drawString(timeDisplay, timeX, hudY + 32);
    }

    private void drawHUDKeyIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(x, y, 9, 9);
        g.drawLine(x + 9, y + 4, x + 18, y + 4);
        g.drawLine(x + 15, y + 4, x + 15, y + 9);
        g.drawLine(x + 18, y + 4, x + 18, y + 8);
    }

    private void drawHUDClockIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.8f));
        g.drawOval(x, y, 14, 14);
        g.drawLine(x + 7, y + 7, x + 7, y + 3);
        g.drawLine(x + 7, y + 7, x + 10, y + 7);
    }

    private void drawMessageBar(Graphics2D g) {
        int mbX = 60;
        int mbY = 648;
        int mbW = 960;
        int mbH = 48;
        int arc = 16;

        String messageToDisplay = (hoveredObject != null) ? hoveredObject.getName() : lastClickedDescription;

        g.setColor(new Color(4, 5, 12, 140));
        g.fillRoundRect(mbX + 2, mbY + 4, mbW, mbH, arc, arc);

        g.setColor(new Color(16, 12, 24, 225));
        g.fillRoundRect(mbX, mbY, mbW, mbH, arc, arc);

        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(mbX, mbY, mbW, mbH, arc, arc);

        Font msgFont = new Font("Georgia", Font.BOLD, 16);
        g.setFont(msgFont);
        FontMetrics fm = g.getFontMetrics(msgFont);

        int tx = mbX + 24;
        int ty = mbY + (mbH - fm.getHeight()) / 2 + fm.getAscent();

        drawDiamond(g, tx + 4, ty - 5, 5);
        tx += 18;

        g.setColor(new Color(255, 248, 230));
        g.drawString(messageToDisplay, tx, ty);
    }

    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.bounds;
        int x = r.x;
        int y = r.y + (btn.pressed ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 18;

        if (btn.hovered) {
            RadialGradientPaint glow = new RadialGradientPaint(
                x + w / 2.0f, y + h / 2.0f, (float) (w * 0.65),
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                    new Color(255, 225, 75, 115),
                    new Color(255, 205, 50, 45),
                    new Color(255, 185, 20, 0)
                }
            );
            g.setPaint(glow);
            g.fillRoundRect(x - 6, y - 6, w + 12, h + 12, arc + 6, arc + 6);
        }

        g.setColor(new Color(4, 5, 12, 170));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        Color topC = btn.pressed ? btn.pressedColor : (btn.hovered ? btn.hoverTopColor : btn.topColor);
        Color botC = btn.pressed ? btn.pressedColor.darker() : (btn.hovered ? btn.hoverBottomColor : btn.bottomColor);

        LinearGradientPaint btnGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{topC, botC}
        );
        g.setPaint(btnGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        Area glossArea = new Area(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        glossArea.intersect(new Area(new Rectangle(x, y, w, h / 2)));
        LinearGradientPaint glossGrad = new LinearGradientPaint(
            x, y, x, y + h / 2,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 255, 255, 50), new Color(255, 255, 255, 10)}
        );
        g.setPaint(glossGrad);
        g.fill(glossArea);

        g.setColor(btn.borderColor);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        Font font = (w < 120) ? new Font("Arial", Font.BOLD, 13) : new Font("Arial", Font.BOLD, 17);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.text)) / 2 + (btn.text.equals("INVENTORY") ? 8 : 0);
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        if (btn.text.equals("INVENTORY")) {
            drawSatchelIcon(g, textX - 22, textY - 12);
        }

        g.setColor(new Color(4, 5, 12, 210));
        g.drawString(btn.text, textX + 1, textY + 2);

        g.setColor(Color.WHITE);
        g.drawString(btn.text, textX, textY);
    }

    private void drawSatchelIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(x, y, 14, 11, 3, 3);
        g.drawArc(x + 3, y - 4, 8, 8, 0, 180);
    }

    private void drawDiamond(Graphics2D g, int cx, int cy, int size) {
        GeneralPath diamond = new GeneralPath();
        diamond.moveTo(cx, cy - size);
        diamond.lineTo(cx + size, cy);
        diamond.lineTo(cx, cy + size);
        diamond.lineTo(cx - size, cy);
        diamond.closePath();

        g.setColor(new Color(255, 215, 64));
        g.fill(diamond);
        g.setColor(new Color(150, 105, 30));
        g.setStroke(new BasicStroke(1.0f));
        g.draw(diamond);
    }

    // =========================================================================
    // GETTERS & DATA ACCESS
    // =========================================================================

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public String getCurrentMessage() {
        return (hoveredObject != null) ? hoveredObject.getName() : lastClickedDescription;
    }

    public String getLastClickedDescription() {
        return lastClickedDescription;
    }

    public void setLastClickedDescription(String lastClickedDescription) {
        this.lastClickedDescription = lastClickedDescription;
    }

    public MenuButton getInventoryButton() {
        return inventoryButton;
    }

    public MenuButton getReturnRoom1Button() {
        return returnRoom1Button;
    }

    public MenuButton getLevel3Button() {
        return level3Button;
    }

    // =========================================================================
    // INNER CLASSES: DustParticle & MenuButton
    // =========================================================================

    private static class DustParticle {
        float x, y;
        int size;
        float speedY;
        float driftX;

        public DustParticle(float x, float y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speedY = 0.2f + (float) Math.random() * 0.22f;
            this.driftX = (float) (Math.random() - 0.5) * 0.15f;
        }

        public void update() {
            y -= speedY;
            x += driftX;
            if (y < 40) {
                y = 660;
                x = 100 + (float) (Math.random() * 1080);
            }
        }
    }

    public static class MenuButton {
        public final String text;
        public final Rectangle bounds;
        public final Color topColor;
        public final Color bottomColor;
        public final Color hoverTopColor;
        public final Color hoverBottomColor;
        public final Color pressedColor;
        public final Color borderColor;
        public final Runnable action;

        public boolean hovered;
        public boolean pressed;

        public MenuButton(String text, Rectangle bounds,
                          Color topColor, Color bottomColor,
                          Color hoverTopColor, Color hoverBottomColor,
                          Color pressedColor, Color borderColor,
                          Runnable action) {
            this.text = text;
            this.bounds = bounds;
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.hoverTopColor = hoverTopColor;
            this.hoverBottomColor = hoverBottomColor;
            this.pressedColor = pressedColor;
            this.borderColor = borderColor;
            this.action = action;
        }

        public boolean contains(Point p) {
            return bounds.contains(p);
        }

        public void trigger() {
            if (action != null) action.run();
        }

        public boolean isHovered() { return hovered; }
        public void setHovered(boolean hovered) { this.hovered = hovered; }
        public boolean isPressed() { return pressed; }
        public void setPressed(boolean pressed) { this.pressed = pressed; }
    }
}
