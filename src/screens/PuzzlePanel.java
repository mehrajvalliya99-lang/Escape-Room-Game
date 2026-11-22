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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Screen 5: Puzzle Screen (Mystery Challenge) for "Escape the Room – Mystery Puzzle Adventure".
 *
 * Appears as a large centered ornate 2D puzzle window over Room 1 (The Study),
 * softly dimming the room behind it. Features a reusable content area supporting
 * all 6 future puzzle formats (Number code, Symbol sequence, Cipher, Pattern,
 * Combination, Logic/deduction), custom-styled keyboard answer input field,
 * 4 polished action buttons (SUBMIT, CLEAR, HINT, CLOSE), temporary feedback banners,
 * and clear integration hooks for Member 2.
 *
 * // TODO: Member 2 - connect PuzzleManager
 * // TODO: Member 2 - connect HintManager
 * // TODO: Member 2 - implement answer validation
 */
public class PuzzlePanel extends JPanel {
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;

    private final GameController controller;
    private final List<Puzzle> samplePuzzles;
    private int currentPuzzleIndex = 0;

    // Viewport scaling and letterboxing
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    // Centered Window logical bounds
    private final Rectangle windowBounds = new Rectangle(210, 50, 860, 620);

    // Answer input field (custom styled JTextField)
    private final Rectangle answerBoxLogicalBounds = new Rectangle(410, 452, 470, 46);
    private final JTextField answerField;

    // Interactive action buttons
    private final MenuButton submitButton;
    private final MenuButton clearButton;
    private final MenuButton hintButton;
    private final MenuButton closeButton;
    private final MenuButton closeXButton;
    private final MenuButton formatSwitcherButton;

    // Keypad buttons for number code format (Reference Image Screen 5)
    private final List<KeypadButton> keypadButtons;

    // Temporary notification feedback banner
    private String temporaryMessage = null;
    private int messageTicksRemaining = 0;
    private Timer feedbackTimer;

    // Ambient subtle glow animation
    private float animPhase = 0.0f;
    private Timer animTimer;

    public PuzzlePanel(GameController controller) {
        this.controller = controller;
        this.samplePuzzles = Puzzle.getAllSamplePuzzles();
        this.keypadButtons = new ArrayList<>();

        setPreferredSize(new Dimension(LOGICAL_WIDTH, LOGICAL_HEIGHT));
        setBackground(new Color(10, 12, 22));
        setLayout(null); // Scaled responsive doLayout()
        setDoubleBuffered(true);
        setFocusable(true);

        // 1. ANSWER INPUT FIELD
        answerField = initAnswerField();
        add(answerField);

        // 2. ACTION BUTTONS
        submitButton = new MenuButton(
            "SUBMIT",
            new Rectangle(240, 565, 175, 48),
            new Color(36, 110, 68),
            new Color(18, 62, 38),
            new Color(48, 145, 90),
            new Color(26, 88, 54),
            new Color(12, 45, 26),
            new Color(255, 215, 64),
            this::handleSubmit
        );

        clearButton = new MenuButton(
            "CLEAR",
            new Rectangle(430, 565, 160, 48),
            new Color(120, 78, 34),
            new Color(68, 42, 16),
            new Color(155, 102, 46),
            new Color(90, 56, 22),
            new Color(48, 28, 10),
            new Color(255, 215, 64),
            this::handleClear
        );

        hintButton = new MenuButton(
            "HINT",
            new Rectangle(605, 565, 160, 48),
            new Color(37, 72, 130),
            new Color(20, 40, 78),
            new Color(50, 95, 170),
            new Color(28, 56, 108),
            new Color(14, 28, 56),
            new Color(255, 215, 64),
            this::handleHint
        );

        closeButton = new MenuButton(
            "CLOSE",
            new Rectangle(780, 565, 190, 48),
            new Color(125, 30, 42),
            new Color(68, 14, 22),
            new Color(160, 42, 58),
            new Color(95, 20, 32),
            new Color(50, 10, 16),
            new Color(255, 215, 64),
            () -> controller.returnToCurrentRoom()
        );

        closeXButton = new MenuButton(
            "X",
            new Rectangle(1022, 66, 34, 34),
            new Color(80, 24, 32),
            new Color(45, 12, 18),
            new Color(120, 36, 48),
            new Color(70, 20, 28),
            new Color(30, 8, 12),
            new Color(255, 215, 64),
            () -> controller.returnToCurrentRoom()
        );

        formatSwitcherButton = new MenuButton(
            "Format: Number Code",
            new Rectangle(236, 68, 185, 30),
            new Color(72, 48, 110),
            new Color(42, 26, 68),
            new Color(95, 65, 145),
            new Color(58, 36, 92),
            new Color(30, 18, 50),
            new Color(212, 175, 55),
            this::cyclePuzzleFormat
        );

        initKeypadButtons();
        initMouseListeners();
        initTimers();
    }

    private JTextField initAnswerField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dark interior background
                g2.setColor(new Color(20, 14, 26));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Subtle inner top shadow
                g2.setColor(new Color(6, 4, 10, 130));
                g2.fillRoundRect(2, 2, getWidth() - 4, 10, 10, 10);

                // Golden border & focus glow
                if (hasFocus()) {
                    g2.setColor(new Color(255, 215, 64, 85));
                    g2.setStroke(new BasicStroke(4.0f));
                    g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);

