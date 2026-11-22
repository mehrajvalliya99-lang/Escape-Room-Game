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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Level 4 — The Final Escape Room Environment.
 * 
 * Implements a grand, atmospheric 2D escape chamber rendered entirely
 * with Java Swing, AWT and Graphics2D. Features a massive ornate golden exit door
 * as the primary focal attraction, flanked by fluted stone pillars, glowing wall glyphs,
 * ancient symbol relief plaques, animated wall mechanisms, blazing bronze torches,
 * an antique document desk with an hourglass, a central ceremonial plinth, and a
 * sacred geometric floor labyrinth.
 *
 * Member 1 (Frontend / 2D UI & Environment Specialist) implementation.
 */
public class FinalEscapePanel extends JPanel {

    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<GameObject> gameObjects;

    // Navigation and UI buttons
    private final MenuButton inventoryButton;
    private final MenuButton returnLevel3Button;

    // Interaction tracking state
    private GameObject hoveredObject = null;
    private String lastClickedDescription = "Explore the grand escape chamber to discover the final secrets...";
    private Point lastMousePoint = null;
    private Point lastClickedPoint = null;

    // Tactile click feedback pulse
    private GameObject clickedObject = null;
    private int clickFeedbackTicks = 0;

    // Viewport scaling and letterboxing
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Ambient animations: torch flickering, dust particles, and mechanical gear rotation
    private Timer ambientTimer;
    private float flickerPhase = 0.0f;
    private float gearRotation = 0.0f;
    private final List<DustParticle> dustParticles;

    public FinalEscapePanel(GameController controller) {
        this.controller = controller;
        this.gameObjects = new ArrayList<>();
        this.dustParticles = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(5, 5, 12));
        setFocusable(true);

        // 1. Initialize atmospheric golden floating motes
        for (int i = 0; i < 28; i++) {
            dustParticles.add(new DustParticle(
                60 + (float) (Math.random() * 1160),
                80 + (float) (Math.random() * 580),
                1 + (int) (Math.random() * 3)
            ));
        }

        // 2. Register all 10 interactive chamber objects
        initializeGameObjects();

        // 3. Navigation and HUD buttons
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

        this.returnLevel3Button = new MenuButton(
            "\u2190 Level 3",
            new Rectangle(875, 24, 105, 34),
            new Color(40, 24, 60), new Color(24, 14, 38),
            new Color(70, 42, 105), new Color(42, 25, 65),
            new Color(20, 10, 30),
            new Color(190, 155, 60),
            () -> {
                if (controller != null) {
                    controller.changeState(GameState.LEVEL_3);
                }
            }
        );

        // 4. Input handling
        setupMouseListeners();

        // 5. Ambient animation loop
        ambientTimer = new Timer(33, e -> {
            flickerPhase += 0.08f;
            if (flickerPhase > 1000.0f) flickerPhase = 0.0f;

            gearRotation += 0.015f;
            if (gearRotation > (float) (Math.PI * 2.0)) gearRotation = 0.0f;

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

    // =========================================================================
    // OBJECT INITIALIZATION (10 INTERACTIVE OBJECTS)
    // =========================================================================

    private void initializeGameObjects() {
        // Child & foreground objects first for accurate hit detection

        // 1. Combination Lock (mounted on the golden door) -> Final Combination Puzzle
        gameObjects.add(new GameObject(
            "combination_lock",
            "Combination Lock",
            "A complicated mechanism with several rotating sections.",
            new Rectangle(605, 235, 70, 80),
            () -> {
                // TODO: Member 2 - Final combination puzzle
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "combination_lock",
                        "Final Combination Puzzle",
                        "Align the concentric gold and iron dials to unlock the grand escape door.",
                        Puzzle.PuzzleType.COMBINATION,
                        "3 - 7 - 9 - 1",
                        "The dials align into the sacred ratio found on the chamber walls."
                    ));
                }
            }
        ));

        // 2. Hourglass (resting on the document table)
        gameObjects.add(new GameObject(
            "hourglass",
            "Hourglass",
            "An ancient hourglass. The remaining sand is slowly falling.",
            new Rectangle(405, 400, 38, 70),
            () -> {
                lastClickedDescription = "The golden sand trickles swiftly. Complete the final mechanisms before time expires!";
                repaint();
            }
        ));

