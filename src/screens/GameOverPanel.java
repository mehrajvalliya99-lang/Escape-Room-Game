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
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Screen 8: Game Over Screen for "Escape the Room – Mystery Puzzle Adventure".
 *
 * Appears when the timer expires or the investigation fails. Features an ominous
 * dark mansion atmosphere with moon partially shrouded by drifting clouds, a large
 * detailed 2D antique hourglass with trickling golden sand, results dossier panel,
 * and matching TRY AGAIN and EXIT buttons.
 *
 * // TODO: Member 2 - scoring
 * // TODO: Member 2 - timer
 * // TODO: Member 2 - trigger GameOverPanel when timer expires
 */
public class GameOverPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;

    // Viewport scaling and letterboxing
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Action buttons
    private final MenuButton tryAgainButton;
    private final MenuButton exitButton;

    // Placeholder results (for Member 2 to hook into)
    private int finalScore = 320;
    private String roomsCompleted = "1/4";

    // Atmospheric animation (drifting clouds & sand flow flicker)
    private Timer ambientTimer;
    private float cloudPhase = 0.0f;
    private float sandPhase = 0.0f;

    public GameOverPanel(GameController controller) {
        this.controller = controller;

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(8, 6, 14));
        setDoubleBuffered(true);
        setFocusable(true);

        // 1. TRY AGAIN BUTTON
        tryAgainButton = new MenuButton(
            "TRY AGAIN",
            new Rectangle(430, 615, 200, 52),
            new Color(38, 125, 75),
            new Color(18, 70, 42),
            new Color(52, 160, 98),
            new Color(26, 96, 58),
            new Color(12, 48, 28),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.PLAYER_DETAILS)
        );

        // 2. EXIT BUTTON
        exitButton = new MenuButton(
            "EXIT",
            new Rectangle(650, 615, 200, 52),
            new Color(125, 30, 42),
            new Color(68, 14, 22),
            new Color(160, 42, 58),
            new Color(95, 20, 32),
            new Color(50, 10, 16),
            new Color(255, 215, 64),
            () -> System.exit(0)
        );

        initMouseListeners();
        initTimer();
    }

    private void initTimer() {
        ambientTimer = new Timer(50, e -> {
            cloudPhase += 0.35f;
            if (cloudPhase > 1280.0f) cloudPhase = 0.0f;

            sandPhase += 0.2f;
            if (sandPhase > 1000.0f) sandPhase = 0.0f;

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
    // MOUSE LISTENER & VIEWPORT COORDINATE SCALING
    // =========================================================================

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                boolean anyHover = false;

                boolean hTry = tryAgainButton.contains(lp);
                tryAgainButton.hovered = hTry;
                if (hTry) anyHover = true;

                boolean hExit = exitButton.contains(lp);
                exitButton.hovered = hExit;
                if (hExit) anyHover = true;

                setCursor(anyHover ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (tryAgainButton.contains(lp)) tryAgainButton.pressed = true;
                if (exitButton.contains(lp)) exitButton.pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (tryAgainButton.pressed) {
                    tryAgainButton.pressed = false;
                    if (tryAgainButton.contains(lp)) tryAgainButton.trigger();
                }
                if (exitButton.pressed) {
                    exitButton.pressed = false;
                    if (exitButton.contains(lp)) exitButton.trigger();
                }
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tryAgainButton.hovered = false; tryAgainButton.pressed = false;
                exitButton.hovered = false; exitButton.pressed = false;
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

        // 1. Fill letterbox void
        g2d.setColor(new Color(6, 7, 14));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 2. Viewport transformation to logical 1280x720
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 3. Dark mansion night atmosphere with shrouded moon & clouds
        drawGameOverBackground(g2d);

        // 4. Large 2D Illustrated Antique Hourglass
        drawHourglassIllustration(g2d, 640, 160);

        // 5. Headings: TIME'S UP! and Subtitle
        drawHeadings(g2d, 640, 275);

        // 6. Results Dossier Panel
        drawResultsPanel(g2d, 640, 450);

        // 7. Action Buttons
        drawButton(g2d, tryAgainButton);
        drawButton(g2d, exitButton);

        g2d.dispose();
    }

    /**
     * Draws the dark mansion atmosphere with moon behind clouds and spooky silhouettes.
     */
    private void drawGameOverBackground(Graphics2D g) {
        // Deep ominous dark navy/purple gradient sky
        LinearGradientPaint sky = new LinearGradientPaint(
            0, 0, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(10, 8, 20), new Color(18, 12, 28), new Color(8, 6, 14)}
        );
        g.setPaint(sky);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Shrouded cold moon in upper center
        RadialGradientPaint moonGlow = new RadialGradientPaint(
            640, 130, 160,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(210, 220, 255, 70), new Color(120, 110, 180, 25), new Color(0, 0, 0, 0)}
        );
        g.setPaint(moonGlow);
        g.fillOval(640 - 160, 130 - 160, 320, 320);

        // Moon disc
        g.setColor(new Color(220, 228, 255, 140));
        g.fillOval(640 - 36, 130 - 36, 72, 72);

        // Drifting clouds across moon
        drawClouds(g);

        // Dark mansion and iron cemetery fence silhouette at bottom
        g.setColor(new Color(6, 4, 10));
        GeneralPath silhouette = new GeneralPath();
        silhouette.moveTo(0, 720);
        silhouette.lineTo(0, 610);
        silhouette.lineTo(80, 580);
        silhouette.lineTo(150, 540);
        silhouette.lineTo(240, 600);
        silhouette.lineTo(380, 560);
        silhouette.lineTo(440, 510);
        silhouette.lineTo(500, 560);
        silhouette.lineTo(640, 575);
        silhouette.lineTo(780, 560);
        silhouette.lineTo(840, 510);
        silhouette.lineTo(900, 560);
        silhouette.lineTo(1040, 600);
        silhouette.lineTo(1130, 540);
        silhouette.lineTo(1200, 580);
        silhouette.lineTo(1280, 610);
        silhouette.lineTo(1280, 720);
        silhouette.closePath();
        g.fill(silhouette);

        // Iron fence pickets
        g.setColor(new Color(14, 10, 22));
        g.setStroke(new BasicStroke(2.0f));
        for (int x = 40; x < LOGICAL_WIDTH; x += 35) {
            g.drawLine(x, 580, x, 630);
            g.drawLine(x - 3, 585, x, 580);
            g.drawLine(x + 3, 585, x, 580);
        }

        // Screen border dark gold rim
        g.setColor(new Color(160, 115, 45, 120));
        g.setStroke(new BasicStroke(3.0f));
        g.drawRect(8, 8, LOGICAL_WIDTH - 16, LOGICAL_HEIGHT - 16);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(14, 14, LOGICAL_WIDTH - 28, LOGICAL_HEIGHT - 28);
    }

    private void drawClouds(Graphics2D g) {
        g.setColor(new Color(22, 16, 32, 110));
        int cx1 = (int) (cloudPhase - 200);
        int cx2 = (int) (cloudPhase + 350);

        g.fillOval(cx1 % 1400 - 100, 110, 240, 55);
        g.fillOval((cx1 + 80) % 1400 - 100, 95, 180, 50);

        g.setColor(new Color(16, 12, 26, 90));
        g.fillOval(cx2 % 1400 - 100, 130, 260, 60);
        g.fillOval((cx2 + 100) % 1400 - 100, 115, 200, 55);
    }

    /**
     * Draws the large detailed 2D antique hourglass with trickling golden sand.
     */
    private void drawHourglassIllustration(Graphics2D g, int cx, int cy) {
        int hw = 68;
        int hh = 94;
        int topY = cy - hh / 2;
        int botY = cy + hh / 2;

        // Hourglass drop shadow
        g.setColor(new Color(4, 3, 8, 160));
        g.fillOval(cx - 38, botY + 2, 76, 14);

        // Top and bottom carved wooden discs
        LinearGradientPaint woodGrad = new LinearGradientPaint(
            cx - hw / 2, topY, cx + hw / 2, topY + 12,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(110, 65, 30), new Color(165, 105, 50), new Color(80, 45, 20)}
        );
        g.setPaint(woodGrad);
        g.fillRoundRect(cx - hw / 2, topY, hw, 12, 6, 6);
        g.fillRoundRect(cx - hw / 2, botY - 12, hw, 12, 6, 6);

        // Gold rim around wooden discs
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(cx - hw / 2, topY, hw, 12, 6, 6);
        g.drawRoundRect(cx - hw / 2, botY - 12, hw, 12, 6, 6);

        // Brass pillars (left, middle, right)
        drawBrassPillar(g, cx - hw / 2 + 5, topY + 12, botY - 12);
        drawBrassPillar(g, cx + hw / 2 - 8, topY + 12, botY - 12);

        // Glass bulbs (upper and lower curved bulbs connected by narrow neck)
        GeneralPath glass = new GeneralPath();
        glass.moveTo(cx - 24, topY + 12);
        glass.lineTo(cx + 24, topY + 12);
        glass.curveTo(cx + 24, cy - 14, cx + 5, cy - 6, cx + 4, cy);
        glass.curveTo(cx + 5, cy + 6, cx + 24, cy + 14, cx + 24, botY - 12);
        glass.lineTo(cx - 24, botY - 12);
        glass.curveTo(cx - 24, cy + 14, cx - 5, cy + 6, cx - 4, cy);
        glass.curveTo(cx - 5, cy - 6, cx - 24, cy - 14, cx - 24, topY + 12);
        glass.closePath();

        // Transparent glass reflection fill
        g.setColor(new Color(180, 210, 255, 30));
        g.fill(glass);
        g.setColor(new Color(210, 230, 255, 110));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(glass);

        // Top bulb: Nearly empty of sand (just a small sliver)
        GeneralPath topSand = new GeneralPath();
        topSand.moveTo(cx - 14, cy - 8);
        topSand.lineTo(cx + 14, cy - 8);
        topSand.curveTo(cx + 10, cy - 2, cx + 2, cy, cx, cy);
        topSand.curveTo(cx - 2, cy, cx - 10, cy - 2, cx - 14, cy - 8);
        topSand.closePath();
        g.setColor(new Color(230, 185, 75));
        g.fill(topSand);

        // Trickling stream of sand falling through neck
        g.setColor(new Color(255, 220, 95));
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(cx, cy, cx, cy + 24);

        // Bottom bulb: Sand pile filling up
        GeneralPath botSand = new GeneralPath();
        botSand.moveTo(cx - 22, botY - 12);
        botSand.lineTo(cx + 22, botY - 12);
        botSand.curveTo(cx + 22, cy + 26, cx + 14, cy + 16, cx, cy + 16);
        botSand.curveTo(cx - 14, cy + 16, cx - 22, cy + 26, cx - 22, botY - 12);
        botSand.closePath();

        LinearGradientPaint sandGrad = new LinearGradientPaint(
            cx, cy + 16, cx, botY - 12,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 225, 110), new Color(195, 140, 45)}
        );
        g.setPaint(sandGrad);
        g.fill(botSand);

        // Glass highlight streaks
        g.setColor(new Color(255, 255, 255, 80));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(cx - 18, topY + 16, cx - 8, cy - 10);
        g.drawLine(cx - 18, botY - 16, cx - 8, cy + 10);
    }

    private void drawBrassPillar(Graphics2D g, int x, int topY, int botY) {
        LinearGradientPaint brass = new LinearGradientPaint(
            x, topY, x + 4, topY,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(212, 175, 55), new Color(255, 235, 140), new Color(150, 110, 30)}
        );
        g.setPaint(brass);
        g.fillRect(x, topY, 4, botY - topY);

        g.setColor(new Color(110, 75, 20));
        g.setStroke(new BasicStroke(0.8f));
        g.drawRect(x, topY, 4, botY - topY);
    }

    /**
     * Draws headings: TIME'S UP! and "The mansion remains a mystery..."
     */
    private void drawHeadings(Graphics2D g, int cx, int cy) {
        // Main heading: TIME'S UP!
        Font titleFont = new Font("Georgia", Font.BOLD, 42);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        String title = "TIME'S UP!";
        int tx = cx - tfm.stringWidth(title) / 2;
        int ty = cy + 16;

        // Crimson shadow
        g.setColor(new Color(75, 10, 18, 230));
        g.drawString(title, tx + 2, ty + 3);

        // Fiery crimson/gold gradient
        LinearGradientPaint titleGrad = new LinearGradientPaint(
            tx, ty - 32, tx, ty,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(255, 110, 95), new Color(230, 45, 55), new Color(160, 20, 32)}
        );
        g.setPaint(titleGrad);
        g.drawString(title, tx, ty);

        // Subtitle: The mansion remains a mystery...
        Font subFont = new Font("Georgia", Font.ITALIC, 18);
        g.setFont(subFont);
        FontMetrics sfm = g.getFontMetrics(subFont);
        String sub = "The mansion remains a mystery...";
        int sx = cx - sfm.stringWidth(sub) / 2;
        int sy = ty + 34;

        g.setColor(new Color(230, 215, 240));
        g.drawString(sub, sx, sy);
    }

    /**
     * Draws results dossier panel showing player name, final score, and rooms completed.
     */
    private void drawResultsPanel(Graphics2D g, int cx, int cy) {
        int pw = 520;
        int ph = 145;
        int px = cx - pw / 2;
        int py = cy;
        int arc = 18;

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 190));
        g.fillRoundRect(px + 2, py + 4, pw, ph, arc, arc);

        // Background (dark stone/slate)
        LinearGradientPaint pbg = new LinearGradientPaint(
            px, py, px, py + ph,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(26, 18, 24), new Color(18, 12, 18), new Color(12, 8, 14)}
        );
        g.setPaint(pbg);
        g.fillRoundRect(px, py, pw, ph, arc, arc);

        // Bronze/gold border
        g.setColor(new Color(190, 135, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(px, py, pw, ph, arc, arc);

        // Inner pinstripe
        g.setColor(new Color(130, 95, 40, 120));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(px + 4, py + 4, pw - 8, ph - 8, arc - 4, arc - 4);

        // Header
        Player p = (controller != null) ? controller.getPlayer() : null;
        String playerName = (p != null && !p.getName().isEmpty()) ? p.getName() : "Detective";

        Font hFont = new Font("Georgia", Font.BOLD, 16);
        g.setFont(hFont);
        g.setColor(new Color(245, 190, 85));
        g.drawString("INVESTIGATION REPORT \u2014 " + playerName.toUpperCase(), px + 28, py + 30);

        // Divider
        g.setColor(new Color(160, 115, 45, 130));
        g.drawLine(px + 28, py + 40, px + pw - 28, py + 40);

        // 3-Column Stats Layout
        // Column 1: Final Score
        drawStatColumn(g, px + 50, py + 58, "SCORE ACCUMULATED", String.valueOf(finalScore), new Color(255, 215, 110));

        // Column 2: Rooms Completed
        drawStatColumn(g, px + 235, py + 58, "ROOMS CLEARED", roomsCompleted, new Color(240, 160, 120));

        // Column 3: Outcome
        drawStatColumn(g, px + 380, py + 58, "OUTCOME", "UNSOLVED", new Color(230, 80, 90));
    }

    private void drawStatColumn(Graphics2D g, int x, int y, String label, String value, Color valColor) {
        Font lFont = new Font("Arial", Font.BOLD, 10);
        g.setFont(lFont);
        g.setColor(new Color(185, 165, 150));
        g.drawString(label, x, y);

        Font vFont = new Font("Georgia", Font.BOLD, 22);
        g.setFont(vFont);
        g.setColor(valColor);
        g.drawString(value, x, y + 26);
    }

    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.bounds;
        int x = r.x;
        int y = r.y + (btn.pressed ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 20;

        if (btn.hovered) {
            RadialGradientPaint glow = new RadialGradientPaint(
                x + w / 2.0f, y + h / 2.0f, (float) (w * 0.7),
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                    new Color(255, 225, 75, 115),
                    new Color(255, 205, 50, 40),
                    new Color(255, 185, 20, 0)
                }
            );
            g.setPaint(glow);
            g.fillRoundRect(x - 6, y - 6, w + 12, h + 12, arc + 6, arc + 6);
        }

        g.setColor(new Color(4, 5, 12, 180));
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
        g.setStroke(new BasicStroke(2.2f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        Font font = new Font("Arial", Font.BOLD, 17);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.text)) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        g.setColor(new Color(4, 5, 12, 220));
        g.drawString(btn.text, textX + 1, textY + 2);

        g.setColor(Color.WHITE);
        g.drawString(btn.text, textX, textY);
    }

    // =========================================================================
    // GETTERS & SETTERS FOR MEMBER 2
    // =========================================================================

    public void setGameResults(int score, String rooms) {
        this.finalScore = score;
        this.roomsCompleted = rooms;
        repaint();
    }

    public int getFinalScore() { return finalScore; }
    public String getRoomsCompleted() { return roomsCompleted; }

    public MenuButton getTryAgainButton() { return tryAgainButton; }
    public MenuButton getExitButton() { return exitButton; }

    // =========================================================================
    // INNER CLASSES: MenuButton
    // =========================================================================

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
    }
}