                    g2.setColor(new Color(255, 225, 80));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 14, 14);
                } else {
                    g2.setColor(new Color(185, 140, 50));
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
                }

                g2.dispose();
                super.paintComponent(g);

                // Placeholder text when empty and unfocused
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D pg = (Graphics2D) g.create();
                    pg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    pg.setFont(getFont());
                    pg.setColor(new Color(175, 155, 125, 140));
                    FontMetrics pfm = pg.getFontMetrics();
                    int py = (getHeight() - pfm.getHeight()) / 2 + pfm.getAscent();
                    pg.drawString("Enter your answer...", 16, py);
                    pg.dispose();
                }
            }
        };

        field.setOpaque(false);
        field.setCaretColor(new Color(255, 215, 64));
        field.setForeground(new Color(255, 248, 230));
        field.setFont(new Font("Georgia", Font.BOLD, 18));
        field.setBorder(new EmptyBorder(4, 16, 4, 16));
        field.setMargin(new Insets(2, 14, 2, 14));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { repaint(); }
            @Override
            public void focusLost(FocusEvent e) { repaint(); }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSubmit();
                }
            }
        });

        return field;
    }

    private void initKeypadButtons() {
        keypadButtons.clear();
        int kx = 640;
        int ky = 205;
        int kw = 64;
        int kh = 44;
        int gap = 8;

        String[][] layout = {
            {"1", "2", "3"},
            {"4", "5", "6"},
            {"7", "8", "9"},
            {"C", "0", "OK"}
        };

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 3; c++) {
                String label = layout[r][c];
                int bx = kx + c * (kw + gap);
                int by = ky + r * (kh + gap);
                keypadButtons.add(new KeypadButton(label, new Rectangle(bx, by, kw, kh)));
            }
        }
    }

    private void initTimers() {
        animTimer = new Timer(50, e -> {
            animPhase += 0.08f;
            if (animPhase > 1000.0f) animPhase = 0.0f;

            if (messageTicksRemaining > 0) {
                messageTicksRemaining--;
                if (messageTicksRemaining == 0) {
                    temporaryMessage = null;
                }
            }

            repaint();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (animTimer != null && !animTimer.isRunning()) {
            animTimer.start();
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }
    }

    // =========================================================================
    // BUTTON ACTIONS & MEMBER 2 HOOKS
    // =========================================================================

    public void onPuzzleOpened() {
        if (answerField != null) {
            answerField.setText("");
            answerField.requestFocusInWindow();
        }
        this.temporaryMessage = null;
        this.messageTicksRemaining = 0;
        Puzzle p = getCurrentPuzzle();
        if (p != null && formatSwitcherButton != null) {
            formatSwitcherButton.text = "Format: " + p.getType().getDisplayName();
        }
        repaint();
    }

    private void handleSubmit() {
        String answer = answerField.getText().trim();
        Puzzle p = getCurrentPuzzle();
        if (p == null) return;

        if (answer.isEmpty()) {
            setFeedbackMessage("Please enter an answer before submitting.");
            return;
        }

        if (controller == null || controller.getPuzzleManager() == null) {
            setFeedbackMessage("Puzzle system offline.");
            return;
        }

        if (controller.getPuzzleManager().isSolved(p.getId())) {
            setFeedbackMessage("This puzzle has already been solved!");
            return;
        }

        if (controller.getPuzzleManager().validateAnswer(p.getId(), answer)) {
            controller.getPuzzleManager().markSolved(p.getId());
            controller.getScoreManager().awardPuzzleScore(p.getId());

            // Contextual puzzle rewards
            if ("chest_code".equals(p.getId())) {
                controller.getInventoryManager().addItem(InventoryItem.createGoldenKey());
                controller.getInventoryManager().addItem(InventoryItem.createOldPhotograph());
                controller.getScoreManager().awardItemDiscoveryScore("golden_key");
                setFeedbackMessage("CORRECT! +100 pts. Iron Chest unlocked: Found Golden Key & Old Photo!");
            } else if ("locked_cabinet".equals(p.getId())) {
                controller.getInventoryManager().addItem(InventoryItem.createClockFragment());
                controller.getInventoryManager().addItem(InventoryItem.createMysteriousBook());
                controller.getScoreManager().awardItemDiscoveryScore("clock_fragment");
                setFeedbackMessage("CORRECT! +100 pts. Cabinet opened: Found Clock Fragment & Tome!");
            } else if ("statue".equals(p.getId())) {
                controller.getInventoryManager().addItem(InventoryItem.createFinalKey());
                controller.getScoreManager().awardItemDiscoveryScore("final_key");
                setFeedbackMessage("CORRECT! +100 pts. Staff pommel opened: Found Obsidian Final Key!");
            } else if ("combination_lock".equals(p.getId())) {
                setFeedbackMessage("CORRECT! +100 pts. The grand door combination lock disengages!");
            } else if ("symbol_panels".equals(p.getId())) {
                setFeedbackMessage("CORRECT! +100 pts. The celestial counterweights release with a thud!");
            } else if ("pedestal_final".equals(p.getId())) {
                setFeedbackMessage("CORRECT! +100 pts. The sacred master key locks into the pedestal!");
            } else {
                setFeedbackMessage("CORRECT! +100 pts. The mechanism clicks and unlocks!");
            }
        } else {
            setFeedbackMessage("INCORRECT! Inspect clues or click HINT (-25 pts).");
        }
    }

    private void handleClear() {
        answerField.setText("");
        setFeedbackMessage("Answer field cleared.");
    }

    private void handleHint() {
        Puzzle p = getCurrentPuzzle();
        if (p == null) return;
        if (controller != null && controller.getHintManager() != null) {
            String hint = controller.getHintManager().requestHint(p.getId(), controller.getScoreManager());
            int penalty = controller.getHintManager().getHintPenalty();
            setFeedbackMessage("HINT (-" + penalty + " pts): " + hint);
        } else {
            String hint = p.getDefaultHint();
            setFeedbackMessage("Hint: " + hint);
        }
    }

    public void setFeedbackMessage(String msg) {
        this.temporaryMessage = msg;
        this.messageTicksRemaining = 70; // ~3.5 seconds
        repaint();
    }

    private void cyclePuzzleFormat() {
        currentPuzzleIndex = (currentPuzzleIndex + 1) % samplePuzzles.size();
        Puzzle p = getCurrentPuzzle();
        formatSwitcherButton.text = "Format: " + p.getType().getDisplayName();
        setFeedbackMessage("Switched puzzle type: " + p.getType().getDisplayName());
        repaint();
    }

    public Puzzle getCurrentPuzzle() {
        if (controller != null && controller.getCurrentPuzzle() != null) {
            return controller.getCurrentPuzzle();
        }
        if (currentPuzzleIndex >= 0 && currentPuzzleIndex < samplePuzzles.size()) {
            return samplePuzzles.get(currentPuzzleIndex);
        }
        return samplePuzzles.get(0);
    }

    public void setPuzzle(Puzzle puzzle) {
        if (controller != null) {
            controller.setCurrentPuzzle(puzzle);
        }
        repaint();
    }

    // =========================================================================
    // RESPONSIVE LAYOUT & MOUSE INTERACTION
    // =========================================================================

    @Override
    public void doLayout() {
        super.doLayout();
        updateScaleAndOffset();
        if (answerField != null) {
            int px = (int) (offsetX + answerBoxLogicalBounds.x * scale);
            int py = (int) (offsetY + answerBoxLogicalBounds.y * scale);
            int pw = (int) (answerBoxLogicalBounds.width * scale);
            int ph = (int) (answerBoxLogicalBounds.height * scale);
            answerField.setBounds(px, py, pw, ph);
            answerField.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) (18 * scale))));
        }
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

    private void initMouseListeners() {
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point lp = toLogicalCoordinates(e.getPoint());
                boolean anyHover = false;

                // Action buttons hover
                boolean bSub = submitButton.contains(lp);
                submitButton.hovered = bSub;
                if (bSub) anyHover = true;

                boolean bClr = clearButton.contains(lp);
                clearButton.hovered = bClr;
                if (bClr) anyHover = true;

                boolean bHnt = hintButton.contains(lp);
                hintButton.hovered = bHnt;
                if (bHnt) anyHover = true;

                boolean bCls = closeButton.contains(lp);
                closeButton.hovered = bCls;
                if (bCls) anyHover = true;

                boolean bX = closeXButton.contains(lp);
                closeXButton.hovered = bX;
                if (bX) anyHover = true;

                boolean bFmt = formatSwitcherButton.contains(lp);
                formatSwitcherButton.hovered = bFmt;
                if (bFmt) anyHover = true;

                // Keypad buttons hover (only if number code puzzle active)
                if (getCurrentPuzzle().getType() == Puzzle.PuzzleType.NUMBER_CODE) {
                    for (KeypadButton kb : keypadButtons) {
                        boolean kh = kb.bounds.contains(lp);
                        kb.hovered = kh;
                        if (kh) anyHover = true;
                    }
                }

                setCursor(anyHover ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (submitButton.contains(lp)) submitButton.pressed = true;
                if (clearButton.contains(lp)) clearButton.pressed = true;
                if (hintButton.contains(lp)) hintButton.pressed = true;
                if (closeButton.contains(lp)) closeButton.pressed = true;
                if (closeXButton.contains(lp)) closeXButton.pressed = true;
                if (formatSwitcherButton.contains(lp)) formatSwitcherButton.pressed = true;

                if (getCurrentPuzzle().getType() == Puzzle.PuzzleType.NUMBER_CODE) {
                    for (KeypadButton kb : keypadButtons) {
                        if (kb.bounds.contains(lp)) {
                            kb.pressed = true;
                        }
                    }
                }

                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                Point lp = toLogicalCoordinates(e.getPoint());

                if (submitButton.pressed) {
                    submitButton.pressed = false;
                    if (submitButton.contains(lp)) submitButton.trigger();
                }
                if (clearButton.pressed) {
                    clearButton.pressed = false;
                    if (clearButton.contains(lp)) clearButton.trigger();
                }
                if (hintButton.pressed) {
                    hintButton.pressed = false;
                    if (hintButton.contains(lp)) hintButton.trigger();
                }
                if (closeButton.pressed) {
                    closeButton.pressed = false;
                    if (closeButton.contains(lp)) closeButton.trigger();
                }
                if (closeXButton.pressed) {
                    closeXButton.pressed = false;
                    if (closeXButton.contains(lp)) closeXButton.trigger();
                }
                if (formatSwitcherButton.pressed) {
                    formatSwitcherButton.pressed = false;
                    if (formatSwitcherButton.contains(lp)) formatSwitcherButton.trigger();
                }

                if (getCurrentPuzzle().getType() == Puzzle.PuzzleType.NUMBER_CODE) {
                    for (KeypadButton kb : keypadButtons) {
                        if (kb.pressed) {
                            kb.pressed = false;
                            if (kb.bounds.contains(lp)) {
                                handleKeypadPress(kb.label);
                            }
                        }
                    }
                }

                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                submitButton.hovered = false; submitButton.pressed = false;
                clearButton.hovered = false; clearButton.pressed = false;
                hintButton.hovered = false; hintButton.pressed = false;
                closeButton.hovered = false; closeButton.pressed = false;
                closeXButton.hovered = false; closeXButton.pressed = false;
                formatSwitcherButton.hovered = false; formatSwitcherButton.pressed = false;
                for (KeypadButton kb : keypadButtons) {
                    kb.hovered = false;
                    kb.pressed = false;
                }
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void handleKeypadPress(String key) {
        if ("C".equals(key)) {
            handleClear();
        } else if ("OK".equals(key)) {
            handleSubmit();
        } else {
            // Append digit to answer field
            String current = answerField.getText();
            if (current.length() < 12) {
                answerField.setText(current + key);
            }
        }
        answerField.requestFocusInWindow();
    }

    // =========================================================================
    // RENDERING PIPELINE
    // =========================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        updateScaleAndOffset();

        // 1. Fill letterbox margin with deep void color
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

        // 3. Draw Room 1 background (dimmed)
        drawDimmedRoomBackground(g2d);

        // 4. Draw Centered Ornate Puzzle Window
        drawPuzzleWindow(g2d);

        // 5. Draw Header (Heading, Subtitle, Decorative Lock)
        drawHeader(g2d);

        // 6. Draw Reusable Content Area (Dynamic based on current PuzzleType)
        drawPuzzleContentArea(g2d);

        // 7. Draw Answer Input Label
        drawAnswerInputLabel(g2d);

        // 8. Draw Feedback Banner (if active)
        if (temporaryMessage != null && messageTicksRemaining > 0) {
            drawFeedbackBanner(g2d, 250, 508, 780, 42);
        }

        // 9. Draw Action Buttons
        drawButton(g2d, submitButton);
        drawButton(g2d, clearButton);
        drawButton(g2d, hintButton);
        drawButton(g2d, closeButton);
        drawButton(g2d, closeXButton);
        drawButton(g2d, formatSwitcherButton);

        g2d.dispose();
    }

    /**
     * Renders Room 1 softly in the background with a darkening translucent scrim.
     */
    private void drawDimmedRoomBackground(Graphics2D g) {
        boolean paintedRoom = false;
        if (controller != null && controller.getGameFrame() != null) {
            GameState roomState = controller.getCurrentRoomState();
            if (roomState == null) roomState = GameState.PLAYING;
            JPanel room = controller.getGameFrame().getScreen(roomState);
            if (room != null) {
                try {
                    room.paint(g);
                    paintedRoom = true;
                } catch (Exception ignored) {
                    paintedRoom = false;
                }
            }
        }

        if (!paintedRoom) {
            drawProceduralStudyBackdrop(g);
        }

        // Dark translucent scrim overlay
        g.setColor(new Color(8, 10, 20, 215));
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);

        // Dark vignette
        RadialGradientPaint vig = new RadialGradientPaint(
            LOGICAL_WIDTH / 2.0f, LOGICAL_HEIGHT / 2.0f, 750.0f,
            new float[]{0.0f, 0.65f, 1.0f},
            new Color[]{new Color(0, 0, 0, 0), new Color(10, 6, 22, 120), new Color(4, 3, 10, 240)}
        );
        g.setPaint(vig);
        g.fillRect(0, 0, LOGICAL_WIDTH, LOGICAL_HEIGHT);
    }

    private void drawProceduralStudyBackdrop(Graphics2D g) {
        g.setColor(new Color(24, 22, 34));
        g.fillRect(0, 0, LOGICAL_WIDTH, 500);

        LinearGradientPaint floor = new LinearGradientPaint(
            0, 500, 0, 720,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(55, 34, 18), new Color(25, 14, 8)}
        );
        g.setPaint(floor);
        g.fillRect(0, 500, LOGICAL_WIDTH, 220);
    }

    /**
     * Draws the centered ornate mystery challenge window.
     */
    private void drawPuzzleWindow(Graphics2D g) {
        int x = windowBounds.x;
        int y = windowBounds.y;
        int w = windowBounds.width;
        int h = windowBounds.height;
        int arc = 24;

        // Soft drop shadow
        for (int i = 12; i >= 1; i--) {
            g.setColor(new Color(2, 3, 8, 18 - i));
            g.fillRoundRect(x - i, y - i + 6, w + i * 2, h + i * 2, arc + i, arc + i);
        }

        // Dark wooden outer frame
        LinearGradientPaint woodGrad = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{new Color(58, 36, 22), new Color(38, 22, 14), new Color(22, 12, 8)}
        );
        g.setPaint(woodGrad);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Frame gold rim
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(2.4f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Inner field (Parchment/leather)
        int pad = 12;
        int ix = x + pad;
        int iy = y + pad;
        int iw = w - pad * 2;
        int ih = h - pad * 2;
        int iarc = arc - 6;

        LinearGradientPaint innerParchment = new LinearGradientPaint(
            ix, iy, ix, iy + ih,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(28, 20, 24), new Color(20, 14, 18), new Color(14, 10, 14)}
        );
        g.setPaint(innerParchment);
        g.fillRoundRect(ix, iy, iw, ih, iarc, iarc);

        // Inner pinstripe
        g.setColor(new Color(180, 145, 45, 180));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(ix + 4, iy + 4, iw - 8, ih - 8, iarc - 4, iarc - 4);

        // Ornate Corner Metal Brackets
        drawCornerBracket(g, ix + 6, iy + 6, 0);
        drawCornerBracket(g, ix + iw - 26, iy + 6, 90);
        drawCornerBracket(g, ix + iw - 26, iy + ih - 26, 180);
        drawCornerBracket(g, ix + 6, iy + ih - 26, 270);

        // Header separator line
        int sepY = y + 104;
        LinearGradientPaint sepGrad = new LinearGradientPaint(
            x + 40, sepY, x + w - 40, sepY,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(212, 175, 55, 0), new Color(255, 215, 64, 190), new Color(212, 175, 55, 0)}
        );
        g.setPaint(sepGrad);
        g.setStroke(new BasicStroke(1.6f));
        g.drawLine(x + 40, sepY, x + w - 40, sepY);

        drawDiamond(g, x + w / 2, sepY, 5);
    }

    private void drawCornerBracket(Graphics2D g, int cx, int cy, int angleDeg) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx + 10, cy + 10);
        g2.rotate(Math.toRadians(angleDeg));

        g2.setColor(new Color(212, 175, 55));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(-10, -10, 8, -10);
        g2.drawLine(-10, -10, -10, 8);

        g2.setColor(new Color(255, 235, 140));
        g2.fillOval(-8, -8, 4, 4);

        g2.dispose();
    }

    /**
     * Draws the main heading (MYSTERY CHALLENGE), decorative lock icon, puzzle title, and description.
     */
    private void drawHeader(Graphics2D g) {
        int cx = windowBounds.x + windowBounds.width / 2;
        int topY = windowBounds.y + 14;

        // Decorative lock / keyhole icon above title
        drawLockIconHeader(g, cx, topY + 10);

        // Main Title: MYSTERY CHALLENGE
        Font titleFont = new Font("Georgia", Font.BOLD, 28);
        g.setFont(titleFont);
        FontMetrics tfm = g.getFontMetrics(titleFont);
        String title = "MYSTERY CHALLENGE";
        int tx = cx - tfm.stringWidth(title) / 2;
        int ty = topY + 48;

        g.setColor(new Color(8, 4, 12, 220));
        g.drawString(title, tx + 2, ty + 2);

        LinearGradientPaint titleGrad = new LinearGradientPaint(
            tx, ty - 22, tx, ty,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(255, 238, 140), new Color(212, 165, 45)}
        );
        g.setPaint(titleGrad);
        g.drawString(title, tx, ty);

        // Puzzle Title & Description
        Puzzle p = getCurrentPuzzle();
        String sub = p.getTitle() + " \u2014 " + p.getDescription();
        Font subFont = new Font("Georgia", Font.ITALIC, 14);
        g.setFont(subFont);
        FontMetrics sfm = g.getFontMetrics(subFont);
        int sx = cx - sfm.stringWidth(sub) / 2;
        int sy = ty + 22;

        g.setColor(new Color(230, 215, 190));
        g.drawString(sub, sx, sy);
    }

    private void drawLockIconHeader(Graphics2D g, int cx, int cy) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(cx, cy);

        // Lock shackle
        g2.setColor(new Color(255, 215, 64));
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawArc(-7, -10, 14, 12, 0, 180);

        // Lock body
        g2.setColor(new Color(212, 175, 55));
        g2.fillRoundRect(-9, -2, 18, 13, 4, 4);
        g2.setColor(new Color(150, 110, 30));
        g2.drawRoundRect(-9, -2, 18, 13, 4, 4);

        // Keyhole
        g2.setColor(new Color(20, 12, 16));
        g2.fillOval(-2, 1, 4, 4);
        g2.drawLine(0, 3, 0, 8);

        g2.dispose();
    }

    // =========================================================================
    // REUSABLE PUZZLE CONTENT AREA (SUPPORTS ALL 6 PUZZLE FORMATS)
    // =========================================================================

    private void drawPuzzleContentArea(Graphics2D g) {
        int cx = 240;
        int cy = 170;
        int cw = 800;
        int ch = 265;
        int arc = 16;

        // Outer drop shadow
        g.setColor(new Color(4, 5, 10, 180));
        g.fillRoundRect(cx + 2, cy + 4, cw, ch, arc, arc);

        // Card background (aged parchment & leather tone)
        LinearGradientPaint cardBg = new LinearGradientPaint(
            cx, cy, cx, cy + ch,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{new Color(32, 22, 20), new Color(24, 16, 16), new Color(16, 11, 14)}
        );
        g.setPaint(cardBg);
        g.fillRoundRect(cx, cy, cw, ch, arc, arc);

        // Golden card border
        g.setColor(new Color(195, 155, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(cx, cy, cw, ch, arc, arc);

        // Inner pinstripe
        g.setColor(new Color(140, 105, 35, 120));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(cx + 4, cy + 4, cw - 8, ch - 8, arc - 4, arc - 4);

        // Content Area Header Badge
        Puzzle p = getCurrentPuzzle();
        drawContentBadge(g, cx + 18, cy + 14, p.getType().getDisplayName());

        // Delegate to specific puzzle format renderer
        switch (p.getType()) {
            case NUMBER_CODE -> drawNumberCodePuzzle(g, cx, cy, cw, ch);
            case SYMBOL_SEQUENCE -> drawSymbolSequencePuzzle(g, cx, cy, cw, ch);
            case CIPHER -> drawCipherPuzzle(g, cx, cy, cw, ch);
            case PATTERN -> drawPatternPuzzle(g, cx, cy, cw, ch);
            case COMBINATION -> drawCombinationPuzzle(g, cx, cy, cw, ch);
            case LOGIC_DEDUCTION -> drawLogicDeductionPuzzle(g, cx, cy, cw, ch);
        }
    }

    private void drawContentBadge(Graphics2D g, int x, int y, String label) {
        Font bFont = new Font("Georgia", Font.BOLD, 13);
        g.setFont(bFont);
        FontMetrics fm = g.getFontMetrics(bFont);
        int bw = fm.stringWidth(label) + 24;
        int bh = 22;

        g.setColor(new Color(60, 38, 22));
        g.fillRoundRect(x, y, bw, bh, 6, 6);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRoundRect(x, y, bw, bh, 6, 6);

        g.setColor(new Color(255, 230, 140));
        g.drawString(label, x + 12, y + 16);
    }

    // 1. NUMBER CODE PUZZLE (Keypad & 4 Code Slots – Reference Image Screen 5)
    private void drawNumberCodePuzzle(Graphics2D g, int cx, int cy, int cw, int ch) {
        // Left side: 4-digit code slots & prompt
        int lx = cx + 40;
        int ly = cy + 60;

        Font pFont = new Font("Georgia", Font.BOLD, 18);
        g.setFont(pFont);
        g.setColor(new Color(255, 225, 110));
        g.drawString("ENTER 4-DIGIT CODE:", lx, ly);

        // 4 code slot boxes
        int slotSize = 52;
        int gap = 14;
        String ans = answerField.getText().trim();

        for (int i = 0; i < 4; i++) {
            int sx = lx + i * (slotSize + gap);
            int sy = ly + 25;

            // Slot background
            g.setColor(new Color(16, 10, 20));
            g.fillRoundRect(sx, sy, slotSize, slotSize, 8, 8);

            // Slot border
            g.setColor(new Color(212, 175, 55));
            g.setStroke(new BasicStroke(1.8f));
            g.drawRoundRect(sx, sy, slotSize, slotSize, 8, 8);

            // Slot character or dash
            if (i < ans.length()) {
                char c = ans.charAt(i);
                Font dFont = new Font("Georgia", Font.BOLD, 26);
                g.setFont(dFont);
                FontMetrics dfm = g.getFontMetrics(dFont);
                int dx = sx + (slotSize - dfm.charWidth(c)) / 2;
                int dy = sy + (slotSize - dfm.getHeight()) / 2 + dfm.getAscent();

                g.setColor(new Color(255, 240, 180));
                g.drawString(String.valueOf(c), dx, dy);
            } else {
                // Dash indicator
                g.setColor(new Color(140, 110, 60));
                g.setStroke(new BasicStroke(2.5f));
                g.drawLine(sx + 14, sy + slotSize / 2 + 2, sx + slotSize - 14, sy + slotSize / 2 + 2);
            }
        }

        // Subtitle prompt below slots
        Font tipFont = new Font("Georgia", Font.ITALIC, 13);
        g.setFont(tipFont);
        g.setColor(new Color(205, 190, 165));
        g.drawString("Use the keypad on the right or type via keyboard.", lx, ly + 115);
        g.drawString("Clues may be hidden in the study's clock and painting.", lx, ly + 138);

        // Right side: 2D Interactive Keypad (Screen 5)
        for (KeypadButton kb : keypadButtons) {
            drawKeypadButton(g, kb);
        }
    }

    private void drawKeypadButton(Graphics2D g, KeypadButton kb) {
        Rectangle r = kb.bounds;
        int x = r.x;
        int y = r.y + (kb.pressed ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 10;

        // Shadow
        g.setColor(new Color(4, 5, 10, 160));
        g.fillRoundRect(x + 1, y + 2, w, h, arc, arc);

        // Background gradient
        Color topC = kb.pressed ? new Color(55, 36, 22) : (kb.hovered ? new Color(75, 50, 30) : new Color(42, 28, 18));
        Color botC = kb.pressed ? new Color(30, 18, 10) : (kb.hovered ? new Color(45, 28, 16) : new Color(24, 14, 10));

        // Green tint for OK / Enter
        if ("OK".equals(kb.label)) {
            topC = kb.hovered ? new Color(48, 130, 80) : new Color(32, 95, 58);
            botC = kb.hovered ? new Color(24, 75, 45) : new Color(16, 58, 34);
        } else if ("C".equals(kb.label)) {
            topC = kb.hovered ? new Color(145, 40, 50) : new Color(110, 26, 36);
            botC = kb.hovered ? new Color(85, 20, 28) : new Color(60, 12, 20);
        }

        LinearGradientPaint bg = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{topC, botC}
        );
        g.setPaint(bg);
        g.fillRoundRect(x, y, w, h, arc, arc);

        // Golden rim
        g.setColor(kb.hovered ? new Color(255, 225, 80) : new Color(185, 145, 50));
        g.setStroke(new BasicStroke(1.4f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Label
        Font kFont = new Font("Arial", Font.BOLD, 17);
        g.setFont(kFont);
        FontMetrics fm = g.getFontMetrics(kFont);
        int tx = x + (w - fm.stringWidth(kb.label)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        g.setColor(new Color(6, 4, 8, 220));
        g.drawString(kb.label, tx + 1, ty + 1);

        g.setColor(Color.WHITE);
        g.drawString(kb.label, tx, ty);
    }

    // 2. SYMBOL SEQUENCE PUZZLE
    private void drawSymbolSequencePuzzle(Graphics2D g, int cx, int cy, int cw, int ch) {
        int midY = cy + 130;
        String[] labels = {"Moon", "Sun", "Star", "Hour"};
        int cardW = 140;
        int cardH = 95;
        int startX = cx + 55;
        int gap = 38;

        for (int i = 0; i < 4; i++) {
            int bx = startX + i * (cardW + gap);
            int by = midY - cardH / 2;
            int ccx = bx + cardW / 2;

            // Stone tablet card
            g.setColor(new Color(20, 15, 24));
            g.fillRoundRect(bx, by, cardW, cardH, 12, 12);
            g.setColor(new Color(212, 175, 55));
            g.setStroke(new BasicStroke(1.6f));
            g.drawRoundRect(bx, by, cardW, cardH, 12, 12);

            // Number tag
            Font numFont = new Font("Arial", Font.BOLD, 11);
            g.setFont(numFont);
            g.setColor(new Color(255, 215, 64));
            g.drawString("STAGE " + (i + 1), bx + 10, by + 18);

            // Procedural Symbol Drawing
            int iconY = by + 46;
            if (i == 0) drawMoonIcon(g, ccx, iconY, 14);
            else if (i == 1) drawSunIcon(g, ccx, iconY, 14);
            else if (i == 2) drawStarIcon(g, ccx, iconY, 14);
            else if (i == 3) drawHourglassIcon(g, ccx, iconY, 13);

            // Label text below icon
            Font lblFont = new Font("Georgia", Font.BOLD, 14);
            g.setFont(lblFont);
            FontMetrics lfm = g.getFontMetrics(lblFont);
            int lx = bx + (cardW - lfm.stringWidth(labels[i])) / 2;
            g.setColor(new Color(255, 235, 180));
            g.drawString(labels[i], lx, by + 82);

            // Sequence arrow between cards
            if (i < 3) {
                int arrowX = bx + cardW + 8;
                drawArrow(g, arrowX, midY, arrowX + gap - 16);
            }
        }

        Font hintFont = new Font("Georgia", Font.ITALIC, 14);
        g.setFont(hintFont);
        g.setColor(new Color(215, 200, 175));
        g.drawString("Arrange the symbols in order of the celestial cycle. Enter sequence names or numbers.", cx + 80, cy + 225);
    }

    private void drawMoonIcon(Graphics2D g, int cx, int cy, int r) {
        Area moon = new Area(new java.awt.geom.Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
        moon.subtract(new Area(new java.awt.geom.Ellipse2D.Double(cx - r * 0.4, cy - r * 1.1, r * 1.8, r * 1.8)));
        g.setColor(new Color(255, 235, 160));
        g.fill(moon);
    }

    private void drawSunIcon(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(255, 220, 80));
        g.fillOval(cx - r / 2, cy - r / 2, r, r);
        g.setStroke(new BasicStroke(1.8f));
        for (int i = 0; i < 8; i++) {
            double rad = Math.toRadians(i * 45);
            int x1 = (int) (cx + Math.cos(rad) * (r * 0.65));
            int y1 = (int) (cy + Math.sin(rad) * (r * 0.65));
            int x2 = (int) (cx + Math.cos(rad) * (r * 1.15));
            int y2 = (int) (cy + Math.sin(rad) * (r * 1.15));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawStarIcon(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(255, 240, 180));
        drawDiamond(g, cx, cy, r);
        drawDiamond(g, cx, cy, r / 2);
    }

    private void drawHourglassIcon(Graphics2D g, int cx, int cy, int r) {
        GeneralPath hg = new GeneralPath();
        hg.moveTo(cx - r, cy - r);
        hg.lineTo(cx + r, cy - r);
        hg.lineTo(cx - r, cy + r);
        hg.lineTo(cx + r, cy + r);
        hg.closePath();
        g.setColor(new Color(245, 215, 140));
        g.setStroke(new BasicStroke(2.0f));
        g.draw(hg);
    }

    private void drawArrow(Graphics2D g, int x1, int y, int x2) {
        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x1, y, x2, y);
        g.drawLine(x2 - 5, y - 5, x2, y);
        g.drawLine(x2 - 5, y + 5, x2, y);
    }

    // 3. CIPHER PUZZLE
    private void drawCipherPuzzle(Graphics2D g, int cx, int cy, int cw, int ch) {
        int px = cx + 80;
        int py = cy + 70;

        // Parchment document box
        g.setColor(new Color(24, 18, 22));
        g.fillRoundRect(px, py, cw - 160, 130, 12, 12);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(px, py, cw - 160, 130, 12, 12);

        Font cFont = new Font("Georgia", Font.BOLD, 24);
        g.setFont(cFont);
        g.setColor(new Color(255, 215, 64));
        g.drawString("ENCRYPTED TEXT:   'E V F D S H'", px + 40, py + 45);

        Font keyFont = new Font("Georgia", Font.ITALIC, 16);
        g.setFont(keyFont);
        g.setColor(new Color(240, 225, 200));
        g.drawString("Clue: 'Each letter has walked forward into tomorrow (+1).'", px + 40, py + 80);
        g.drawString("Shift backwards (-1) across the alphabet to reveal the password.", px + 40, py + 105);

        Font botFont = new Font("Arial", Font.PLAIN, 13);
        g.setFont(botFont);
        g.setColor(new Color(190, 175, 155));
        g.drawString("Enter the decrypted word into the answer box below.", px + 40, py + 155);
    }

    // 4. PATTERN PUZZLE
    private void drawPatternPuzzle(Graphics2D g, int cx, int cy, int cw, int ch) {
        int midY = cy + 120;
        int startX = cx + 110;
        int tileSize = 78;
        int gap = 34;

        for (int i = 0; i < 4; i++) {
            int bx = startX + i * (tileSize + gap);
            int by = midY - tileSize / 2;
            int tcx = bx + tileSize / 2;
            int tcy = by + tileSize / 2;

            g.setColor(new Color(22, 16, 26));
            g.fillRoundRect(bx, by, tileSize, tileSize, 12, 12);
            g.setColor(i == 3 ? new Color(255, 120, 120) : new Color(212, 175, 55));
            g.setStroke(new BasicStroke(1.8f));
            g.drawRoundRect(bx, by, tileSize, tileSize, 12, 12);

            if (i == 0 || i == 2) {
                // Solid Diamond
                drawDiamond(g, tcx, tcy, 18);
            } else if (i == 1) {
                // Hollow Diamond
                g.setColor(new Color(255, 225, 120));
                g.setStroke(new BasicStroke(2.4f));
                GeneralPath d = new GeneralPath();
                d.moveTo(tcx, tcy - 18);
                d.lineTo(tcx + 18, tcy);
                d.lineTo(tcx, tcy + 18);
                d.lineTo(tcx - 18, tcy);
                d.closePath();
                g.draw(d);
            } else {
                // Question mark
                Font qFont = new Font("Georgia", Font.BOLD, 30);
                g.setFont(qFont);
                FontMetrics qfm = g.getFontMetrics(qFont);
                int qx = bx + (tileSize - qfm.stringWidth("?")) / 2;
                int qy = by + (tileSize - qfm.getHeight()) / 2 + qfm.getAscent();
                g.setColor(new Color(255, 140, 140));
                g.drawString("?", qx, qy);
            }
        }

        Font hFont = new Font("Georgia", Font.ITALIC, 14);
        g.setFont(hFont);
        g.setColor(new Color(210, 195, 175));
        g.drawString("Identify the missing symbol to complete the mosaic pattern. Enter 'Solid' or 'Hollow'.", cx + 100, cy + 220);
    }

    // 5. COMBINATION PUZZLE
    private void drawCombinationPuzzle(Graphics2D g, int cx, int cy, int cw, int ch) {
        int midY = cy + 120;
        int startX = cx + 140;
        int dialW = 120;
        int dialH = 95;
        int gap = 45;

        String[] dials = {"[ III ]", "[ VI ]", "[ IX ]"};
        for (int i = 0; i < 3; i++) {
            int bx = startX + i * (dialW + gap);
            int by = midY - dialH / 2;

            // Tumbler cylinder
            g.setColor(new Color(26, 18, 14));
            g.fillRoundRect(bx, by, dialW, dialH, 14, 14);
            g.setColor(new Color(212, 175, 55));
            g.setStroke(new BasicStroke(2.0f));
            g.drawRoundRect(bx, by, dialW, dialH, 14, 14);

            // Dial ribs
            g.setColor(new Color(160, 120, 45, 120));
            g.drawLine(bx + 10, by + 26, bx + dialW - 10, by + 26);
            g.drawLine(bx + 10, by + dialH - 26, bx + dialW - 10, by + dialH - 26);

            Font dFont = new Font("Georgia", Font.BOLD, 22);
            g.setFont(dFont);
            FontMetrics dfm = g.getFontMetrics(dFont);
            int tx = bx + (dialW - dfm.stringWidth(dials[i])) / 2;
            int ty = by + (dialH - dfm.getHeight()) / 2 + dfm.getAscent();

            g.setColor(new Color(255, 235, 160));
            g.drawString(dials[i], tx, ty);
        }

        Font cFont = new Font("Georgia", Font.ITALIC, 14);
        g.setFont(cFont);
        g.setColor(new Color(210, 195, 175));
        g.drawString("Rotate the brass dial rings to align with the grandfather clock hands. Enter '3-6-9'.", cx + 120, cy + 220);
    }

    // 6. LOGIC & DEDUCTION PUZZLE
    private void drawLogicDeductionPuzzle(Graphics2D g, int cx, int cy, int cw, int ch) {
        int px = cx + 60;
        int py = cy + 65;

        g.setColor(new Color(22, 16, 24));
        g.fillRoundRect(px, py, cw - 120, 135, 12, 12);
        g.setColor(new Color(212, 175, 55));
        g.setStroke(new BasicStroke(1.6f));
        g.drawRoundRect(px, py, cw - 120, 135, 12, 12);

        Font dFont = new Font("Georgia", Font.BOLD, 15);
        g.setFont(dFont);
        g.setColor(new Color(255, 225, 110));
        g.drawString("INVESTIGATIVE NOTES \u2014 THREE STATEMENTS:", px + 24, py + 28);

        Font bFont = new Font("Georgia", Font.PLAIN, 14);
        g.setFont(bFont);
        g.setColor(new Color(240, 230, 210));
        g.drawString("1. 'The key was hidden where shadows converge, never in direct sunlight.'", px + 24, py + 56);
        g.drawString("2. 'It rests inside an iron-bound receptacle near dusty manuscripts.'", px + 24, py + 80);
        g.drawString("3. 'The grandfather clock stopped 15 minutes before the key was locked.'", px + 24, py + 104);

        Font promptFont = new Font("Arial", Font.ITALIC, 13);
        g.setFont(promptFont);
        g.setColor(new Color(190, 175, 155));
        g.drawString("Deduce the hiding location. Enter 'Chest', 'Bookshelf', or 'Clock'.", px + 24, py + 155);
    }

    // =========================================================================
    // ANSWER INPUT LABEL & FEEDBACK BANNER
    // =========================================================================

    private void drawAnswerInputLabel(Graphics2D g) {
        Font labelFont = new Font("Georgia", Font.BOLD, 15);
        g.setFont(labelFont);
        g.setColor(new Color(245, 215, 95));
        g.drawString("YOUR ANSWER:", 250, 481);

        // Small decorative diamond before label
        drawDiamond(g, 242, 476, 4);
    }

    private void drawFeedbackBanner(Graphics2D g, int x, int y, int w, int h) {
        int arc = 12;

        g.setColor(new Color(4, 5, 10, 200));
        g.fillRoundRect(x + 1, y + 2, w, h, arc, arc);

        LinearGradientPaint bg = new LinearGradientPaint(
            x, y, x, y + h,
            new float[]{0.0f, 1.0f},
            new Color[]{new Color(75, 45, 15), new Color(42, 24, 8)}
        );
        g.setPaint(bg);
        g.fillRoundRect(x, y, w, h, arc, arc);

        g.setColor(new Color(255, 215, 64));
        g.setStroke(new BasicStroke(1.8f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        Font bFont = new Font("Georgia", Font.BOLD, 13);
        g.setFont(bFont);
        FontMetrics fm = g.getFontMetrics(bFont);
        int tx = x + (w - fm.stringWidth(temporaryMessage)) / 2;
        int ty = y + (h - fm.getHeight()) / 2 + fm.getAscent();

        drawDiamond(g, tx - 14, ty - 4, 4);

        g.setColor(new Color(255, 248, 220));
        g.drawString(temporaryMessage, tx, ty);
    }

    // =========================================================================
    // LAYER: POLISHED BUTTONS & HELPERS
    // =========================================================================

    private void drawButton(Graphics2D g, MenuButton btn) {
        Rectangle r = btn.bounds;
        int x = r.x;
        int y = r.y + (btn.pressed ? 2 : 0);
        int w = r.width;
        int h = r.height;
        int arc = 18;

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

        // Drop shadow
        g.setColor(new Color(4, 5, 12, 170));
        g.fillRoundRect(x + 2, y + 4, w, h, arc, arc);

        // Gradient
        Color topC = btn.pressed ? btn.pressedColor : (btn.hovered ? btn.hoverTopColor : btn.topColor);
        Color botC = btn.pressed ? btn.pressedColor.darker() : (btn.hovered ? btn.hoverBottomColor : btn.bottomColor);

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
            new Color[]{new Color(255, 255, 255, 50), new Color(255, 255, 255, 10)}
        );
        g.setPaint(glossGrad);
        g.fill(glossArea);

        // Golden border
        g.setColor(btn.borderColor);
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(x, y, w, h, arc, arc);

        // Text or Icon
        if ("X".equals(btn.text)) {
            // Clean vector crossed diagonal lines
            g.setColor(new Color(4, 5, 12, 220));
            g.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 10, y + 10, x + w - 10, y + h - 10);
            g.drawLine(x + w - 10, y + 10, x + 10, y + h - 10);

            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 9, y + 9, x + w - 11, y + h - 11);
            g.drawLine(x + w - 11, y + 9, x + 9, y + h - 11);
            return;
        }

        Font font = (w < 40) ? new Font("Arial", Font.BOLD, 16) : new Font("Arial", Font.BOLD, 15);
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
    // GETTERS FOR TESTING & VERIFICATION
    // =========================================================================

    public JTextField getAnswerField() {
        return answerField;
    }

    public MenuButton getSubmitButton() {
        return submitButton;
    }

    public MenuButton getClearButton() {
        return clearButton;
    }

    public MenuButton getHintButton() {
        return hintButton;
    }

    public MenuButton getCloseButton() {
        return closeButton;
    }

    public MenuButton getCloseXButton() {
        return closeXButton;
    }

    public String getTemporaryMessage() {
        return temporaryMessage;
    }

    // =========================================================================
    // INNER CLASSES: KeypadButton & MenuButton
    // =========================================================================

    private static class KeypadButton {
        final String label;
        final Rectangle bounds;
        boolean hovered;
        boolean pressed;

        KeypadButton(String label, Rectangle bounds) {
            this.label = label;
            this.bounds = bounds;
        }
    }

    public static class MenuButton {
        public String text;
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
