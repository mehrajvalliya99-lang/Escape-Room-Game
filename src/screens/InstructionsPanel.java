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
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Polished 2D Instructions Screen for "Escape the Room – Mystery Puzzle Adventure".
 * Features an old mysterious manuscript / parchment UI inside a haunted mansion library,
 * flanked by antique books and a warm glowing candle, with 6 organized instruction cards,
 * pure Graphics2D procedural icons, and an interactive Back button.
 */
public class InstructionsPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<InstructionCard> instructionCards;
    private MenuButton backButton;

    // Viewport scaling and letterboxing metrics
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Subtle atmospheric candlelight flicker and dust timer
    private Timer ambientTimer;
    private float flickerPhase = 0.0f;
    private final List<DustParticle> dustParticles;

    public InstructionsPanel(GameController controller) {
        this.controller = controller;
        this.instructionCards = new ArrayList<>();
        this.dustParticles = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 12, 22));
        setDoubleBuffered(true);
        setFocusable(true);

        initInstructionCards();
        initBackButton();
        initDustParticles();
        initMouseListeners();
        initAmbientTimer();
    }

    private void initInstructionCards() {
        // Parchment bounds: x=160, y=45, w=960, h=610
        // Left column x=205, Right column x=655, card width=420, height=98
        int col1X = 205;
        int col2X = 655;
        int cardW = 420;
        int cardH = 98;

        int row1Y = 136;
        int row2Y = 248;
        int row3Y = 360;

        // 1. EXPLORE (Left Col, Row 1)
        instructionCards.add(new InstructionCard(
            "EXPLORE",
            "Investigate each room and examine objects that may contain hidden evidence.",
            IconType.MAGNIFYING_GLASS,
            new Rectangle(col1X, row1Y, cardW, cardH)
        ));

        // 2. SOLVE (Left Col, Row 2)
        instructionCards.add(new InstructionCard(
            "SOLVE",
            "Solve ciphers, codes, and scientific puzzles to unlock secure areas.",
            IconType.PUZZLE_PIECE,
            new Rectangle(col1X, row2Y, cardW, cardH)
        ));

        // 3. COLLECT (Left Col, Row 3)
        instructionCards.add(new InstructionCard(
            "COLLECT",
            "Collect access keys, security cards, and experiment reports in your inventory.",
            IconType.KEY,
            new Rectangle(col1X, row3Y, cardW, cardH)
        ));

        // 4. OBSERVE (Right Col, Row 1)
        instructionCards.add(new InstructionCard(
            "OBSERVE",
            "Clues may be hidden in notebooks, clocks, electrical conduits, and apparatus.",
            IconType.CLOCK,
            new Rectangle(col2X, row1Y, cardW, cardH)
        ));

        // 5. ESCAPE (Right Col, Row 2)
        instructionCards.add(new InstructionCard(
            "ESCAPE",
            "Restore power, breach the Ancient Vault, and escape before quarantine engages.",
            IconType.ESCAPE_DOOR,
            new Rectangle(col2X, row2Y, cardW, cardH)
        ));

        // 6. HINTS (Right Col, Row 3)
        instructionCards.add(new InstructionCard(
            "HINTS",
            "Use progressive hints when stuck. Each hint deducts 25 points from your score.",
            IconType.LIGHTBULB,
            new Rectangle(col2X, row3Y, cardW, cardH)
        ));
    }

    private void initBackButton() {
        int btnW = 180;
        int btnH = 46;
        int btnX = (LOGICAL_WIDTH - btnW) / 2; // 550
        int btnY = 582;

        backButton = new MenuButton(
            "Back",
            new Rectangle(btnX, btnY, btnW, btnH),
            new Color(32, 92, 218),
            new Color(18, 52, 142),
            new Color(56, 124, 248),
            new Color(24, 72, 186),
            new Color(12, 36, 102),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.HOME)
        );
    }

    private void initDustParticles() {
        dustParticles.clear();
        int[][] pData = {
            {280, 180, 2}, {340, 310, 3}, {750, 160, 2}, {820, 290, 3},
            {1100, 360, 2}, {1160, 480, 3}, {150, 520, 2}, {950, 520, 2}
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
                boolean hovered = backButton.contains(lp);
                if (backButton.isHovered() != hovered) {
                    backButton.setHovered(hovered);
                    repaint();
                }
                setCursor(hovered ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());
                if (backButton.contains(lp)) {
                    backButton.setPressed(true);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());
                if (backButton.isPressed()) {
                    backButton.setPressed(false);
                    repaint();
                    if (backButton.contains(lp)) {
                        backButton.trigger();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (backButton.isHovered() || backButton.isPressed()) {
                    backButton.setHovered(false);
                    backButton.setPressed(false);
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }
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

        // Viewport transformation
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Draw layered elements
        drawBackground(g2d);
        drawBookshelfDetails(g2d);
        drawCandle(g2d);
        drawAntiqueBooks(g2d);
        drawParchment(g2d);
        drawDecorativeFrame(g2d);
        drawTitle(g2d);
        drawInstructionCards(g2d);
        drawBottomMessage(g2d);
        drawButton(g2d, backButton);
        drawDustParticles(g2d);

        g2d.dispose();
    }

    // =========================================================================
    // LAYER 1: MANSION LIBRARY BACKGROUND
    // =========================================================================
    private void drawBackground(Graphics2D g) {
        // Deep midnight navy to rich library mahogany gradient
        LinearGradientPaint roomGrad = new LinearGradientPaint(
            0, 0, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.45f, 0.85f, 1.0f},
            new Color[]{
                new Color(9, 11, 22),
                new Color(16, 20, 38),
                new Color(24, 20, 34),
                new Color(18, 14, 24)
            }
        );
        g.setPaint(roomGrad);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Wooden wall paneling vertical seams
        g.setColor(new Color(12, 14, 26, 120));
        g.setStroke(new BasicStroke(2.0f));
        for (int x = 80; x < LOGICAL_WIDTH; x += 140) {
            g.drawLine(x, 0, x, LOGICAL_HEIGHT);
        }

        // Heavy dark damask curtain folds at top corners
        g.setColor(new Color(20, 12, 28, 200));
        GeneralPath leftCurtain = new GeneralPath();
        leftCurtain.moveTo(0, 0);
        leftCurtain.curveTo(160, 40, 90, 240, 0, 360);
        leftCurtain.closePath();
        g.fill(leftCurtain);

        GeneralPath rightCurtain = new GeneralPath();
        rightCurtain.moveTo(LOGICAL_WIDTH, 0);
        rightCurtain.curveTo(LOGICAL_WIDTH - 160, 40, LOGICAL_WIDTH - 90, 240, LOGICAL_WIDTH, 360);
        rightCurtain.closePath();
        g.fill(rightCurtain);
    }

    // =========================================================================
    // LAYER 2: BOOKSHELVES & SHADOWS
    // =========================================================================
    private void drawBookshelfDetails(Graphics2D g) {
        // Faint silhouette of bookshelves in the background sides
        g.setColor(new Color(14, 16, 30, 160));
        // Left bookshelf
        g.fillRect(20, 40, 110, 480);
        g.setColor(new Color(22, 24, 42, 140));
        g.fillRect(20, 160, 110, 10);
        g.fillRect(20, 280, 110, 10);
        g.fillRect(20, 400, 110, 10);

        // Faint book silhouettes on shelves
        g.setColor(new Color(28, 26, 46, 120));
        g.fillRect(30, 80, 16, 80);
        g.fillRect(50, 95, 18, 65);
        g.fillRect(72, 70, 22, 90);
        g.fillRect(98, 85, 20, 75);

        // Right bookshelf
        g.setColor(new Color(14, 16, 30, 160));
        g.fillRect(1150, 40, 110, 480);
        g.setColor(new Color(22, 24, 42, 140));
        g.fillRect(1150, 160, 110, 10);
        g.fillRect(1150, 280, 110, 10);
        g.fillRect(1150, 400, 110, 10);

        // Dark vignette border around entire screen edges
        RadialGradientPaint vignette = new RadialGradientPaint(
            LOGICAL_WIDTH / 2.0f, LOGICAL_HEIGHT / 2.0f, 750.0f,
            new float[]{0.0f, 0.65f, 1.0f},
            new Color[]{
                new Color(0, 0, 0, 0),
                new Color(4, 5, 12, 110),
                new Color(2, 3, 8, 225)
            }
        );
        g.setPaint(vignette);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
    }

    // =========================================================================
    // LAYER 3: CANDLESTICK & GLOW (Bottom Right)
    // =========================================================================
    private void drawCandle(Graphics2D g) {
        int candleX = 1190;
        int candleBaseY = 640;
        int candleH = 95;
        int flameY = candleBaseY - candleH - 22;

        // Subtle dynamic flicker radius variation
        float flickerOffset = (float) Math.sin(flickerPhase) * 2.5f;
        float glowRadius = 150.0f + flickerOffset;

        // Multi-layered warm ambient candlelight glow
        RadialGradientPaint flameBloom = new RadialGradientPaint(
            candleX, flameY, glowRadius,
            new float[]{0.0f, 0.25f, 0.60f, 1.0f},
            new Color[]{
                new Color(255, 220, 90, 150),
                new Color(255, 175, 45, 75),
                new Color(245, 130, 20, 25),
                new Color(210, 90, 10, 0)
            }
        );
        g.setPaint(flameBloom);
        g.fillOval((int) (candleX - glowRadius), (int) (flameY - glowRadius), (int) (glowRadius * 2), (int) (glowRadius * 2));

        // Brass Candlestick Saucer & Stem
        Color brassDark = new Color(110, 75, 16);
        Color brassMid = new Color(185, 135, 35);
        Color brassLight = new Color(245, 205, 85);

        // Wide drip saucer pan
        g.setColor(brassDark);
        g.fillOval(candleX - 35, candleBaseY - 14, 70, 24);
        g.setColor(brassMid);
        g.fillOval(candleX - 32, candleBaseY - 12, 64, 20);

        // Finger loop handle
        g.setColor(brassMid);
        g.setStroke(new BasicStroke(4.0f));
        g.drawOval(candleX + 22, candleBaseY - 32, 24, 28);

        // Turned brass stem
        g.setColor(brassDark);
        g.fillRect(candleX - 8, candleBaseY - 45, 16, 35);
        g.setColor(brassLight);
        g.fillRect(candleX - 4, candleBaseY - 45, 6, 35); // highlight line
        g.setColor(brassMid);
        g.fillOval(candleX - 12, candleBaseY - 42, 24, 10);
        g.fillOval(candleX - 10, candleBaseY - 24, 20, 8);

        // Wax Candle Column (Ivory / Cream)
        int waxTopY = candleBaseY - candleH;
        LinearGradientPaint waxGrad = new LinearGradientPaint(
            candleX - 11, waxTopY, candleX + 11, waxTopY,
            new float[]{0.0f, 0.35f, 1.0f},
            new Color[]{new Color(230, 220, 195), new Color(255, 252, 235), new Color(205, 192, 168)}
        );
        g.setPaint(waxGrad);
        g.fillRoundRect(candleX - 10, waxTopY, 20, candleH - 35, 6, 6);

        // Wax drips along the candle side
        g.setColor(new Color(255, 252, 240));
        g.fillRoundRect(candleX - 11, waxTopY + 8, 5, 22, 3, 3);
        g.fillRoundRect(candleX + 6, waxTopY + 14, 5, 18, 3, 3);

        // Wick
        g.setColor(new Color(40, 30, 20));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(candleX, waxTopY, candleX, waxTopY - 7);

        // Teardrop Flame with White/Yellow/Orange Core
        GeneralPath flame = new GeneralPath();
        float flameFlickerX = (float) Math.cos(flickerPhase * 1.5) * 1.2f;
        flame.moveTo(candleX - 6, waxTopY - 6);
        flame.curveTo(candleX - 8, waxTopY - 16, candleX + flameFlickerX - 4, waxTopY - 26, candleX + flameFlickerX, waxTopY - 32);
        flame.curveTo(candleX + flameFlickerX + 4, waxTopY - 26, candleX + 8, waxTopY - 16, candleX + 6, waxTopY - 6);
        flame.closePath();

        RadialGradientPaint flameCore = new RadialGradientPaint(
            candleX, waxTopY - 16, 18,
            new float[]{0.0f, 0.45f, 0.85f, 1.0f},
            new Color[]{new Color(255, 255, 255), new Color(255, 235, 110), new Color(255, 140, 20), new Color(220, 60, 10, 0)}
        );
        g.setPaint(flameCore);
        g.fill(flame);
    }

    // =========================================================================
    // LAYER 4: ANTIQUE BOOKS STACK (Bottom Left)
    // =========================================================================
    private void drawAntiqueBooks(Graphics2D g) {
        int bx = 55;
        int by = 550;

        // Book 1 (Bottom): Deep Crimson Leather
        drawBook(g, bx, by + 46, 120, 28, new Color(125, 24, 42), new Color(85, 14, 28), "VOL. I");

        // Book 2 (Middle): Dark Sapphire/Indigo Leather
        drawBook(g, bx + 8, by + 22, 108, 25, new Color(28, 48, 96), new Color(16, 28, 62), "CLUES");

        // Book 3 (Top, tilted slightly): Deep Emerald Leather
        drawBook(g, bx + 16, by, 96, 23, new Color(24, 82, 48), new Color(14, 52, 30), "SECRETS");

        // Red silk bookmark ribbon trailing from top book
        g.setColor(new Color(185, 28, 45));
        GeneralPath ribbon = new GeneralPath();
        ribbon.moveTo(bx + 90, by + 12);
        ribbon.curveTo(bx + 115, by + 30, bx + 105, by + 65, bx + 125, by + 85);
        ribbon.lineTo(bx + 120, by + 86);
        ribbon.curveTo(bx + 100, by + 65, bx + 110, by + 30, bx + 86, by + 12);
        ribbon.closePath();
        g.fill(ribbon);
    }

    private void drawBook(Graphics2D g, int x, int y, int w, int h, Color coverTop, Color coverDark, String label) {
        // Book shadow
        g.setColor(new Color(4, 5, 12, 140));
        g.fillRoundRect(x + 3, y + 4, w, h, 6, 6);

        // Book cover
        LinearGradientPaint bg = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{coverTop, coverDark}
        );
        g.setPaint(bg);
        g.fillRoundRect(x, y, w, h, 6, 6);

        // Gold embossed ribbing lines on book spine
        g.setColor(new Color(225, 185, 65, 190));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(x + 12, y + 3, x + 12, y + h - 3);
        g.drawLine(x + 16, y + 3, x + 16, y + h - 3);
        g.drawLine(x + w - 14, y + 3, x + w - 14, y + h - 3);

        // Page edges (aged paper cream) on right edge
        g.setColor(new Color(236, 222, 185));
        g.fillRect(x + w - 10, y + 3, 8, h - 6);
        g.setColor(new Color(175, 155, 115));
        g.drawLine(x + w - 6, y + 3, x + w - 6, y + h - 3);
    }

    // =========================================================================
    // LAYER 5: MAIN PARCHMENT PANEL
    // =========================================================================
    private void drawParchment(Graphics2D g) {
        int px = 160;
        int py = 45;
        int pw = 960;
        int ph = 610;
        int arc = 22;

        // 1. Soft Warm Ambient Glow behind parchment
        RadialGradientPaint backGlow = new RadialGradientPaint(
            px + pw / 2.0f, py + ph / 2.0f, 540.0f,
            new float[]{0.0f, 0.70f, 1.0f},
            new Color[]{
                new Color(255, 225, 130, 45),
                new Color(230, 185, 80, 18),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(backGlow);
        g.fillRoundRect(px - 20, py - 20, pw + 40, ph + 40, arc + 16, arc + 16);

        // 2. Heavy Drop Shadow behind parchment for 2D depth
        g.setColor(new Color(4, 5, 12, 200));
        g.fillRoundRect(px + 6, py + 10, pw, ph, arc, arc);

        // 3. Aged Manuscript Parchment Body (Center ivory to aged darker amber-cream edges)
        RadialGradientPaint parchmentGrad = new RadialGradientPaint(
            px + pw / 2.0f, py + ph / 2.0f, (float) (pw * 0.65),
            new float[]{0.0f, 0.60f, 0.88f, 1.0f},
            new Color[]{
                new Color(254, 248, 230), // Luminous cream center
                new Color(248, 238, 212), // Vintage manuscript
                new Color(234, 216, 178), // Aged amber
                new Color(208, 182, 134)  // Burnt antique edges
            }
        );
        g.setPaint(parchmentGrad);
        g.fillRoundRect(px, py, pw, ph, arc, arc);

        // 4. Subtle Aged Texture / Deckled Inner Vignette
        Area innerArea = new Area(new RoundRectangle2D.Double(px, py, pw, ph, arc, arc));
        Area cutArea = new Area(new RoundRectangle2D.Double(px + 8, py + 8, pw - 16, ph - 16, arc - 6, arc - 6));
        innerArea.subtract(cutArea);

        LinearGradientPaint edgeBurn = new LinearGradientPaint(
            px, py, px, py + ph,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(160, 128, 80, 110), new Color(140, 105, 60, 80), new Color(175, 140, 88, 120)}
        );
        g.setPaint(edgeBurn);
        g.fill(innerArea);
    }

    // =========================================================================
    // LAYER 6: DECORATIVE GOLDEN FRAME
    // =========================================================================
    private void drawDecorativeFrame(Graphics2D g) {
        int px = 160;
        int py = 45;
        int pw = 960;
        int ph = 610;
        int arc = 22;

        // Outer Dark Antique Bronze-Gold Edge (3px)
        g.setColor(new Color(130, 94, 28));
        g.setStroke(new BasicStroke(3.2f));
        g.drawRoundRect(px, py, pw, ph, arc, arc);

        // Middle Polished Gold Highlight (1.5px)
        g.setColor(new Color(255, 218, 70));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(px + 3, py + 3, pw - 6, ph - 6, arc - 4, arc - 4);

        // Inner Filigree Line (1px)
        g.setColor(new Color(175, 130, 48, 160));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(px + 8, py + 8, pw - 16, ph - 16, arc - 8, arc - 8);

        // Four Ornate Corner Accents
        drawCornerFlourish(g, px + 8, py + 8, true, true);
        drawCornerFlourish(g, px + pw - 8, py + 8, false, true);
        drawCornerFlourish(g, px + 8, py + ph - 8, true, false);
        drawCornerFlourish(g, px + pw - 8, py + ph - 8, false, false);
    }

    private void drawCornerFlourish(Graphics2D g, int cx, int cy, boolean isLeft, boolean isTop) {
        int dx = isLeft ? 1 : -1;
        int dy = isTop ? 1 : -1;

        g.setColor(new Color(212, 165, 45));
        g.setStroke(new BasicStroke(2.0f));

        // Corner L-bracket line
        g.drawLine(cx, cy + dy * 24, cx, cy);
        g.drawLine(cx, cy, cx + dx * 24, cy);

        // Corner decorative gold rosette stud
        g.setColor(new Color(255, 225, 80));
        g.fillOval(cx + (isLeft ? 4 : -10), cy + (isTop ? 4 : -10), 6, 6);
        g.setColor(new Color(140, 98, 25));
        g.drawOval(cx + (isLeft ? 4 : -10), cy + (isTop ? 4 : -10), 6, 6);
    }

    // =========================================================================
    // LAYER 7: PARCHMENT TITLE & DECORATIVE DIVIDER
    // =========================================================================
    private void drawTitle(Graphics2D g) {
        String titleText = "Investigation Protocol";
        Font titleFont = new Font("Georgia", Font.BOLD, 33);
        FontMetrics fm = g.getFontMetrics(titleFont);

        int tx = (LOGICAL_WIDTH - fm.stringWidth(titleText)) / 2;
        int ty = 92;

        // Title drop shadow
        g.setFont(titleFont);
        g.setColor(new Color(110, 80, 30, 95));
        g.drawString(titleText, tx + 2, ty + 2);

        // Title antique bronze-gold gradient
        LinearGradientPaint titleGrad = new LinearGradientPaint(
            tx, ty - 30, tx, ty,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(145, 96, 24), new Color(196, 142, 38), new Color(125, 78, 16)}
        );
        g.setPaint(titleGrad);
        g.drawString(titleText, tx, ty);

        // Graphical Decorative Divider: "✦ ───────────── ✦" drawn with Graphics2D
        int divCenterY = 110;
        int divHalfW = 160;
        int centerX = LOGICAL_WIDTH / 2;

        // Left tapering gold line
        LinearGradientPaint leftLine = new LinearGradientPaint(
            centerX - divHalfW, divCenterY, centerX - 18, divCenterY,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(185, 140, 50, 0), new Color(185, 140, 50, 220)}
        );
        g.setPaint(leftLine);
        g.setStroke(new BasicStroke(1.8f));
        g.drawLine(centerX - divHalfW, divCenterY, centerX - 18, divCenterY);

        // Right tapering gold line
        LinearGradientPaint rightLine = new LinearGradientPaint(
            centerX + 18, divCenterY, centerX + divHalfW, divCenterY,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(185, 140, 50, 220), new Color(185, 140, 50, 0)}
        );
        g.setPaint(rightLine);
        g.drawLine(centerX + 18, divCenterY, centerX + divHalfW, divCenterY);

        // Central diamond flourish gem
        drawDiamond(g, centerX, divCenterY, 8);
        drawDiamond(g, centerX - divHalfW + 10, divCenterY, 4);
        drawDiamond(g, centerX + divHalfW - 10, divCenterY, 4);
    }

    private void drawDiamond(Graphics2D g, int cx, int cy, int size) {
        GeneralPath diamond = new GeneralPath();
        diamond.moveTo(cx, cy - size);
        diamond.lineTo(cx + size, cy);
        diamond.lineTo(cx, cy + size);
        diamond.lineTo(cx - size, cy);
        diamond.closePath();

        g.setColor(new Color(255, 220, 75));
        g.fill(diamond);
        g.setColor(new Color(150, 105, 30));
        g.setStroke(new BasicStroke(1.0f));
        g.draw(diamond);
    }

    // =========================================================================
    // LAYER 8: INSTRUCTION CARDS (2 COLUMNS × 3 ROWS)
    // =========================================================================
    private void drawInstructionCards(Graphics2D g) {
        for (InstructionCard card : instructionCards) {
            drawInstructionCard(g, card);
        }
    }

    private void drawInstructionCard(Graphics2D g, InstructionCard card) {
        Rectangle r = card.getBounds();
        int x = r.x;
        int y = r.y;
        int w = r.width;
        int h = r.height;
        int arc = 16;

        // Card soft shadow
        g.setColor(new Color(140, 115, 75, 45));
        g.fillRoundRect(x + 2, y + 3, w, h, arc, arc);

        // Card background (subtle parchment inset)
        LinearGradientPaint cardBg = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 252, 244), new Color(244, 234, 214)}
        );
        g.setPaint(cardBg);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Card inner border
        g.setColor(new Color(212, 192, 150));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Golden accent line on top of card
        g.setColor(new Color(240, 195, 80, 150));
        g.drawLine(x + arc, y + 1, x + w - arc, y + 1);

        // 1. Decorative Icon Badge (Circular antique shield)
        int badgeX = x + 16;
        int badgeY = y + (h - 48) / 2;
        int badgeR = 48;

        // Badge shadow
        g.setColor(new Color(120, 95, 60, 60));
        g.fillOval(badgeX + 1, badgeY + 2, badgeR, badgeR);

        // Badge background: dark leather / bronze gradient
        LinearGradientPaint badgeGrad = new LinearGradientPaint(
            badgeX, badgeY, badgeX, badgeY + badgeR,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(68, 42, 22), new Color(38, 22, 12)}
        );
        g.setPaint(badgeGrad);
        g.fillOval(badgeX, badgeY, badgeR, badgeR);

        // Golden rim around badge
        g.setColor(new Color(225, 185, 65));
        g.setStroke(new BasicStroke(1.8f));
        g.drawOval(badgeX, badgeY, badgeR, badgeR);

        // Render the custom procedural 2D icon
        drawIcon(g, card.getIconType(), badgeX + badgeR / 2, badgeY + badgeR / 2);

        // 2. Card Header / Title
        int textX = x + 76;
        Font headerFont = new Font("Georgia", Font.BOLD, 17);
        g.setFont(headerFont);
        g.setColor(new Color(110, 68, 18));
        g.drawString(card.getTitle(), textX, y + 30);

        // Small gold underline under title
        FontMetrics fm = g.getFontMetrics(headerFont);
        int titleW = fm.stringWidth(card.getTitle());
        g.setColor(new Color(218, 168, 55, 180));
        g.setStroke(new BasicStroke(1.4f));
        g.drawLine(textX, y + 35, textX + titleW + 8, y + 35);

        // 3. Card Description Text (Word-wrapped dark sepia text)
        Font bodyFont = new Font("Arial", Font.PLAIN, 13);
        g.setFont(bodyFont);
        g.setColor(new Color(58, 42, 28));

        int maxTextW = w - 88;
        drawWrappedText(g, card.getDescription(), textX, y + 55, maxTextW, 17);
    }

    private void drawWrappedText(Graphics2D g, String text, int x, int y, int maxW, int lineH) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int curY = y;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(testLine) > maxW && line.length() > 0) {
                g.drawString(line.toString(), x, curY);
                curY += lineH;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        if (line.length() > 0) {
            g.drawString(line.toString(), x, curY);
        }
    }

    // =========================================================================
    // LAYER 9: PROCEDURAL 2D ICONS (Gold & highlights, purely 2D)
    // =========================================================================
    private void drawIcon(Graphics2D g, IconType type, int cx, int cy) {
        Graphics2D ig = (Graphics2D) g.create();
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color goldLight = new Color(255, 235, 120);
        Color goldMid = new Color(230, 180, 45);
        Color goldDark = new Color(160, 115, 25);

        switch (type) {
            case MAGNIFYING_GLASS -> {
                // Glass lens ring
                ig.setColor(goldDark);
                ig.setStroke(new BasicStroke(3.2f));
                ig.drawOval(cx - 9, cy - 10, 16, 16);
                ig.setColor(goldMid);
                ig.setStroke(new BasicStroke(2.0f));
                ig.drawOval(cx - 9, cy - 10, 16, 16);

                // Pale cyan glass reflection
                ig.setColor(new Color(200, 235, 255, 140));
                ig.fillOval(cx - 7, cy - 8, 12, 12);
                ig.setColor(Color.WHITE);
                ig.setStroke(new BasicStroke(1.2f));
                ig.drawLine(cx - 4, cy - 5, cx - 1, cy - 2);

                // Brass handle
                ig.setColor(goldLight);
                ig.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                ig.drawLine(cx + 4, cy + 3, cx + 10, cy + 9);
            }
            case PUZZLE_PIECE -> {
                // Interlocking puzzle piece shape
                GeneralPath p = new GeneralPath();
                p.moveTo(cx - 9, cy - 9);
                p.lineTo(cx - 3, cy - 9);
                p.curveTo(cx - 3, cy - 13, cx + 3, cy - 13, cx + 3, cy - 9); // top tab
                p.lineTo(cx + 9, cy - 9);
                p.lineTo(cx + 9, cy - 3);
                p.curveTo(cx + 13, cy - 3, cx + 13, cy + 3, cx + 9, cy + 3); // right tab
                p.lineTo(cx + 9, cy + 9);
                p.lineTo(cx + 3, cy + 9);
                p.curveTo(cx + 3, cy + 6, cx - 3, cy + 6, cx - 3, cy + 9);   // bottom blank
                p.lineTo(cx - 9, cy + 9);
                p.closePath();

                ig.setColor(goldMid);
                ig.fill(p);
                ig.setColor(goldLight);
                ig.setStroke(new BasicStroke(1.6f));
                ig.draw(p);
            }
            case KEY -> {
                // Skeleton key
                ig.setColor(goldLight);
                ig.setStroke(new BasicStroke(2.2f));
                ig.drawOval(cx - 10, cy - 5, 9, 9);
                ig.drawLine(cx - 1, cy - 1, cx + 10, cy - 1); // stem
                ig.drawLine(cx + 7, cy - 1, cx + 7, cy + 5);  // tooth 1
                ig.drawLine(cx + 10, cy - 1, cx + 10, cy + 4); // tooth 2
            }
            case CLOCK -> {
                // Pocket watch / clock
                ig.setColor(goldDark);
                ig.setStroke(new BasicStroke(2.5f));
                ig.drawOval(cx - 9, cy - 8, 18, 18);
                ig.setColor(new Color(255, 252, 235));
                ig.fillOval(cx - 8, cy - 7, 16, 16);

                // Clock hands (10:10)
                ig.setColor(new Color(40, 28, 16));
                ig.setStroke(new BasicStroke(1.5f));
                ig.drawLine(cx, cy + 1, cx - 4, cy - 4);
                ig.drawLine(cx, cy + 1, cx + 4, cy - 3);

                // Top watch loop
                ig.setColor(goldLight);
                ig.setStroke(new BasicStroke(1.8f));
                ig.drawOval(cx - 3, cy - 13, 6, 5);
            }
            case ESCAPE_DOOR -> {
                // Arched doorway with light beaming through
                Shape doorArch = new RoundRectangle2D.Double(cx - 8, cy - 10, 16, 20, 6, 6);
                ig.setColor(new Color(255, 235, 120)); // bright light beam
                ig.fill(doorArch);

                ig.setColor(goldDark);
                ig.setStroke(new BasicStroke(2.2f));
                ig.draw(doorArch);

                // Door frame posts
                ig.drawLine(cx - 8, cy + 10, cx + 8, cy + 10);
                // Door knob
                ig.setColor(new Color(110, 75, 20));
                ig.fillOval(cx + 2, cy, 3, 3);
            }
            case LIGHTBULB -> {
                // Glowing hint bulb
                GeneralPath bulb = new GeneralPath();
                bulb.moveTo(cx - 7, cy - 5);
                bulb.curveTo(cx - 9, cy - 12, cx + 9, cy - 12, cx + 7, cy - 5);
                bulb.curveTo(cx + 5, cy - 1, cx + 4, cy + 3, cx + 3, cy + 6);
                bulb.lineTo(cx - 3, cy + 6);
                bulb.curveTo(cx - 4, cy + 3, cx - 5, cy - 1, cx - 7, cy - 5);
                bulb.closePath();

                ig.setColor(goldLight);
                ig.fill(bulb);
                ig.setColor(goldDark);
                ig.setStroke(new BasicStroke(1.5f));
                ig.draw(bulb);

                // Screw base
                ig.setColor(new Color(180, 140, 60));
                ig.fillRect(cx - 3, cy + 6, 6, 4);

                // Spark rays
                ig.setColor(new Color(255, 240, 150));
                ig.setStroke(new BasicStroke(1.4f));
                ig.drawLine(cx, cy - 14, cx, cy - 17);
                ig.drawLine(cx - 10, cy - 10, cx - 13, cy - 13);
                ig.drawLine(cx + 10, cy - 10, cx + 13, cy - 13);
            }
        }
        ig.dispose();
    }

    // =========================================================================
    // LAYER 10: BOTTOM MESSAGE
    // =========================================================================
    private void drawBottomMessage(Graphics2D g) {
        String msg = "Good Luck, Detective.";
        Font msgFont = new Font("Georgia", Font.BOLD | Font.ITALIC, 21);
        FontMetrics fm = g.getFontMetrics(msgFont);

        int mx = (LOGICAL_WIDTH - fm.stringWidth(msg)) / 2;
        int my = 548;

        // Message shadow
        g.setFont(msgFont);
        g.setColor(new Color(130, 95, 45, 90));
        g.drawString(msg, mx + 1, my + 2);

        // Warm dark sepia / antique gold message text
        g.setColor(new Color(90, 56, 20));
        g.drawString(msg, mx, my);
    }

    // =========================================================================
    // LAYER 11: BACK BUTTON
    // =========================================================================
    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.getBounds();
        int x = r.x;
        int y = r.y + (btn.isPressed() ? 2 : 0); // Tactile 2px downward press offset
        int w = r.width;
        int h = r.height;
        int arc = 24;

        // 1. Golden Aura Glow on Hover
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

        // 2. Button Outer Drop Shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        // 3. Button Background Gradient
        Color topC = btn.isPressed() ? btn.getPressedColor() : (btn.isHovered() ? btn.getHoverTopColor() : btn.getTopColor());
        Color botC = btn.isPressed() ? btn.getPressedColor().darker() : (btn.isHovered() ? btn.getHoverBottomColor() : btn.getBottomColor());

        LinearGradientPaint btnGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{topC, botC}
        );
        g.setPaint(btnGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // 4. Subtle Top Half Gloss Reflection
        Area glossArea = new Area(new RoundRectangle2D.Double(x, y, w, h, arc, arc));
        glossArea.intersect(new Area(new Rectangle(x, y, w, h / 2)));
        LinearGradientPaint glossGrad = new LinearGradientPaint(
            x, y, x, y + h / 2,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 255, 255, 55), new Color(255, 255, 255, 10)}
        );
        g.setPaint(glossGrad);
        g.fill(glossArea);

        // 5. Ornate Golden Border
        g.setColor(btn.getBorderColor());
        g.setStroke(new BasicStroke(2.4f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Inner subtle golden highlight border
        g.setColor(new Color(255, 245, 180, btn.isHovered() ? 150 : 85));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, arc - 4, arc - 4);

        // 6. Centered Bold Button Text
        Font font = new Font("Arial", Font.BOLD, 19);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.getText())) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Text drop shadow
        g.setColor(new Color(4, 5, 12, 210));
        g.drawString(btn.getText(), textX + 1, textY + 2);

        // Text white/ivory
        g.setColor(Color.WHITE);
        g.drawString(btn.getText(), textX, textY);
    }

    // =========================================================================
    // LAYER 12: FLOATING DUST PARTICLES
    // =========================================================================
    private void drawDustParticles(Graphics2D g) {
        g.setColor(new Color(255, 235, 170, 75));
        for (DustParticle p : dustParticles) {
            g.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }
    }

    // =========================================================================
    // SUPPORTING ENUMS AND CLASSES
    // =========================================================================
    private enum IconType {
        MAGNIFYING_GLASS,
        PUZZLE_PIECE,
        KEY,
        CLOCK,
        ESCAPE_DOOR,
        LIGHTBULB
    }

    private static class InstructionCard {
        private final String title;
        private final String description;
        private final IconType iconType;
        private final Rectangle bounds;

        public InstructionCard(String title, String description, IconType iconType, Rectangle bounds) {
            this.title = title;
            this.description = description;
            this.iconType = iconType;
            this.bounds = bounds;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public IconType getIconType() { return iconType; }
        public Rectangle getBounds() { return bounds; }
    }

    private static class DustParticle {
        float x, y;
        int size;
        float speedY;
        float driftX;

        public DustParticle(float x, float y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speedY = 0.2f + (float) Math.random() * 0.25f;
            this.driftX = (float) (Math.random() - 0.5) * 0.15f;
        }

        public void update() {
            y -= speedY;
            x += driftX;
            if (y < 40) {
                y = 660;
                x = 180 + (float) (Math.random() * 920);
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
