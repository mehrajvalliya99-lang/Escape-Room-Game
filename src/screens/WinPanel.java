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
 * Screen 7: Win Screen for "Escape the Room – Mystery Puzzle Adventure".
 *
 * Appears when the player successfully escapes the mansion. Features a celebratory
 * haunted mansion night sky, glowing celebration particles, a large illustrated
 * golden trophy, a results panel with player statistics, and polished PLAY AGAIN
 * and EXIT buttons.
 *
 * // TODO: Member 2 - scoring
 * // TODO: Member 2 - timer
 * // TODO: Member 2 - final escape condition
 * // TODO: Member 2 - trigger WinPanel after final escape
 */
public class WinPanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;

    // Viewport scaling and letterboxing
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Action buttons
    private final MenuButton playAgainButton;
    private final MenuButton mainMenuButton;
    private final MenuButton exitButton;

    // Results state (updated by GameController)
    private int finalScore = 850;
    private String timeTaken = "03:12";
    private String roomsCompleted = "4/4";
    private int puzzlesSolved = 4;
    private int hintsUsed = 0;

    // Ambient celebration confetti & sparkles
    private Timer celebrationTimer;
    private float animPhase = 0.0f;
    private final List<ConfettiParticle> confetti;

    public WinPanel(GameController controller) {
        this.controller = controller;
        this.confetti = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 12, 24));
        setDoubleBuffered(true);
        setFocusable(true);

        // 1. PLAY AGAIN BUTTON
        playAgainButton = new MenuButton(
            "PLAY AGAIN",
            new Rectangle(320, 615, 190, 52),
            new Color(38, 125, 75),
            new Color(18, 70, 42),
            new Color(52, 160, 98),
            new Color(26, 96, 58),
            new Color(12, 48, 28),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.PLAYER_DETAILS)
        );

        // 2. MAIN MENU BUTTON
        mainMenuButton = new MenuButton(
            "MAIN MENU",
            new Rectangle(545, 615, 190, 52),
            new Color(110, 72, 30),
            new Color(60, 38, 14),
            new Color(145, 95, 42),
            new Color(85, 54, 22),
            new Color(42, 24, 8),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.HOME)
        );

        // 3. EXIT BUTTON
        exitButton = new MenuButton(
            "EXIT",
            new Rectangle(770, 615, 190, 52),
            new Color(37, 72, 130),
            new Color(20, 40, 78),
            new Color(50, 95, 170),
            new Color(28, 56, 108),
            new Color(14, 28, 56),
            new Color(255, 215, 64),
            () -> System.exit(0)
        );

        initConfetti();
        initMouseListeners();
        initTimer();
    }

    private void initConfetti() {
        confetti.clear();
        Color[] colors = {
            new Color(255, 215, 64), // Gold
            new Color(255, 95, 110), // Coral red
            new Color(90, 200, 255), // Cyan
            new Color(180, 110, 255),// Purple
            new Color(120, 255, 150),// Emerald
            new Color(255, 245, 190) // Cream
        };

        for (int i = 0; i < 45; i++) {
            float x = (float) (Math.random() * LOGICAL_WIDTH);
            float y = (float) (Math.random() * LOGICAL_HEIGHT);
            Color c = colors[i % colors.length];
            confetti.add(new ConfettiParticle(x, y, c));
        }
    }

    private void initTimer() {
        celebrationTimer = new Timer(40, e -> {
            animPhase += 0.08f;
            if (animPhase > 1000.0f) animPhase = 0.0f;

            for (ConfettiParticle p : confetti) {
                p.update();
            }

            repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (celebrationTimer != null && !celebrationTimer.isRunning()) {
            celebrationTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (celebrationTimer != null && celebrationTimer.isRunning()) {
            celebrationTimer.stop();
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

                boolean hPlay = playAgainButton.contains(lp);
                playAgainButton.hovered = hPlay;
                if (hPlay) anyHover = true;

                boolean hMenu = mainMenuButton.contains(lp);
                mainMenuButton.hovered = hMenu;
                if (hMenu) anyHover = true;

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

                if (playAgainButton.contains(lp)) playAgainButton.pressed = true;
                if (mainMenuButton.contains(lp)) mainMenuButton.pressed = true;
                if (exitButton.contains(lp)) exitButton.pressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (playAgainButton.pressed) {
                    playAgainButton.pressed = false;
                    if (playAgainButton.contains(lp)) playAgainButton.trigger();
                }
                if (mainMenuButton.pressed) {
                    mainMenuButton.pressed = false;
                    if (mainMenuButton.contains(lp)) mainMenuButton.trigger();
                }
                if (exitButton.pressed) {
                    exitButton.pressed = false;
                    if (exitButton.contains(lp)) exitButton.trigger();
                }
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                playAgainButton.hovered = false; playAgainButton.pressed = false;
                mainMenuButton.hovered = false; mainMenuButton.pressed = false;
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

        // 1. Fill letterbox margin
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

        // 3. Victory Night Sky & Mansion Silhouette
        drawVictoryBackground(g2d);

        // 4. Confetti & Celebration Sparkles
        drawConfetti(g2d);

        // 5. Golden Trophy with Radiant Glow
        drawGoldenTrophy(g2d, 640, 160);

        // 6. Victory Ribbon & Headings
        drawVictoryRibbonAndHeadings(g2d, 640, 275);

        // 7. Results Panel
        drawResultsPanel(g2d, 640, 450);

        // 8. Action Buttons
        drawButton(g2d, playAgainButton);
        drawButton(g2d, mainMenuButton);
        drawButton(g2d, exitButton);

        g2d.dispose();
    }

    /**
     * Draws the rich night sky, full moon, and distant mansion silhouette with victory lighting.
     */
    private void drawVictoryBackground(Graphics2D g) {
        // Deep midnight blue/purple sky
        LinearGradientPaint sky = new LinearGradientPaint(
            0, 0, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(12, 16, 38), new Color(20, 24, 56), new Color(10, 12, 28)}
        );
        g.setPaint(sky);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Radiant moon in upper background
        RadialGradientPaint moonHalo = new RadialGradientPaint(
            640, 140, 220,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(255, 235, 160, 90), new Color(180, 205, 255, 30), new Color(0, 0, 0, 0)}
        );
        g.setPaint(moonHalo);
        g.fillOval(640 - 220, 140 - 220, 440, 440);

        // Mansion roofline silhouette at bottom
        g.setColor(new Color(6, 8, 18));
        GeneralPath silhouette = new GeneralPath();
        silhouette.moveTo(0, 720);
        silhouette.lineTo(0, 600);
        silhouette.lineTo(140, 560);
        silhouette.lineTo(210, 520);
        silhouette.lineTo(270, 560);
        silhouette.lineTo(380, 580);
        silhouette.lineTo(480, 530);
        silhouette.lineTo(540, 500);
        silhouette.lineTo(600, 530);
        silhouette.lineTo(640, 510);
        silhouette.lineTo(680, 530);
        silhouette.lineTo(740, 500);
        silhouette.lineTo(800, 530);
        silhouette.lineTo(910, 580);
        silhouette.lineTo(1010, 560);
        silhouette.lineTo(1070, 520);
        silhouette.lineTo(1140, 560);
        silhouette.lineTo(1280, 600);
        silhouette.lineTo(1280, 720);
        silhouette.closePath();
        g.fill(silhouette);

        // Ground hill gradient
        LinearGradientPaint ground = new LinearGradientPaint(
            0, 600, 0, 720,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(12, 16, 28, 200), new Color(6, 8, 16)}
        );
        g.setPaint(ground);
        g.fillRect(0, 600, LOGICAL_WIDTH, 120);

        // Screen border gold trim
        g.setColor(new Color(212, 175, 55, 140));
        g.setStroke(new BasicStroke(3.0f));
        g.drawRect(8, 8, LOGICAL_WIDTH - 16, LOGICAL_HEIGHT - 16);
        g.setStroke(new BasicStroke(1.2f));
        g.drawRect(14, 14, LOGICAL_WIDTH - 28, LOGICAL_HEIGHT - 28);
    }

    private void drawConfetti(Graphics2D g) {
        for (ConfettiParticle p : confetti) {
            g.setColor(p.color);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(p.x, p.y);
            g2.rotate(Math.toRadians(p.angle));
            g2.fillRect(-p.size / 2, -p.size / 4, p.size, p.size / 2);
            g2.dispose();
        }
    }

    /**
     * Draws the large detailed 2D golden trophy with laurel wreath, handles, and radiant aura.
     */
    private void drawGoldenTrophy(Graphics2D g, int cx, int cy) {
        // Radiant aura
        RadialGradientPaint aura = new RadialGradientPaint(
            cx, cy, 110,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 230, 110, 120), new Color(255, 190, 40, 35), new Color(0, 0, 0, 0)}
        );
        g.setPaint(aura);
        g.fillOval(cx - 110, cy - 110, 220, 220);

        // Trophy drop shadow
        g.setColor(new Color(4, 5, 12, 140));
        g.fillOval(cx - 36, cy + 50, 72, 14);

        // Laurel wreath leaves (surrounding trophy)
        drawLaurelWreath(g, cx, cy);

        // Trophy base pedestal
        LinearGradientPaint baseGrad = new LinearGradientPaint(
            cx - 38, cy + 30, cx + 38, cy + 52,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(180, 135, 40), new Color(255, 225, 110), new Color(130, 90, 25)}
        );
        g.setPaint(baseGrad);
        g.fillRoundRect(cx - 38, cy + 38, 76, 14, 4, 4);
        g.fillRoundRect(cx - 26, cy + 26, 52, 14, 4, 4);

        // Trophy stem
        LinearGradientPaint stemGrad = new LinearGradientPaint(
            cx - 10, cy + 10, cx + 10, cy + 26,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 225, 110), new Color(212, 175, 55), new Color(140, 100, 25)}
        );
        g.setPaint(stemGrad);
        g.fillRect(cx - 8, cy + 8, 16, 20);

        // Trophy handles (left and right curved loops)
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(4.5f));
        g.drawArc(cx - 56, cy - 38, 30, 42, 90, 180);
        g.drawArc(cx + 26, cy - 38, 30, 42, 270, 180);

        // Trophy cup body (semi-ellipse & top rim)
        GeneralPath cup = new GeneralPath();
        cup.moveTo(cx - 40, cy - 38);
        cup.lineTo(cx + 40, cy - 38);
        cup.curveTo(cx + 40, cy, cx + 22, cy + 12, cx + 10, cy + 12);
        cup.lineTo(cx - 10, cy + 12);
        cup.curveTo(cx - 22, cy + 12, cx - 40, cy, cx - 40, cy - 38);
        cup.closePath();

        LinearGradientPaint cupGrad = new LinearGradientPaint(
            cx - 40, cy - 38, cx + 40, cy + 12,
            new float[]{0.0f, 0.35f, 0.7f, 1.0f},
            new Color[]{new Color(255, 238, 140), new Color(255, 215, 64), new Color(205, 155, 35), new Color(140, 95, 20)}
        );
        g.setPaint(cupGrad);
        g.fill(cup);

        // Cup top rim
        g.setPaint(baseGrad);
        g.fillOval(cx - 42, cy - 43, 84, 12);
        g.setColor(new Color(110, 75, 20));
        g.setStroke(new BasicStroke(1.2f));
        g.drawOval(cx - 42, cy - 43, 84, 12);

        // Specular cup highlight
        g.setColor(new Color(255, 255, 255, 110));
        g.fillOval(cx - 28, cy - 34, 14, 28);

        // Star on center of cup
        drawDiamond(g, cx, cy - 14, 8);
    }

    private void drawLaurelWreath(Graphics2D g, int cx, int cy) {
        g.setColor(new Color(255, 215, 64, 190));
        for (int i = 0; i < 7; i++) {
            double angleL = Math.toRadians(140 + i * 16);
            int lx = (int) (cx + Math.cos(angleL) * 65);
            int ly = (int) (cy - 6 + Math.sin(angleL) * 55);
            g.fillOval(lx - 4, ly - 7, 8, 14);

            double angleR = Math.toRadians(40 - i * 16);
            int rx = (int) (cx + Math.cos(angleR) * 65);
            int ry = (int) (cy - 6 + Math.sin(angleR) * 55);
            g.fillOval(rx - 4, ry - 7, 8, 14);
        }
    }

    /**
     * Draws the gold ribbon banner with "YOU ESCAPED!" and victory subtitle.
     */
    private void drawVictoryRibbonAndHeadings(Graphics2D g, int cx, int cy) {
        int rw = 480;
        int rh = 56;
        int rx = cx - rw / 2;
        int ry = cy;

        // Ribbon tail shadows
        g.setColor(new Color(120, 80, 20));
        GeneralPath leftTail = new GeneralPath();
        leftTail.moveTo(rx - 20, ry + 16);
        leftTail.lineTo(rx + 10, ry + 16);
        leftTail.lineTo(rx + 10, ry + rh + 10);
        leftTail.lineTo(rx - 30, ry + rh + 10);
        leftTail.lineTo(rx - 15, ry + rh / 2 + 13);
        leftTail.closePath();
        g.fill(leftTail);

        GeneralPath rightTail = new GeneralPath();
        rightTail.moveTo(rx + rw + 20, ry + 16);
        rightTail.lineTo(rx + rw - 10, ry + 16);
        rightTail.lineTo(rx + rw - 10, ry + rh + 10);
        rightTail.lineTo(rx + rw + 30, ry + rh + 10);
        rightTail.lineTo(rx + rw + 15, ry + rh / 2 + 13);
        rightTail.closePath();
        g.fill(rightTail);

        // Ribbon main body drop shadow
        g.setColor(new Color(4, 5, 12, 180));
        g.fillRoundRect(rx + 2, ry + 4, rw, rh, 14, 14);

        // Ribbon golden gradient
        LinearGradientPaint ribGrad = new LinearGradientPaint(
            rx, ry, rx, ry + rh,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(255, 238, 140), new Color(255, 210, 60), new Color(205, 150, 30)}
        );
        g.setPaint(ribGrad);
        g.fillRoundRect(rx, ry, rw, rh, 14, 14);

        // Ribbon golden border
        g.setColor(new Color(150, 105, 25));
        g.setStroke(new BasicStroke(2.2f));
        g.drawRoundRect(rx, ry, rw, rh, 14, 14);

        // Ribbon inner line
        g.setColor(new Color(255, 250, 200, 180));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(rx + 3, ry + 3, rw - 6, rh - 6, 12, 12);

        // Heading: YOU ESCAPED!
        Font titleFont = new Font("Georgia", Font.BOLD, 36);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        String title = "YOU ESCAPED!";
        int tx = cx - tfm.stringWidth(title) / 2;
        int ty = ry + (rh - tfm.getHeight()) / 2 + tfm.getAscent();

        // Text drop shadow
        g.setColor(new Color(60, 35, 10));
        g.drawString(title, tx + 1, ty + 2);

        g.setColor(new Color(36, 18, 6));
        g.drawString(title, tx, ty);

        // Subtitle: Congratulations, Detective! The Last Experiment mystery is solved.
        Font subFont = new Font("Georgia", Font.ITALIC, 17);
        g.setFont(subFont);
        FontMetrics sfm = g.getFontMetrics(subFont);
        String sub = "Congratulations, Detective! The Last Experiment mystery is solved.";
        int sx = cx - sfm.stringWidth(sub) / 2;
        int sy = ry + rh + 28;

        g.setColor(new Color(255, 245, 220));
        g.drawString(sub, sx, sy);
    }

    /**
     * Draws the results card showing player name, final score, time taken, rooms completed, puzzles solved, and hints used.
     */
    private void drawResultsPanel(Graphics2D g, int cx, int cy) {
        int pw = 680;
        int ph = 150;
        int px = cx - pw / 2;
        int py = cy;
        int arc = 18;

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 190));
        g.fillRoundRect(px + 2, py + 4, pw, ph, arc, arc);

        // Background (rich dark parchment/leather)
        LinearGradientPaint pbg = new LinearGradientPaint(
            px, py, px, py + ph,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(28, 20, 26), new Color(20, 14, 18), new Color(14, 10, 14)}
        );
        g.setPaint(pbg);
        g.fillRoundRect(px, py, pw, ph, arc, arc);

        // Golden frame
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(px, py, pw, ph, arc, arc);

        // Inner pinstripe
        g.setColor(new Color(160, 125, 45, 130));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(px + 4, py + 4, pw - 8, ph - 8, arc - 4, arc - 4);

        // Player Name & Header
        Player p = (controller != null) ? controller.getPlayer() : null;
        String playerName = (p != null && !p.getName().isEmpty()) ? p.getName() : "Detective";

        Font hFont = new Font("Georgia", Font.BOLD, 17);
        g.setFont(hFont);
        g.setColor(new Color(255, 220, 95));
        g.drawString("MISSION DOSSIER \u2014 " + playerName.toUpperCase(), px + 28, py + 32);

        // Divider
        g.setColor(new Color(180, 140, 45, 140));
        g.drawLine(px + 28, py + 42, px + pw - 28, py + 42);

        // 5-Column Stats Layout
        int colW = (pw - 40) / 5;
        // Column 1: Final Score
        drawStatColumn(g, px + 24, py + 62, "FINAL SCORE", String.valueOf(finalScore), new Color(255, 235, 140));

        // Column 2: Time Taken
        drawStatColumn(g, px + 24 + colW, py + 62, "TIME TAKEN", timeTaken, new Color(255, 245, 220));

        // Column 3: Rooms Completed
        drawStatColumn(g, px + 24 + colW * 2, py + 62, "ROOMS CLEARED", roomsCompleted, new Color(130, 230, 160));

        // Column 4: Puzzles Solved
        drawStatColumn(g, px + 24 + colW * 3, py + 62, "PUZZLES SOLVED", String.valueOf(puzzlesSolved), new Color(120, 220, 255));

        // Column 5: Hints Used
        drawStatColumn(g, px + 24 + colW * 4, py + 62, "HINTS USED", String.valueOf(hintsUsed), new Color(255, 180, 140));
    }

    private void drawStatColumn(Graphics2D g, int x, int y, String label, String value, Color valColor) {
        Font lFont = new Font("Arial", Font.BOLD, 11);
        g.setFont(lFont);
        g.setColor(new Color(195, 180, 155));
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
    // GETTERS & DATA SETTERS FOR MEMBER 2
    // =========================================================================

    public void setGameResults(int score, String time, String rooms) {
        setGameResults(score, time, rooms, 4, 0);
    }

    public void setGameResults(int score, String time, String rooms, int puzzles, int hints) {
        this.finalScore = score;
        this.timeTaken = time;
        this.roomsCompleted = rooms;
        this.puzzlesSolved = puzzles;
        this.hintsUsed = hints;
        repaint();
    }

    public int getFinalScore() { return finalScore; }
    public String getTimeTaken() { return timeTaken; }
    public String getRoomsCompleted() { return roomsCompleted; }
    public int getPuzzlesSolved() { return puzzlesSolved; }
    public int getHintsUsed() { return hintsUsed; }

    public MenuButton getPlayAgainButton() { return playAgainButton; }
    public MenuButton getMainMenuButton() { return mainMenuButton; }
    public MenuButton getExitButton() { return exitButton; }

    // =========================================================================
    // INNER CLASSES: ConfettiParticle & MenuButton
    // =========================================================================

    private static class ConfettiParticle {
        float x, y;
        int size;
        float speedY;
        float driftX;
        float angle;
        float rotSpeed;
        Color color;

        public ConfettiParticle(float x, float y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.size = 6 + (int) (Math.random() * 8);
            this.speedY = 1.0f + (float) Math.random() * 2.2f;
            this.driftX = (float) (Math.random() - 0.5) * 1.5f;
            this.angle = (float) (Math.random() * 360);
            this.rotSpeed = (float) (Math.random() - 0.5) * 6.0f;
        }

        public void update() {
            y += speedY;
            x += driftX;
            angle += rotSpeed;
            if (y > 720) {
                y = -10;
                x = (float) (Math.random() * LOGICAL_WIDTH);
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
    }
}