        // 3. Central Pedestal (foreground plinth in front of door) -> Multi-Part Puzzle
        gameObjects.add(new GameObject(
            "pedestal",
            "Central Pedestal",
            "A pedestal with four empty spaces.",
            new Rectangle(560, 435, 160, 145),
            () -> {
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "pedestal_final",
                        "Multi-Part Puzzle",
                        "The ceremonial receptacle demands the ancient triangular master key and elemental stones.",
                        Puzzle.PuzzleType.PATTERN,
                        "KEY OF OSIRIS",
                        "Insert the golden master key gathered from the previous chambers."
                    ));
                }
            }
        ));

        // 4. Golden Exit Door (the grand focal portal)
        gameObjects.add(new GameObject(
            "golden_door",
            "Golden Exit Door",
            "The final door awaits the correct combination.",
            new Rectangle(515, 70, 250, 410),
            () -> {
                if (controller != null && controller.getLevelManager() != null) {
                    boolean canEscape = controller.getLevelManager().canEscapeFinalDoor(
                        controller.getPuzzleManager(),
                        controller.getInventoryManager()
                    );

                    if (canEscape) {
                        controller.getLevelManager().completeLevel(4);
                        if (controller.getScoreManager() != null) {
                            controller.getScoreManager().awardLevelCompletionScore(4);
                        }
                        int remaining = (controller.getGameTimer() != null) ? controller.getGameTimer().getRemainingSeconds() : 0;
                        int finalScore = (controller.getScoreManager() != null)
                            ? controller.getScoreManager().calculateFinalWinScore(remaining)
                            : 1000;
                        String timeStr = (controller.getGameTimer() != null)
                            ? controller.getGameTimer().getFormattedElapsedTime()
                            : "00:00";

                        controller.triggerWin(finalScore, timeStr, "4/4");
                    } else {
                        lastClickedDescription = "The Golden Door remains sealed! Disengage the Combination Lock, activate the Symbol Panels, and place the Master Key on the Pedestal.";
                        repaint();
                    }
                }
            }
        ));

        // 5. Document Table (left midground desk) -> Final Clue Puzzle
        gameObjects.add(new GameObject(
            "document_table",
            "Document Table",
            "Old documents covered in notes and strange markings.",
            new Rectangle(270, 430, 175, 125),
            () -> {
                // TODO: Member 2 - Final clue
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "document_table",
                        "Final Clue Puzzle",
                        "Decipher the faded notes describing the counterweight release sequence.",
                        Puzzle.PuzzleType.CIPHER,
                        "SANCTUARY",
                        "The parchment reveals an architectural schematic of the final locking rods."
                    ));
                }
            }
        ));

        // 6. Stone Pillars (left and right soaring columns)
        gameObjects.add(new GameObject(
            "pillars",
            "Stone Pillars",
            "Ancient pillars covered in faded symbols.",
            new Rectangle(90, 70, 80, 430),
            () -> {
                lastClickedDescription = "Ancient pillars inscribed with celestial glyphs: SUN - MOON - STAR - SERPENT.";
                repaint();
            }
        ));

        // 7. Symbol Panels (four relief plaques on walls) -> Symbol Sequence Puzzle
        gameObjects.add(new GameObject(
            "symbol_panels",
            "Symbol Panels",
            "Each panel contains a different mysterious symbol.",
            new Rectangle(200, 130, 90, 230),
            () -> {
                // TODO: Member 2 - Symbol sequence puzzle
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "symbol_panels",
                        "Symbol Sequence Puzzle",
                        "Depress the four relief plaques in astronomical sequence: Sun, Moon, Star, Serpent.",
                        Puzzle.PuzzleType.SYMBOL_SEQUENCE,
                        "SUN - MOON - STAR - SERPENT",
                        "Each plaque clicks inward, releasing a heavy brass locking counterweight."
                    ));
                }
            }
        ));

        // 8. Torches (wall sconces flanking the door)
        gameObjects.add(new GameObject(
            "torches",
            "Torches",
            "The flames cast unusual shadows across the symbols.",
            new Rectangle(465, 180, 35, 80),
            () -> {
                lastClickedDescription = "Brilliant golden flames illuminate the sacred labyrinth inlaid in the floor.";
                repaint();
            }
        ));

        // 9. Floor Pattern (concentric ceremonial labyrinth) -> Pattern Puzzle
        gameObjects.add(new GameObject(
            "floor_pattern",
            "Floor Pattern",
            "A circular pattern connects several symbols around the chamber.",
            new Rectangle(420, 520, 440, 150),
            () -> {
                // TODO: Member 2 - Pattern deduction
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "floor_pattern",
                        "Pattern Puzzle",
                        "Trace the unbroken gold ribbon from the perimeter into the golden pedestal.",
                        Puzzle.PuzzleType.PATTERN,
                        "LABYRINTHOS",
                        "The inlaid celestial runes illuminate when traced in correct order."
                    ));
                }
            }
        ));

        // 10. Wall Mechanisms (interlocking golden gear trains) -> Mechanism Puzzle
        gameObjects.add(new GameObject(
            "wall_mechanisms",
            "Wall Mechanisms",
            "Ancient mechanisms built into the walls.",
            new Rectangle(305, 75, 155, 85),
            () -> {
                // TODO: Member 2 - Wall mechanism puzzle
                if (controller != null) {
                    controller.openPuzzle(new Puzzle(
                        "wall_mechanisms",
                        "Mechanism Puzzle",
                        "Engage the differential brass gears to synchronize with the golden door bolts.",
                        Puzzle.PuzzleType.COMBINATION,
                        "SYNCHRONY",
                        "The gear teeth lock into place with a resounding hydraulic click."
                    ));
                }
            }
        ));
    }

    // =========================================================================
    // MOUSE LISTENER SETUP
    // =========================================================================

    private void setupMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                lastMousePoint = lp;

                boolean cursorHand = false;
                GameObject newlyHovered = null;

                // Priority check for objects
                for (GameObject obj : gameObjects) {
                    boolean hits = checkObjectHit(obj, lp);
                    obj.setHovered(hits);
                    if (hits && newlyHovered == null) {
                        newlyHovered = obj;
                        cursorHand = true;
                    }
                }

                if (newlyHovered != clickedObject) {
                    clickedObject = null;
                }
                hoveredObject = newlyHovered;

                // Button hover check
                boolean invHover = inventoryButton.contains(lp);
                inventoryButton.setHovered(invHover);
                if (invHover) cursorHand = true;

                boolean retHover = returnLevel3Button.contains(lp);
                returnLevel3Button.setHovered(retHover);
                if (retHover) cursorHand = true;

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
                    if (checkObjectHit(obj, lp)) {
                        hoveredObject = obj;
                        clickedObject = obj;
                        clickFeedbackTicks = 14;
                        lastClickedDescription = obj.getDescription();

                        obj.interact(); // Trigger puzzle / interaction hook
                        repaint();
                        return;
                    }
                }

                if (inventoryButton.contains(lp)) {
                    inventoryButton.setPressed(true);
                    repaint();
                }
                if (returnLevel3Button.contains(lp)) {
                    returnLevel3Button.setPressed(true);
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
                if (returnLevel3Button.isPressed()) {
                    returnLevel3Button.setPressed(false);
                    repaint();
                    if (returnLevel3Button.contains(lp)) {
                        returnLevel3Button.trigger();
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
                returnLevel3Button.setHovered(false);
                returnLevel3Button.setPressed(false);
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private boolean checkObjectHit(GameObject obj, Point lp) {
        if (obj == null || lp == null) return false;
        boolean hits = obj.contains(lp);

        // Multi-instance bounding hit support for paired fixtures
        if ("torches".equals(obj.getId())) {
            Rectangle r2 = new Rectangle(780, 180, 35, 80);
            if (r2.contains(lp)) hits = true;
        } else if ("pillars".equals(obj.getId())) {
            Rectangle p2 = new Rectangle(1110, 70, 80, 430);
            if (p2.contains(lp)) hits = true;
        } else if ("symbol_panels".equals(obj.getId())) {
            Rectangle sp2 = new Rectangle(990, 130, 90, 230);
            if (sp2.contains(lp)) hits = true;
        } else if ("wall_mechanisms".equals(obj.getId())) {
            Rectangle wm2 = new Rectangle(825, 75, 155, 85);
            if (wm2.contains(lp)) hits = true;
        }

        return hits;
    }

    // =========================================================================
    // VIEWPORT TRANSFORMATION & LETTERBOXING
    // =========================================================================

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

        // High quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 3. Draw Escape Chamber Artwork Layers (Ordered by depth)
        drawChamberWalls(g2d);
        drawWallSymbols(g2d);
        drawWallMechanisms(g2d);
        drawSymbolPanels(g2d);
        drawGoldenDoor(g2d);
        drawCombinationLock(g2d);
        drawStonePillars(g2d);
        drawTorches(g2d);
        drawFloor(g2d);
        drawFloorPattern(g2d);
        drawDocumentTable(g2d);
        drawHourglass(g2d);
        drawCentralPedestal(g2d);

        // Atmospheric lighting & particles
        drawAtmosphericLighting(g2d);
        drawDustParticles(g2d);
        drawInteractivityFeedback(g2d);

        // 4. Draw HUD & Controls
        drawHUD(g2d);
        drawMessageBar(g2d);
        drawButton(g2d, inventoryButton);
        drawButton(g2d, returnLevel3Button);

        g2d.dispose();
    }

    // =========================================================================
    // SPECIFIED DRAWING METHODS
    // =========================================================================

    /**
     * drawBackground(Graphics2D g)
     * Renders letterboxing and subterranean void background.
     */
    public void drawBackground(Graphics2D g) {
        int w = getWidth();
        int h = getHeight();
        g.setColor(new Color(4, 4, 10));
        g.fillRect(0, 0, w, h);
    }

    /**
     * drawChamberWalls(Graphics2D g)
     * Draws ancient stone ashlar masonry walls with deep navy and dark purple undertones.
     */
    public void drawChamberWalls(Graphics2D g) {
        // Upper wall gradient
        LinearGradientPaint wallGrad = new LinearGradientPaint(
            0, 60, 0, 480,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{
                new Color(16, 18, 32),
                new Color(24, 22, 40),
                new Color(14, 13, 26)
            }
        );
        g.setPaint(wallGrad);
        g.fillRect(0, 60, LOGICAL_WIDTH, 420);

        // Staggered stone blocks
        g.setStroke(new BasicStroke(1.2f));
        int rowHeight = 38;
        int blockWidth = 105;

        for (int y = 60, row = 0; y < 480; y += rowHeight, row++) {
            // Horizontal mortar shadow & bevel
            g.setColor(new Color(8, 8, 16, 220));
            g.drawLine(0, y, LOGICAL_WIDTH, y);
            g.setColor(new Color(48, 44, 70, 90));
            g.drawLine(0, y + 1, LOGICAL_WIDTH, y + 1);

            int xOffset = (row % 2 == 0) ? 0 : blockWidth / 2;
            for (int x = -xOffset; x < LOGICAL_WIDTH; x += blockWidth) {
                // Vertical mortar seam
                g.setColor(new Color(8, 8, 16, 200));
                g.drawLine(x, y, x, Math.min(y + rowHeight, 480));
                g.setColor(new Color(50, 46, 75, 75));
                g.drawLine(x + 1, y, x + 1, Math.min(y + rowHeight, 480));

                // Subtle block tone variety
                int hash = (row * 31 + x) & 0x7;
                if (hash == 1) {
                    g.setColor(new Color(36, 32, 54, 45));
                    g.fillRect(x + 2, y + 2, blockWidth - 3, rowHeight - 3);
                } else if (hash == 4) {
                    g.setColor(new Color(18, 26, 42, 40));
                    g.fillRect(x + 2, y + 2, blockWidth - 3, rowHeight - 3);
                }
            }
        }

        // Grand upper architrave cornice
        GradientPaint corniceGrad = new GradientPaint(
            0, 60, new Color(42, 38, 62),
            0, 78, new Color(18, 16, 28)
        );
        g.setPaint(corniceGrad);
        g.fillRect(0, 60, LOGICAL_WIDTH, 18);
        g.setColor(new Color(180, 145, 60, 110));
        g.drawLine(0, 78, LOGICAL_WIDTH, 78);
        g.setColor(new Color(10, 8, 18));
        g.drawLine(0, 79, LOGICAL_WIDTH, 79);

        // Soaring stone archway framing the central door
        int archX = 490;
        int archY = 56;
        int archW = 300;
        int archH = 424;

        g.setColor(new Color(12, 10, 20));
        g.fillRoundRect(archX - 6, archY - 6, archW + 12, archH + 12, 40, 40);

        GradientPaint archFrame = new GradientPaint(
            archX, archY, new Color(52, 45, 74),
            archX + archW, archY + archH, new Color(26, 22, 38)
        );
        g.setPaint(archFrame);
        g.fillRoundRect(archX, archY, archW, archH, 36, 36);

        // Gold relief trim around arch
        g.setColor(new Color(212, 175, 55, 140));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(archX + 4, archY + 4, archW - 8, archH - 8, 32, 32);

        // Keystone at arch apex
        int kx = 640;
        int ky = 52;
        GeneralPath keystone = new GeneralPath();
        keystone.moveTo(kx - 18, ky);
        keystone.lineTo(kx + 18, ky);
        keystone.lineTo(kx + 12, ky + 24);
        keystone.lineTo(kx - 12, ky + 24);
        keystone.closePath();
        g.setPaint(new GradientPaint(kx, ky, new Color(220, 180, 70), kx, ky + 24, new Color(130, 95, 30)));
        g.fill(keystone);
        g.setColor(new Color(60, 40, 10));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(keystone);
    }

    /**
     * drawFloor(Graphics2D g)
     * Draws ancient stone flagstones with receding perspective and golden door reflections.
     */
    public void drawFloor(Graphics2D g) {
        int floorY = 480;
        int floorH = LOGICAL_HEIGHT - floorY;

        // Base dark flagstone gradient
        LinearGradientPaint floorGrad = new LinearGradientPaint(
            0, floorY, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.4f, 1.0f},
            new Color[]{
                new Color(22, 20, 32),
                new Color(15, 14, 24),
                new Color(8, 7, 14)
            }
        );
        g.setPaint(floorGrad);
        g.fillRect(0, floorY, LOGICAL_WIDTH, floorH);

        // Perspective grid lines
        g.setStroke(new BasicStroke(1.0f));
        int vanishingX = 640;
        int vanishingY = 320;

        for (int x = -160; x <= LOGICAL_WIDTH + 160; x += 110) {
            g.setColor(new Color(36, 32, 52, 110));
            g.drawLine(vanishingX + (x - vanishingX) / 3, floorY, x, LOGICAL_HEIGHT);
        }

        // Horizontal depth stone courses
        int[] rowsY = {480, 502, 528, 560, 600, 650, 720};
        for (int i = 0; i < rowsY.length; i++) {
            int y = rowsY[i];
            g.setColor(new Color(6, 6, 12, 230));
            g.drawLine(0, y, LOGICAL_WIDTH, y);
            g.setColor(new Color(55, 48, 80, 90));
            g.drawLine(0, y + 1, LOGICAL_WIDTH, y + 1);
        }

        // Golden floor reflection radiating outward from the exit door
        RadialGradientPaint floorGlow = new RadialGradientPaint(
            640.0f, 490.0f, 380.0f,
            new float[]{0.0f, 0.35f, 0.8f, 1.0f},
            new Color[]{
                new Color(245, 205, 90, 85),
                new Color(212, 165, 45, 45),
                new Color(160, 110, 25, 15),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(floorGlow);
        g.fillRect(200, floorY, 880, floorH);
    }

    /**
     * drawFloorPattern(Graphics2D g)
     * Draws the mysterious concentric ceremonial floor labyrinth with inlaid lapis lazuli and gold.
     */
    public void drawFloorPattern(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int cx = 640;
        int cy = 575;
        int rx = 210;
        int ry = 62;

        // Outer inlaid ring
        g2.setColor(new Color(15, 25, 48, 160)); // Lapis lazuli base
        g2.fillOval(cx - rx, cy - ry, rx * 2, ry * 2);

        // Gold outer border
        g2.setColor(new Color(212, 175, 55, 175));
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawOval(cx - rx, cy - ry, rx * 2, ry * 2);

        // Intermediate concentric rings
        int rx2 = rx - 35;
        int ry2 = ry - 11;
        g2.setColor(new Color(180, 140, 45, 130));
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawOval(cx - rx2, cy - ry2, rx2 * 2, ry2 * 2);

        int rx3 = rx - 75;
        int ry3 = ry - 22;
        g2.setColor(new Color(20, 40, 75, 180));
        g2.fillOval(cx - rx3, cy - ry3, rx3 * 2, ry3 * 2);
        g2.setColor(new Color(240, 200, 70, 180));
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawOval(cx - rx3, cy - ry3, rx3 * 2, ry3 * 2);

        // 8 Radial celestial spokes
        g2.setStroke(new BasicStroke(1.2f));
        for (int i = 0; i < 8; i++) {
            double angle = i * (Math.PI / 4.0);
            int x1 = cx + (int) (Math.cos(angle) * (rx3));
            int y1 = cy + (int) (Math.sin(angle) * (ry3));
            int x2 = cx + (int) (Math.cos(angle) * (rx - 4));
            int y2 = cy + (int) (Math.sin(angle) * (ry - 4));

            g2.setColor(new Color(212, 175, 55, 150));
            g2.drawLine(x1, y1, x2, y2);

            // Small diamond nodes along spokes
            int mx = (x1 + x2) / 2;
            int my = (y1 + y2) / 2;
            drawSmallDiamond(g2, mx, my, 3, new Color(80, 230, 215, 190));
        }

        // Inner glowing teal ring
        int rx4 = rx3 - 40;
        int ry4 = ry3 - 12;
        g2.setColor(new Color(64, 224, 208, 140));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawOval(cx - rx4, cy - ry4, rx4 * 2, ry4 * 2);

        g2.dispose();
    }

    /**
     * drawGoldenDoor(Graphics2D g)
     * Draws the massive ornate golden exit door at the center as the primary focal attraction.
     */
    public void drawGoldenDoor(Graphics2D g) {
        int doorX = 515;
        int doorY = 70;
        int doorW = 250;
        int doorH = 410;

        // Radiant glow leaking from door frame
        RadialGradientPaint frameGlow = new RadialGradientPaint(
            640.0f, 260.0f, 260.0f,
            new float[]{0.0f, 0.45f, 0.85f, 1.0f},
            new Color[]{
                new Color(255, 230, 140, 170),
                new Color(230, 180, 50, 110),
                new Color(180, 110, 20, 45),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(frameGlow);
        g.fillRect(doorX - 40, doorY - 30, doorW + 80, doorH + 60);

        // Door frame surround (heavy antique bronze)
        LinearGradientPaint frameGrad = new LinearGradientPaint(
            doorX, doorY, doorX + doorW, doorY,
            new float[]{0.0f, 0.08f, 0.92f, 1.0f},
            new Color[]{
                new Color(90, 65, 20),
                new Color(170, 130, 45),
                new Color(170, 130, 45),
                new Color(90, 65, 20)
            }
        );
        g.setPaint(frameGrad);
        g.fillRoundRect(doorX, doorY, doorW, doorH, 16, 16);

        // Inner door leaf recess
        int innerX = doorX + 10;
        int innerY = doorY + 10;
        int innerW = doorW - 20;
        int innerH = doorH - 10;

        g.setColor(new Color(24, 18, 8));
        g.fillRect(innerX, innerY, innerW, innerH);

        // Left & Right Golden Door Leaves
        int leafW = (innerW - 4) / 2;
        drawDoorLeaf(g, innerX, innerY, leafW, innerH, true);
        drawDoorLeaf(g, innerX + leafW + 4, innerY, leafW, innerH, false);

        // Center meeting stile seam with golden light line
        int seamX = innerX + leafW + 1;
        g.setColor(new Color(255, 240, 180, 230));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(seamX, innerY + 4, seamX, innerY + innerH - 4);

        g.setColor(new Color(212, 175, 55, 180));
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(seamX - 1, innerY + 4, seamX - 1, innerY + innerH - 4);
        g.drawLine(seamX + 2, innerY + 4, seamX + 2, innerY + innerH - 4);

        // Grand ornate arch crest above door
        drawDoorArchCrest(g, doorX, doorY, doorW);
    }

    private void drawDoorLeaf(Graphics2D g, int x, int y, int w, int h, boolean isLeft) {
        // Base antique gold plate gradient
        LinearGradientPaint leafGrad = new LinearGradientPaint(
            x, y, x + w, y,
            new float[]{0.0f, 0.25f, 0.75f, 1.0f},
            isLeft ? new Color[]{
                new Color(150, 110, 32),
                new Color(230, 185, 65),
                new Color(195, 150, 45),
                new Color(130, 95, 25)
            } : new Color[]{
                new Color(130, 95, 25),
                new Color(195, 150, 45),
                new Color(230, 185, 65),
                new Color(150, 110, 32)
            }
        );
        g.setPaint(leafGrad);
        g.fillRect(x, y, w, h);

        // 3 Vertical relief panels per door leaf
        int pMargin = 8;
        int pw = w - pMargin * 2;
        int ph = (h - pMargin * 4) / 3;

        for (int i = 0; i < 3; i++) {
            int py = y + pMargin + i * (ph + pMargin);

            // Recessed panel shadow
            g.setColor(new Color(60, 42, 12, 190));
            g.fillRect(x + pMargin, py, pw, ph);

            // Raised inner gold panel
            LinearGradientPaint pGrad = new LinearGradientPaint(
                x + pMargin + 2, py + 2, x + pMargin + pw - 2, py + ph - 2,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{
                    new Color(225, 180, 60),
                    new Color(175, 130, 35),
                    new Color(120, 85, 20)
                }
            );
            g.setPaint(pGrad);
            g.fillRect(x + pMargin + 2, py + 2, pw - 4, ph - 4);

            // Filigree gold border
            g.setColor(new Color(255, 225, 110, 210));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRect(x + pMargin + 2, py + 2, pw - 4, ph - 4);

            // Celestial relief motifs inside panels
            drawPanelReliefMotif(g, x + pMargin + pw / 2, py + ph / 2, i);

            // Corner rivets
            drawGoldRivet(g, x + pMargin + 6, py + 6);
            drawGoldRivet(g, x + pMargin + pw - 6, py + 6);
            drawGoldRivet(g, x + pMargin + 6, py + ph - 6);
            drawGoldRivet(g, x + pMargin + pw - 6, py + ph - 6);
        }

        // Heavy ornate handle ring
        int hx = isLeft ? x + w - 16 : x + 16;
        int hy = y + h / 2 + 10;
        g.setColor(new Color(70, 48, 14));
        g.fillOval(hx - 10, hy - 10, 20, 20);
        g.setColor(new Color(245, 205, 80));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(hx - 8, hy - 8, 16, 16);
    }

    private void drawPanelReliefMotif(Graphics2D g, int cx, int cy, int index) {
        g.setColor(new Color(255, 230, 120, 160));
        g.setStroke(new BasicStroke(1.4f));

        if (index == 0) {
            // Sunburst motif
            g.drawOval(cx - 10, cy - 10, 20, 20);
            for (int r = 0; r < 8; r++) {
                double a = r * (Math.PI / 4.0);
                int x1 = cx + (int) (Math.cos(a) * 11);
                int y1 = cy + (int) (Math.sin(a) * 11);
                int x2 = cx + (int) (Math.cos(a) * 19);
                int y2 = cy + (int) (Math.sin(a) * 19);
                g.drawLine(x1, y1, x2, y2);
            }
        } else if (index == 1) {
            // Celestial star compass
            GeneralPath star = new GeneralPath();
            star.moveTo(cx, cy - 18);
            star.lineTo(cx + 4, cy - 4);
            star.lineTo(cx + 18, cy);
            star.lineTo(cx + 4, cy + 4);
            star.lineTo(cx, cy + 18);
            star.lineTo(cx - 4, cy + 4);
            star.lineTo(cx - 18, cy);
            star.lineTo(cx - 4, cy - 4);
            star.closePath();
            g.draw(star);
        } else {
            // Sacred geometric diamond & ring
            g.drawOval(cx - 14, cy - 14, 28, 28);
            drawSmallDiamond(g, cx, cy, 10, new Color(255, 220, 100, 160));
        }
    }

    private void drawDoorArchCrest(Graphics2D g, int x, int y, int w) {
        int cx = x + w / 2;
        // Ornate sculpted arch lintel
        GradientPaint lintelGrad = new GradientPaint(
            x, y - 14, new Color(230, 185, 65),
            x, y, new Color(130, 95, 25)
        );
        g.setPaint(lintelGrad);
        g.fillRoundRect(x - 8, y - 16, w + 16, 18, 8, 8);
        g.setColor(new Color(255, 230, 130));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x - 8, y - 16, w + 16, 18, 8, 8);

        // Center eagle/phoenix wing motif
        g.setColor(new Color(255, 235, 140, 220));
        g.setStroke(new BasicStroke(2.0f));
        g.drawArc(cx - 35, y - 12, 35, 20, 0, 180);
        g.drawArc(cx, y - 12, 35, 20, 0, 180);
        drawSmallDiamond(g, cx, y - 7, 5, new Color(80, 230, 215));
    }

    /**
     * drawCombinationLock(Graphics2D g)
     * Draws the intricate cylindrical combination lock mounted directly on the golden exit door.
     */
    public void drawCombinationLock(Graphics2D g) {
        int lockX = 605;
        int lockY = 235;
        int lockW = 70;
        int lockH = 80;
        int cx = lockX + lockW / 2;
        int cy = lockY + lockH / 2;

        // Radiating locking deadbolts extending into door frame
        g.setColor(new Color(110, 85, 28));
        g.fillRect(lockX - 35, cy - 5, lockW + 70, 10);
        g.fillRect(cx - 5, lockY - 25, 10, lockH + 50);

        g.setColor(new Color(245, 210, 90));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(lockX - 35, cy - 5, lockW + 70, 10);
        g.drawRect(cx - 5, lockY - 25, 10, lockH + 50);

        // Heavy circular lock housing
        RadialGradientPaint baseHousing = new RadialGradientPaint(
            cx - 6, cy - 6, 42.0f,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(90, 85, 100),
                new Color(36, 32, 45),
                new Color(15, 12, 22)
            }
        );
        g.setPaint(baseHousing);
        g.fillOval(cx - 34, cy - 34, 68, 68);

        // Gold outer tumbler dial
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(3.0f));
        g.drawOval(cx - 32, cy - 32, 64, 64);

        // Engraved dial tick marks
        g.setStroke(new BasicStroke(1.2f));
        for (int i = 0; i < 12; i++) {
            double angle = i * (Math.PI / 6.0);
            int x1 = cx + (int) (Math.cos(angle) * 26);
            int y1 = cy + (int) (Math.sin(angle) * 26);
            int x2 = cx + (int) (Math.cos(angle) * 31);
            int y2 = cy + (int) (Math.sin(angle) * 31);
            g.setColor(new Color(255, 225, 110));
            g.drawLine(x1, y1, x2, y2);
        }

        // Inner brass tumbler dial
        RadialGradientPaint innerDial = new RadialGradientPaint(
            cx - 3, cy - 3, 24.0f,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{
                new Color(240, 195, 75),
                new Color(175, 130, 40),
                new Color(100, 70, 18)
            }
        );
        g.setPaint(innerDial);
        g.fillOval(cx - 20, cy - 20, 40, 40);

        // Center escutcheon with cyan gemstone
        g.setColor(new Color(255, 255, 255));
        g.setColor(new Color(25, 18, 32));
        g.fillOval(cx - 10, cy - 10, 20, 20);

        RadialGradientPaint gemGlow = new RadialGradientPaint(
            cx - 2, cy - 2, 10.0f,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{
                new Color(180, 255, 245),
                new Color(40, 210, 195),
                new Color(10, 110, 100)
            }
        );
        g.setPaint(gemGlow);
        g.fillOval(cx - 6, cy - 6, 12, 12);

        g.setColor(new Color(255, 255, 255, 230));
        g.fillOval(cx - 3, cy - 4, 3, 3);
    }

    /**
     * drawStonePillars(Graphics2D g)
     * Draws two tall towering fluted marble-granite pillars supporting the archway.
     */
    public void drawStonePillars(Graphics2D g) {
        // Left pillar: x = 90, Right pillar: x = 1110
        drawSinglePillar(g, 90, 70, 80, 430, true);
        drawSinglePillar(g, 1110, 70, 80, 430, false);
    }

    private void drawSinglePillar(Graphics2D g, int x, int y, int w, int h, boolean isLeft) {
        // Pillar base plinth (tiered stone)
        int baseY = y + h - 35;
        GradientPaint plinthGrad = new GradientPaint(
            x - 6, baseY, new Color(48, 42, 65),
            x + w + 6, baseY, new Color(20, 18, 30)
        );
        g.setPaint(plinthGrad);
        g.fillRect(x - 6, baseY, w + 12, 35);
        g.setColor(new Color(212, 175, 55, 130));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x - 6, baseY, w + 12, 35);

        // Column shaft cylindrical gradient
        LinearGradientPaint shaftGrad = new LinearGradientPaint(
            x, y + 35, x + w, y + 35,
            new float[]{0.0f, 0.25f, 0.6f, 1.0f},
            isLeft ? new Color[]{
                new Color(30, 28, 46),
                new Color(65, 58, 88),
                new Color(48, 42, 66),
                new Color(22, 20, 34)
            } : new Color[]{
                new Color(22, 20, 34),
                new Color(48, 42, 66),
                new Color(65, 58, 88),
                new Color(30, 28, 46)
            }
        );
        g.setPaint(shaftGrad);
        g.fillRect(x, y + 35, w, h - 70);

        // Vertical fluting lines
        g.setStroke(new BasicStroke(1.2f));
        int flutes = 5;
        int fluteSpacing = w / (flutes + 1);
        for (int i = 1; i <= flutes; i++) {
            int fx = x + i * fluteSpacing;
            g.setColor(new Color(15, 14, 24, 200));
            g.drawLine(fx, y + 38, fx, y + h - 38);
            g.setColor(new Color(90, 80, 115, 120));
            g.drawLine(fx + 1, y + 38, fx + 1, y + h - 38);
        }

        // Capital at top (tiered with gold acanthus leaf accent)
        GradientPaint capGrad = new GradientPaint(
            x - 8, y, new Color(55, 48, 75),
            x + w + 8, y, new Color(24, 20, 36)
        );
        g.setPaint(capGrad);
        g.fillRect(x - 8, y, w + 16, 35);

        // Gold carved collar trim
        g.setColor(new Color(220, 180, 60, 180));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRect(x - 8, y, w + 16, 35);
        g.drawLine(x, y + 35, x + w, y + 35);

        // Small gold rosette in capital center
        drawSmallDiamond(g, x + w / 2, y + 17, 6, new Color(255, 215, 70));
    }

    /**
     * drawWallSymbols(Graphics2D g)
     * Draws ancient symbols engraved on the masonry with glowing teal and gold luminescence.
     */
    public void drawWallSymbols(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Left upper wall symbols (x: 200-470, y: 90-340)
        drawGlowingGlyph(g2, 230, 95, 0);
        drawGlowingGlyph(g2, 380, 190, 1);
        drawGlowingGlyph(g2, 240, 380, 2);
        drawGlowingGlyph(g2, 430, 360, 3);

        // Right upper wall symbols (x: 800-1080, y: 90-340)
        drawGlowingGlyph(g2, 1020, 95, 2);
        drawGlowingGlyph(g2, 880, 190, 3);
        drawGlowingGlyph(g2, 1030, 380, 0);
        drawGlowingGlyph(g2, 830, 360, 1);

        g2.dispose();
    }

    private void drawGlowingGlyph(Graphics2D g, int cx, int cy, int type) {
        // Soft teal outer luminescence
        g.setColor(new Color(64, 224, 208, 35));
        g.fillOval(cx - 24, cy - 24, 48, 48);

        // Crisp engraved rune lines
        g.setColor(new Color(80, 235, 220, 175));
        g.setStroke(new BasicStroke(1.8f));

        if (type == 0) {
            // Solar Eye of Horus / Eye symbol
            g.drawArc(cx - 16, cy - 8, 32, 16, 0, 180);
            g.drawArc(cx - 16, cy - 8, 32, 16, 180, 180);
            g.fillOval(cx - 4, cy - 4, 8, 8);
            g.drawLine(cx, cy + 8, cx, cy + 18);
        } else if (type == 1) {
            // Ankh / Key of Life
            g.drawOval(cx - 6, cy - 16, 12, 14);
            g.drawLine(cx, cy - 2, cx, cy + 16);
            g.drawLine(cx - 10, cy + 4, cx + 10, cy + 4);
        } else if (type == 2) {
            // Sacred Delta / Pyramid with Eye
            GeneralPath triangle = new GeneralPath();
            triangle.moveTo(cx, cy - 16);
            triangle.lineTo(cx + 16, cy + 14);
            triangle.lineTo(cx - 16, cy + 14);
            triangle.closePath();
            g.draw(triangle);
            g.drawOval(cx - 3, cy + 1, 6, 6);
        } else {
            // Celestial Ouroboros ring with star
            g.drawOval(cx - 14, cy - 14, 28, 28);
            drawSmallDiamond(g, cx, cy, 6, new Color(255, 220, 90, 200));
        }
    }

    /**
     * drawSymbolPanels(Graphics2D g)
     * Draws the four decorative symbol relief plaques mounted on the walls flanking the golden door.
     */
    public void drawSymbolPanels(Graphics2D g) {
        // Left side panels (Top: Sun, Bottom: Moon)
        drawSingleSymbolPanel(g, 210, 140, 70, 90, "SUN", 0);
        drawSingleSymbolPanel(g, 210, 260, 70, 90, "MOON", 1);

        // Right side panels (Top: Star, Bottom: Serpent)
        drawSingleSymbolPanel(g, 1000, 140, 70, 90, "STAR", 2);
        drawSingleSymbolPanel(g, 1000, 260, 70, 90, "SERPENT", 3);
    }

    private void drawSingleSymbolPanel(Graphics2D g, int x, int y, int w, int h, String label, int motifIndex) {
        // Sculpted stone / bronze molded plaque
        GradientPaint plaqueGrad = new GradientPaint(
            x, y, new Color(45, 36, 58),
            x + w, y + h, new Color(20, 16, 28)
        );
        g.setPaint(plaqueGrad);
        g.fillRoundRect(x, y, w, h, 10, 10);

        // Antique gold relief border
        g.setColor(new Color(212, 175, 55, 170));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(x, y, w, h, 10, 10);
        g.drawRoundRect(x + 3, y + 3, w - 6, h - 6, 8, 8);

        int cx = x + w / 2;
        int cy = y + 38;

        // Embossed emblem
        g.setColor(new Color(245, 210, 90, 210));
        g.setStroke(new BasicStroke(2.0f));

        if (motifIndex == 0) {
            // Sun
            g.drawOval(cx - 12, cy - 12, 24, 24);
            g.fillOval(cx - 5, cy - 5, 10, 10);
            for (int r = 0; r < 8; r++) {
                double a = r * (Math.PI / 4.0);
                g.drawLine(cx + (int)(Math.cos(a)*13), cy + (int)(Math.sin(a)*13),
                           cx + (int)(Math.cos(a)*18), cy + (int)(Math.sin(a)*18));
            }
        } else if (motifIndex == 1) {
            // Moon crescent
            Area moon = new Area(new Ellipse2D.Double(cx - 14, cy - 14, 28, 28));
            moon.subtract(new Area(new Ellipse2D.Double(cx - 8, cy - 16, 26, 28)));
            g.fill(moon);
            drawSmallDiamond(g, cx + 8, cy - 6, 3, new Color(255, 230, 120));
        } else if (motifIndex == 2) {
            // Star
            GeneralPath star = new GeneralPath();
            star.moveTo(cx, cy - 15);
            star.lineTo(cx + 4, cy - 4);
            star.lineTo(cx + 15, cy);
            star.lineTo(cx + 4, cy + 4);
            star.lineTo(cx, cy + 15);
            star.lineTo(cx - 4, cy + 4);
            star.lineTo(cx - 15, cy);
            star.lineTo(cx - 4, cy - 4);
            star.closePath();
            g.fill(star);
        } else {
            // Serpent / Ouroboros ring
            g.drawOval(cx - 14, cy - 14, 28, 28);
            g.drawOval(cx - 9, cy - 9, 18, 18);
            g.fillOval(cx - 4, cy - 16, 8, 6);
        }

        // Bottom label text
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        int tx = cx - fm.stringWidth(label) / 2;
        g.setColor(new Color(230, 195, 80));
        g.drawString(label, tx, y + h - 12);
    }

    /**
     * drawWallMechanisms(Graphics2D g)
     * Draws interlocking golden gear trains, counterweight chains, and brass escapements.
     */
    public void drawWallMechanisms(Graphics2D g) {
        // Left mechanism train: x: 305, y: 75, w: 155, h: 85
        drawGearTrain(g, 305, 75, 155, 85, true);

        // Right mechanism train: x: 825, y: 75, w: 155, h: 85
        drawGearTrain(g, 825, 75, 155, 85, false);
    }

    private void drawGearTrain(Graphics2D g, int x, int y, int w, int h, boolean isLeft) {
        // Wall recess housing
        g.setColor(new Color(10, 8, 18, 220));
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(new Color(180, 140, 45, 120));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, 12, 12);

        // Conduit pipes leading into door arch
        g.setColor(new Color(140, 105, 35));
        g.fillRect(isLeft ? x + w - 4 : x - 20, y + h / 2 - 4, 24, 8);

        // 3 Interlocking gears
        int g1x = x + 35;
        int g1y = y + 42;
        int g1r = 24;

        int g2x = x + 78;
        int g2y = y + 40;
        int g2r = 18;

        int g3x = x + 118;
        int g3y = y + 45;
        int g3r = 20;

        drawSingleGear(g, g1x, g1y, g1r, 12, gearRotation);
        drawSingleGear(g, g2x, g2y, g2r, 10, -gearRotation * 1.3f);
        drawSingleGear(g, g3x, g3y, g3r, 11, gearRotation * 1.1f);

        // Brass counterweight chain
        g.setColor(new Color(200, 160, 50, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(g1x - 12, g1y, g1x - 12, y + h - 6);
        g.fillOval(g1x - 16, y + h - 10, 8, 8);
    }

    private void drawSingleGear(Graphics2D g, int cx, int cy, int radius, int teeth, float angleOffset) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx, cy);
        g2.rotate(angleOffset);

        // Gear teeth
        g2.setColor(new Color(212, 175, 55));
        g2.setStroke(new BasicStroke(2.2f));
        for (int i = 0; i < teeth; i++) {
            double a = i * (Math.PI * 2.0 / teeth);
            int tx = (int) (Math.cos(a) * (radius + 4));
            int ty = (int) (Math.sin(a) * (radius + 4));
            g2.drawLine(0, 0, tx, ty);
        }

        // Solid gear body
        RadialGradientPaint bodyGrad = new RadialGradientPaint(
            -2, -2, radius,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(245, 205, 80),
                new Color(185, 140, 40),
                new Color(110, 75, 20)
            }
        );
        g2.setPaint(bodyGrad);
        g2.fillOval(-radius, -radius, radius * 2, radius * 2);

        // Center hub & spokes
        g2.setColor(new Color(20, 15, 28));
        g2.fillOval(-6, -6, 12, 12);
        g2.setColor(new Color(255, 230, 120));
        g2.fillOval(-3, -3, 6, 6);

        g2.dispose();
    }

    /**
     * drawTorches(Graphics2D g)
     * Draws twin bronze sconces with animated flickering eternal golden flames.
     */
    public void drawTorches(Graphics2D g) {
        // Left torch: x = 465, y = 180
        drawSingleTorch(g, 465, 180, true);

        // Right torch: x = 780, y = 180
        drawSingleTorch(g, 780, 180, false);
    }

    private void drawSingleTorch(Graphics2D g, int x, int y, boolean isLeft) {
        int cx = x + 17;
        int cy = y + 36;

        // Wall bracket mount plate
        g.setColor(new Color(50, 36, 16));
        g.fillRoundRect(isLeft ? x - 4 : x + 20, y + 20, 18, 32, 6, 6);
        g.setColor(new Color(212, 175, 55, 180));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(isLeft ? x - 4 : x + 20, y + 20, 18, 32, 6, 6);

        // Sconce arm bracket
        g.setColor(new Color(150, 110, 35));
        g.setStroke(new BasicStroke(3.5f));
        if (isLeft) {
            g.drawLine(x + 5, y + 42, cx, y + 36);
        } else {
            g.drawLine(x + 28, y + 42, cx, y + 36);
        }

        // Bronze torch bowl
        GradientPaint bowlGrad = new GradientPaint(
            cx - 15, cy, new Color(215, 165, 50),
            cx + 15, cy, new Color(90, 60, 15)
        );
        g.setPaint(bowlGrad);
        g.fillRoundRect(cx - 14, cy - 8, 28, 22, 8, 8);
        g.setColor(new Color(255, 215, 80));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(cx - 14, cy - 8, 28, 22, 8, 8);

        // Animated flickering flame
        float flickerOffset = (float) Math.sin(flickerPhase * 2.5f + (isLeft ? 0.0f : 1.5f)) * 3.0f;
        int tipX = cx + (int) flickerOffset;
        int tipY = cy - 42 - (int) (Math.abs(flickerOffset) * 1.5f);

        // Outer fiery orange/red flame mantle
        GeneralPath flameOuter = new GeneralPath();
        flameOuter.moveTo(cx - 12, cy - 6);
        flameOuter.curveTo(cx - 16, cy - 22, tipX - 10, cy - 30, tipX, tipY);
        flameOuter.curveTo(tipX + 10, cy - 30, cx + 16, cy - 22, cx + 12, cy - 6);
        flameOuter.closePath();

        RadialGradientPaint flameGrad1 = new RadialGradientPaint(
            tipX, cy - 15, 32.0f,
            new float[]{0.0f, 0.4f, 0.8f, 1.0f},
            new Color[]{
                new Color(255, 245, 160, 240),
                new Color(255, 175, 40, 210),
                new Color(220, 75, 15, 150),
                new Color(160, 25, 5, 0)
            }
        );
        g.setPaint(flameGrad1);
        g.fill(flameOuter);

        // Inner brilliant white-yellow flame core
        GeneralPath flameInner = new GeneralPath();
        flameInner.moveTo(cx - 6, cy - 6);
        flameInner.curveTo(cx - 8, cy - 16, tipX - 4, cy - 22, tipX, tipY + 12);
        flameInner.curveTo(tipX + 4, cy - 22, cx + 8, cy - 16, cx + 6, cy - 6);
        flameInner.closePath();

        g.setColor(new Color(255, 255, 220, 230));
        g.fill(flameInner);
    }

    /**
     * drawDocumentTable(Graphics2D g)
     * Draws the small mahogany study desk strewn with ancient schematics and cryptographer notes.
     */
    public void drawDocumentTable(Graphics2D g) {
        int tx = 270;
        int ty = 430;
        int tw = 175;
        int th = 125;

        // Cast shadow on floor
        g.setColor(new Color(4, 4, 8, 140));
        g.fillOval(tx - 10, ty + th - 20, tw + 20, 30);

        // Desk turned legs
        g.setColor(new Color(42, 22, 10));
        g.fillRect(tx + 12, ty + 24, 12, th - 24);
        g.fillRect(tx + tw - 24, ty + 24, 12, th - 24);
        g.fillRect(tx + 38, ty + 20, 10, th - 32);
        g.fillRect(tx + tw - 48, ty + 20, 10, th - 32);

        // Cross stretcher
        g.setColor(new Color(30, 15, 6));
        g.fillRect(tx + 12, ty + th - 22, tw - 36, 6);

        // Desk aprons & drawer front
        LinearGradientPaint apronGrad = new LinearGradientPaint(
            tx, ty + 12, tx, ty + 36,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(75, 40, 18), new Color(48, 24, 10)}
        );
        g.setPaint(apronGrad);
        g.fillRect(tx + 8, ty + 12, tw - 16, 24);

        // Brass drawer handle
        g.setColor(new Color(212, 175, 55));
        g.fillRect(tx + tw / 2 - 14, ty + 22, 28, 4);

        // Tabletop bevelled slab
        LinearGradientPaint topGrad = new LinearGradientPaint(
            tx, ty, tx + tw, ty + 14,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(110, 60, 28), new Color(85, 45, 20), new Color(60, 30, 12)}
        );
        g.setPaint(topGrad);
        g.fillRoundRect(tx, ty, tw, 15, 6, 6);
        g.setColor(new Color(160, 95, 45));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(tx, ty, tw, 15, 6, 6);

        // Documents and architectural scrolls strewn on desk
        // Scroll 1: Open blueprint
        g.setColor(new Color(225, 215, 185));
        g.fillRoundRect(tx + 22, ty - 8, 85, 18, 4, 4);
        g.setColor(new Color(80, 130, 160));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRect(tx + 26, ty - 6, 77, 14);
        g.drawLine(tx + 30, ty, tx + 65, ty);

        // Scroll 2: Rolled parchment with wax seal
        g.setColor(new Color(210, 195, 155));
        g.fillRoundRect(tx + 95, ty - 4, 42, 10, 4, 4);
        g.setColor(new Color(180, 40, 40)); // Red seal
        g.fillOval(tx + 112, ty - 2, 7, 7);

        // Brass inkwell and quill
        g.setColor(new Color(160, 125, 40));
        g.fillRect(tx + tw - 38, ty - 6, 12, 10);
        g.setColor(new Color(245, 240, 230)); // White quill
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(tx + tw - 32, ty - 6, tx + tw - 20, ty - 26);
    }

    /**
     * drawHourglass(Graphics2D g)
     * Draws the antique gilded brass hourglass with glowing golden sand.
     */
    public void drawHourglass(Graphics2D g) {
        int hx = 405;
        int hy = 400;
        int hw = 38;
        int hh = 70;
        int cx = hx + hw / 2;

        // Brass top and bottom caps
        GradientPaint capGrad = new GradientPaint(
            hx, hy, new Color(245, 205, 80),
            hx + hw, hy, new Color(130, 95, 25)
        );
        g.setPaint(capGrad);
        g.fillRoundRect(hx, hy, hw, 7, 4, 4);
        g.fillRoundRect(hx, hy + hh - 7, hw, 7, 4, 4);

        g.setColor(new Color(255, 225, 110));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(hx, hy, hw, 7, 4, 4);
        g.drawRoundRect(hx, hy + hh - 7, hw, 7, 4, 4);

        // Spherical finials
        drawSmallDiamond(g, cx, hy - 3, 3, new Color(255, 215, 60));
        drawSmallDiamond(g, cx, hy + hh + 3, 3, new Color(255, 215, 60));

        // 3 Spiral brass support pillars
        g.setColor(new Color(180, 140, 45));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(hx + 3, hy + 7, hx + 3, hy + hh - 7);
        g.drawLine(hx + hw - 3, hy + 7, hx + hw - 3, hy + hh - 7);

        // Blown glass teardrop bulbs
        int midY = hy + hh / 2;
        g.setColor(new Color(200, 235, 255, 80));
        g.fillOval(cx - 12, hy + 7, 24, 26);
        g.fillOval(cx - 12, midY - 3, 24, 26);

        // Glass outline
        g.setColor(new Color(220, 245, 255, 140));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(cx - 12, hy + 7, 24, 26);
        g.drawOval(cx - 12, midY - 3, 24, 26);

        // Golden sand inside bulbs
        // Upper bulb: sand receding
        g.setColor(new Color(245, 205, 85, 210));
        g.fillArc(cx - 10, hy + 14, 20, 16, 0, 180);

        // Lower bulb: conical sand mound
        GeneralPath sandMound = new GeneralPath();
        sandMound.moveTo(cx - 10, hy + hh - 10);
        sandMound.lineTo(cx, hy + hh - 24);
        sandMound.lineTo(cx + 10, hy + hh - 10);
        sandMound.closePath();
        g.fill(sandMound);

        // Sand stream trickling between bulbs
        g.setColor(new Color(255, 235, 120, 230));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(cx, hy + 26, cx, hy + hh - 22);
    }

    /**
     * drawCentralPedestal(Graphics2D g)
     * Draws the polished basalt plinth with celestial channels and ceremonial key receptacle.
     */
    public void drawCentralPedestal(Graphics2D g) {
        int px = 560;
        int py = 435;
        int pw = 160;
        int ph = 145;
        int cx = px + pw / 2;

        // Cast shadow on floor
        g.setColor(new Color(4, 4, 8, 160));
        g.fillOval(px - 15, py + ph - 24, pw + 30, 34);

        // Bottom plinth tier
        int tier1Y = py + ph - 38;
        GradientPaint tier1Grad = new GradientPaint(
            px - 8, tier1Y, new Color(42, 38, 56),
            px + pw + 8, tier1Y, new Color(18, 16, 26)
        );
        g.setPaint(tier1Grad);
        g.fillRoundRect(px - 8, tier1Y, pw + 16, 38, 8, 8);
        g.setColor(new Color(212, 175, 55, 140));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(px - 8, tier1Y, pw + 16, 38, 8, 8);

        // Mid pedestal column body
        LinearGradientPaint bodyGrad = new LinearGradientPaint(
            px, py + 28, px + pw, py + 28,
            new float[]{0.0f, 0.2f, 0.8f, 1.0f},
            new Color[]{
                new Color(22, 20, 32),
                new Color(46, 40, 62),
                new Color(36, 30, 50),
                new Color(16, 14, 24)
            }
        );
        g.setPaint(bodyGrad);
        g.fillRect(px + 10, py + 28, pw - 20, ph - 66);

        // Engraved celestial channel lines on plinth body
        g.setColor(new Color(80, 230, 215, 130));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(cx, py + 34, cx, py + ph - 42);
        g.drawLine(cx - 30, py + 48, cx - 30, py + ph - 48);
        g.drawLine(cx + 30, py + 48, cx + 30, py + ph - 48);

        // Top pedestal ceremonial slab
        GradientPaint topGrad = new GradientPaint(
            px - 4, py, new Color(58, 50, 78),
            px + pw + 4, py, new Color(26, 22, 38)
        );
        g.setPaint(topGrad);
        g.fillRoundRect(px - 4, py, pw + 8, 28, 8, 8);
        g.setColor(new Color(255, 215, 75));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(px - 4, py, pw + 8, 28, 8, 8);

        // Ceremonial key receptacle in center
        g.setColor(new Color(10, 8, 16));
        g.fillOval(cx - 28, py + 4, 56, 18);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx - 28, py + 4, 56, 18);

        // Velvet cushion & key slot
        g.setColor(new Color(90, 18, 30)); // Royal crimson velvet
        g.fillOval(cx - 20, py + 7, 40, 12);

        // Golden keyhole slot
        g.setColor(new Color(255, 230, 120));
        g.fillRect(cx - 4, py + 10, 8, 6);
        drawSmallDiamond(g, cx, py + 12, 4, new Color(80, 230, 215));
    }

    /**
     * drawAtmosphericLighting(Graphics2D g)
     * Creates warm golden illumination, soft purple shadows, and radial torchlight.
     */
    public void drawAtmosphericLighting(Graphics2D g) {
        // Grand golden radiance from exit door
        RadialGradientPaint doorGlow = new RadialGradientPaint(
            640.0f, 260.0f, 540.0f,
            new float[]{0.0f, 0.35f, 0.75f, 1.0f},
            new Color[]{
                new Color(255, 220, 120, 95),
                new Color(220, 165, 45, 55),
                new Color(150, 90, 25, 20),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(doorGlow);
        g.fillRect(0, 60, LOGICAL_WIDTH, 660);

        // Left torch radial glow
        RadialGradientPaint torchGlowL = new RadialGradientPaint(
            482.0f, 205.0f, 180.0f,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(255, 185, 60, 95),
                new Color(220, 110, 30, 35),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(torchGlowL);
        g.fillRect(320, 60, 320, 300);

        // Right torch radial glow
        RadialGradientPaint torchGlowR = new RadialGradientPaint(
            797.0f, 205.0f, 180.0f,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(255, 185, 60, 95),
                new Color(220, 110, 30, 35),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(torchGlowR);
        g.fillRect(640, 60, 320, 300);

        // Ambient dark purple vignette in corners
        RadialGradientPaint vig = new RadialGradientPaint(
            640.0f, 360.0f, 760.0f,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(0, 0, 0, 0),
                new Color(10, 6, 20, 90),
                new Color(4, 2, 10, 190)
            }
        );
        g.setPaint(vig);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
    }

    /**
     * drawDustParticles(Graphics2D g)
     * Renders floating golden motes reflecting the warm radiant chamber light.
     */
    public void drawDustParticles(Graphics2D g) {
        Composite orig = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

        for (DustParticle p : dustParticles) {
            float alpha = 0.35f + (float) Math.sin(flickerPhase * 1.8f + p.x) * 0.25f;
            alpha = Math.max(0.1f, Math.min(0.85f, alpha));

            g.setColor(new Color(1.0f, 0.88f, 0.45f, alpha));
            g.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }

        g.setComposite(orig);
    }

    /**
     * drawInteractivityFeedback(Graphics2D g)
     * Draws golden hover outlines, object highlights, and click pulses.
     */
    private void drawInteractivityFeedback(Graphics2D g) {
        // 1. Hover highlight & outline
        if (hoveredObject != null) {
            Rectangle b = hoveredObject.getBounds();
            if ("torches".equals(hoveredObject.getId()) && lastMousePoint != null && lastMousePoint.x >= 700) {
                b = new Rectangle(780, 180, 35, 80);
            } else if ("pillars".equals(hoveredObject.getId()) && lastMousePoint != null && lastMousePoint.x >= 700) {
                b = new Rectangle(1110, 70, 80, 430);
            } else if ("symbol_panels".equals(hoveredObject.getId()) && lastMousePoint != null && lastMousePoint.x >= 700) {
                b = new Rectangle(990, 130, 90, 230);
            } else if ("wall_mechanisms".equals(hoveredObject.getId()) && lastMousePoint != null && lastMousePoint.x >= 700) {
                b = new Rectangle(825, 75, 155, 85);
            }

            g.setColor(new Color(255, 215, 64, 45));
            g.fillRoundRect(b.x - 4, b.y - 4, b.width + 8, b.height + 8, 12, 12);

            g.setColor(new Color(255, 215, 64, 210));
            g.setStroke(new BasicStroke(2.2f));
            g.drawRoundRect(b.x - 4, b.y - 4, b.width + 8, b.height + 8, 12, 12);

            // Floating badge above object
            String label = hoveredObject.getName();
            Font tipFont = new Font("Georgia", Font.BOLD, 14);
            g.setFont(tipFont);
            FontMetrics fm = g.getFontMetrics(tipFont);

            int tipW = fm.stringWidth(label) + 20;
            int tipH = 26;
            int tipX = b.x + (b.width - tipW) / 2;
            int tipY = Math.max(76, b.y - 32);

            g.setColor(new Color(4, 5, 12, 170));
            g.fillRoundRect(tipX + 2, tipY + 2, tipW, tipH, 10, 10);

            g.setColor(new Color(22, 16, 32, 230));
            g.fillRoundRect(tipX, tipY, tipW, tipH, 10, 10);

            g.setColor(new Color(255, 215, 64));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(tipX, tipY, tipW, tipH, 10, 10);

            g.setColor(new Color(255, 245, 220));
            g.drawString(label, tipX + 10, tipY + 18);
        }

        // 2. Click tactile feedback pulse ripple & highlight
        if (clickedObject != null && clickFeedbackTicks > 0) {
            Rectangle cb = clickedObject.getBounds();
            if ("torches".equals(clickedObject.getId()) && lastClickedPoint != null && lastClickedPoint.x >= 700) {
                cb = new Rectangle(780, 180, 35, 80);
            } else if ("pillars".equals(clickedObject.getId()) && lastClickedPoint != null && lastClickedPoint.x >= 700) {
                cb = new Rectangle(1110, 70, 80, 430);
            } else if ("symbol_panels".equals(clickedObject.getId()) && lastClickedPoint != null && lastClickedPoint.x >= 700) {
                cb = new Rectangle(990, 130, 90, 230);
            } else if ("wall_mechanisms".equals(clickedObject.getId()) && lastClickedPoint != null && lastClickedPoint.x >= 700) {
                cb = new Rectangle(825, 75, 155, 85);
            }

            float progress = (14.0f - clickFeedbackTicks) / 14.0f;

            // Soft golden highlight fill
            int fillAlpha = (int) ((1.0f - progress) * 85);
            g.setColor(new Color(255, 235, 140, Math.max(0, fillAlpha)));
            g.fillRoundRect(cb.x - 3, cb.y - 3, cb.width + 6, cb.height + 6, 12, 12);

            // Bold golden border highlight
            int borderAlpha = (int) ((1.0f - progress * 0.5f) * 240);
            g.setColor(new Color(255, 215, 64, Math.max(0, borderAlpha)));
            g.setStroke(new BasicStroke(2.8f));
            g.drawRoundRect(cb.x - 3, cb.y - 3, cb.width + 6, cb.height + 6, 12, 12);

            // Expanding golden interaction pulse ripple
            int expand = (int) (progress * 18);
            float rippleAlpha = Math.max(0.0f, (1.0f - progress) * 0.85f);
            g.setColor(new Color(1.0f, 0.84f, 0.25f, rippleAlpha));
            g.setStroke(new BasicStroke(Math.max(1.0f, 2.4f * (1.0f - progress * 0.5f))));
            g.drawRoundRect(
                cb.x - 3 - expand, cb.y - expand,
                cb.width + 6 + expand * 2, cb.height + expand * 2,
                16, 16
            );

            // Sparkle pulse diamond at click point
            if (lastClickedPoint != null) {
                int starSize = (int) (3 + progress * 10);
                drawSmallDiamond(g, lastClickedPoint.x, lastClickedPoint.y, starSize, new Color(255, 235, 120));
            }
        }
    }

    /**
     * drawHUD(Graphics2D g)
     * Displays top navigation HUD, player details, timer, score, and level title.
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
        Player player = (controller != null) ? controller.getPlayer() : null;
        String playerName = (player != null && !player.getName().isEmpty()) ? player.getName() : "Detective";
        String playerDisplay = "PLAYER: " + playerName;

        drawHUDKeyIcon(g, hudX + 18, hudY + 15);
        Font hudFont = new Font("Georgia", Font.BOLD, 17);
        g.setFont(hudFont);
        g.setColor(new Color(255, 245, 220));
        g.drawString(playerDisplay, hudX + 46, hudY + 32);

        // 2. Room Title: LEVEL 4 — THE FINAL ESCAPE
        String roomTitle = "LEVEL 4 \u2014 THE FINAL ESCAPE";
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
     * Displays bottom status bar with hovered object name or click description.
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

        drawSmallDiamond(g, tx + 4, ty - 5, 5, new Color(255, 215, 64));
        tx += 18;

        g.setColor(new Color(255, 248, 230));
        g.drawString(messageToDisplay, tx, ty);
    }

    private void drawButton(Graphics2D g, MenuButton btn) {
        if (btn == null) return;
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
        Color botC = btn.pressed ? btn.pressedColor : (btn.hovered ? btn.hoverBottomColor : btn.bottomColor);

        GradientPaint bg = new GradientPaint(x, y, topC, x, y + h, botC);
        g.setPaint(bg);
        g.fillRoundRect(x, y, w, h, arc, arc);

        g.setColor(btn.hovered ? new Color(255, 235, 120) : btn.borderColor);
        g.setStroke(new BasicStroke(btn.hovered ? 2.0f : 1.4f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        g.setFont(new Font("Georgia", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        int tx = x + (w - fm.stringWidth(btn.text)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        g.setColor(new Color(10, 8, 16));
        g.drawString(btn.text, tx + 1, ty + 1);

        g.setColor(btn.hovered ? new Color(255, 245, 200) : new Color(240, 220, 160));
        g.drawString(btn.text, tx, ty);
    }

    private void drawSmallDiamond(Graphics2D g, int cx, int cy, int size, Color c) {
        GeneralPath diamond = new GeneralPath();
        diamond.moveTo(cx, cy - size);
        diamond.lineTo(cx + size, cy);
        diamond.lineTo(cx, cy + size);
        diamond.lineTo(cx - size, cy);
        diamond.closePath();

        g.setColor(c);
        g.fill(diamond);
        g.setColor(new Color(80, 50, 15));
        g.setStroke(new BasicStroke(0.8f));
        g.draw(diamond);
    }

    private void drawGoldRivet(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(70, 48, 16));
        g.fillOval(cx - 3, cy - 3, 6, 6);
        g.setColor(new Color(255, 220, 100));
        g.fillOval(cx - 2, cy - 2, 4, 4);
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

    public MenuButton getReturnLevel3Button() {
        return returnLevel3Button;
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
            this.speedY = 0.18f + (float) Math.random() * 0.24f;
            this.driftX = (float) (Math.random() - 0.5) * 0.16f;
        }

        public void update() {
            y -= speedY;
            x += driftX;
            if (y < 50) {
                y = 670;
                x = 60 + (float) (Math.random() * 1160);
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
