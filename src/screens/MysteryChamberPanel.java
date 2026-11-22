import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
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
 * Playable Level 3 – The Mystery Chamber for "Escape the Room – Mystery Puzzle Adventure".
 *
 * An ancient subterranean puzzle vault featuring detailed stone masonry,
 * atmospheric torchlight and teal mechanical luminescence, a massive central
 * puzzle pedestal, three imposing carved pillars, a circular astronomical wall mechanism,
 * ancient astronomical clock, locked stone door, floor-engraved golden seal, scroll table,
 * and a watchful guardian statue.
 *
 * Strict 2D Java Swing, AWT, and Graphics2D architecture with layered rendering,
 * hover outlines, click feedback, HUD, and seamless navigation.
 */
public class MysteryChamberPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<GameObject> gameObjects;
    private final MenuButton inventoryButton;
    private final MenuButton returnLevel2Button;
    private final MenuButton level4Button;

    // Message bar state tracking
    private static final String DEFAULT_PROMPT = "The Mystery Chamber awaits. Ancient stones and arcane mechanisms guard the final passage...";
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

    // Ambient floating dust motes & torch flicker animation
    private Timer ambientTimer;
    private float flickerPhase = 0.0f;
    private final List<DustParticle> dustParticles;

    public MysteryChamberPanel(GameController controller) {
        this.controller = controller;
        this.gameObjects = new ArrayList<>();
        this.dustParticles = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(6, 6, 12));
        setFocusable(true);

        // 1. Initialize Atmospheric Floating Dust Particles
        for (int i = 0; i < 26; i++) {
            dustParticles.add(new DustParticle(
                80 + (float) (Math.random() * 1120),
                80 + (float) (Math.random() * 580),
                1 + (int) (Math.random() * 3)
            ));
        }

        // 2. Register all 9 Interactive Chamber Objects
        initializeGameObjects();

        // 3. Navigation & Inventory Buttons
        this.inventoryButton = new MenuButton(
            "INVENTORY",
            new Rectangle(1040, 648, 180, 48),
            new Color(60, 24, 75), new Color(38, 14, 52),
            new Color(95, 38, 120), new Color(60, 24, 75),
            new Color(32, 10, 44),
            new Color(212, 175, 55),
            () -> {
                if (controller != null) {
                    controller.changeState(GameState.INVENTORY);
                }
            }
        );

        this.returnLevel2Button = new MenuButton(
            "\u2190 Level 2",
            new Rectangle(760, 24, 105, 34),
            new Color(40, 24, 60), new Color(24, 14, 38),
            new Color(70, 42, 105), new Color(42, 25, 65),
            new Color(20, 10, 30),
            new Color(190, 155, 60),
            () -> {
                if (controller != null) {
                    controller.changeState(GameState.LEVEL_2);
                }
            }
        );

        this.level4Button = new MenuButton(
            "Level 4 \u2192",
            new Rectangle(875, 24, 105, 34),
            new Color(60, 45, 95), new Color(35, 24, 60),
            new Color(85, 62, 135), new Color(50, 34, 85),
            new Color(25, 16, 45),
            new Color(212, 175, 55),
            () -> {
                if (controller != null && controller.getLevelManager() != null) {
                    if (controller.getLevelManager().isLevelUnlocked(4)) {
                        controller.changeState(GameState.LEVEL_4);
                    } else {
                        lastClickedDescription = "Level 4 is sealed. Unlock the stone door first!";
                        repaint();
                    }
                }
            }
        );

        // 4. Mouse Input Handling
        setupMouseListeners();

        // 5. Ambient Animation Loop
        ambientTimer = new Timer(33, e -> {
            flickerPhase += 0.08f;
            if (flickerPhase > 1000.0f) flickerPhase = 0.0f;

            for (DustParticle p : dustParticles) {
                p.update();
            }

            if (clickFeedbackTicks > 0) {
                clickFeedbackTicks--;
            }

            repaint();
        });
        ambientTimer.start();
    }

    private void initializeGameObjects() {
        // 1. Central Pedestal → Symbol Alignment Puzzle
        // TODO: Member 2 - Symbol alignment puzzle logic & validation
        gameObjects.add(new GameObject(
            "pedestal",
            "Central Pedestal",
            "An ancient pedestal with a strange circular marking.",
            new Rectangle(530, 355, 220, 225),
            () -> {
                // TODO: Member 2 - Symbol alignment puzzle logic & validation
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "pedestal",
                        "Symbol Alignment Puzzle",
                        "Align the arcane celestial symbols into harmonious resonance.",
                        Puzzle.PuzzleType.SYMBOL_SEQUENCE,
                        "☉ ☽ ♃ ♄ ♅",
                        "Observe the engraved star chart on the scroll table."
                    ));
                }
            }
        ));

        // 2. Stone Pillars
        // TODO: Member 2 - Pillar inspection and runes
        gameObjects.add(new GameObject(
            "pillars",
            "Stone Pillars",
            "Three pillars stand with mysterious symbols engraved on them.",
            new Rectangle(130, 95, 65, 425),
            () -> {
                lastClickedDescription = "Engraved runes glow in teal light along the stone pillars: VII - IX - IV - I.";
                repaint();
            }
        ));

        // 3. Wall Mechanism → Mechanical Puzzle
        // TODO: Member 2 - Mechanical puzzle logic & validation
        gameObjects.add(new GameObject(
            "wall_mechanism",
            "Wall Mechanism",
            "A complex mechanism with several rotating sections.",
            new Rectangle(560, 90, 160, 160),
            () -> {
                // TODO: Member 2 - Mechanical puzzle logic & validation
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "wall_mechanism",
                        "Mechanical Puzzle",
                        "Rotate the concentric celestial gear rings into mechanical resonance.",
                        Puzzle.PuzzleType.COMBINATION,
                        "GEAR RATIO: 3:5:9 - TEAL CORE RESONANCE",
                        "Match the outer gear teeth with the inner brass markers."
                    ));
                }
            }
        ));

        // 4. Ancient Clock → Time Deduction Puzzle
        // TODO: Member 2 - Time deduction puzzle logic & validation
        gameObjects.add(new GameObject(
            "clock",
            "Ancient Clock",
            "The clock hands point towards an unusual combination.",
            new Rectangle(220, 110, 75, 120),
            () -> {
                // TODO: Member 2 - Time deduction puzzle logic & validation
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "clock",
                        "Time Deduction Puzzle",
                        "Deduce the true celestial hour from the clock hands and escapement.",
                        Puzzle.PuzzleType.LOGIC_DEDUCTION,
                        "LOCKED AT III - MOON AT MERIDIAN",
                        "Cross-reference the clock hand angle with the historical journals."
                    ));
                }
            }
        ));

        // 5. Locked Stone Door
        gameObjects.add(new GameObject(
            "door",
            "Locked Stone Door",
            "A massive stone door blocks the way forward.",
            new Rectangle(868, 94, 174, 346),
            () -> {
                if (controller != null && controller.getLevelManager() != null) {
                    boolean hasFinalKey = controller.getInventoryManager().hasItem("final_key");
                    boolean statueSolved = controller.getPuzzleManager().isSolved("statue");
                    boolean wallSolved = controller.getPuzzleManager().isSolved("wall_mechanism");

                    if (controller.getLevelManager().isLevelUnlocked(4)) {
                        controller.changeState(GameState.LEVEL_4);
                    } else if (hasFinalKey || (statueSolved && wallSolved)) {
                        controller.getLevelManager().completeLevel(3);
                        if (controller.getScoreManager() != null) {
                            controller.getScoreManager().awardLevelCompletionScore(3);
                        }
                        lastClickedDescription = "The stone door grinds open on counterweights! Level 3 Complete! (+150 pts)";
                        controller.changeState(GameState.LEVEL_4);
                    } else {
                        lastClickedDescription = "The stone door is sealed. Solve the Wall Mechanism and obtain the Final Key from the statue!";
                        repaint();
                    }
                }
            }
        ));

        // 6. Wall Torches
        gameObjects.add(new GameObject(
            "torches",
            "Torches",
            "The flames flicker as if reacting to something nearby.",
            new Rectangle(75, 180, 40, 85),
            () -> {
                lastClickedDescription = "The wall torch flames flare turquoise as you approach the locked door.";
                repaint();
            }
        ));

        // 7. Floor Symbol → Pattern Puzzle
        // TODO: Member 2 - Pattern puzzle logic & validation
        gameObjects.add(new GameObject(
            "floor_symbol",
            "Floor Symbol",
            "An intricate symbol has been carved into the stone.",
            new Rectangle(460, 510, 360, 120),
            () -> {
                // TODO: Member 2 - Pattern puzzle logic & validation
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "floor_symbol",
                        "Pattern Puzzle",
                        "Trace the correct geometric path through the engraved floor heptagram.",
                        Puzzle.PuzzleType.PATTERN,
                        "⬡ ⬢ ⬡ ⬢ ⬡",
                        "Follow the golden star vertex intersections in ascending sequence."
                    ));
                }
            }
        ));

        // 8. Scroll Table → Cipher Puzzle
        // TODO: Member 2 - Cipher puzzle logic & validation
        gameObjects.add(new GameObject(
            "scroll_table",
            "Scroll Table",
            "Several ancient scrolls contain faded writings.",
            new Rectangle(50, 415, 150, 130),
            () -> {
                // TODO: Member 2 - Cipher puzzle logic & validation
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "scroll_table",
                        "Cipher Puzzle",
                        "Decipher the astrological treatise and faded star chart cipher.",
                        Puzzle.PuzzleType.CIPHER,
                        "CIPHER: 'A L T A R' -> VIGENERE KEY: 'STONE'",
                        "The ribbon seals hint at the key cipher alphabet."
                    ));
                }
            }
        ));

        // 9. Stone Statue → Hidden Clue
        // TODO: Member 2 - Hidden clue logic & validation
        gameObjects.add(new GameObject(
            "statue",
            "Stone Statue",
            "The statue appears to be guarding something.",
            new Rectangle(1090, 330, 120, 240),
            () -> {
                // TODO: Member 2 - Hidden clue logic & validation
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "statue",
                        "Hidden Clue",
                        "Examine the guardian statue's ceremonial staff for concealed markings.",
                        Puzzle.PuzzleType.NUMBER_CODE,
                        "RUNES: VII - IX - IV - I",
                        "The golden staff pommel unscrews to reveal an engraved number code."
                    ));
                }
            }
        ));
    }

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                lastMousePoint = lp;

                boolean cursorHand = false;
                GameObject newlyHovered = null;

                // Check Objects
                for (GameObject obj : gameObjects) {
                    boolean hits = obj.contains(lp);

                    // Multi-instance bounding hit support
                    if ("torches".equals(obj.getId())) {
                        Rectangle r2 = new Rectangle(815, 190, 35, 75);
                        if (r2.contains(lp)) hits = true;
                    } else if ("pillars".equals(obj.getId())) {
                        Rectangle p2 = new Rectangle(400, 95, 65, 425);
                        Rectangle p3 = new Rectangle(1070, 95, 65, 425);
                        if (p2.contains(lp) || p3.contains(lp)) hits = true;
                    }

                    obj.setHovered(hits);
                    if (hits) {
                        newlyHovered = obj;
                        cursorHand = true;
                    }
                }

                if (newlyHovered != clickedObject) {
                    clickedObject = null;
                }
                hoveredObject = newlyHovered;

                // Check Buttons
                boolean invHover = inventoryButton.contains(lp);
                inventoryButton.setHovered(invHover);
                if (invHover) cursorHand = true;

                boolean retHover = returnLevel2Button.contains(lp);
                returnLevel2Button.setHovered(retHover);
                if (retHover) cursorHand = true;

                boolean l4Hover = level4Button.contains(lp);
                level4Button.setHovered(l4Hover);
                if (l4Hover) cursorHand = true;

                setCursor(cursorHand ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());
                lastClickedPoint = lp;

                // Check interactive chamber objects
                for (GameObject obj : gameObjects) {
                    boolean hits = obj.contains(lp);
                    if ("torches".equals(obj.getId())) {
                        Rectangle r2 = new Rectangle(815, 190, 35, 75);
                        if (r2.contains(lp)) hits = true;
                    } else if ("pillars".equals(obj.getId())) {
                        Rectangle p2 = new Rectangle(400, 95, 65, 425);
                        Rectangle p3 = new Rectangle(1070, 95, 65, 425);
                        if (p2.contains(lp) || p3.contains(lp)) hits = true;
                    }

                    if (hits) {
                        hoveredObject = obj;
                        clickedObject = obj;
                        clickFeedbackTicks = 12;
                        lastClickedDescription = obj.getDescription();

                        obj.interact(); // Member 2 puzzle hooks
                        repaint();
                        return;
                    }
                }

                if (inventoryButton.contains(lp)) {
                    inventoryButton.setPressed(true);
                    repaint();
                }
                if (returnLevel2Button.contains(lp)) {
                    returnLevel2Button.setPressed(true);
                    repaint();
                }
                if (level4Button.contains(lp)) {
                    level4Button.setPressed(true);
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
                if (returnLevel2Button.isPressed()) {
                    returnLevel2Button.setPressed(false);
                    repaint();
                    if (returnLevel2Button.contains(lp)) {
                        returnLevel2Button.trigger();
                    }
                }
                if (level4Button.isPressed()) {
                    level4Button.setPressed(false);
                    repaint();
                    if (level4Button.contains(lp)) {
                        level4Button.trigger();
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
                returnLevel2Button.setHovered(false);
                returnLevel2Button.setPressed(false);
                level4Button.setHovered(false);
                level4Button.setPressed(false);
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

        // 1. Draw outer letterbox & background
        drawBackground(g2d);

        // 2. Viewport transformation
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // High quality rendering hints & antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 3. Draw Chamber Artwork Layers (Ordered by depth)
        drawStoneWalls(g2d);
        drawFloor(g2d);
        drawFloorSymbol(g2d);
        drawLockedDoor(g2d);
        drawWallMechanism(g2d);
        drawAncientClock(g2d);
        drawPillars(g2d);
        drawTorches(g2d);
        drawScrollTable(g2d);
        drawStoneStatue(g2d);
        drawCentralPedestal(g2d);

        // Atmospheric lighting, particles & feedback
        drawAtmosphericLighting(g2d);
        drawDustParticles(g2d);
        drawInteractivityFeedback(g2d);

        // 4. Draw HUD & Controls
        drawHUD(g2d);
        drawMessageBar(g2d);
        drawButton(g2d, inventoryButton);
        drawButton(g2d, returnLevel2Button);
        drawButton(g2d, level4Button);

        g2d.dispose();
    }

    // =========================================================================
    // DRAWING METHODS (SPECIFIED BY STEP 11A)
    // =========================================================================

    /**
     * drawBackground(Graphics2D g)
     * Clears letterbox borders and backdrop with deep subterranean mystery navy.
     */
    public void drawBackground(Graphics2D g) {
        g.setColor(new Color(6, 6, 12));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * drawStoneWalls(Graphics2D g)
     * Ancient grey stone masonry with ashlar courses, mortar seams, and Gothic arch ribs.
     */
    public void drawStoneWalls(Graphics2D g) {
        // Deep subterranean stone gradient
        LinearGradientPaint wallGrad = new LinearGradientPaint(
            0, 0, 0, 460,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(20, 18, 30), new Color(34, 32, 46), new Color(22, 20, 32)}
        );
        g.setPaint(wallGrad);
        g.fillRect(0, 0, LOGICAL_WIDTH, 460);

        // Stone ashlar masonry courses with mortar joints
        int blockH = 34;
        int numCourses = 460 / blockH + 1;
        for (int c = 0; c < numCourses; c++) {
            int y = c * blockH;
            int offset = (c % 2 == 0) ? 0 : 55;

            // Course horizontal mortar groove
            g.setColor(new Color(12, 10, 18));
            g.drawLine(0, y, LOGICAL_WIDTH, y);
            g.setColor(new Color(48, 44, 62, 120));
            g.drawLine(0, y + 1, LOGICAL_WIDTH, y + 1);

            // Vertical block seams
            int blockW = 110;
            for (int x = offset - blockW; x < LOGICAL_WIDTH + blockW; x += blockW) {
                g.setColor(new Color(12, 10, 18));
                g.drawLine(x, y, x, y + blockH);
                g.setColor(new Color(48, 44, 62, 90));
                g.drawLine(x + 1, y, x + 1, y + blockH);

                // Subtle individual stone variance
                if ((x + c * 17) % 7 == 0) {
                    g.setColor(new Color(255, 255, 255, 6));
                    g.fillRect(x + 2, y + 2, blockW - 4, blockH - 4);
                } else if ((x + c * 13) % 5 == 0) {
                    g.setColor(new Color(0, 0, 0, 25));
                    g.fillRect(x + 2, y + 2, blockW - 4, blockH - 4);
                }
            }
        }

        // Upper Gothic ceiling arch ribs
        g.setColor(new Color(16, 14, 24));
        g.setStroke(new BasicStroke(4.0f));
        g.drawArc(60, -120, 480, 400, 180, 180);
        g.drawArc(740, -120, 480, 400, 180, 180);
        g.drawArc(380, -160, 520, 440, 180, 180);

        // Decorative carved stone runic molding frieze above floor line
        g.setColor(new Color(40, 36, 52));
        g.fillRect(0, 442, LOGICAL_WIDTH, 18);
        g.setColor(new Color(212, 175, 55, 120));
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(0, 442, LOGICAL_WIDTH, 442);
        g.drawLine(0, 460, LOGICAL_WIDTH, 460);

        // Subtle gold runic wave on frieze
        g.setColor(new Color(255, 215, 64, 70));
        for (int x = 20; x < LOGICAL_WIDTH; x += 40) {
            g.drawRect(x, 446, 12, 10);
            g.drawLine(x + 6, 446, x + 6, 456);
        }
    }

    /**
     * drawFloor(Graphics2D g)
     * Ancient stone floor with perspective flagstones and tile texture.
     */
    public void drawFloor(Graphics2D g) {
        LinearGradientPaint floorGrad = new LinearGradientPaint(
            0, 460, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.4f, 1.0f},
            new Color[]{new Color(24, 22, 34), new Color(18, 16, 26), new Color(12, 10, 18)}
        );
        g.setPaint(floorGrad);
        g.fillRect(0, 460, LOGICAL_WIDTH, LOGICAL_HEIGHT - 460);

        // Perspective horizontal stone courses
        int[] floorRows = {460, 495, 535, 580, 630, 685, 720};
        for (int i = 0; i < floorRows.length - 1; i++) {
            int y = floorRows[i];
            g.setColor(new Color(10, 8, 14));
            g.drawLine(0, y, LOGICAL_WIDTH, y);
            g.setColor(new Color(55, 50, 70, 90));
            g.drawLine(0, y + 1, LOGICAL_WIDTH, y + 1);

            // Vertical slab cuts in perspective
            int stepW = 120 + i * 20;
            int offset = (i % 2 == 0) ? 0 : stepW / 2;
            for (int x = offset; x < LOGICAL_WIDTH + stepW; x += stepW) {
                g.setColor(new Color(10, 8, 14));
                g.drawLine(x, y, x, floorRows[i + 1]);
                g.setColor(new Color(55, 50, 70, 60));
                g.drawLine(x + 1, y, x + 1, floorRows[i + 1]);
            }
        }
    }

    /**
     * drawPillars(Graphics2D g)
     * Three decorative stone pillars with gold accent bands and capitals.
     */
    public void drawPillars(Graphics2D g) {
        // Pillar 1: Left
        drawSinglePillar(g, 130, 95, 65, 425);

        // Pillar 2: Center-Left (flanking mechanism)
        drawSinglePillar(g, 400, 95, 65, 425);

        // Pillar 3: Far Right
        drawSinglePillar(g, 1070, 95, 65, 425);
    }

    private void drawSinglePillar(Graphics2D g, int x, int y, int w, int h) {
        // Pillar drop shadow
        g.setColor(new Color(6, 5, 12, 170));
        g.fillRect(x + 4, y + 6, w, h);

        // Ornate stepped top capital
        LinearGradientPaint capGrad = new LinearGradientPaint(
            x - 8, y, x + w + 8, y + 26,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(55, 50, 70), new Color(85, 80, 105), new Color(45, 40, 58)}
        );
        g.setPaint(capGrad);
        g.fillRect(x - 8, y, w + 16, 26);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRect(x - 8, y, w + 16, 26);

        // Pillar column shaft with 3D cylindrical shading
        LinearGradientPaint shaftGrad = new LinearGradientPaint(
            x, y + 26, x + w, y + 26,
            new float[]{0.0f, 0.25f, 0.7f, 1.0f},
            new Color[]{new Color(36, 32, 48), new Color(68, 64, 85), new Color(52, 48, 68), new Color(28, 24, 38)}
        );
        g.setPaint(shaftGrad);
        g.fillRect(x, y + 26, w, h - 56);

        // Vertical fluting grooves
        g.setColor(new Color(16, 14, 22, 180));
        g.setStroke(new BasicStroke(1.6f));
        g.drawLine(x + 12, y + 28, x + 12, y + h - 32);
        g.drawLine(x + w / 2, y + 28, x + w / 2, y + h - 32);
        g.drawLine(x + w - 12, y + 28, x + w - 12, y + h - 32);

        // Engraved gold runic ring bands
        g.setColor(new Color(212, 175, 55, 160));
        g.fillRect(x, y + 80, w, 6);
        g.fillRect(x, y + 200, w, 6);
        g.fillRect(x, y + 320, w, 6);

        // Stepped base plinth
        g.setPaint(capGrad);
        g.fillRect(x - 6, y + h - 30, w + 12, 30);
        g.setColor(new Color(212, 175, 55));
        g.drawRect(x - 6, y + h - 30, w + 12, 30);
    }

    /**
     * drawCentralPedestal(Graphics2D g)
     * Large central puzzle pedestal with glowing cyan orb (main visual attraction).
     */
    public void drawCentralPedestal(Graphics2D g) {
        int px = 530;
        int py = 370;
        int pw = 220;
        int ph = 210;
        int cx = px + pw / 2;

        // Pedestal drop shadow on floor symbol
        g.setColor(new Color(4, 4, 10, 200));
        g.fillOval(px + 10, py + ph - 25, pw - 20, 45);

        // Stepped octagonal base tier 1
        LinearGradientPaint baseGrad = new LinearGradientPaint(
            px, py + ph - 45, px + pw, py + ph,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(50, 45, 62), new Color(78, 72, 98), new Color(40, 35, 50)}
        );
        g.setPaint(baseGrad);
        g.fillRoundRect(px, py + ph - 45, pw, 35, 12, 12);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(px, py + ph - 45, pw, 35, 12, 12);

        // Stepped base tier 2
        g.setPaint(baseGrad);
        g.fillRoundRect(px + 20, py + ph - 70, pw - 40, 28, 10, 10);
        g.drawRoundRect(px + 20, py + ph - 70, pw - 40, 28, 10, 10);

        // Central pillar shaft
        LinearGradientPaint shaftGrad = new LinearGradientPaint(
            px + 45, py + 55, px + pw - 45, py + 55,
            new float[]{0.0f, 0.3f, 0.7f, 1.0f},
            new Color[]{new Color(42, 38, 54), new Color(70, 65, 88), new Color(55, 50, 70), new Color(34, 30, 44)}
        );
        g.setPaint(shaftGrad);
        g.fillRect(px + 45, py + 55, pw - 90, ph - 125);
        g.setColor(new Color(25, 20, 32));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRect(px + 45, py + 55, pw - 90, ph - 125);

        // Golden glyph inlays along pedestal shaft
        g.setColor(new Color(255, 215, 64, 200));
        g.setStroke(new BasicStroke(1.6f));
        for (int y = py + 75; y < py + ph - 85; y += 24) {
            g.drawRect(cx - 10, y, 20, 12);
            g.drawLine(cx, y, cx, y + 12);
        }

        // Top altar basin & stone cornice
        LinearGradientPaint altarGrad = new LinearGradientPaint(
            px + 10, py + 25, px + pw - 10, py + 55,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(60, 55, 75), new Color(90, 85, 115), new Color(48, 42, 60)}
        );
        g.setPaint(altarGrad);
        g.fillRoundRect(px + 10, py + 25, pw - 20, 32, 14, 14);
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(px + 10, py + 25, pw - 20, 32, 14, 14);

        // Glowing crystal orb / puzzle mechanism on altar
        float flicker = (float) Math.sin(flickerPhase * 2.0f) * 6.0f;
        RadialGradientPaint orbBloom = new RadialGradientPaint(
            cx, py + 15, 85.0f + flicker,
            new float[]{0.0f, 0.4f, 0.8f, 1.0f},
            new Color[]{
                new Color(110, 255, 240, 220),
                new Color(40, 200, 210, 110),
                new Color(15, 90, 120, 35),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(orbBloom);
        g.fillOval((int) (cx - (85 + flicker)), (int) (py + 15 - (85 + flicker)), (int) ((85 + flicker) * 2), (int) ((85 + flicker) * 2));

        // Core cyan glowing sphere
        LinearGradientPaint orb = new LinearGradientPaint(
            cx - 18, py - 6, cx + 18, py + 30,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(240, 255, 255), new Color(70, 230, 220), new Color(15, 120, 140)}
        );
        g.setPaint(orb);
        g.fillOval(cx - 18, py - 6, 36, 36);
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.4f));
        g.drawOval(cx - 18, py - 6, 36, 36);

        // Brass support prongs gripping the orb
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.4f));
        g.drawArc(cx - 24, py + 4, 48, 22, 180, 180);
    }

    /**
     * drawWallMechanism(Graphics2D g)
     * Circular brass and stone wall mechanism with concentric rings and glowing teal core.
     */
    public void drawWallMechanism(Graphics2D g) {
        int mx = 560;
        int my = 90;
        int mw = 160;
        int mh = 160;
        int cx = mx + mw / 2;
        int cy = my + mh / 2;

        // Shadow
        g.setColor(new Color(6, 5, 12, 170));
        g.fillOval(mx + 3, my + 5, mw, mh);

        // Outer stone gear ring
        LinearGradientPaint ring1 = new LinearGradientPaint(
            mx, my, mx + mw, my + mh,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(55, 50, 70), new Color(75, 70, 95), new Color(42, 38, 55)}
        );
        g.setPaint(ring1);
        g.fillOval(mx, my, mw, mh);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(mx, my, mw, mh);

        // Outer ring cogs / teeth
        for (int a = 0; a < 360; a += 30) {
            double rad = Math.toRadians(a);
            int tx = (int) (cx + Math.cos(rad) * 78);
            int ty = (int) (cy + Math.sin(rad) * 78);
            g.setColor(new Color(212, 175, 55));
            g.fillRect(tx - 4, ty - 4, 8, 8);
        }

        // Middle brass celestial ring
        int r2 = 120;
        LinearGradientPaint ring2 = new LinearGradientPaint(
            cx - r2 / 2, cy - r2 / 2, cx + r2 / 2, cy + r2 / 2,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(145, 105, 30), new Color(212, 175, 55), new Color(110, 80, 22)}
        );
        g.setPaint(ring2);
        g.fillOval(cx - r2 / 2, cy - r2 / 2, r2, r2);
        g.setColor(new Color(30, 20, 10));
        g.drawOval(cx - r2 / 2, cy - r2 / 2, r2, r2);

        // Star / astrological lines on brass ring
        g.setColor(new Color(255, 235, 140, 190));
        g.setStroke(new BasicStroke(1.2f));
        for (int a = 0; a < 360; a += 45) {
            double rad = Math.toRadians(a);
            int x1 = (int) (cx + Math.cos(rad) * 40);
            int y1 = (int) (cy + Math.sin(rad) * 40);
            int x2 = (int) (cx + Math.cos(rad) * 58);
            int y2 = (int) (cy + Math.sin(rad) * 58);
            g.drawLine(x1, y1, x2, y2);
        }

        // Inner glowing teal mechanical core
        int r3 = 70;
        g.setColor(new Color(16, 18, 28));
        g.fillOval(cx - r3 / 2, cy - r3 / 2, r3, r3);

        // Soft teal pulsating radial bloom
        float flicker = (float) Math.sin(flickerPhase * 2.5f) * 4.0f;
        RadialGradientPaint tealBloom = new RadialGradientPaint(
            cx, cy, 38.0f + flicker,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(90, 255, 235, 230), new Color(30, 180, 185, 120), new Color(10, 60, 80, 0)}
        );
        g.setPaint(tealBloom);
        g.fillOval((int) (cx - (38 + flicker)), (int) (cy - (38 + flicker)), (int) ((38 + flicker) * 2), (int) ((38 + flicker) * 2));

        // Intricate center gold gear
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.6f));
        g.drawOval(cx - 12, cy - 12, 24, 24);
        g.drawLine(cx - 16, cy, cx + 16, cy);
        g.drawLine(cx, cy - 16, cx, cy + 16);
    }

    /**
     * drawAncientClock(Graphics2D g)
     * Ancient astronomical clock with Roman numerals and celestial markings.
     */
    public void drawAncientClock(Graphics2D g) {
        int cx = 220;
        int cy = 110;
        int cw = 75;
        int ch = 120;

        // Shadow
        g.setColor(new Color(6, 5, 12, 170));
        g.fillRect(cx + 3, cy + 5, cw, ch);

        // Carved stone bracket casing
        LinearGradientPaint caseGrad = new LinearGradientPaint(
            cx, cy, cx + cw, cy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(50, 46, 62), new Color(72, 68, 88), new Color(40, 36, 50)}
        );
        g.setPaint(caseGrad);
        g.fillRect(cx, cy, cw, ch);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRect(cx, cy, cw, ch);

        // Bronze clock dial
        int dialSize = 52;
        int dx = cx + (cw - dialSize) / 2;
        int dy = cy + 12;

        LinearGradientPaint dialGrad = new LinearGradientPaint(
            dx, dy, dx + dialSize, dy + dialSize,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(235, 215, 160), new Color(212, 175, 55), new Color(150, 110, 35)}
        );
        g.setPaint(dialGrad);
        g.fillOval(dx, dy, dialSize, dialSize);
        g.setColor(new Color(45, 30, 12));
        g.setStroke(new BasicStroke(1.6f));
        g.drawOval(dx, dy, dialSize, dialSize);

        // Roman astrological numerals
        Font rf = new Font("Georgia", Font.BOLD, 8);
        g.setFont(rf);
        g.setColor(new Color(40, 25, 10));
        g.drawString("XII", dx + 20, dy + 11);
        g.drawString("VI", dx + 22, dy + dialSize - 4);
        g.drawString("IX", dx + 5, dy + 29);
        g.drawString("III", dx + dialSize - 16, dy + 29);

        // Hands (locked at 3:00)
        int centerDX = dx + dialSize / 2;
        int centerDY = dy + dialSize / 2;
        g.setColor(new Color(25, 16, 8));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(centerDX, centerDY, centerDX + 14, centerDY);
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(centerDX, centerDY, centerDX, centerDY - 16);
        g.fillOval(centerDX - 2, centerDY - 2, 4, 4);

        // Exposed brass escapement gear visible beneath dial
        g.setColor(new Color(185, 145, 45));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(cx + cw / 2 - 14, cy + 78, 28, 28);
        g.drawLine(cx + cw / 2 - 14, cy + 92, cx + cw / 2 + 14, cy + 92);
        g.drawLine(cx + cw / 2, cy + 78, cx + cw / 2, cy + 106);
    }

    /**
     * drawLockedDoor(Graphics2D g)
     * Massive locked stone door with bronze reinforcement bars, studs, and keyhole.
     */
    public void drawLockedDoor(Graphics2D g) {
        int dx = 880;
        int dy = 110;
        int dw = 150;
        int dh = 330;

        // Shadow cast on wall
        g.setColor(new Color(6, 5, 10, 180));
        g.fillRect(dx + 6, dy + 8, dw, dh);

        // Outer stone portal frame with arched pediment
        LinearGradientPaint frameGrad = new LinearGradientPaint(
            dx - 12, dy - 16, dx + dw + 12, dy + dh,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(50, 46, 62), new Color(75, 70, 92), new Color(42, 38, 54)}
        );
        g.setPaint(frameGrad);
        g.fillRect(dx - 12, dy - 16, dw + 24, dh + 16);

        // Carved frame inner bevel & keystone
        g.setColor(new Color(22, 18, 28));
        g.drawRect(dx - 12, dy - 16, dw + 24, dh + 16);
        g.fillRect(dx + dw / 2 - 16, dy - 22, 32, 20);
        g.setColor(new Color(212, 175, 55));
        g.drawRect(dx + dw / 2 - 16, dy - 22, 32, 20);

        // Massive twin stone door panels
        LinearGradientPaint doorGrad = new LinearGradientPaint(
            dx, dy, dx + dw, dy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(36, 32, 45), new Color(48, 44, 60), new Color(30, 26, 38)}
        );
        g.setPaint(doorGrad);
        g.fillRect(dx, dy, dw, dh);

        // Vertical central door seam
        g.setColor(new Color(12, 10, 16));
        g.setStroke(new BasicStroke(3.0f));
        g.drawLine(dx + dw / 2, dy, dx + dw / 2, dy + dh);

        // Heavy reinforced horizontal bronze locking bars
        int[] barY = {dy + 45, dy + 155, dy + 265};
        for (int by : barY) {
            LinearGradientPaint bar = new LinearGradientPaint(
                dx + 4, by, dx + dw - 4, by + 18,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{new Color(110, 80, 32), new Color(175, 135, 50), new Color(90, 65, 24)}
            );
            g.setPaint(bar);
            g.fillRoundRect(dx + 6, by, dw - 12, 16, 4, 4);
            g.setColor(new Color(255, 215, 64));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(dx + 6, by, dw - 12, 16, 4, 4);

            // Heavy rivets on bars
            for (int rx = dx + 18; rx < dx + dw - 10; rx += 28) {
                g.setColor(new Color(255, 235, 140));
                g.fillOval(rx, by + 4, 6, 6);
                g.setColor(new Color(60, 40, 12));
                g.drawOval(rx, by + 4, 6, 6);
            }
        }

        // Central runic lock core
        int lcx = dx + dw / 2;
        int lcy = dy + 155 + 8;
        g.setColor(new Color(255, 215, 64));
        g.fillOval(lcx - 16, lcy - 16, 32, 32);
        g.setColor(new Color(60, 42, 12));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(lcx - 16, lcy - 16, 32, 32);

        // Keyhole
        g.setColor(new Color(16, 12, 18));
        g.fillOval(lcx - 4, lcy - 8, 8, 8);
        g.fillRect(lcx - 2, lcy - 2, 4, 10);
    }

    /**
     * drawTorches(Graphics2D g)
     * Two wall-mounted iron torches with flickering flames and warm radial glow.
     */
    public void drawTorches(Graphics2D g) {
        // Torch 1: Left wall
        drawSingleTorch(g, 75, 190);

        // Torch 2: Flanking door on right
        drawSingleTorch(g, 815, 190);
    }

    private void drawSingleTorch(Graphics2D g, int x, int y) {
        // Iron bracket sconce with metal GradientPaint
        GradientPaint sconcePaint = new GradientPaint(x + 12, y + 35, new Color(50, 40, 56), x + 22, y + 75, new Color(18, 14, 22));
        g.setPaint(sconcePaint);
        g.fillRect(x + 12, y + 35, 10, 40);
        g.setColor(new Color(90, 75, 60));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRect(x + 12, y + 35, 10, 40);

        // Diagonal bracket support
        g.drawLine(x + 12, y + 65, x - 6, y + 75);

        // Torch cup holder
        g.setColor(new Color(60, 45, 30));
        g.fillRoundRect(x + 6, y + 20, 22, 18, 4, 4);
        g.setColor(new Color(212, 175, 55));
        g.drawRoundRect(x + 6, y + 20, 22, 18, 4, 4);

        // Flickering warm orange flame bloom with AlphaComposite
        Composite origComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

        float flicker = (float) Math.sin(flickerPhase * 3.0f + x) * 5.0f;
        RadialGradientPaint torchBloom = new RadialGradientPaint(
            x + 17, y + 10, 110.0f + flicker,
            new float[]{0.0f, 0.35f, 0.75f, 1.0f},
            new Color[]{
                new Color(255, 235, 120, 140),
                new Color(255, 160, 40, 75),
                new Color(210, 80, 20, 25),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(torchBloom);
        g.fillOval((int) (x + 17 - (110 + flicker)), (int) (y + 10 - (110 + flicker)), (int) ((110 + flicker) * 2), (int) ((110 + flicker) * 2));

        // Flame tongue illustration
        GeneralPath flame = new GeneralPath();
        flame.moveTo(x + 17, y - 8 + (int) (flicker * 0.4f));
        flame.curveTo(x + 28, y + 8, x + 24, y + 22, x + 17, y + 22);
        flame.curveTo(x + 10, y + 22, x + 6, y + 8, x + 17, y - 8 + (int) (flicker * 0.4f));
        flame.closePath();

        LinearGradientPaint flameGrad = new LinearGradientPaint(
            x + 17, y - 8, x + 17, y + 22,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(255, 255, 220), new Color(255, 180, 40), new Color(220, 60, 15)}
        );
        g.setPaint(flameGrad);
        g.fill(flame);

        g.setComposite(origComposite);
    }

    /**
     * drawFloorSymbol(Graphics2D g)
     * Engraved golden alchemical symbol on the floor in perspective.
     */
    public void drawFloorSymbol(Graphics2D g) {
        int cx = 640;
        int cy = 570;
        int rx = 180;
        int ry = 60;

        // Soft ambient golden floor glow
        RadialGradientPaint symbolGlow = new RadialGradientPaint(
            cx, cy, 220.0f,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 215, 64, 45), new Color(212, 175, 55, 18), new Color(0, 0, 0, 0)}
        );
        g.setPaint(symbolGlow);
        g.fillOval(cx - 220, cy - 80, 440, 160);

        // Outer concentric engraved ellipses
        g.setColor(new Color(212, 175, 55, 190));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(cx - rx, cy - ry, rx * 2, ry * 2);
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(cx - rx + 14, cy - ry + 5, (rx - 14) * 2, (ry - 5) * 2);
        g.drawOval(cx - rx + 30, cy - ry + 10, (rx - 30) * 2, (ry - 10) * 2);

        // 8-Pointed golden star geometry in perspective
        g.setColor(new Color(255, 225, 90, 160));
        g.setStroke(new BasicStroke(1.4f));
        int[][] pts = {
            {cx, cy - ry + 10}, {cx + 60, cy - 15}, {cx + rx - 20, cy}, {cx + 60, cy + 15},
            {cx, cy + ry - 10}, {cx - 60, cy + 15}, {cx - rx + 20, cy}, {cx - 60, cy - 15}
        };
        for (int i = 0; i < pts.length; i++) {
            int next = (i + 1) % pts.length;
            int opp = (i + 4) % pts.length;
            g.drawLine(pts[i][0], pts[i][1], pts[next][0], pts[next][1]);
            g.drawLine(pts[i][0], pts[i][1], pts[opp][0], pts[opp][1]);
        }

        // Inner circular core
        g.setColor(new Color(255, 215, 64, 220));
        g.drawOval(cx - 35, cy - 12, 70, 24);
        g.fillOval(cx - 8, cy - 3, 16, 6);
    }

    /**
     * drawScrollTable(Graphics2D g)
     * Small antique stone/wood table containing old parchment scrolls and manuscripts.
     */
    public void drawScrollTable(Graphics2D g) {
        int tx = 50;
        int ty = 430;
        int tw = 150;
        int th = 115;

        // Table drop shadow
        g.setColor(new Color(6, 5, 12, 170));
        g.fillRoundRect(tx + 4, ty + 15, tw, th - 10, 12, 12);

        // Stone table slab & legs with GradientPaint
        GradientPaint tableGrad = new GradientPaint(
            tx, ty, new Color(75, 68, 88),
            tx + tw, ty + th, new Color(42, 36, 52)
        );
        g.setPaint(tableGrad);

        // Legs
        g.fillRect(tx + 12, ty + 20, 18, th - 20);
        g.fillRect(tx + tw - 30, ty + 20, 18, th - 20);
        g.setColor(new Color(20, 16, 26));
        g.drawRect(tx + 12, ty + 20, 18, th - 20);
        g.drawRect(tx + tw - 30, ty + 20, 18, th - 20);

        // Table top slab
        g.setPaint(tableGrad);
        g.fillRoundRect(tx, ty + 4, tw, 20, 8, 8);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(tx, ty + 4, tw, 20, 8, 8);

        // Ancient rolled parchment scrolls
        drawScroll(g, tx + 20, ty - 4, 45, 12, new Color(245, 230, 195), new Color(180, 40, 40));
        drawScroll(g, tx + 45, ty - 12, 50, 12, new Color(235, 215, 180), new Color(40, 80, 150));
        drawScroll(g, tx + 75, ty - 6, 48, 12, new Color(240, 225, 190), new Color(160, 120, 40));

        // Small unrolled star chart fragment
        g.setColor(new Color(230, 215, 180));
        g.fillRect(tx + 25, ty + 7, 70, 14);
        g.setColor(new Color(80, 50, 30));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRect(tx + 25, ty + 7, 70, 14);
        g.drawLine(tx + 35, ty + 14, tx + 85, ty + 14);
    }

    private void drawScroll(Graphics2D g, int x, int y, int w, int h, Color paperColor, Color ribbonColor) {
        g.setColor(paperColor);
        g.fillRoundRect(x, y, w, h, 6, 6);
        g.setColor(new Color(140, 110, 70));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(x, y, w, h, 6, 6);

        // Ribbon seal in center
        g.setColor(ribbonColor);
        g.fillRect(x + w / 2 - 3, y, 6, h);
    }

    /**
     * drawStoneStatue(Graphics2D g)
     * Ancient carved stone guardian statue holding a sentinel staff.
     */
    public void drawStoneStatue(Graphics2D g) {
        int sx = 1090;
        int sy = 330;
        int sw = 120;
        int sh = 240;

        // Statue drop shadow
        g.setColor(new Color(6, 5, 12, 170));
        g.fillRoundRect(sx + 6, sy + 15, sw, sh - 10, 16, 16);

        // Stepped stone pedestal with GradientPaint
        GradientPaint pedGrad = new GradientPaint(
            sx, sy + sh - 40, new Color(75, 70, 92),
            sx + sw, sy + sh, new Color(40, 35, 50)
        );
        g.setPaint(pedGrad);
        g.fillRect(sx + 10, sy + sh - 40, sw - 20, 40);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRect(sx + 10, sy + sh - 40, sw - 20, 40);

        // Guardian robe & silhouette
        GradientPaint robeGrad = new GradientPaint(
            sx, sy, new Color(75, 70, 95),
            sx + sw, sy + sh, new Color(34, 30, 45)
        );
        g.setPaint(robeGrad);

        // Hooded head & cowl
        g.fillOval(sx + sw / 2 - 18, sy + 15, 36, 40);

        // Robed torso & flared cowl
        GeneralPath body = new GeneralPath();
        body.moveTo(sx + sw / 2 - 14, sy + 50);
        body.lineTo(sx + 24, sy + sh - 40);
        body.lineTo(sx + sw - 24, sy + sh - 40);
        body.lineTo(sx + sw / 2 + 14, sy + 50);
        body.closePath();
        g.fill(body);

        // Stone folds & details
        g.setColor(new Color(24, 20, 32));
        g.setStroke(new BasicStroke(1.8f));
        g.drawLine(sx + sw / 2, sy + 55, sx + sw / 2 - 10, sy + sh - 42);
        g.drawLine(sx + sw / 2, sy + 55, sx + sw / 2 + 12, sy + sh - 42);

        // Guardian's ceremonial brass staff
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(3.0f));
        g.drawLine(sx + sw - 18, sy + 10, sx + sw - 18, sy + sh - 40);

        // Staff orb head
        g.setColor(new Color(255, 225, 90));
        g.fillOval(sx + sw - 25, sy + 5, 14, 14);
        g.setColor(new Color(150, 110, 30));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(sx + sw - 25, sy + 5, 14, 14);
    }

    /**
     * drawHUD(Graphics2D g)
     * Reusable top HUD displaying Player name, room title, score, and dynamic time.
     */
    public void drawHUD(Graphics2D g) {
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

        // 2. Room Title: LEVEL 3 — MYSTERY CHAMBER
        String roomTitle = "LEVEL 3 \u2014 MYSTERY CHAMBER";
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

    /**
     * drawMessageBar(Graphics2D g)
     * Bottom message bar displaying inspection descriptions or hover object names.
     */
    public void drawMessageBar(Graphics2D g) {
        int mbX = 60;
        int mbY = 648;
        int mbW = 960;
        int mbH = 48;
        int arc = 16;

        String messageToDisplay = getCurrentMessage();

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

    // =========================================================================
    // LIGHTING, PARTICLES & FEEDBACK
    // =========================================================================

    private void drawAtmosphericLighting(Graphics2D g) {
        Composite orig = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

        RadialGradientPaint ambVig = new RadialGradientPaint(
            640.0f, 360.0f, 760.0f,
            new float[]{0.0f, 0.65f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(18, 12, 32, 75), new Color(6, 4, 14, 215)}
        );
        g.setPaint(ambVig);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        g.setComposite(orig);
    }

    private void drawDustParticles(Graphics2D g) {
        Composite orig = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f));

        g.setColor(new Color(255, 235, 170, 75));
        for (DustParticle p : dustParticles) {
            g.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }

        g.setComposite(orig);
    }

    private void drawInteractivityFeedback(Graphics2D g) {
        Composite orig = g.getComposite();

        // 1. Subtle Golden Click Interaction Animation & Highlight
        if (clickedObject != null && clickFeedbackTicks > 0) {
            Rectangle cr = clickedObject.getBounds();
            if ("torches".equals(clickedObject.getId()) && lastClickedPoint != null && lastClickedPoint.x >= 700) {
                cr = new Rectangle(815, 180, 40, 85);
            } else if ("pillars".equals(clickedObject.getId()) && lastClickedPoint != null) {
                if (lastClickedPoint.x >= 900) cr = new Rectangle(1070, 95, 65, 425);
                else if (lastClickedPoint.x >= 350) cr = new Rectangle(400, 95, 65, 425);
            }

            float progress = (12.0f - clickFeedbackTicks) / 12.0f;

            // Soft golden highlight fill
            int fillAlpha = (int) ((1.0f - progress) * 75);
            g.setColor(new Color(255, 235, 140, Math.max(0, fillAlpha)));
            g.fillRoundRect(cr.x - 3, cr.y - 3, cr.width + 6, cr.height + 6, 12, 12);

            // Bold golden border highlight
            int borderAlpha = (int) ((1.0f - progress * 0.5f) * 230);
            g.setColor(new Color(255, 215, 64, Math.max(0, borderAlpha)));
            g.setStroke(new BasicStroke(2.8f));
            g.drawRoundRect(cr.x - 3, cr.y - 3, cr.width + 6, cr.height + 6, 12, 12);

            // Expanding golden interaction pulse ripple
            int expand = (int) (progress * 18);
            float rippleAlpha = Math.max(0.0f, (1.0f - progress) * 0.85f);
            g.setColor(new Color(1.0f, 0.84f, 0.25f, rippleAlpha));
            g.setStroke(new BasicStroke(Math.max(1.0f, 2.4f * (1.0f - progress * 0.5f))));
            g.drawRoundRect(cr.x - 3 - expand, cr.y - 3 - expand,
                            cr.width + 6 + expand * 2, cr.height + 6 + expand * 2, 16, 16);

            // Sparkle pulse diamond at click point
            if (lastClickedPoint != null) {
                int starSize = (int) (3 + progress * 10);
                drawDiamond(g, lastClickedPoint.x, lastClickedPoint.y, starSize);
            }
        }

        // 2. Elegant Hover Outline, Brightening & Tooltip Badge
        if (hoveredObject != null) {
            Rectangle r = hoveredObject.getBounds();
            if ("torches".equals(hoveredObject.getId()) && lastMousePoint != null && lastMousePoint.x >= 700) {
                r = new Rectangle(815, 180, 40, 85);
            } else if ("pillars".equals(hoveredObject.getId()) && lastMousePoint != null) {
                if (lastMousePoint.x >= 900) r = new Rectangle(1070, 95, 65, 425);
                else if (lastMousePoint.x >= 350) r = new Rectangle(400, 95, 65, 425);
            }

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));

            // Subtle golden outline
            g.setColor(new Color(255, 215, 64, 195));
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 12, 12);

            // Subtle brightening fill
            g.setColor(new Color(255, 235, 140, 28));
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

        g.setComposite(orig);
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
        if (clickedObject != null && (hoveredObject == clickedObject || hoveredObject == null)) {
            return lastClickedDescription;
        }
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

    public MenuButton getReturnLevel2Button() {
        return returnLevel2Button;
    }

    public MenuButton getLevel4Button() {
        return level4Button;
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
                x = 80 + (float) (Math.random() * 1120);
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

        public void setHovered(boolean hovered) {
            this.hovered = hovered;
        }

        public boolean isHovered() {
            return hovered;
        }

        public void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

        public boolean isPressed() {
            return pressed;
        }
    }
}
