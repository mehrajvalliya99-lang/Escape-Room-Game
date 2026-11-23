import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Polished 2D Player Details & Difficulty Screen for "Escape the Room – Mystery Puzzle Adventure".
 * Features an elegant antique study aesthetic with custom-styled text input,
 * three interactive 2D difficulty selection cards (EASY, MEDIUM, HARD),
 * input validation, and animated button interactions.
 */
public class PlayerDetailsPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final JTextField nameField;
    private final Rectangle nameBoxLogicalBounds;

    private Difficulty selectedDifficulty = Difficulty.EASY;
    private final List<DifficultyCard> difficultyCards;

    private MenuButton backButton;
    private MenuButton enterButton;

    private String warningMessage = "";
    private float warningAlpha = 0.0f;

    // Viewport scaling and letterboxing metrics
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Atmospheric ambient timer for candlelight and dust
    private Timer ambientTimer;
    private float flickerPhase = 0.0f;
    private final List<DustParticle> dustParticles;

    public PlayerDetailsPanel(GameController controller) {
        this.controller = controller;
        this.difficultyCards = new ArrayList<>();
        this.dustParticles = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 12, 22));
        setLayout(null); // Absolute layout with custom responsive doLayout()
        setDoubleBuffered(true);
        setFocusable(true);

        // Logical bounds for name input field (centered)
        nameBoxLogicalBounds = new Rectangle(390, 168, 500, 46);

        // Custom-styled name text field
        nameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Deep dark interior background
                g2.setColor(new Color(20, 16, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Subtle inner shadow at the top
                g2.setColor(new Color(6, 4, 10, 120));
                g2.fillRoundRect(2, 2, getWidth() - 4, 10, 12, 12);

                // Focus glow or normal gold border
                if (hasFocus()) {
                    g2.setColor(new Color(255, 215, 64, 90));
                    g2.setStroke(new BasicStroke(4.0f));
                    g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);

                    g2.setColor(new Color(255, 225, 80));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);
                } else {
                    g2.setColor(new Color(185, 140, 50));
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                }

                g2.dispose();
                super.paintComponent(g);

                // Placeholder text if empty and not focused
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D pg = (Graphics2D) g.create();
                    pg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    pg.setFont(getFont());
                    pg.setColor(new Color(175, 155, 125, 140));
                    FontMetrics pfm = pg.getFontMetrics();
                    int py = (getHeight() - pfm.getHeight()) / 2 + pfm.getAscent();
                    pg.drawString("Detective Name", 20, py);
                    pg.dispose();
                }
            }
        };

        nameField.setOpaque(false);
        nameField.setCaretColor(new Color(255, 215, 64));
        nameField.setForeground(new Color(255, 248, 230));
        nameField.setFont(new Font("Georgia", Font.BOLD, 18));
        nameField.setBorder(new EmptyBorder(4, 18, 4, 18));
        nameField.setMargin(new Insets(2, 16, 2, 16));

        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Clear warning when player begins typing
                if (!warningMessage.isEmpty()) {
                    warningMessage = "";
                    repaint();
                }
            }
        });

        add(nameField);

        initDifficultyCards();
        initButtons();
        initDustParticles();
        initMouseListeners();
        initAmbientTimer();
    }

    private void initDifficultyCards() {
        // Main panel bounds: x=200, y=40, w=880, h=630
        // 3 Cards: cardW=255, cardH=180, gap=20
        // Total width: 255*3 + 20*2 = 805, leftMargin = (1280 - 805) / 2 = 237
        int cardW = 255;
        int cardH = 175;
        int cardY = 278;
        int startX = 237;
        int gap = 20;

        // EASY
        difficultyCards.add(new DifficultyCard(
            Difficulty.EASY,
            new Rectangle(startX, cardY, cardW, cardH),
            new Color(36, 145, 68),
            new Color(20, 85, 38)
        ));

        // MEDIUM
        difficultyCards.add(new DifficultyCard(
            Difficulty.MEDIUM,
            new Rectangle(startX + cardW + gap, cardY, cardW, cardH),
            new Color(32, 92, 218),
            new Color(18, 52, 142)
        ));

        // HARD
        difficultyCards.add(new DifficultyCard(
            Difficulty.HARD,
            new Rectangle(startX + (cardW + gap) * 2, cardY, cardW, cardH),
            new Color(185, 42, 42),
            new Color(112, 22, 22)
        ));
    }

    private void initButtons() {
        // Back Button (Bottom Left)
        backButton = new MenuButton(
            "Back",
            new Rectangle(240, 582, 160, 48),
            new Color(32, 92, 218),
            new Color(18, 52, 142),
            new Color(56, 124, 248),
            new Color(24, 72, 186),
            new Color(12, 36, 102),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.HOME)
        );

        // Enter the Facility Button (Bottom Right)
        enterButton = new MenuButton(
            "ENTER THE FACILITY",
            new Rectangle(700, 580, 340, 52),
            new Color(245, 165, 25),
            new Color(195, 95, 10),
            new Color(255, 190, 45),
            new Color(225, 115, 15),
            new Color(145, 65, 8),
            new Color(255, 225, 80),
            this::handleEnterMansion
        );
    }

    private void handleEnterMansion() {
        String enteredName = nameField.getText().trim();
        if (enteredName.isEmpty()) {
            warningMessage = "Please enter your detective name.";
            nameField.requestFocusInWindow();
            repaint();
            return;
        }

        // Create player with entered details and difficulty, score initialized to 0
        Player player = new Player(enteredName, selectedDifficulty);
        controller.startNewGame(player);
    }

    private void initDustParticles() {
        dustParticles.clear();
        int[][] pData = {
            {280, 160, 2}, {360, 240, 3}, {780, 150, 2}, {850, 260, 3},
            {1120, 340, 2}, {1180, 460, 3}, {140, 500, 2}, {960, 510, 2}
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
                boolean anyHovered = false;

                // Check cards hover
                for (DifficultyCard card : difficultyCards) {
                    boolean h = card.contains(lp);
                    if (card.isHovered() != h) {
                        card.setHovered(h);
                        repaint();
                    }
                    if (h) anyHovered = true;
                }

                // Check buttons hover
                boolean backH = backButton.contains(lp);
                if (backButton.isHovered() != backH) {
                    backButton.setHovered(backH);
                    repaint();
                }
                if (backH) anyHovered = true;

                boolean enterH = enterButton.contains(lp);
                if (enterButton.isHovered() != enterH) {
                    enterButton.setHovered(enterH);
                    repaint();
                }
                if (enterH) anyHovered = true;

                setCursor(anyHovered ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                // Click difficulty card to select
                for (DifficultyCard card : difficultyCards) {
                    if (card.contains(lp)) {
                        selectedDifficulty = card.getDifficulty();
                        repaint();
                        break;
                    }
                }

                if (backButton.contains(lp)) {
                    backButton.setPressed(true);
                    repaint();
                }

                if (enterButton.contains(lp)) {
                    enterButton.setPressed(true);
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

                if (enterButton.isPressed()) {
                    enterButton.setPressed(false);
                    repaint();
                    if (enterButton.contains(lp)) {
                        enterButton.trigger();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boolean needRepaint = false;
                for (DifficultyCard card : difficultyCards) {
                    if (card.isHovered()) {
                        card.setHovered(false);
                        needRepaint = true;
                    }
                }
                if (backButton.isHovered() || backButton.isPressed()) {
                    backButton.setHovered(false);
                    backButton.setPressed(false);
                    needRepaint = true;
                }
                if (enterButton.isHovered() || enterButton.isPressed()) {
                    enterButton.setHovered(false);
                    enterButton.setPressed(false);
                    needRepaint = true;
                }
                setCursor(Cursor.getDefaultCursor());
                if (needRepaint) repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updateScaleAndOffset();

        // Responsive position of name text field matching 16:9 viewport
        int physX = (int) (offsetX + nameBoxLogicalBounds.x * scale);
        int physY = (int) (offsetY + nameBoxLogicalBounds.y * scale);
        int physW = (int) (nameBoxLogicalBounds.width * scale);
        int physH = (int) (nameBoxLogicalBounds.height * scale);

        nameField.setBounds(physX, physY, physW, physH);
        nameField.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) (18 * scale))));
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
        drawCandle(g2d);
        drawMainPanel(g2d);
        drawDecorativeFrame(g2d);
        drawHeader(g2d);
        drawNameSectionLabels(g2d);
        drawDifficultySection(g2d);
        drawButton(g2d, backButton);
        drawButton(g2d, enterButton);
        drawDustParticles(g2d);

        g2d.dispose();
    }

    // =========================================================================
    // LAYER 1: ATMOSPHERIC STUDY BACKGROUND
    // =========================================================================
    private void drawBackground(Graphics2D g) {
        // Deep midnight navy to library mahogany gradient
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

        // Faint background bookshelves on sides
        g.setColor(new Color(14, 16, 30, 150));
        g.fillRect(25, 40, 115, 520);
        g.fillRect(1140, 40, 115, 520);

        g.setColor(new Color(22, 24, 42, 130));
        g.fillRect(25, 170, 115, 10);
        g.fillRect(25, 310, 115, 10);
        g.fillRect(25, 450, 115, 10);
        g.fillRect(1140, 170, 115, 10);
        g.fillRect(1140, 310, 115, 10);
        g.fillRect(1140, 450, 115, 10);

        // Faint book spine silhouettes on shelves
        g.setColor(new Color(28, 26, 46, 115));
        g.fillRect(35, 90, 18, 80);
        g.fillRect(58, 105, 18, 65);
        g.fillRect(80, 80, 22, 90);
        g.fillRect(106, 95, 20, 75);

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
    // LAYER 2: CANDLESTICK & GLOW (Bottom Right)
    // =========================================================================
    private void drawCandle(Graphics2D g) {
        int candleX = 1195;
        int candleBaseY = 645;
        int candleH = 90;
        int flameY = candleBaseY - candleH - 22;

        float flickerOffset = (float) Math.sin(flickerPhase) * 2.5f;
        float glowRadius = 145.0f + flickerOffset;

        // Radiant warm ambient candlelight glow
        RadialGradientPaint flameBloom = new RadialGradientPaint(
            candleX, flameY, glowRadius,
            new float[]{0.0f, 0.25f, 0.60f, 1.0f},
            new Color[]{
                new Color(255, 220, 90, 140),
                new Color(255, 175, 45, 70),
                new Color(245, 130, 20, 20),
                new Color(210, 90, 10, 0)
            }
        );
        g.setPaint(flameBloom);
        g.fillOval((int) (candleX - glowRadius), (int) (flameY - glowRadius), (int) (glowRadius * 2), (int) (glowRadius * 2));

        // Brass candlestick base
        g.setColor(new Color(110, 75, 16));
        g.fillOval(candleX - 32, candleBaseY - 14, 64, 22);
        g.setColor(new Color(185, 135, 35));
        g.fillOval(candleX - 28, candleBaseY - 12, 56, 18);

        // Finger loop handle
        g.setColor(new Color(185, 135, 35));
        g.setStroke(new BasicStroke(3.5f));
        g.drawOval(candleX + 20, candleBaseY - 30, 22, 26);

        // Turned stem
        g.setColor(new Color(110, 75, 16));
        g.fillRect(candleX - 7, candleBaseY - 42, 14, 32);
        g.setColor(new Color(245, 205, 85));
        g.fillRect(candleX - 3, candleBaseY - 42, 5, 32);

        // Wax pillar
        int waxTopY = candleBaseY - candleH;
        LinearGradientPaint waxGrad = new LinearGradientPaint(
            candleX - 10, waxTopY, candleX + 10, waxTopY,
            new float[]{0.0f, 0.35f, 1.0f},
            new Color[]{new Color(230, 220, 195), new Color(255, 252, 235), new Color(205, 192, 168)}
        );
        g.setPaint(waxGrad);
        g.fillRoundRect(candleX - 9, waxTopY, 18, candleH - 32, 6, 6);

        // Wick
        g.setColor(new Color(40, 30, 20));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(candleX, waxTopY, candleX, waxTopY - 6);

        // Teardrop flame
        GeneralPath flame = new GeneralPath();
        float flameFlickerX = (float) Math.cos(flickerPhase * 1.5) * 1.2f;
        flame.moveTo(candleX - 5, waxTopY - 6);
        flame.curveTo(candleX - 7, waxTopY - 15, candleX + flameFlickerX - 3, waxTopY - 24, candleX + flameFlickerX, waxTopY - 30);
        flame.curveTo(candleX + flameFlickerX + 3, waxTopY - 24, candleX + 7, waxTopY - 15, candleX + 5, waxTopY - 6);
        flame.closePath();

        RadialGradientPaint flameCore = new RadialGradientPaint(
            candleX, waxTopY - 15, 16,
            new float[]{0.0f, 0.45f, 0.85f, 1.0f},
            new Color[]{new Color(255, 255, 255), new Color(255, 235, 110), new Color(255, 140, 20), new Color(220, 60, 10, 0)}
        );
        g.setPaint(flameCore);
        g.fill(flame);
    }

    // =========================================================================
    // LAYER 3: MAIN PARCHMENT PANEL
    // =========================================================================
    private void drawMainPanel(Graphics2D g) {
        int px = 200;
        int py = 40;
        int pw = 880;
        int ph = 630;
        int arc = 22;

        // Warm ambient glow behind parchment
        RadialGradientPaint backGlow = new RadialGradientPaint(
            px + pw / 2.0f, py + ph / 2.0f, 520.0f,
            new float[]{0.0f, 0.70f, 1.0f},
            new Color[]{
                new Color(255, 225, 130, 40),
                new Color(230, 185, 80, 16),
                new Color(0, 0, 0, 0)
            }
        );
        g.setPaint(backGlow);
        g.fillRoundRect(px - 18, py - 18, pw + 36, ph + 36, arc + 14, arc + 14);

        // Heavy drop shadow
        g.setColor(new Color(4, 5, 12, 195));
        g.fillRoundRect(px + 6, py + 10, pw, ph, arc, arc);

        // Parchment gradient center (cream to antique amber edges)
        RadialGradientPaint parchmentGrad = new RadialGradientPaint(
            px + pw / 2.0f, py + ph / 2.0f, (float) (pw * 0.65),
            new float[]{0.0f, 0.60f, 0.88f, 1.0f},
            new Color[]{
                new Color(254, 248, 230),
                new Color(248, 238, 212),
                new Color(234, 216, 178),
                new Color(208, 182, 134)
            }
        );
        g.setPaint(parchmentGrad);
        g.fillRoundRect(px, py, pw, ph, arc, arc);

        // Inner edge burn vignette
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
    // LAYER 4: DECORATIVE FRAME
    // =========================================================================
    private void drawDecorativeFrame(Graphics2D g) {
        int px = 200;
        int py = 40;
        int pw = 880;
        int ph = 630;
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

        g.drawLine(cx, cy + dy * 22, cx, cy);
        g.drawLine(cx, cy, cx + dx * 22, cy);

        // Decorative rosette stud
        g.setColor(new Color(255, 225, 80));
        g.fillOval(cx + (isLeft ? 4 : -10), cy + (isTop ? 4 : -10), 6, 6);
        g.setColor(new Color(140, 98, 25));
        g.drawOval(cx + (isLeft ? 4 : -10), cy + (isTop ? 4 : -10), 6, 6);
    }

    // =========================================================================
    // LAYER 5: HEADER & DIVIDER
    // =========================================================================
    private void drawHeader(Graphics2D g) {
        String titleText = "PLAYER DETAILS";
        String subtitleText = "Tell us who is entering the mansion...";

        Font titleFont = new Font("Georgia", Font.BOLD, 30);
        Font subFont = new Font("Georgia", Font.BOLD | Font.ITALIC, 16);

        FontMetrics tm = g.getFontMetrics(titleFont);
        FontMetrics sm = g.getFontMetrics(subFont);

        int tx = (LOGICAL_WIDTH - tm.stringWidth(titleText)) / 2;
        int ty = 84;

        int sx = (LOGICAL_WIDTH - sm.stringWidth(subtitleText)) / 2;
        int sy = 110;

        // Title drop shadow
        g.setFont(titleFont);
        g.setColor(new Color(110, 80, 30, 90));
        g.drawString(titleText, tx + 2, ty + 2);

        // Title antique bronze-gold gradient
        LinearGradientPaint titleGrad = new LinearGradientPaint(
            tx, ty - 26, tx, ty,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(145, 96, 24), new Color(196, 142, 38), new Color(125, 78, 16)}
        );
        g.setPaint(titleGrad);
        g.drawString(titleText, tx, ty);

        // Subtitle
        g.setFont(subFont);
        g.setColor(new Color(102, 70, 44));
        g.drawString(subtitleText, sx, sy);

        // Graphical decorative divider
        int divCenterY = 126;
        int divHalfW = 140;
        int centerX = LOGICAL_WIDTH / 2;

        LinearGradientPaint leftLine = new LinearGradientPaint(
            centerX - divHalfW, divCenterY, centerX - 16, divCenterY,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(185, 140, 50, 0), new Color(185, 140, 50, 220)}
        );
        g.setPaint(leftLine);
        g.setStroke(new BasicStroke(1.8f));
        g.drawLine(centerX - divHalfW, divCenterY, centerX - 16, divCenterY);

        LinearGradientPaint rightLine = new LinearGradientPaint(
            centerX + 16, divCenterY, centerX + divHalfW, divCenterY,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(185, 140, 50, 220), new Color(185, 140, 50, 0)}
        );
        g.setPaint(rightLine);
        g.drawLine(centerX + 16, divCenterY, centerX + divHalfW, divCenterY);

        drawDiamond(g, centerX, divCenterY, 6);
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
    // LAYER 6: NAME SECTION LABELS & IN-GAME WARNING
    // =========================================================================
    private void drawNameSectionLabels(Graphics2D g) {
        String label = "ENTER YOUR NAME";
        Font labelFont = new Font("Georgia", Font.BOLD, 15);
        FontMetrics fm = g.getFontMetrics(labelFont);

        int lx = (LOGICAL_WIDTH - fm.stringWidth(label)) / 2;
        int ly = 154;

        g.setFont(labelFont);
        g.setColor(new Color(110, 68, 18));
        g.drawString(label, lx, ly);

        // In-game Warning Message (if validation failed)
        if (!warningMessage.isEmpty()) {
            Font warnFont = new Font("Arial", Font.BOLD, 14);
            g.setFont(warnFont);
            FontMetrics wfm = g.getFontMetrics(warnFont);

            int wx = (LOGICAL_WIDTH - wfm.stringWidth(warningMessage)) / 2;
            int wy = 232;

            // Warning icon and text
            g.setColor(new Color(205, 32, 32));
            g.drawString("⚠ " + warningMessage, wx, wy);
        }
    }

    // =========================================================================
    // LAYER 7: DIFFICULTY SELECTION SECTION
    // =========================================================================
    private void drawDifficultySection(Graphics2D g) {
        String label = "CHOOSE DIFFICULTY";
        Font labelFont = new Font("Georgia", Font.BOLD, 15);
        FontMetrics fm = g.getFontMetrics(labelFont);

        int lx = (LOGICAL_WIDTH - fm.stringWidth(label)) / 2;
        int ly = 260;

        g.setFont(labelFont);
        g.setColor(new Color(110, 68, 18));
        g.drawString(label, lx, ly);

        // Draw the 3 Difficulty Cards
        for (DifficultyCard card : difficultyCards) {
            drawDifficultyCard(g, card);
        }
    }

    private void drawDifficultyCard(Graphics2D g, DifficultyCard card) {
        Rectangle r = card.getBounds();
        int x = r.x;
        int y = r.y;
        int w = r.width;
        int h = r.height;
        int arc = 18;

        boolean isSelected = (card.getDifficulty() == selectedDifficulty);
        boolean isHovered = card.isHovered();

        // 1. Golden Aura Glow if selected or hovered
        if (isSelected || isHovered) {
            int glowAlpha = isSelected ? 120 : 65;
            RadialGradientPaint glow = new RadialGradientPaint(
                x + w / 2, y + h / 2, (float) (w * 0.65),
                new float[]{0.0f, 0.70f, 1.0f},
                new Color[]{
                    new Color(255, 220, 75, glowAlpha),
                    new Color(255, 195, 45, glowAlpha / 2),
                    new Color(255, 180, 20, 0)
                }
            );
            g.setPaint(glow);
            g.fillRoundRect(x - 8, y - 8, w + 16, h + 16, arc + 8, arc + 8);
        }

        // 2. Card Shadow
        g.setColor(new Color(140, 115, 75, isSelected ? 65 : 45));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        // 3. Card Background
        Color bgTop = isSelected ? new Color(255, 254, 245) : (isHovered ? new Color(255, 252, 242) : new Color(250, 242, 226));
        Color bgBot = isSelected ? new Color(248, 238, 215) : (isHovered ? new Color(244, 232, 208) : new Color(238, 224, 198));

        LinearGradientPaint cardBg = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{bgTop, bgBot}
        );
        g.setPaint(cardBg);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // 4. Card Border
        if (isSelected) {
            // Bright thick gold border with inner highlight
            g.setColor(new Color(255, 215, 64));
            g.setStroke(new BasicStroke(3.0f));
            g.drawRoundRect(x, y, w, h, arc, arc);

            g.setColor(new Color(255, 245, 190, 160));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x + 3, y + 3, w - 6, h - 6, arc - 4, arc - 4);
        } else if (isHovered) {
            g.setColor(new Color(240, 195, 80));
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(x, y, w, h, arc, arc);
        } else {
            g.setColor(new Color(205, 182, 138));
            g.setStroke(new BasicStroke(1.4f));
            g.drawRoundRect(x, y, w, h, arc, arc);
        }

        // 5. Decorative 2D Icon badge at top
        int iconBadgeCX = x + w / 2;
        int iconBadgeCY = y + 40;
        drawDifficultyIconBadge(g, card.getDifficulty(), iconBadgeCX, iconBadgeCY, isSelected, isHovered);

        // 6. Difficulty Name Header
        String name = card.getDifficulty().getDisplayName().toUpperCase();
        Font titleFont = new Font("Georgia", Font.BOLD, 19);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        int tx = x + (w - tfm.stringWidth(name)) / 2;
        int ty = y + 88;

        g.setColor(new Color(110, 68, 18));
        g.drawString(name, tx, ty);

        // 7. Time Label Badge (Pill button look)
        String timeText = card.getDifficulty().getTimeLabel();
        Font timeFont = new Font("Arial", Font.BOLD, 12);
        g.setFont(timeFont);
        FontMetrics timeFm = g.getFontMetrics(timeFont);

        int pillW = timeFm.stringWidth(timeText) + 20;
        int pillH = 22;
        int pillX = x + (w - pillW) / 2;
        int pillY = y + 102;

        // Pill background
        g.setColor(new Color(28, 22, 36));
        g.fillRoundRect(pillX, pillY, pillW, pillH, 12, 12);

        // Pill border
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(pillX, pillY, pillW, pillH, 12, 12);

        // Pill text
        g.setColor(new Color(255, 245, 220));
        g.drawString(timeText, pillX + 10, pillY + 15);

        // 8. Description Text
        String desc = card.getDifficulty().getDescription();
        Font descFont = new Font("Arial", Font.PLAIN, 12);
        g.setFont(descFont);
        FontMetrics dfm = g.getFontMetrics(descFont);
        int dx = x + (w - dfm.stringWidth(desc)) / 2;
        int dy = y + 148;

        g.setColor(new Color(75, 55, 38));
        g.drawString(desc, dx, dy);

        // 9. Selected Checkmark Indicator (Top right corner of card)
        if (isSelected) {
            int checkCX = x + w - 18;
            int checkCY = y + 18;
            int checkR = 11;

            // Check badge background
            g.setColor(new Color(255, 215, 64));
            g.fillOval(checkCX - checkR, checkCY - checkR, checkR * 2, checkR * 2);
            g.setColor(new Color(140, 95, 20));
            g.setStroke(new BasicStroke(1.4f));
            g.drawOval(checkCX - checkR, checkCY - checkR, checkR * 2, checkR * 2);

            // Checkmark tick symbol
            g.setColor(new Color(24, 18, 12));
            g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(checkCX - 5, checkCY, checkCX - 1, checkCY + 4);
            g.drawLine(checkCX - 1, checkCY + 4, checkCX + 5, checkCY - 4);
        }
    }

    private void drawDifficultyIconBadge(Graphics2D g, Difficulty diff, int cx, int cy, boolean isSelected, boolean isHovered) {
        int r = 22;

        // Badge shadow
        g.setColor(new Color(120, 95, 60, 50));
        g.fillOval(cx - r + 1, cy - r + 2, r * 2, r * 2);

        // Badge background (dark leather/bronze gradient)
        LinearGradientPaint bg = new LinearGradientPaint(
            cx, cy - r, cx, cy + r,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(58, 38, 20), new Color(32, 18, 10)}
        );
        g.setPaint(bg);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Badge golden rim
        g.setColor(isSelected ? new Color(255, 225, 80) : new Color(215, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);

        // Procedural 2D Icons
        Graphics2D ig = (Graphics2D) g.create();
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color goldLight = isHovered || isSelected ? new Color(255, 245, 150) : new Color(255, 235, 120);
        Color goldMid = new Color(230, 180, 45);
        Color goldDark = new Color(160, 115, 25);

        switch (diff) {
            case EASY -> {
                // Glowing Candle Icon
                ig.setColor(new Color(235, 225, 205));
                ig.fillRoundRect(cx - 4, cy - 2, 8, 14, 2, 2);
                ig.setColor(new Color(50, 40, 30));
                ig.drawLine(cx, cy - 2, cx, cy - 6);

                // Flame
                ig.setColor(new Color(255, 205, 50));
                GeneralPath f = new GeneralPath();
                f.moveTo(cx - 3, cy - 6);
                f.curveTo(cx - 4, cy - 11, cx, cy - 15, cx, cy - 15);
                f.curveTo(cx, cy - 15, cx + 4, cy - 11, cx + 3, cy - 6);
                f.closePath();
                ig.fill(f);
                ig.setColor(new Color(255, 255, 220));
                ig.fillOval(cx - 1, cy - 10, 2, 4);
            }
            case MEDIUM -> {
                // Balanced Scales of Justice Icon
                ig.setColor(goldLight);
                ig.setStroke(new BasicStroke(1.8f));
                ig.drawLine(cx, cy - 12, cx, cy + 10); // pillar
                ig.drawLine(cx - 11, cy - 8, cx + 11, cy - 8); // beam

                // Left & Right scale pans
                ig.drawLine(cx - 11, cy - 8, cx - 15, cy + 2);
                ig.drawLine(cx - 11, cy - 8, cx - 7, cy + 2);
                ig.drawLine(cx - 16, cy + 2, cx - 6, cy + 2);

                ig.drawLine(cx + 11, cy - 8, cx + 7, cy + 2);
                ig.drawLine(cx + 11, cy - 8, cx + 15, cy + 2);
                ig.drawLine(cx + 6, cy + 2, cx + 16, cy + 2);

                // Base
                ig.drawLine(cx - 6, cy + 10, cx + 6, cy + 10);
            }
            case HARD -> {
                // Dark Gothic Padlock Icon
                ig.setColor(goldLight);
                ig.setStroke(new BasicStroke(2.2f));
                ig.drawArc(cx - 6, cy - 13, 12, 14, 0, 180); // shackle

                // Lock body
                ig.setColor(new Color(195, 45, 45));
                ig.fillRoundRect(cx - 9, cy - 4, 18, 15, 4, 4);
                ig.setColor(goldMid);
                ig.setStroke(new BasicStroke(1.5f));
                ig.drawRoundRect(cx - 9, cy - 4, 18, 15, 4, 4);

                // Keyhole
                ig.setColor(new Color(255, 225, 80));
                ig.fillOval(cx - 2, cy, 4, 4);
                ig.drawLine(cx, cy + 2, cx, cy + 7);
            }
        }
        ig.dispose();
    }

    // =========================================================================
    // LAYER 8: BUTTON RENDERING
    // =========================================================================
    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.getBounds();
        int x = r.x;
        int y = r.y + (btn.isPressed() ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 26;

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

        // 2. Drop Shadow
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
        Font font = new Font("Arial", Font.BOLD, 18);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.getText())) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Text drop shadow
        g.setColor(new Color(4, 5, 12, 210));
        g.drawString(btn.getText(), textX + 1, textY + 2);

        // Text white/cream
        g.setColor(Color.WHITE);
        g.drawString(btn.getText(), textX, textY);
    }

    // =========================================================================
    // LAYER 9: FLOATING DUST PARTICLES
    // =========================================================================
    private void drawDustParticles(Graphics2D g) {
        g.setColor(new Color(255, 235, 170, 75));
        for (DustParticle p : dustParticles) {
            g.fillOval((int) p.x, (int) p.y, p.size, p.size);
        }
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public String getEnteredName() {
        return nameField.getText().trim();
    }

    public void setEnteredName(String name) {
        nameField.setText(name);
    }

    // =========================================================================
    // INNER CLASSES: DifficultyCard, DustParticle, MenuButton
    // =========================================================================
    private static class DifficultyCard {
        private final Difficulty difficulty;
        private final Rectangle bounds;
        private final Color accentColor;
        private final Color accentDark;
        private boolean hovered;

        public DifficultyCard(Difficulty difficulty, Rectangle bounds, Color accentColor, Color accentDark) {
            this.difficulty = difficulty;
            this.bounds = bounds;
            this.accentColor = accentColor;
            this.accentDark = accentDark;
        }

        public boolean contains(Point p) {
            return bounds.contains(p);
        }

        public Difficulty getDifficulty() { return difficulty; }
        public Rectangle getBounds() { return bounds; }
        public Color getAccentColor() { return accentColor; }
        public Color getAccentDark() { return accentDark; }

        public boolean isHovered() { return hovered; }
        public void setHovered(boolean hovered) { this.hovered = hovered; }
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
