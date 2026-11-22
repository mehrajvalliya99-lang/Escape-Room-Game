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
 * Playable Room 1 – The Study for "Escape the Room – Mystery Puzzle Adventure".
 * Features genuine 2D interactive objects with hover golden outlines, object name displays
 * in the message bar, tactile click feedback highlights, inspection descriptions,
 * future puzzle integration hooks for Member 2, and full game HUD.
 */
public class RoomPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<GameObject> gameObjects;
    private final MenuButton inventoryButton;
    private final MenuButton level2Button;

    // Message bar state tracking
    private static final String DEFAULT_PROMPT = "Explore the study. Something here may help you escape...";
    private String lastClickedDescription = DEFAULT_PROMPT;
    private GameObject hoveredObject = null;

    // Small tactile visual feedback on click (brief flash/highlight)
    private GameObject clickedObject = null;
    private int clickFeedbackTicks = 0;

    // Viewport scaling and letterboxing metrics
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Ambient floating dust particles and gentle lighting flicker
    private Timer ambientTimer;
    private float flickerPhase = 0.0f;
    private final List<DustParticle> dustParticles;

    public RoomPanel(GameController controller) {
        this.controller = controller;
        this.gameObjects = new ArrayList<>();
        this.dustParticles = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 12, 22));
        setDoubleBuffered(true);
        setFocusable(true);

        initGameObjects();
        inventoryButton = initInventoryButton();
        level2Button = new MenuButton(
            "Level 2 \u2192",
            new Rectangle(875, 24, 105, 34),
            new Color(60, 45, 95),
            new Color(35, 24, 60),
            new Color(85, 62, 135),
            new Color(50, 34, 85),
            new Color(25, 16, 45),
            new Color(212, 175, 55),
            () -> controller.changeState(GameState.LEVEL_2)
        );
        initDustParticles();
        initMouseListeners();
        initAmbientTimer();
    }

    private void initGameObjects() {
        gameObjects.clear();

        // 1. WINDOW (Far left wall)
        // TODO: Member 2 - connect puzzle logic (Window observation / moonlight clue)
        gameObjects.add(new GameObject(
            "window",
            "Window",
            "Moonlight falls across the room.",
            new Rectangle(60, 110, 160, 240),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 2. PLANT (Far left foreground)
        // TODO: Member 2 - connect puzzle logic (Hidden item/key in pot)
        gameObjects.add(new GameObject(
            "plant",
            "Plant",
            "A surprisingly healthy plant in an otherwise dusty room.",
            new Rectangle(35, 410, 95, 165),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 3. STUDY DESK (Center-left)
        // TODO: Member 2 - connect puzzle logic (Item/clue)
        gameObjects.add(new GameObject(
            "desk",
            "Desk",
            "An old study desk. There may be something hidden inside.",
            new Rectangle(170, 380, 320, 180),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 4. DESK LAMP (On left side of desk)
        // TODO: Member 2 - connect puzzle logic (Lamp shadow / light puzzle)
        gameObjects.add(new GameObject(
            "lamp",
            "Lamp",
            "The lamp casts a strangely shaped shadow.",
            new Rectangle(215, 300, 70, 95),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 5. GLOBE (On right side of desk)
        // TODO: Member 2 - connect puzzle logic (Globe coordinate rotation)
        gameObjects.add(new GameObject(
            "globe",
            "Globe",
            "There are unusual markings on the globe.",
            new Rectangle(405, 310, 65, 88),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 6. PAINTING (Above desk on wall)
        // TODO: Member 2 - connect puzzle logic (Hidden clue)
        gameObjects.add(new GameObject(
            "painting",
            "Painting",
            "The eyes seem to follow you...",
            new Rectangle(285, 115, 165, 125),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 7. WALL CLOCK (Wall, between painting and bookshelf)
        // TODO: Member 2 - connect puzzle logic (Clock puzzle)
        gameObjects.add(new GameObject(
            "clock",
            "Old Clock",
            "The old clock has stopped at a peculiar time.",
            new Rectangle(475, 110, 65, 130),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 8. BOOKSHELF (Center-right wall)
        // TODO: Member 2 - connect puzzle logic (Book puzzle)
        gameObjects.add(new GameObject(
            "bookshelf",
            "Bookshelf",
            "Rows of old books. One section looks strangely disturbed.",
            new Rectangle(565, 120, 205, 350),
            () -> {
                // TODO: Member 2 - connect puzzle logic
            }
        ));

        // 9. LOCKED CHEST (Floor, in front of bookshelf)
        // TODO: Member 2 - connect puzzle logic (Lock puzzle)
        gameObjects.add(new GameObject(
            "chest",
            "Locked Chest",
            "A sturdy chest. It is locked.",
            new Rectangle(710, 465, 125, 90),
            () -> {
                // TODO: Member 2 - connect puzzle logic
                controller.changeState(GameState.PUZZLE);
            }
        ));

        // 10. EXIT DOOR (Right wall)
        // TODO: Member 2 - level progression
        // TODO: Member 2 - connect puzzle logic (Room progression)
        gameObjects.add(new GameObject(
            "door",
            "Door",
            "The door is locked. There must be another way to open it.",
            new Rectangle(860, 125, 150, 350),
            () -> {
                // Testing transition to Level 2 (The Secret Library)
                controller.changeState(GameState.LEVEL_2);
            }
        ));
    }

    private MenuButton initInventoryButton() {
        int btnW = 180;
        int btnH = 48;
        int btnX = 1040;
        int btnY = 648;

        return new MenuButton(
            "INVENTORY",
            new Rectangle(btnX, btnY, btnW, btnH),
            new Color(90, 34, 136),
            new Color(50, 16, 82),
            new Color(118, 48, 178),
            new Color(72, 22, 118),
            new Color(36, 10, 60),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.INVENTORY)
        );
    }

    private void initDustParticles() {
        dustParticles.clear();
        int[][] pData = {
            {260, 330, 2}, {320, 370, 3}, {180, 240, 2}, {440, 340, 3},
            {650, 280, 2}, {740, 420, 3}, {900, 310, 2}, {150, 460, 2}
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

            // Click feedback countdown
            if (clickFeedbackTicks > 0) {
                clickFeedbackTicks--;
                if (clickFeedbackTicks == 0) {
                    if (clickedObject != null) {
                        clickedObject.setClicked(false);
                        clickedObject = null;
                    }
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

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                boolean anyHovered = false;

                // Check game objects hover
                GameObject found = null;
                for (GameObject obj : gameObjects) {
                    boolean h = obj.contains(lp);
                    obj.setHovered(h);
                    if (h) {
                        found = obj;
                        anyHovered = true;
                    }
                }
                hoveredObject = found;

                // Check inventory button hover
                boolean invH = inventoryButton.contains(lp);
                if (inventoryButton.isHovered() != invH) {
                    inventoryButton.setHovered(invH);
                }
                if (invH) anyHovered = true;

                // Check Level 2 testing button hover
                boolean l2H = level2Button.contains(lp);
                if (level2Button.isHovered() != l2H) {
                    level2Button.setHovered(l2H);
                }
                if (l2H) anyHovered = true;

                setCursor(anyHovered ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                for (GameObject obj : gameObjects) {
                    if (obj.contains(lp)) {
                        // Click interaction & visual feedback
                        lastClickedDescription = obj.getDescription();

                        // Visual feedback trigger (brief golden pulse & highlight)
                        if (clickedObject != null) {
                            clickedObject.setClicked(false);
                        }
                        clickedObject = obj;
                        clickedObject.setClicked(true);
                        clickFeedbackTicks = 6; // ~300ms

                        obj.interact(); // trigger future Member 2 puzzle hooks
                        repaint();
                        return;
                    }
                }

                if (inventoryButton.contains(lp)) {
                    inventoryButton.setPressed(true);
                    repaint();
                }

                if (level2Button.contains(lp)) {
                    level2Button.setPressed(true);
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

                if (level2Button.isPressed()) {
                    level2Button.setPressed(false);
                    repaint();
                    if (level2Button.contains(lp)) {
                        level2Button.trigger();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (GameObject obj : gameObjects) {
                    obj.setHovered(false);
                }
                hoveredObject = null;
                if (inventoryButton.isHovered() || inventoryButton.isPressed()) {
                    inventoryButton.setHovered(false);
                    inventoryButton.setPressed(false);
                }
                if (level2Button.isHovered() || level2Button.isPressed()) {
                    level2Button.setHovered(false);
                    level2Button.setPressed(false);
                }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        updateScaleAndOffset();

        // Fill letterbox margin with deep dark tone
        g2d.setColor(new Color(6, 7, 14));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Viewport transformation to logical 1280x720
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Draw visual room layers
        drawRoomBackground(g2d);
        drawFloor(g2d);
        drawWindow(g2d);
        drawDoor(g2d);
        drawBookshelf(g2d);
        drawPainting(g2d);
        drawClock(g2d);
        drawDesk(g2d);
        drawGlobe(g2d);
        drawLamp(g2d);
        drawPlant(g2d);
        drawChest(g2d);
        drawLighting(g2d);
        drawDustParticles(g2d);
        drawObjectInteractivityFeedback(g2d);
        drawHUD(g2d);
        drawMessageBar(g2d);
        drawButton(g2d, inventoryButton);
        drawButton(g2d, level2Button);

        g2d.dispose();
    }

    // =========================================================================
    // LAYER 1: ROOM BACKGROUND & WALLS
    // =========================================================================
    private void drawRoomBackground(Graphics2D g) {
        // Deep study wall gradient
        LinearGradientPaint wallGrad = new LinearGradientPaint(
            0, 0, 0, 440,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(24, 18, 30), new Color(34, 26, 40), new Color(22, 16, 26)}
        );
        g.setPaint(wallGrad);
        g.fillRect(0, 0, LOGICAL_WIDTH, 440);

        // Vertical wood wall panels with subtle bevels
        g.setColor(new Color(16, 12, 22, 140));
        g.setStroke(new BasicStroke(2.0f));
        for (int x = 110; x < LOGICAL_WIDTH; x += 130) {
            g.drawLine(x, 0, x, 440);
        }

        // Horizontal chair rail / wainscoting molding
        g.setColor(new Color(42, 32, 48));
        g.fillRect(0, 350, LOGICAL_WIDTH, 12);
        g.setColor(new Color(16, 12, 20));
        g.fillRect(0, 362, LOGICAL_WIDTH, 4);

        // Baseboard molding along wall-floor junction
        g.setColor(new Color(48, 36, 52));
        g.fillRect(0, 432, LOGICAL_WIDTH, 12);
        g.setColor(new Color(18, 12, 22));
        g.fillRect(0, 442, LOGICAL_WIDTH, 4);
    }

    // =========================================================================
    // LAYER 2: FLOOR & PERSIAN RUG
    // =========================================================================
    private void drawFloor(Graphics2D g) {
        // Dark polished hardwood floor planks
        LinearGradientPaint floorGrad = new LinearGradientPaint(
            0, 440, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(44, 28, 20), new Color(36, 22, 15), new Color(24, 14, 10)}
        );
        g.setPaint(floorGrad);
        g.fillRect(0, 440, LOGICAL_WIDTH, LOGICAL_HEIGHT - 440);

        // Perspective floorboard seams
        g.setColor(new Color(18, 10, 6, 120));
        g.setStroke(new BasicStroke(1.5f));
        int[] floorY = {470, 510, 560, 620, 690};
        for (int fy : floorY) {
            g.drawLine(0, fy, LOGICAL_WIDTH, fy);
        }

        // Large ornamental Persian Rug in center/left floor
        int rugX = 170;
        int rugY = 485;
        int rugW = 690;
        int rugH = 175;

        // Rug shadow
        g.setColor(new Color(8, 4, 3, 130));
        g.fillRoundRect(rugX + 4, rugY + 6, rugW, rugH, 16, 16);

        // Rug outer burgundy border
        g.setColor(new Color(90, 22, 34));
        g.fillRoundRect(rugX, rugY, rugW, rugH, 16, 16);

        // Rug golden filigree inner border
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(rugX + 6, rugY + 6, rugW - 12, rugH - 12, 12, 12);

        // Rug dark indigo/crimson center medallion field
        g.setColor(new Color(48, 14, 28));
        g.fillRoundRect(rugX + 16, rugY + 16, rugW - 32, rugH - 32, 10, 10);

        // Center floral medallion
        g.setColor(new Color(185, 145, 45, 110));
        g.fillOval(rugX + rugW / 2 - 40, rugY + rugH / 2 - 25, 80, 50);
    }

    // =========================================================================
    // LAYER 3: WINDOW & COOL BLUE MOONLIGHT
    // =========================================================================
    private void drawWindow(Graphics2D g) {
        int wx = 60;
        int wy = 110;
        int ww = 160;
        int wh = 240;

        // Window recess frame
        g.setColor(new Color(20, 16, 26));
        g.fillRoundRect(wx, wy, ww, wh, 20, 20);

        // Night sky outside window
        LinearGradientPaint skyOutside = new LinearGradientPaint(
            wx + 8, wy + 8, wx + 8, wy + wh - 16,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(10, 18, 42), new Color(18, 30, 64)}
        );
        g.setPaint(skyOutside);
        g.fillRoundRect(wx + 8, wy + 8, ww - 16, wh - 16, 16, 16);

        // Stars outside window
        g.setColor(new Color(255, 255, 255, 160));
        g.fillRect(wx + 30, wy + 30, 2, 2);
        g.fillRect(wx + 80, wy + 45, 2, 2);
        g.fillRect(wx + 120, wy + 25, 2, 2);
        g.fillRect(wx + 45, wy + 90, 2, 2);

        // Full moon outside window
        RadialGradientPaint moonGrad = new RadialGradientPaint(
            wx + 105, wy + 65, 28,
            new float[]{0.0f, 0.8f, 1.0f},
            new Color[]{new Color(245, 250, 255), new Color(205, 222, 238), new Color(160, 185, 210)}
        );
        g.setPaint(moonGrad);
        g.fillOval(wx + 85, wy + 45, 40, 40);

        // Window glass panes / dark mullions
        g.setColor(new Color(32, 26, 40));
        g.setStroke(new BasicStroke(3.0f));
        g.drawRoundRect(wx + 8, wy + 8, ww - 16, wh - 16, 16, 16);
        g.drawLine(wx + ww / 2, wy + 8, wx + ww / 2, wy + wh - 8);
        g.drawLine(wx + 8, wy + wh / 2, wx + ww - 8, wy + wh / 2);

        // Rich purple velvet curtains pulled to left and right
        g.setColor(new Color(58, 20, 68));
        GeneralPath leftCurtain = new GeneralPath();
        leftCurtain.moveTo(wx - 10, wy - 10);
        leftCurtain.lineTo(wx + 35, wy - 10);
        leftCurtain.curveTo(wx + 25, wy + 120, wx + 40, wy + 200, wx - 10, wy + wh + 20);
        leftCurtain.closePath();
        g.fill(leftCurtain);

        GeneralPath rightCurtain = new GeneralPath();
        rightCurtain.moveTo(wx + ww + 10, wy - 10);
        rightCurtain.lineTo(wx + ww - 35, wy - 10);
        rightCurtain.curveTo(wx + ww - 25, wy + 120, wx + ww - 40, wy + 200, wx + ww + 10, wy + wh + 20);
        rightCurtain.closePath();
        g.fill(rightCurtain);

        // Golden curtain tie-backs
        g.setColor(new Color(215, 175, 55));
        g.setStroke(new BasicStroke(3.0f));
        g.drawArc(wx - 5, wy + 140, 25, 20, -45, 180);
        g.drawArc(wx + ww - 20, wy + 140, 25, 20, 45, 180);
    }

    // =========================================================================
    // LAYER 4: EXIT DOOR
    // =========================================================================
    private void drawDoor(Graphics2D g) {
        int dx = 860;
        int dy = 125;
        int dw = 150;
        int dh = 350;

        // Heavy wooden door frame
        g.setColor(new Color(22, 16, 26));
        g.fillRoundRect(dx - 8, dy - 8, dw + 16, dh + 8, 14, 14);

        // Door frame gold moulding
        g.setColor(new Color(145, 110, 45));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(dx - 8, dy - 8, dw + 16, dh + 8, 14, 14);

        // Dark gothic oak door body
        LinearGradientPaint doorWood = new LinearGradientPaint(
            dx, dy, dx + dw, dy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(64, 40, 28), new Color(82, 52, 36), new Color(52, 32, 22)}
        );
        g.setPaint(doorWood);
        g.fillRect(dx, dy, dw, dh);

        // Recessed gothic door panels
        g.setColor(new Color(36, 22, 16));
        g.fillRoundRect(dx + 16, dy + 20, dw - 32, 85, 8, 8);
        g.fillRoundRect(dx + 16, dy + 125, dw - 32, 85, 8, 8);
        g.fillRoundRect(dx + 16, dy + 230, dw - 32, 95, 8, 8);

        // Panel inner highlight borders
        g.setColor(new Color(98, 64, 45));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(dx + 16, dy + 20, dw - 32, 85, 8, 8);
        g.drawRoundRect(dx + 16, dy + 125, dw - 32, 85, 8, 8);
        g.drawRoundRect(dx + 16, dy + 230, dw - 32, 95, 8, 8);

        // Ornate Brass Door Handle Plate & Keyhole
        int plateX = dx + 22;
        int plateY = dy + 160;
        g.setColor(new Color(215, 175, 55));
        g.fillRoundRect(plateX, plateY, 18, 50, 6, 6);
        g.setColor(new Color(130, 95, 25));
        g.drawRoundRect(plateX, plateY, 18, 50, 6, 6);

        // Brass door knob / lever
        g.setColor(new Color(255, 225, 80));
        g.fillOval(plateX + 4, plateY + 10, 10, 10);
        g.fillRect(plateX - 6, plateY + 12, 12, 6);

        // Keyhole
        g.setColor(new Color(24, 18, 12));
        g.fillOval(plateX + 6, plateY + 30, 6, 6);
        GeneralPath kh = new GeneralPath();
        kh.moveTo(plateX + 7, plateY + 33);
        kh.lineTo(plateX + 6, plateY + 42);
        kh.lineTo(plateX + 12, plateY + 42);
        kh.lineTo(plateX + 11, plateY + 33);
        kh.closePath();
        g.fill(kh);
    }

    // =========================================================================
    // LAYER 5: BOOKSHELF
    // =========================================================================
    private void drawBookshelf(Graphics2D g) {
        int bx = 565;
        int by = 120;
        int bw = 205;
        int bh = 350;

        // Bookshelf outer shadow
        g.setColor(new Color(10, 8, 14, 160));
        g.fillRect(bx + 6, by + 8, bw, bh);

        // Heavy dark oak frame
        LinearGradientPaint wood = new LinearGradientPaint(
            bx, by, bx + bw, by,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(55, 36, 26), new Color(74, 48, 34), new Color(46, 28, 20)}
        );
        g.setPaint(wood);
        g.fillRect(bx, by, bw, bh);

        // Bookshelf gold top molding
        g.setColor(new Color(165, 125, 45));
        g.fillRect(bx - 6, by - 8, bw + 12, 10);

        // Shelves interior (4 shelf tiers)
        int shelfH = 75;
        for (int i = 0; i < 4; i++) {
            int sy = by + 12 + i * (shelfH + 10);

            // Dark inner shadow inside shelf
            g.setColor(new Color(24, 16, 12));
            g.fillRect(bx + 10, sy, bw - 20, shelfH);

            // Shelf ledge
            g.setColor(new Color(90, 60, 42));
            g.fillRect(bx + 6, sy + shelfH, bw - 12, 10);
            g.setColor(new Color(40, 25, 18));
            g.fillRect(bx + 6, sy + shelfH + 8, bw - 12, 2);

            // Colorful book spines on shelf
            drawBookRow(g, bx + 16, sy + 6, bw - 32, shelfH - 8, i);
        }
    }

    private void drawBookRow(Graphics2D g, int x, int y, int w, int h, int tier) {
        Color[] colors = {
            new Color(145, 32, 42), new Color(34, 62, 118), new Color(26, 88, 52),
            new Color(160, 115, 30), new Color(92, 38, 112), new Color(130, 48, 32)
        };

        int curX = x;
        int idx = tier * 3;
        while (curX < x + w - 16) {
            int bW = 12 + (idx % 3) * 3;
            int bH = h - (idx % 4) * 4;
            Color c = colors[idx % colors.length];

            // On 2nd shelf (tier 1), draw one tilted book sticking out!
            if (tier == 1 && curX > x + 40 && curX < x + 65) {
                Graphics2D bg = (Graphics2D) g.create();
                bg.rotate(Math.toRadians(12), curX + bW / 2, y + h);
                bg.setColor(new Color(212, 175, 55)); // Special golden book
                bg.fillRect(curX, y + h - bH, bW + 2, bH);
                bg.setColor(new Color(110, 85, 20));
                bg.drawRect(curX, y + h - bH, bW + 2, bH);
                bg.dispose();
                curX += bW + 6;
                idx++;
                continue;
            }

            g.setColor(c);
            g.fillRect(curX, y + h - bH, bW, bH);

            // Gold spine accent line
            g.setColor(new Color(255, 225, 120, 160));
            g.drawLine(curX + 2, y + h - bH + 4, curX + bW - 2, y + h - bH + 4);
            g.drawLine(curX + 2, y + h - 6, curX + bW - 2, y + h - 6);

            curX += bW + 2;
            idx++;
        }
    }

    // =========================================================================
    // LAYER 6: PAINTING & CLOCK
    // =========================================================================
    private void drawPainting(Graphics2D g) {
        int px = 285;
        int py = 115;
        int pw = 165;
        int ph = 125;

        // Shadow
        g.setColor(new Color(10, 8, 14, 150));
        g.fillRect(px + 4, py + 6, pw, ph);

        // Ornate antique gold frame
        g.setColor(new Color(185, 140, 45));
        g.fillRect(px, py, pw, ph);
        g.setColor(new Color(255, 225, 95));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRect(px + 3, py + 3, pw - 6, ph - 6);
        g.setColor(new Color(115, 82, 22));
        g.drawRect(px + 8, py + 8, pw - 16, ph - 16);

        // Canvas artwork: mysterious moonlit mansion landscape
        LinearGradientPaint canvasGrad = new LinearGradientPaint(
            px + 10, py + 10, px + 10, py + ph - 20,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(18, 28, 54), new Color(38, 52, 85), new Color(14, 20, 32)}
        );
        g.setPaint(canvasGrad);
        g.fillRect(px + 10, py + 10, pw - 20, ph - 20);

        // Painting moon & eerie glowing mansion silhouette
        g.setColor(new Color(255, 248, 210));
        g.fillOval(px + pw - 45, py + 22, 16, 16);
        g.setColor(new Color(8, 10, 18));
        GeneralPath silhouette = new GeneralPath();
        silhouette.moveTo(px + 20, py + ph - 10);
        silhouette.lineTo(px + 50, py + 60);
        silhouette.lineTo(px + 85, py + ph - 10);
        silhouette.closePath();
        g.fill(silhouette);
    }

    private void drawClock(Graphics2D g) {
        int cx = 475;
        int cy = 110;
        int cw = 65;
        int ch = 130;

        // Wooden clock housing
        g.setColor(new Color(55, 36, 26));
        g.fillRoundRect(cx, cy, cw, ch, 8, 8);
        g.setColor(new Color(195, 155, 55));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(cx, cy, cw, ch, 8, 8);

        // Round clock dial
        g.setColor(new Color(255, 252, 238));
        g.fillOval(cx + 10, cy + 12, 45, 45);
        g.setColor(new Color(110, 80, 25));
        g.drawOval(cx + 10, cy + 12, 45, 45);

        // Clock hands stopped at peculiar time (11:42)
        int dialCX = cx + 32;
        int dialCY = cy + 34;
        g.setColor(new Color(28, 18, 12));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(dialCX, dialCY, dialCX - 6, dialCY - 14); // hour hand
        g.drawLine(dialCX, dialCY, dialCX - 12, dialCY + 4); // minute hand

        // Glass lower cabinet showing swinging brass pendulum
        g.setColor(new Color(16, 12, 20));
        g.fillRect(cx + 14, cy + 65, cw - 28, 55);

        // Brass pendulum
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(dialCX, cy + 65, dialCX, cy + 98);
        g.fillOval(dialCX - 7, cy + 98, 14, 14);
    }

    // =========================================================================
    // LAYER 7: STUDY DESK, PAPERS & DRAWERS
    // =========================================================================
    private void drawDesk(Graphics2D g) {
        int dx = 170;
        int dy = 380;
        int dw = 320;
        int dh = 180;

        // Desk shadow on floor
        g.setColor(new Color(10, 6, 8, 160));
        g.fillRoundRect(dx - 10, dy + dh - 15, dw + 20, 25, 12, 12);

        // Polished mahogany desk top surface
        LinearGradientPaint deskTop = new LinearGradientPaint(
            dx - 10, dy, dx + dw + 10, dy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(75, 46, 32), new Color(98, 62, 44), new Color(64, 38, 26)}
        );
        g.setPaint(deskTop);
        g.fillRoundRect(dx - 10, dy, dw + 20, 34, 8, 8);

        // Desk top beveled edge
        g.setColor(new Color(145, 96, 68));
        g.drawLine(dx - 8, dy + 2, dx + dw + 8, dy + 2);

        // Left pedestal drawers (x: 170-260)
        drawPedestalDrawers(g, dx, dy + 34, 90, dh - 34);

        // Right pedestal drawers (x: 400-490)
        drawPedestalDrawers(g, dx + dw - 90, dy + 34, 90, dh - 34);

        // Kneehole back panel (x: 260-400)
        g.setColor(new Color(24, 15, 10));
        g.fillRect(dx + 90, dy + 34, dw - 180, dh - 34);

        // Stack of parchment paper sheets & quill pen on desk
        drawPapersOnDesk(g, dx + 115, dy - 2);
    }

    private void drawPedestalDrawers(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(55, 34, 24));
        g.fillRect(x, y, w, h);

        int drawerH = (h - 12) / 3;
        for (int i = 0; i < 3; i++) {
            int dry = y + 4 + i * (drawerH + 2);
            g.setColor(new Color(72, 45, 32));
            g.fillRect(x + 4, dry, w - 8, drawerH);
            g.setColor(new Color(36, 22, 16));
            g.drawRect(x + 4, dry, w - 8, drawerH);

            // Brass drawer pull
            g.setColor(new Color(212, 175, 55));
            g.fillRoundRect(x + w / 2 - 12, dry + drawerH / 2 - 3, 24, 6, 3, 3);
        }
    }

    private void drawPapersOnDesk(Graphics2D g, int x, int y) {
        // Stack of parchment paper sheets
        g.setColor(new Color(245, 238, 220));
        g.fillRect(x, y + 6, 65, 18);
        g.setColor(new Color(255, 250, 235));
        g.fillRect(x + 3, y + 2, 62, 18);

        // Handwriting lines
        g.setColor(new Color(110, 85, 60, 140));
        g.drawLine(x + 8, y + 7, x + 55, y + 7);
        g.drawLine(x + 8, y + 11, x + 50, y + 11);
        g.drawLine(x + 8, y + 15, x + 45, y + 15);

        // Feather quill pen
        g.setColor(new Color(220, 200, 160));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(x + 58, y + 16, x + 85, y - 10);
    }

    // =========================================================================
    // LAYER 8: DESK LAMP & GLOBE
    // =========================================================================
    private void drawLamp(Graphics2D g) {
        int lx = 215;
        int ly = 300;

        // Brass lamp base
        g.setColor(new Color(185, 140, 45));
        g.fillOval(lx + 15, ly + 80, 40, 14);

        // Curved brass arm
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath arm = new GeneralPath();
        arm.moveTo(lx + 35, ly + 82);
        arm.curveTo(lx + 10, ly + 40, lx + 30, ly + 15, lx + 45, ly + 18);
        g.draw(arm);

        // Glowing Emerald Glass Lampshade
        LinearGradientPaint shadeGrad = new LinearGradientPaint(
            lx + 15, ly + 12, lx + 70, ly + 36,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(36, 165, 85), new Color(18, 110, 52), new Color(10, 68, 32)}
        );
        g.setPaint(shadeGrad);
        g.fillRoundRect(lx + 20, ly + 15, 50, 24, 12, 12);
        g.setColor(new Color(215, 185, 65));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(lx + 20, ly + 15, 50, 24, 12, 12);
    }

    private void drawGlobe(Graphics2D g) {
        int gx = 405;
        int gy = 310;

        // Turned wood base
        g.setColor(new Color(64, 40, 28));
        g.fillOval(gx + 12, gy + 74, 42, 14);
        g.fillRect(gx + 29, gy + 52, 8, 24);

        // Brass gimbal meridian ring
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(3.0f));
        g.drawArc(gx + 2, gy + 6, 60, 60, 45, 230);

        // Antique parchment globe sphere
        RadialGradientPaint globeGrad = new RadialGradientPaint(
            gx + 28, gy + 30, 26,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{new Color(245, 235, 205), new Color(205, 188, 148), new Color(155, 134, 98)}
        );
        g.setPaint(globeGrad);
        g.fillOval(gx + 8, gy + 12, 48, 48);

        // Continents / latitude lines
        g.setColor(new Color(95, 135, 85, 170));
        g.fillOval(gx + 16, gy + 24, 18, 14);
        g.fillOval(gx + 32, gy + 32, 14, 18);
        g.setColor(new Color(130, 105, 70, 120));
        g.setStroke(new BasicStroke(1.0f));
        g.drawOval(gx + 14, gy + 18, 36, 36);
    }

    // =========================================================================
    // LAYER 9: PLANT & LOCKED CHEST
    // =========================================================================
    private void drawPlant(Graphics2D g) {
        int px = 35;
        int py = 410;

        // Wooden plant stand
        g.setColor(new Color(48, 32, 22));
        g.fillRect(px + 28, py + 115, 38, 12);
        g.fillRect(px + 32, py + 127, 8, 35);
        g.fillRect(px + 54, py + 127, 8, 35);

        // Terracotta ceramic plant pot
        LinearGradientPaint potGrad = new LinearGradientPaint(
            px + 25, py + 80, px + 70, py + 80,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(185, 85, 52), new Color(215, 115, 75), new Color(155, 65, 36)}
        );
        g.setPaint(potGrad);
        GeneralPath pot = new GeneralPath();
        pot.moveTo(px + 22, py + 80);
        pot.lineTo(px + 72, py + 80);
        pot.lineTo(px + 64, py + 120);
        pot.lineTo(px + 30, py + 120);
        pot.closePath();
        g.fill(pot);

        // Lush green fern / monstera leaves
        g.setColor(new Color(36, 145, 68));
        g.fillOval(px + 12, py + 40, 36, 50);
        g.fillOval(px + 45, py + 35, 40, 52);
        g.setColor(new Color(24, 110, 48));
        g.fillOval(px + 30, py + 18, 35, 55);
        g.setColor(new Color(48, 185, 85));
        g.fillOval(px + 28, py + 32, 22, 38);
    }

    private void drawChest(Graphics2D g) {
        int cx = 710;
        int cy = 465;
        int cw = 125;
        int ch = 90;

        // Shadow on floor
        g.setColor(new Color(8, 5, 4, 160));
        g.fillRoundRect(cx + 4, cy + ch - 12, cw, 18, 10, 10);

        // Oak chest body
        LinearGradientPaint chestWood = new LinearGradientPaint(
            cx, cy, cx + cw, cy,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(75, 48, 32), new Color(98, 65, 44), new Color(62, 38, 24)}
        );
        g.setPaint(chestWood);
        g.fillRect(cx, cy + 22, cw, ch - 22);

        // Curved domed chest lid
        GeneralPath lid = new GeneralPath();
        lid.moveTo(cx - 3, cy + 22);
        lid.curveTo(cx + 10, cy, cx + cw - 10, cy, cx + cw + 3, cy + 22);
        lid.closePath();
        g.fill(lid);

        // Iron straps & brass studs
        g.setColor(new Color(38, 34, 42));
        g.fillRect(cx + 18, cy + 5, 12, ch - 5);
        g.fillRect(cx + cw - 30, cy + 5, 12, ch - 5);

        g.setColor(new Color(212, 175, 55));
        g.fillOval(cx + 22, cy + 14, 4, 4);
        g.fillOval(cx + 22, cy + 40, 4, 4);
        g.fillOval(cx + cw - 26, cy + 14, 4, 4);
        g.fillOval(cx + cw - 26, cy + 40, 4, 4);

        // Front latch plate & Golden Padlock
        g.setColor(new Color(185, 140, 45));
        g.fillRect(cx + cw / 2 - 10, cy + 26, 20, 22);

        // Padlock shackle & body
        g.setColor(new Color(255, 235, 120));
        g.setStroke(new BasicStroke(2.5f));
        g.drawArc(cx + cw / 2 - 6, cy + 30, 12, 14, 0, 180);

        g.setColor(new Color(255, 215, 64));
        g.fillRoundRect(cx + cw / 2 - 9, cy + 38, 18, 16, 4, 4);
        g.setColor(new Color(110, 75, 18));
        g.drawRoundRect(cx + cw / 2 - 9, cy + 38, 18, 16, 4, 4);

        // Keyhole in padlock
        g.setColor(new Color(32, 22, 12));
        g.fillOval(cx + cw / 2 - 2, cy + 42, 4, 4);
        g.fillRect(cx + cw / 2 - 1, cy + 44, 2, 4);
    }

    // =========================================================================
    // LAYER 10: DUAL LIGHTING (Moonlight & Warm Lamp)
    // =========================================================================
    private void drawLighting(Graphics2D g) {
        // 1. Cool Blue Moonlight streaming diagonally down-right across room
        GeneralPath moonBeam = new GeneralPath();
        moonBeam.moveTo(110, 140);
        moonBeam.lineTo(210, 140);
        moonBeam.lineTo(520, 680);
        moonBeam.lineTo(260, 680);
        moonBeam.closePath();

        LinearGradientPaint moonLight = new LinearGradientPaint(
            150, 140, 400, 680,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(195, 230, 255, 60), new Color(175, 215, 255, 25), new Color(160, 200, 255, 0)}
        );
        g.setPaint(moonLight);
        g.fill(moonBeam);

        // 2. Warm Golden Lamp Light radiating down onto desk, globe, and floor
        float flicker = (float) Math.sin(flickerPhase) * 3.0f;
        RadialGradientPaint lampBloom = new RadialGradientPaint(
            245, 310, 220 + flicker,
            new float[]{0.0f, 0.35f, 0.70f, 1.0f},
            new Color[]{
                new Color(255, 225, 95, 115),
                new Color(255, 195, 50, 55),
                new Color(245, 150, 20, 18),
                new Color(210, 110, 10, 0)
            }
        );
        g.setPaint(lampBloom);
        g.fillOval((int) (245 - 220), (int) (310 - 220), 440, 440);
    }

    // =========================================================================
    // LAYER 11: INTERACTIVITY FEEDBACK (Hover Outline & Click Highlight)
    // =========================================================================
    private void drawObjectInteractivityFeedback(Graphics2D g) {
        // 1. Click Flash Feedback (Brief golden highlight pulse)
        if (clickedObject != null && clickFeedbackTicks > 0) {
            Rectangle cr = clickedObject.getBounds();
            // Subtle pulse brightening
            g.setColor(new Color(255, 240, 160, 45));
            g.fillRoundRect(cr.x - 3, cr.y - 3, cr.width + 6, cr.height + 6, 12, 12);

            // Bright golden highlight outline
            g.setColor(new Color(255, 235, 90, 230));
            g.setStroke(new BasicStroke(2.8f));
            g.drawRoundRect(cr.x - 3, cr.y - 3, cr.width + 6, cr.height + 6, 12, 12);
        }

        // 2. Hover Highlight Outline & Floating Badge
        if (hoveredObject != null) {
            Rectangle r = hoveredObject.getBounds();

            // Subtle golden outline around object
            g.setColor(new Color(255, 215, 64, 185));
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 12, 12);

            // Slight brightening overlay
            g.setColor(new Color(255, 235, 140, 25));
            g.fillRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, 12, 12);

            // Small elegant hover tooltip badge above object
            String label = hoveredObject.getName();
            Font tipFont = new Font("Georgia", Font.BOLD, 14);
            g.setFont(tipFont);
            FontMetrics fm = g.getFontMetrics(tipFont);

            int tipW = fm.stringWidth(label) + 20;
            int tipH = 26;
            int tipX = r.x + (r.width - tipW) / 2;
            int tipY = Math.max(76, r.y - 32);

            // Tooltip shadow
            g.setColor(new Color(4, 5, 12, 160));
            g.fillRoundRect(tipX + 2, tipY + 2, tipW, tipH, 10, 10);

            // Tooltip background
            g.setColor(new Color(22, 16, 32, 230));
            g.fillRoundRect(tipX, tipY, tipW, tipH, 10, 10);

            // Tooltip golden border
            g.setColor(new Color(255, 215, 64));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(tipX, tipY, tipW, tipH, 10, 10);

            // Tooltip text
            g.setColor(new Color(255, 245, 220));
            g.drawString(label, tipX + 10, tipY + 18);
        }
    }

    // =========================================================================
    // LAYER 12: HUD (Top Bar & Bottom Message Bar)
    // =========================================================================
    private void drawHUD(Graphics2D g) {
        int hudX = 30;
        int hudY = 16;
        int hudW = 1220;
        int hudH = 50;
        int arc = 16;

        // HUD background bar (dark translucent with gold border)
        g.setColor(new Color(16, 12, 24, 215));
        g.fillRoundRect(hudX, hudY, hudW, hudH, arc, arc);

        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(hudX, hudY, hudW, hudH, arc, arc);

        // 1. TOP-LEFT: PLAYER NAME
        Player player = controller.getPlayer();
        String playerName = (player != null && !player.getName().isEmpty()) ? player.getName() : "Detective";
        String playerDisplay = "PLAYER: " + playerName;

        // Golden Key / Detective Badge Icon
        drawHUDKeyIcon(g, hudX + 18, hudY + 15);

        Font hudFont = new Font("Georgia", Font.BOLD, 17);
        g.setFont(hudFont);
        g.setColor(new Color(255, 245, 220));
        g.drawString(playerDisplay, hudX + 46, hudY + 32);

        // 2. TOP-CENTER: THE STUDY
        String roomTitle = "THE STUDY";
        Font titleFont = new Font("Georgia", Font.BOLD, 22);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        int rtx = hudX + (hudW - tfm.stringWidth(roomTitle)) / 2;

        // Title drop shadow & gold gradient
        g.setColor(new Color(10, 8, 14, 210));
        g.drawString(roomTitle, rtx + 1, hudY + 34);

        g.setColor(new Color(255, 215, 64));
        g.drawString(roomTitle, rtx, hudY + 33);

        // 3. TOP-RIGHT: SCORE & TIME
        // TODO: Member 2 - scoring
        String scoreDisplay = "SCORE: 0";
        // TODO: Member 2 - timer
        int durationMinutes = (player != null && player.getDifficulty() != null)
            ? player.getDifficulty().getDurationMinutes()
            : 15;
        String timeDisplay = String.format("TIME: %02d:00", durationMinutes);

        Font rightFont = new Font("Arial", Font.BOLD, 16);
        g.setFont(rightFont);
        FontMetrics rfm = g.getFontMetrics(rightFont);

        int timeW = rfm.stringWidth(timeDisplay);
        int scoreW = rfm.stringWidth(scoreDisplay);

        int timeX = hudX + hudW - timeW - 24;
        int scoreX = timeX - scoreW - 32;

        g.setColor(new Color(245, 215, 95));
        g.drawString(scoreDisplay, scoreX, hudY + 32);

        // Clock icon next to time
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

        // Message to display:
        // When hovering an interactive object: show its name!
        // Otherwise: show the last clicked description!
        String messageToDisplay;
        if (hoveredObject != null) {
            messageToDisplay = hoveredObject.getName();
        } else {
            messageToDisplay = lastClickedDescription;
        }

        // Message bar soft outer shadow
        g.setColor(new Color(4, 5, 12, 140));
        g.fillRoundRect(mbX + 2, mbY + 4, mbW, mbH, arc, arc);

        // Message bar background (dark translucent with gold border)
        g.setColor(new Color(16, 12, 24, 225));
        g.fillRoundRect(mbX, mbY, mbW, mbH, arc, arc);

        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(mbX, mbY, mbW, mbH, arc, arc);

        // Message text
        Font msgFont = new Font("Georgia", Font.BOLD, 16);
        g.setFont(msgFont);
        FontMetrics fm = g.getFontMetrics(msgFont);

        int tx = mbX + 24;
        int ty = mbY + (mbH - fm.getHeight()) / 2 + fm.getAscent();

        // Decorative diamond gem before message
        drawDiamond(g, tx + 4, ty - 5, 5);
        tx += 18;

        // Text display in cream
        g.setColor(new Color(255, 248, 230));
        g.drawString(messageToDisplay, tx, ty);
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
    // LAYER 13: INVENTORY BUTTON
    // =========================================================================
    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.getBounds();
        int x = r.x;
        int y = r.y + (btn.isPressed() ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 20;

        // Hover glow
        if (btn.isHovered()) {
            RadialGradientPaint glow = new RadialGradientPaint(
                x + w / 2, y + h / 2, (float) (w * 0.65),
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                    new Color(255, 225, 75, 115),
                    new Color(255, 205, 50, 45),
                    new Color(255, 185, 20, 0)
                }
            );
            g.setPaint(glow);
            g.fillRoundRect(x - 8, y - 8, w + 16, h + 16, arc + 8, arc + 8);
        }

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        // Gradient
        Color topC = btn.isPressed() ? btn.getPressedColor() : (btn.isHovered() ? btn.getHoverTopColor() : btn.getTopColor());
        Color botC = btn.isPressed() ? btn.getPressedColor().darker() : (btn.isHovered() ? btn.getHoverBottomColor() : btn.getBottomColor());

        LinearGradientPaint btnGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{topC, botC}
        );
        g.setPaint(btnGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Top gloss reflection
        Area glossArea = new Area(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        glossArea.intersect(new Area(new Rectangle(x, y, w, h / 2)));
        LinearGradientPaint glossGrad = new LinearGradientPaint(
            x, y, x, y + h / 2,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 255, 255, 55), new Color(255, 255, 255, 10)}
        );
        g.setPaint(glossGrad);
        g.fill(glossArea);

        // Golden border
        g.setColor(btn.getBorderColor());
        g.setStroke(new BasicStroke(2.2f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Text & satchel icon
        Font font = new Font("Arial", Font.BOLD, 17);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.getText())) / 2 + 8;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Small satchel icon before text
        drawSatchelIcon(g, textX - 22, textY - 12);

        // Text shadow and text
        g.setColor(new Color(4, 5, 12, 210));
        g.drawString(btn.getText(), textX + 1, textY + 2);

        g.setColor(Color.WHITE);
        g.drawString(btn.getText(), textX, textY);
    }

    private void drawSatchelIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(x, y, 14, 11, 3, 3);
        g.drawArc(x + 3, y - 4, 8, 8, 0, 180);
    }

    // =========================================================================
    // LAYER 14: FLOATING DUST PARTICLES
    // =========================================================================
    private void drawDustParticles(Graphics2D g) {
        g.setColor(new Color(255, 235, 170, 70));
        for (DustParticle p : dustParticles) {
            g.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public String getCurrentMessage() {
        return hoveredObject != null ? hoveredObject.getName() : lastClickedDescription;
    }

    public String getLastClickedDescription() {
        return lastClickedDescription;
    }

    public void setLastClickedDescription(String lastClickedDescription) {
        this.lastClickedDescription = lastClickedDescription;
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

    private static class MenuButton {
        private final String text;
        private final Rectangle bounds;
        private final Color topColor;
        private final Color bottomColor;
        private final Color hoverTopColor;
        private final Color hoverBottomColor;
        private final Color pressedColor;
        private final Color borderColor;
        private final Runnable action;

        private boolean hovered;
        private boolean pressed;

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

        public String getText() { return text; }
        public Rectangle getBounds() { return bounds; }
        public Color getTopColor() { return topColor; }
        public Color getBottomColor() { return bottomColor; }
        public Color getHoverTopColor() { return hoverTopColor; }
        public Color getHoverBottomColor() { return hoverBottomColor; }
        public Color getPressedColor() { return pressedColor; }
        public Color getBorderColor() { return borderColor; }

        public boolean isHovered() { return hovered; }
        public void setHovered(boolean hovered) { this.hovered = hovered; }

        public boolean isPressed() { return pressed; }
        public void setPressed(boolean pressed) { this.pressed = pressed; }
    }
}
