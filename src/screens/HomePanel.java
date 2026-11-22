import java.awt.BasicStroke;
import java.awt.Color;
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
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Polished 2D Home Screen for "Escape the Room – Mystery Puzzle Adventure".
 * Features an atmospheric haunted mansion scene at night with full moon,
 * gnarled trees with foliage, iron gates, glowing lanterns, lit mansion windows,
 * prominent golden title, decorative golden key, and custom interactive game buttons.
 */
public class HomePanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<MenuButton> buttons;

    // Viewport scaling and letterboxing metrics
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    public HomePanel(GameController controller) {
        this.controller = controller;
        this.buttons = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(8, 10, 20));
        setDoubleBuffered(true);
        setFocusable(true);

        initButtons();
        initMouseListeners();
    }

    private void initButtons() {
        int btnWidth = 300;
        int btnHeight = 52;
        int startX = (LOGICAL_WIDTH - btnWidth) / 2; // 490

        // 1. START GAME (Emerald/Green gradient with gold border)
        buttons.add(new MenuButton(
            "Start Game",
            new Rectangle(startX, 255, btnWidth, btnHeight),
            new Color(30, 150, 68),
            new Color(16, 92, 40),
            new Color(46, 196, 90),
            new Color(22, 122, 54),
            new Color(10, 62, 26),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.PLAYER_DETAILS)
        ));

        // 2. INSTRUCTIONS (Sapphire/Royal Blue gradient with gold border)
        buttons.add(new MenuButton(
            "Instructions",
            new Rectangle(startX, 324, btnWidth, btnHeight),
            new Color(34, 96, 222),
            new Color(18, 54, 146),
            new Color(58, 130, 252),
            new Color(26, 76, 190),
            new Color(12, 38, 106),
            new Color(255, 215, 64),
            () -> controller.changeState(GameState.INSTRUCTIONS)
        ));

        // 3. EXIT (Amethyst/Royal Purple gradient with gold border)
        buttons.add(new MenuButton(
            "Exit",
            new Rectangle(startX, 393, btnWidth, btnHeight),
            new Color(124, 44, 188),
            new Color(70, 22, 116),
            new Color(156, 62, 232),
            new Color(94, 30, 152),
            new Color(46, 12, 78),
            new Color(255, 215, 64),
            this::showExitConfirmation
        ));
    }

    private void showExitConfirmation() {
        int option = JOptionPane.showOptionDialog(
            this,
            "Are you sure you want to exit?",
            "Exit Game",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new Object[]{"Yes", "No"},
            "No"
        );
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                boolean anyHovered = false;
                for (MenuButton btn : buttons) {
                    boolean h = btn.contains(lp);
                    if (btn.isHovered() != h) {
                        btn.setHovered(h);
                        repaint();
                    }
                    if (h) anyHovered = true;
                }
                setCursor(anyHovered ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());
                for (MenuButton btn : buttons) {
                    if (btn.contains(lp)) {
                        btn.setPressed(true);
                        repaint();
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());
                for (MenuButton btn : buttons) {
                    if (btn.isPressed()) {
                        btn.setPressed(false);
                        repaint();
                        if (btn.contains(lp)) {
                            btn.trigger();
                        }
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boolean needRepaint = false;
                for (MenuButton btn : buttons) {
                    if (btn.isHovered() || btn.isPressed()) {
                        btn.setHovered(false);
                        btn.setPressed(false);
                        needRepaint = true;
                    }
                }
                setCursor(Cursor.getDefaultCursor());
                if (needRepaint) repaint();
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

        // Fill letterbox area with deep midnight tone
        g2d.setColor(new Color(6, 7, 14));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Apply viewport transformation to 1280x720 canvas
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        // Clip strictly to logical bounds
        g2d.clipRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Enable high-quality anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Draw visual layers
        drawBackground(g2d);
        drawMoon(g2d);
        drawMansion(g2d);
        drawGroundAndPath(g2d);
        drawTrees(g2d);
        drawGateAndLanterns(g2d);
        drawTitle(g2d);
        drawDecorativeKey(g2d);
        drawButtons(g2d);

        g2d.dispose();
    }

    // =========================================================================
    // BACKGROUND & NIGHT SKY
    // =========================================================================
    private void drawBackground(Graphics2D g) {
        // Night sky gradient
        LinearGradientPaint skyGrad = new LinearGradientPaint(
            0, 0, 0, 520,
            new float[]{0.0f, 0.45f, 0.85f, 1.0f},
            new Color[]{
                new Color(7, 10, 24),
                new Color(13, 19, 44),
                new Color(24, 28, 58),
                new Color(36, 32, 64)
            }
        );
        g.setPaint(skyGrad);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Subtle stars across the upper sky
        g.setColor(new Color(255, 255, 255, 140));
        int[][] stars = {
            {90, 50}, {160, 95}, {240, 40}, {310, 85}, {410, 40},
            {480, 70}, {720, 35}, {820, 65}, {890, 40}, {1080, 55},
            {1150, 35}, {1210, 75}, {140, 160}, {220, 130}, {1120, 140}
        };
        for (int[] star : stars) {
            g.fillRect(star[0], star[1], 2, 2);
        }

        // Faint distant ridge silhouette
        g.setColor(new Color(12, 14, 28));
        GeneralPath distantHills = new GeneralPath();
        distantHills.moveTo(0, 440);
        distantHills.curveTo(300, 415, 600, 435, 1280, 420);
        distantHills.lineTo(1280, 520);
        distantHills.lineTo(0, 520);
        distantHills.closePath();
        g.fill(distantHills);
    }

    // =========================================================================
    // MOON & GLOW
    // =========================================================================
    private void drawMoon(Graphics2D g) {
        int cx = 980;
        int cy = 95;
        int r = 48;

        // Radiant moon halo
        RadialGradientPaint halo = new RadialGradientPaint(
            cx, cy, 135,
            new float[]{0.0f, 0.35f, 0.70f, 1.0f},
            new Color[]{
                new Color(220, 235, 255, 95),
                new Color(180, 210, 255, 45),
                new Color(140, 175, 230, 15),
                new Color(100, 140, 200, 0)
            }
        );
        g.setPaint(halo);
        g.fillOval(cx - 135, cy - 135, 270, 270);

        // Moon disc gradient
        RadialGradientPaint moonDisc = new RadialGradientPaint(
            cx - 10, cy - 10, r,
            new float[]{0.0f, 0.75f, 1.0f},
            new Color[]{
                new Color(248, 252, 255),
                new Color(216, 228, 240),
                new Color(176, 194, 212)
            }
        );
        g.setPaint(moonDisc);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Moon craters
        g.setColor(new Color(156, 174, 194, 65));
        g.fillOval(cx - 16, cy - 12, 14, 10);
        g.fillOval(cx + 8, cy - 18, 12, 9);
        g.fillOval(cx + 10, cy + 6, 16, 12);
        g.fillOval(cx - 20, cy + 10, 12, 9);

        // Soft cloud across moon base
        g.setColor(new Color(22, 24, 48, 115));
        GeneralPath cloud = new GeneralPath();
        cloud.moveTo(cx - 85, cy + 22);
        cloud.curveTo(cx - 40, cy + 8, cx + 20, cy + 12, cx + 65, cy + 18);
        cloud.curveTo(cx + 95, cy + 26, cx + 45, cy + 38, cx, cy + 32);
        cloud.curveTo(cx - 45, cy + 36, cx - 75, cy + 28, cx - 85, cy + 22);
        cloud.closePath();
        g.fill(cloud);
    }

    // =========================================================================
    // HAUNTED MANSION
    // =========================================================================
    private void drawMansion(Graphics2D g) {
        // Mansion architectural palette
        Color wallColor = new Color(24, 30, 52);
        Color wallShade = new Color(18, 22, 38);
        Color roofColor = new Color(34, 40, 68);
        Color roofHighlight = new Color(44, 52, 86);
        Color trimColor = new Color(48, 56, 92);

        // 1. Chimneys
        g.setColor(wallShade);
        g.fillRect(405, 80, 24, 90);
        g.fillRect(851, 80, 24, 90);
        g.setColor(trimColor);
        g.fillRect(401, 74, 32, 8);
        g.fillRect(847, 74, 32, 8);

        // 2. Left Wing & Right Wing Walls (x: 310-490, 790-970; y: 160-440)
        g.setColor(wallColor);
        g.fillRect(310, 170, 180, 270);
        g.fillRect(790, 170, 180, 270);

        // Sloping rooflines on wings
        g.setColor(roofColor);
        GeneralPath leftRoof = new GeneralPath();
        leftRoof.moveTo(295, 170);
        leftRoof.lineTo(400, 110);
        leftRoof.lineTo(490, 170);
        leftRoof.closePath();
        g.fill(leftRoof);

        GeneralPath rightRoof = new GeneralPath();
        rightRoof.moveTo(790, 170);
        rightRoof.lineTo(880, 110);
        rightRoof.lineTo(985, 170);
        rightRoof.closePath();
        g.fill(rightRoof);

        // 3. Center Mansion Block (x: 470-810, y: 130-440)
        g.setColor(wallColor);
        g.fillRect(470, 140, 340, 300);

        // Large Center Gable Roof (Peaks high so circular attic window sits cleanly above title!)
        GeneralPath centerRoof = new GeneralPath();
        centerRoof.moveTo(450, 145);
        centerRoof.lineTo(640, 30);
        centerRoof.lineTo(830, 145);
        centerRoof.closePath();
        g.setColor(roofColor);
        g.fill(centerRoof);

        // Gable trim overhang
        g.setColor(roofHighlight);
        g.setStroke(new BasicStroke(4.0f));
        g.drawLine(450, 145, 640, 30);
        g.drawLine(640, 30, 830, 145);

        // Spire & Finial atop center peak
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(640, 30, 640, 12);
        g.drawOval(636, 4, 8, 8);

        // 4. Circular Rose Attic Window (Center at x=640, y=75 - well above title at y=145!)
        int winX = 640;
        int winY = 75;
        int winR = 21;

        // Warm ambient light cast from rose window
        RadialGradientPaint roseGlow = new RadialGradientPaint(
            winX, winY, 55,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 205, 65, 120), new Color(255, 175, 45, 40), new Color(255, 150, 20, 0)}
        );
        g.setPaint(roseGlow);
        g.fillOval(winX - 55, winY - 55, 110, 110);

        // Rose window glass
        RadialGradientPaint roseGlass = new RadialGradientPaint(
            winX, winY, winR,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{new Color(255, 244, 150), new Color(255, 190, 48), new Color(212, 134, 22)}
        );
        g.setPaint(roseGlass);
        g.fillOval(winX - winR, winY - winR, winR * 2, winR * 2);

        // Stone trim and rose mullions
        g.setColor(wallShade);
        g.setStroke(new BasicStroke(3.0f));
        g.drawOval(winX - winR, winY - winR, winR * 2, winR * 2);
        g.setStroke(new BasicStroke(2.0f));
        g.drawLine(winX - winR, winY, winX + winR, winY);
        g.drawLine(winX, winY - winR, winX, winY + winR);
        g.drawLine(winX - 14, winY - 14, winX + 14, winY + 14);
        g.drawLine(winX - 14, winY + 14, winX + 14, winY - 14);

        // 5. Wing Windows (Lit with warm amber glow)
        // 2nd floor arched windows
        drawArchedWindow(g, 355, 205, 36, 54);
        drawArchedWindow(g, 425, 205, 36, 54);
        drawArchedWindow(g, 820, 205, 36, 54);
        drawArchedWindow(g, 890, 205, 36, 54);

        // 1st floor rectangular paned windows
        drawPanedWindow(g, 355, 305, 36, 56);
        drawPanedWindow(g, 425, 305, 36, 56);
        drawPanedWindow(g, 820, 305, 36, 56);
        drawPanedWindow(g, 890, 305, 36, 56);

        // 6. Grand Entrance Double Doors (Center background behind pathway)
        g.setColor(new Color(16, 12, 18));
        g.fillRoundRect(605, 345, 70, 95, 12, 12);
        g.setColor(new Color(28, 20, 30));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(605, 345, 70, 95, 12, 12);
        g.drawLine(640, 345, 640, 440);
        g.setColor(new Color(212, 175, 55));
        g.fillOval(632, 395, 4, 8);
        g.fillOval(644, 395, 4, 8);
    }

    private void drawArchedWindow(Graphics2D g, int x, int y, int w, int h) {
        // Window warm bloom
        RadialGradientPaint bloom = new RadialGradientPaint(
            x + w / 2, y + h / 2, 52,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(255, 200, 55, 80), new Color(255, 165, 35, 25), new Color(255, 140, 10, 0)}
        );
        g.setPaint(bloom);
        g.fillOval(x + w / 2 - 52, y + h / 2 - 52, 104, 104);

        // Glass
        Shape arch = createArchedShape(x, y, w, h);
        LinearGradientPaint glass = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(255, 240, 145), new Color(255, 188, 42), new Color(208, 128, 16)}
        );
        g.setPaint(glass);
        g.fill(arch);

        // Mullions & Frame
        g.setColor(new Color(18, 20, 34));
        g.setStroke(new BasicStroke(2.0f));
        g.draw(arch);
        g.drawLine(x + w / 2, y, x + w / 2, y + h);
        g.drawLine(x, y + (int) (h * 0.45), x + w, y + (int) (h * 0.45));
    }

    private void drawPanedWindow(Graphics2D g, int x, int y, int w, int h) {
        // Window warm bloom
        RadialGradientPaint bloom = new RadialGradientPaint(
            x + w / 2, y + h / 2, 48,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(255, 200, 55, 75), new Color(255, 165, 35, 22), new Color(255, 140, 10, 0)}
        );
        g.setPaint(bloom);
        g.fillOval(x + w / 2 - 48, y + h / 2 - 48, 96, 96);

        // Glass
        LinearGradientPaint glass = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{new Color(255, 238, 135), new Color(255, 184, 38), new Color(198, 118, 16)}
        );
        g.setPaint(glass);
        g.fillRect(x, y, w, h);

        // Mullions & Frame
        g.setColor(new Color(18, 20, 34));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRect(x, y, w, h);
        g.drawLine(x + w / 2, y, x + w / 2, y + h);
        g.drawLine(x, y + h / 3, x + w, y + h / 3);
        g.drawLine(x, y + (2 * h) / 3, x + w, y + (2 * h) / 3);
    }

    private Shape createArchedShape(int x, int y, int w, int h) {
        GeneralPath p = new GeneralPath();
        p.moveTo(x, y + h);
        p.lineTo(x, y + w / 2);
        p.curveTo(x, y, x + w, y, x + w, y + w / 2);
        p.lineTo(x + w, y + h);
        p.closePath();
        return p;
    }

    // =========================================================================
    // GROUND & PATHWAY
    // =========================================================================
    private void drawGroundAndPath(Graphics2D g) {
        // Dark terrain gradient
        LinearGradientPaint grass = new LinearGradientPaint(
            0, 430, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(16, 20, 32), new Color(11, 14, 22), new Color(7, 9, 15)}
        );
        g.setPaint(grass);
        g.fillRect(0, 430, LOGICAL_WIDTH, LOGICAL_HEIGHT - 430);

        // Stone pathway widening toward foreground
        GeneralPath path = new GeneralPath();
        path.moveTo(590, 440);
        path.lineTo(690, 440);
        path.lineTo(880, LOGICAL_HEIGHT);
        path.lineTo(400, LOGICAL_HEIGHT);
        path.closePath();

        LinearGradientPaint pathGrad = new LinearGradientPaint(
            0, 440, 0, LOGICAL_HEIGHT,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(28, 30, 44), new Color(34, 36, 52), new Color(22, 24, 36)}
        );
        g.setPaint(pathGrad);
        g.fill(path);

        // Cobblestone flagstone lines on path
        g.setColor(new Color(48, 52, 74, 110));
        g.setStroke(new BasicStroke(1.5f));
        int[] stoneY = {460, 490, 530, 580, 640, 700};
        for (int sy : stoneY) {
            double prog = (double) (sy - 440) / (LOGICAL_HEIGHT - 440);
            int lx = (int) (590 - 190 * prog);
            int rx = (int) (690 + 190 * prog);
            g.drawLine(lx + 8, sy, rx - 8, sy);
        }

        // Shrubbery / dark bushes at base of mansion & near fences
        g.setColor(new Color(12, 16, 26));
        int[][] bushes = {
            {250, 440, 75, 42}, {305, 445, 65, 36}, {455, 440, 65, 32},
            {760, 440, 68, 32}, {910, 445, 68, 36}, {965, 440, 75, 42}
        };
        for (int[] b : bushes) {
            g.fillOval(b[0], b[1], b[2], b[3]);
        }

        // Creeping mist across the ground
        LinearGradientPaint mist = new LinearGradientPaint(
            0, 620, 0, 715,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(180, 200, 230, 0), new Color(180, 200, 230, 26), new Color(180, 200, 230, 0)}
        );
        g.setPaint(mist);
        g.fillRect(0, 620, LOGICAL_WIDTH, 95);
    }

    // =========================================================================
    // GNARLED TREES & FOLIAGE (Framing both sides)
    // =========================================================================
    private void drawTrees(Graphics2D g) {
        Color treeDark = new Color(8, 10, 18);
        Color foliageDark = new Color(10, 12, 22, 235);

        // Foliage clumps on left side
        g.setColor(foliageDark);
        g.fillOval(-60, 140, 180, 140);
        g.fillOval(20, 80, 150, 120);
        g.fillOval(100, 120, 130, 100);

        // Foliage clumps on right side
        g.fillOval(1160, 140, 180, 140);
        g.fillOval(1110, 80, 150, 120);
        g.fillOval(1050, 120, 130, 100);

        // LEFT TREE TRUNK & BRANCHES
        g.setColor(treeDark);
        GeneralPath leftTrunk = new GeneralPath();
        leftTrunk.moveTo(0, 720);
        leftTrunk.curveTo(70, 580, 40, 420, 0, 320);
        leftTrunk.lineTo(0, 720);
        leftTrunk.closePath();
        g.fill(leftTrunk);

        g.setStroke(new BasicStroke(16.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath b1 = new GeneralPath();
        b1.moveTo(0, 380);
        b1.curveTo(70, 330, 110, 240, 180, 180);
        b1.curveTo(230, 140, 290, 130, 340, 110);
        g.draw(b1);

        g.setStroke(new BasicStroke(9.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath b2 = new GeneralPath();
        b2.moveTo(180, 180);
        b2.curveTo(220, 220, 260, 240, 320, 260);
        g.draw(b2);

        GeneralPath b3 = new GeneralPath();
        b3.moveTo(110, 240);
        b3.curveTo(130, 160, 160, 100, 220, 70);
        g.draw(b3);

        g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath b4 = new GeneralPath();
        b4.moveTo(340, 110);
        b4.curveTo(380, 100, 400, 70, 430, 60);
        g.draw(b4);
        GeneralPath b5 = new GeneralPath();
        b5.moveTo(220, 70);
        b5.curveTo(250, 50, 280, 40, 310, 30);
        g.draw(b5);

        // RIGHT TREE TRUNK & BRANCHES
        GeneralPath rightTrunk = new GeneralPath();
        rightTrunk.moveTo(LOGICAL_WIDTH, 720);
        rightTrunk.curveTo(1210, 580, 1240, 420, LOGICAL_WIDTH, 320);
        rightTrunk.lineTo(LOGICAL_WIDTH, 720);
        rightTrunk.closePath();
        g.fill(rightTrunk);

        g.setStroke(new BasicStroke(16.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath rb1 = new GeneralPath();
        rb1.moveTo(LOGICAL_WIDTH, 380);
        rb1.curveTo(1210, 330, 1170, 240, 1100, 180);
        rb1.curveTo(1050, 140, 990, 130, 940, 110);
        g.draw(rb1);

        g.setStroke(new BasicStroke(9.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath rb2 = new GeneralPath();
        rb2.moveTo(1100, 180);
        rb2.curveTo(1060, 220, 1020, 240, 960, 260);
        g.draw(rb2);

        GeneralPath rb3 = new GeneralPath();
        rb3.moveTo(1170, 240);
        rb3.curveTo(1150, 160, 1120, 100, 1060, 70);
        g.draw(rb3);

        g.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath rb4 = new GeneralPath();
        rb4.moveTo(940, 110);
        rb4.curveTo(900, 100, 880, 70, 850, 60);
        g.draw(rb4);
        GeneralPath rb5 = new GeneralPath();
        rb5.moveTo(1060, 70);
        rb5.curveTo(1030, 50, 1000, 40, 970, 30);
        g.draw(rb5);
    }

    // =========================================================================
    // IRON GATE & OUTDOOR LANTERNS
    // =========================================================================
    private void drawGateAndLanterns(Graphics2D g) {
        int leftPillarX = 250;
        int rightPillarX = 980;
        int pillarW = 50;
        int pillarTop = 360;
        int pillarH = 200;

        // 1. Wrought Iron Fence (Left and Right)
        g.setColor(new Color(14, 16, 26));
        g.setStroke(new BasicStroke(3.0f));

        // Horizontal rails
        g.drawLine(0, 440, leftPillarX, 440);
        g.drawLine(0, 520, leftPillarX, 520);
        g.drawLine(rightPillarX + pillarW, 440, LOGICAL_WIDTH, 440);
        g.drawLine(rightPillarX + pillarW, 520, LOGICAL_WIDTH, 520);

        // Vertical pickets with spearheads
        for (int x = 20; x < leftPillarX; x += 22) {
            drawFencePicket(g, x, 400, 550);
        }
        for (int x = rightPillarX + pillarW + 22; x < LOGICAL_WIDTH; x += 22) {
            drawFencePicket(g, x, 400, 550);
        }

        // 2. Stone Gate Pillars
        Color stoneBody = new Color(28, 34, 50);
        Color stoneShade = new Color(18, 22, 34);
        Color stoneCap = new Color(42, 50, 74);

        // Left Pillar
        g.setColor(stoneBody);
        g.fillRect(leftPillarX, pillarTop, pillarW, pillarH);
        g.setColor(stoneShade);
        g.fillRect(leftPillarX + pillarW - 8, pillarTop, 8, pillarH);
        g.setColor(stoneCap);
        g.fillRect(leftPillarX - 6, pillarTop - 12, pillarW + 12, 12);

        // Right Pillar
        g.setColor(stoneBody);
        g.fillRect(rightPillarX, pillarTop, pillarW, pillarH);
        g.setColor(stoneShade);
        g.fillRect(rightPillarX, pillarTop, 8, pillarH);
        g.setColor(stoneCap);
        g.fillRect(rightPillarX - 6, pillarTop - 12, pillarW + 12, 12);

        // 3. Outdoor Glowing Lanterns
        drawLantern(g, leftPillarX + pillarW / 2, pillarTop - 12);
        drawLantern(g, rightPillarX + pillarW / 2, pillarTop - 12);
    }

    private void drawFencePicket(Graphics2D g, int x, int topY, int botY) {
        g.drawLine(x, topY, x, botY);
        GeneralPath spear = new GeneralPath();
        spear.moveTo(x - 4, topY);
        spear.lineTo(x, topY - 14);
        spear.lineTo(x + 4, topY);
        spear.closePath();
        g.fill(spear);
    }

    private void drawLantern(Graphics2D g, int cx, int baseY) {
        int lanternH = 50;
        int lanternW = 28;
        int topY = baseY - lanternH;

        // Radiant warm amber glow casting around lantern and pillar
        RadialGradientPaint lightBloom = new RadialGradientPaint(
            cx, topY + 24, 150,
            new float[]{0.0f, 0.35f, 0.70f, 1.0f},
            new Color[]{
                new Color(255, 215, 60, 130),
                new Color(255, 175, 30, 60),
                new Color(245, 130, 15, 20),
                new Color(220, 100, 0, 0)
            }
        );
        g.setPaint(lightBloom);
        g.fillOval(cx - 150, topY + 24 - 150, 300, 300);

        // Glowing Glass Core
        RadialGradientPaint glassGlow = new RadialGradientPaint(
            cx, topY + 24, 18,
            new float[]{0.0f, 0.6f, 1.0f},
            new Color[]{new Color(255, 255, 200), new Color(255, 205, 50), new Color(240, 135, 10)}
        );
        g.setPaint(glassGlow);
        g.fillRect(cx - lanternW / 2 + 3, topY + 12, lanternW - 6, 26);

        // Cast Iron Lantern Frame
        g.setColor(new Color(12, 14, 20));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRect(cx - lanternW / 2 + 3, topY + 12, lanternW - 6, 26);

        // Lantern pyramid roof
        GeneralPath roof = new GeneralPath();
        roof.moveTo(cx - lanternW / 2 - 2, topY + 12);
        roof.lineTo(cx, topY);
        roof.lineTo(cx + lanternW / 2 + 2, topY + 12);
        roof.closePath();
        g.fill(roof);

        // Finial ring
        g.drawOval(cx - 3, topY - 6, 6, 6);

        // Base mounting bracket
        g.fillRect(cx - 6, baseY - 12, 12, 12);
    }

    // =========================================================================
    // TITLE & SUBTITLE
    // =========================================================================
    private void drawTitle(Graphics2D g) {
        String mainTitle = "Escape the Room";
        String subtitle = "Mystery Puzzle Adventure";

        Font titleFont = new Font("Georgia", Font.BOLD, 52);
        Font subFont = new Font("Georgia", Font.BOLD | Font.ITALIC, 20);

        FontMetrics tm = g.getFontMetrics(titleFont);
        FontMetrics sm = g.getFontMetrics(subFont);

        int titleX = (LOGICAL_WIDTH - tm.stringWidth(mainTitle)) / 2;
        int titleY = 145;

        int subX = (LOGICAL_WIDTH - sm.stringWidth(subtitle)) / 2;
        int subY = 182;

        // 1. Title Deep Shadow
        g.setFont(titleFont);
        g.setColor(new Color(4, 5, 12, 240));
        g.drawString(mainTitle, titleX + 3, titleY + 4);

        // 2. Title Golden Gradient Fill
        LinearGradientPaint goldGrad = new LinearGradientPaint(
            titleX, titleY - 50, titleX, titleY,
            new float[]{0.0f, 0.45f, 0.85f, 1.0f},
            new Color[]{
                new Color(255, 248, 175),
                new Color(255, 215, 64),
                new Color(230, 162, 18),
                new Color(185, 115, 8)
            }
        );
        g.setPaint(goldGrad);
        g.drawString(mainTitle, titleX, titleY);

        // 3. Subtitle Shadow & Ivory Cream Fill
        g.setFont(subFont);
        g.setColor(new Color(4, 5, 12, 220));
        g.drawString(subtitle, subX + 2, subY + 2);
        g.setColor(new Color(248, 244, 232));
        g.drawString(subtitle, subX, subY);
    }

    // =========================================================================
    // DECORATIVE GOLDEN KEY
    // =========================================================================
    private void drawDecorativeKey(Graphics2D g) {
        int cx = LOGICAL_WIDTH / 2;
        int cy = 210;

        // Key drop shadow
        Graphics2D gShadow = (Graphics2D) g.create();
        gShadow.translate(2, 2);
        gShadow.setColor(new Color(5, 5, 12, 180));
        renderKeyShape(gShadow, cx, cy);
        gShadow.dispose();

        // Key golden gradient
        LinearGradientPaint keyGold = new LinearGradientPaint(
            cx - 36, cy - 10, cx + 36, cy + 10,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(255, 240, 140), new Color(255, 205, 50), new Color(205, 135, 15)}
        );
        g.setPaint(keyGold);
        renderKeyShape(g, cx, cy);

        // Specular highlight line along the key stem
        g.setColor(new Color(255, 255, 220, 220));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(cx - 8, cy - 1, cx + 24, cy - 1);
    }

    private void renderKeyShape(Graphics2D g, int cx, int cy) {
        int bowX = cx - 26;
        int bowR = 12;

        // Bow ring
        Area keyArea = new Area(new Ellipse2D.Double(bowX - bowR, cy - bowR, bowR * 2, bowR * 2));
        keyArea.subtract(new Area(new Ellipse2D.Double(bowX - 5, cy - 5, 10, 10)));

        // Stem
        keyArea.add(new Area(new RoundRectangle2D.Double(bowX + bowR - 3, cy - 3, 44, 6, 2, 2)));

        // Raised collar
        keyArea.add(new Area(new RoundRectangle2D.Double(bowX + bowR + 4, cy - 5, 4, 10, 2, 2)));

        // Bit & notches
        keyArea.add(new Area(new Rectangle(cx + 18, cy + 3, 5, 8)));
        keyArea.add(new Area(new Rectangle(cx + 25, cy + 3, 5, 10)));
        keyArea.add(new Area(new Rectangle(cx + 21, cy + 7, 4, 3)));

        g.fill(keyArea);
    }

    // =========================================================================
    // BUTTONS
    // =========================================================================
    private void drawButtons(Graphics2D g) {
        for (MenuButton btn : buttons) {
            drawButton(g, btn);
        }
    }

    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.getBounds();
        int x = r.x;
        int y = r.y + (btn.isPressed() ? 2 : 0); // Tactile 2px downward shift
        int w = r.width;
        int h = r.height;
        int arc = 26;

        // 1. Golden Aura Glow on Hover
        if (btn.isHovered()) {
            RadialGradientPaint glow = new RadialGradientPaint(
                x + w / 2, y + h / 2, (float) (w * 0.58),
                new float[]{0.0f, 0.65f, 1.0f},
                new Color[]{
                    new Color(255, 225, 75, 110),
                    new Color(255, 205, 50, 45),
                    new Color(255, 185, 20, 0)
                }
            );
            g.setPaint(glow);
            g.fillRoundRect(x - 8, y - 8, w + 16, h + 16, arc + 8, arc + 8);
        }

        // 2. Outer Drop Shadow
        g.setColor(new Color(4, 5, 12, 160));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        // 3. Background Gradient
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
        g.setColor(new Color(255, 245, 180, btn.isHovered() ? 140 : 80));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, arc - 4, arc - 4);

        // 6. Centered Bold Button Text with Shadow
        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        int textX = x + (w - fm.stringWidth(btn.getText())) / 2;
        int textY = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 210));
        g.drawString(btn.getText(), textX + 1, textY + 2);

        // Text white color
        g.setColor(Color.WHITE);
        g.drawString(btn.getText(), textX, textY);
    }

    // =========================================================================
    // INNER CLASS: MenuButton
    // =========================================================================
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
            if (action != null) {
                action.run();
            }
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
